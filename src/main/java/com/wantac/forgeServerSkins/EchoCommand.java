package com.wantac.forgeServerSkins;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class EchoCommand extends CommandBase {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {

        forgeServerSkins.logger.info("execute called");

        if (params != null && params.length > 0) {
            for (String param : params) {
                String message = "Echo : " + param;
                TextComponentString text = new TextComponentString(message);
                text.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(text);
            }
        }
    }

    @Override
    public String getName() {
        return "echo";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.echo.usage";
    }
}