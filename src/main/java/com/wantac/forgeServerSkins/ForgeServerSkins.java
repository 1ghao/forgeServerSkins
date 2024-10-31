package com.wantac.forgeServerSkins;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "forgeserverskins", name = "Forge  Server Skins", version = "1.0", acceptableRemoteVersions = "*")
public class ForgeServerSkins
{
    public static final String MODID = "forgeserverskins";
    public static final String NAME = "Forge Server Skins";
    public static final String VERSION = "1.0";

    static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        logger.info("initalise FMLServerStartingEvent :" + NAME);
        event.registerServerCommand(new SkinCommand());
    }
}
