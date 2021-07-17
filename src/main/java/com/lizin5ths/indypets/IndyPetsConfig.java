package com.lizin5ths.indypets;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/*  Code logic taken from https://github.com/ladysnake/illuminations
    in file: Illuminations/src/main/java/ladysnake/illuminations/client/Config.java
    by replacing relevant arguments with my own. */

public class IndyPetsConfig {
    public static final Path PROPERTIES_PATH = FabricLoader.getInstance().getConfigDir().resolve("indypets.properties");
    private static final Properties config = new Properties();

    private static boolean disableCatFollow;
    private static boolean disableParrotFollow;
    private static boolean disableWolfFollow;
    private static boolean selectiveFollowing;
    private static boolean silentMode;

    public static void load() {
        if (Files.isRegularFile(PROPERTIES_PATH)) {
            // load indypets.properties
            try {
                config.load(Files.newBufferedReader(PROPERTIES_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // if no indypets.properties, load default values
            // define default properties
            setDisableCatFollow(true);
            setDisableParrotFollow(true);
            setDisableWolfFollow(true);
            setSelectiveFollowing(true);
            setSilentMode(false);
        }

        try {
            disableCatFollow = Boolean.parseBoolean(config.getProperty("disableCatFollow"));
            disableParrotFollow = Boolean.parseBoolean(config.getProperty("disableParrotFollow"));
            disableWolfFollow = Boolean.parseBoolean(config.getProperty("disableWolfFollow"));
            selectiveFollowing = Boolean.parseBoolean(config.getProperty("selectiveFollowing"));
            silentMode = Boolean.parseBoolean(config.getProperty("silentMode"));
        } catch (Exception e) {
            setDisableCatFollow(true);
            setDisableParrotFollow(true);
            setDisableWolfFollow(true);
            setSelectiveFollowing(true);
            setSilentMode(false);
        }
    }

    public static void save() {
        try {
            config.store(Files.newBufferedWriter(IndyPetsConfig.PROPERTIES_PATH), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getDisableCatFollow() {
        return disableCatFollow;
    }
    public static boolean getDisableParrotFollow() {
        return disableParrotFollow;
    }
    public static boolean getDisableWolfFollow() {
        return disableWolfFollow;
    }
    public static boolean getSelectiveFollowing() {
        return selectiveFollowing;
    }
    public static boolean getSilentMode() {
        return silentMode;
    }

    private static void setDisableCatFollow(boolean value) {
        disableCatFollow = value;
        config.setProperty("disableCatFollow", Boolean.toString(value));
        IndyPetsConfig.save();
    }
    private static void setDisableParrotFollow(boolean value) {
        disableParrotFollow = value;
        config.setProperty("disableParrotFollow", Boolean.toString(value));
        IndyPetsConfig.save();
    }
    private static void setDisableWolfFollow(boolean value) {
        disableWolfFollow = value;
        config.setProperty("disableWolfFollow", Boolean.toString(value));
        IndyPetsConfig.save();
    }
    private static void setSelectiveFollowing(boolean value) {
        selectiveFollowing = value;
        config.setProperty("selectiveFollowing", Boolean.toString(value));
        IndyPetsConfig.save();
    }
    private static void setSilentMode (boolean value) {
        silentMode = value;
        config.setProperty("silentMode", Boolean.toString(value));
        IndyPetsConfig.save();
    }
}