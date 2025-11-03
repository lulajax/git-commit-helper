package com.junjie.githelper.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.junjie.githelper.model.AppConfig;
import com.junjie.githelper.model.LLMSettings;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ConfigService {

    private static final Path CONFIG_DIR = Path.of(System.getProperty("user.home"), ".commit-pal");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AppConfig loadConfig() throws IOException {
        if (!Files.exists(CONFIG_FILE)) {
            return createDefaultConfig();
        }
        try (FileReader reader = new FileReader(CONFIG_FILE.toFile())) {
            return gson.fromJson(reader, AppConfig.class);
        }
    }

    public void saveConfig(AppConfig config) throws IOException {
        Files.createDirectories(CONFIG_DIR);
        try (FileWriter writer = new FileWriter(CONFIG_FILE.toFile())) {
            gson.toJson(config, writer);
        }
    }

    private AppConfig createDefaultConfig() throws IOException {
        LLMSettings defaultLLMSettings = new LLMSettings("openai", "YOUR_API_KEY", "gpt-4o", "https://api.openai.com/v1");
        AppConfig defaultConfig = new AppConfig("1.0", defaultLLMSettings, new ArrayList<>(), null);
        saveConfig(defaultConfig);
        return defaultConfig;
    }
}
