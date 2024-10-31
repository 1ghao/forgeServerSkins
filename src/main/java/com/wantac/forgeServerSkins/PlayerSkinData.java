package com.wantac.forgeServerSkins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class PlayerSkinData {
    private final String filepath;
    private Map<String, SkinInfo> skinData;
    private final Gson gson;

    public PlayerSkinData(String filepath) {
        this.filepath = filepath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.skinData = loadSkinData();
    }

    private Map<String, SkinInfo> loadSkinData() {
        try (Reader reader = new FileReader(filepath)) {
            Type type = new TypeToken<Map<String, SkinInfo>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    public void saveSkinData() {
        try (Writer writer = new FileWriter(filepath)) {
            gson.toJson(skinData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSkin(String playerName, String skinValue, String skinSignature) {
        skinData.put(playerName, new SkinInfo(skinValue, skinSignature));
        saveSkinData();
    }

    public SkinInfo getSkin(String playerName) {
        return skinData.get(playerName);
    }

    public static class SkinInfo {
        public String value;
        public String signature;

        public SkinInfo(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }
    }
}
