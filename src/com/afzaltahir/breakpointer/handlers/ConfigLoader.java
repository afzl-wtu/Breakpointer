package com.afzaltahir.breakpointer.handlers;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ConfigLoader {
    private Map<String, Object> configData;

    public void loadConfig() {
        try {
            // Determine the path based on the operating system
            Path configFilePath = Paths.get(System.getProperty("user.home"), "bpa.json");;

            // Load and parse the JSON file
            try (Reader reader = new FileReader(configFilePath.toFile())) {
                Gson gson = new Gson();
                configData = gson.fromJson(reader, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get the list of keywords
    public List<String> getKeywords() {
        if (configData != null && configData.containsKey("keywords")) {
            return (List<String>) configData.get("keywords");
        }
        return null;
    }

    // Method to get the list of packages
    public List<String> getPackages() {
        if (configData != null && configData.containsKey("packages")) {
            return (List<String>) configData.get("packages");
        }
        return null;
    }
    public List<String> getJavaClasses() {
        if (configData != null && configData.containsKey("javaclasses")) {
            return (List<String>) configData.get("javaclasses");
        }
        return null;
    }

    public Map<String, Object> getConfigData() {
        return configData;
    }
}



