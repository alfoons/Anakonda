package com.anakonda.client.config;

import com.anakonda.client.Anakonda;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.ModuleManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    public static final ConfigManager INSTANCE = new ConfigManager();
    private final File configFile;
    private final Gson gson;

    public ConfigManager() {
        this.configFile = FabricLoader.getInstance().getConfigDir().resolve("anakonda.json").toFile();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void save() {
        JsonArray array = new JsonArray();
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", module.getName());
            obj.addProperty("enabled", module.isEnabled());
            obj.addProperty("key", module.getKey());
            array.add(obj);
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            Anakonda.LOGGER.error("Failed to save config", e);
        }
    }

    public void load() {
        if (!configFile.exists()) return;

        try (FileReader reader = new FileReader(configFile)) {
            // Use generic JsonElement to handle potential structure changes gracefully
            com.google.gson.JsonElement element = com.google.gson.JsonParser.parseReader(reader);

            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (var el : array) {
                    if (el.isJsonObject()) {
                        JsonObject obj = el.getAsJsonObject();
                        if (obj.has("name")) {
                            String name = obj.get("name").getAsString();
                            Module module = ModuleManager.INSTANCE.getModule(name);
                            if (module != null) {
                                if (obj.has("enabled") && obj.get("enabled").getAsBoolean()) {
                                    module.setEnabled(true);
                                }
                                if (obj.has("key")) {
                                    module.setKey(obj.get("key").getAsInt());
                                }
                            }
                        }
                    }
                }
            } else {
                Anakonda.LOGGER.warn("Config file structure invalid (not an array), resetting config.");
            }
        } catch (Exception e) {
            Anakonda.LOGGER.error("Failed to load config, starting with defaults", e);
        }
    }
}
