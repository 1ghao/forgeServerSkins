package com.wantac.forgeServerSkins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "forgeserverskins", name = "Forge  Server Skins", version = "1.0", acceptableRemoteVersions = "*")
public class ForgeServerSkins {
    public static final String MODID = "forgeserverskins";
    public static final String NAME = "Forge Server Skins";
    public static final String VERSION = "1.0";

    static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    private SkinCommand skinCommand;

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        logger.info("initalise FMLServerStartingEvent :" + NAME);

        String skinDataPath = event.getServer().getDataDirectory().getPath() + "/config/skin_data.json";
        skinCommand = new SkinCommand(skinDataPath);
        event.registerServerCommand(skinCommand);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            skinCommand.applyStoredSkin(player);
        }
    }
}
