package com.wantac.forgeServerSkins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class SkinCommand extends CommandBase {
    private final PlayerSkinData skinData;

    public SkinCommand(String dataFilePath) {
        this.skinData  = new PlayerSkinData((dataFilePath));
    }

    @Override
    public String getName() {
        return "skin";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/skin <username>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length != 1) {
            throw new CommandException("Usage: /skin <username>");
        }

//        if (sender.canUseCommand())

        String targetUsername = args[0];
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        applySkin(sender, player, targetUsername);
    }

    private void applySkin(ICommandSender sender, EntityPlayerMP player, String targetUsername) {
        try {
            // Fetch UUID for the target username
            URL uuidURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + targetUsername);
            BufferedReader reader = new BufferedReader(new InputStreamReader(uuidURL.openStream()));
            JsonObject uuidData = new JsonParser().parse(reader).getAsJsonObject();
            reader.close();
            String uuid = uuidData.get("id").getAsString();

            // Fetch skin properties for the UUID
            URL skinURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            reader = new BufferedReader(new InputStreamReader(skinURL.openStream()));
            JsonObject profileData = new JsonParser().parse(reader).getAsJsonObject();
            reader.close();

            JsonArray properties = profileData.getAsJsonArray("properties");
            for (JsonElement element : properties) {
                JsonObject property = element.getAsJsonObject();
                if (property.get("name").getAsString().equals("textures")) {
                    String skinValue = property.get("value").getAsString();
                    String skinSignature = property.get("signature").getAsString();

                    // Save skin data
                    skinData.setSkin(player.getName(), skinValue, skinSignature);

                    applyStoredSkin(player);


                    // Apply the skin to the player
                    GameProfile newProfile = new GameProfile(player.getUniqueID(), player.getName());

                    // Add the skin property to the new profile
                    newProfile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));

                    // Set the gameProfile field
                    ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, player, newProfile, "field_146106_i");

                    // Refresh player
                    refreshPlayerSkin(player);
                    resyncPlayerInventory(player);
                    sender.sendMessage(new TextComponentString("Skin changed to " + targetUsername));
                    return;
                }
            }

            sender.sendMessage(new TextComponentString("Could not retrieve skin data for " + targetUsername));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponentString("An error occurred while fetching skin data."));
        }
    }

    private void refreshPlayerSkin(EntityPlayerMP player) {
        MinecraftServer server = player.getServer();
        assert server != null;

        // Remove the player from the player list
        server.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, player));

        // Update the player's skin on the client side
        player.connection.sendPacket(new SPacketEntityMetadata(player.getEntityId(), player.getDataManager(), true));

        // Add the player back to the player list
        server.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, player));

        // Respawn the player to force a complete refresh
        player.connection.sendPacket(new SPacketRespawn(
                player.dimension,
                player.world.getDifficulty(),
                player.world.getWorldInfo().getTerrainType(),
                player.interactionManager.getGameType()
        ));

        // Update the player's position
        player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
    }

    private void resyncPlayerInventory(EntityPlayerMP player) {
        player.sendContainerToPlayer(player.inventoryContainer);
    }

    public void applyStoredSkin(EntityPlayerMP player) {
        PlayerSkinData.SkinInfo skinInfo = skinData.getSkin(player.getName());
        if (skinInfo != null) {
            GameProfile newProfile = new GameProfile(player.getUniqueID(), player.getName());
            newProfile.getProperties().put("textures", new Property("textures", skinInfo.value, skinInfo.signature));
            ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, player, newProfile, "field_146106_i");
            refreshPlayerSkin(player);
            resyncPlayerInventory(player);
        }
    }

}



