package com.anakonda.client;

import com.anakonda.client.config.ConfigManager;
import com.anakonda.client.module.ModuleManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Anakonda implements ModInitializer {
    public static final String MOD_ID = "anakonda";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Anakonda INSTANCE = new Anakonda();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Anakonda Client...");
        ModuleManager.INSTANCE.init();
        ConfigManager.INSTANCE.load();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Saving Anakonda Config...");
            ConfigManager.INSTANCE.save();
        }));

        LOGGER.info("Anakonda Client initialized!");
    }
}
