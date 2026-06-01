package dev.naxemis.roulettepaymenthandler.client.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundEvents;

public class FileManager {
    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();
    private static final Gson gson = new Gson();
    private static final String DATA_DIRECTORY_NAME = "RoulettePaymentTracker";

    public static String resolveDataPath(String fileName) {
        return FabricLoader.getInstance().getConfigDir()
                .resolve(DATA_DIRECTORY_NAME)
                .resolve(fileName)
                .toString();
    }

    public boolean checkForDataDirectory(String filePath) {
        final Path dataFilePath = Paths.get(filePath);
        final Path fileName = dataFilePath.getFileName();

        try {
            if (!Files.exists(dataFilePath.getParent())) {
                System.out.println("Creating directories for " + fileName + ".");
                Files.createDirectories(dataFilePath.getParent());
            }
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to create directories for " + fileName + ": " + exception.getMessage());
            return false;
        }
    }

    public boolean checkForDataJson(String filePath, String defaultJson) {
        final Path dataFilePath = Paths.get(filePath);
        final Path fileName = dataFilePath.getFileName();
        try {
            if (!Files.exists(dataFilePath) || Files.size(dataFilePath) == 0) {
                System.out.println(fileName + " missing or empty, creating default.");
                Files.writeString(dataFilePath, defaultJson);
                System.out.println("Created " + fileName + ".");
            }
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to create " + fileName + ": " + exception.getMessage());
            actionBarNotification.sendMessage("Failed to create " + fileName + ".", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            return false;
        }
    }

    public boolean saveJson(String filePath, JsonObject jsonObject) {
        if (!checkForDataDirectory(filePath)) {
            return false;
        }

        final Path dataFilePath = Paths.get(filePath);
        final Path fileName = dataFilePath.getFileName();

        try (BufferedWriter fileWriter = Files.newBufferedWriter(dataFilePath, StandardCharsets.UTF_8)) {
            gson.toJson(jsonObject, fileWriter);
            System.out.println("Successfully saved " + fileName + ".");
            actionBarNotification.sendMessage("Saved " + fileName + ".", "§a");
            playSoundEffect.playSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to save " + fileName + ": " + exception.getMessage());
            actionBarNotification.sendMessage("Failed to save " + fileName + ".", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            return false;
        }
    }

    public JsonObject loadJson(String filePath, JsonObject defaultJson) {
        if (!checkForDataDirectory(filePath) || !checkForDataJson(filePath, gson.toJson(defaultJson))) {
            return defaultJson;
        }

        final Path dataFilePath = Paths.get(filePath);
        final Path fileName = dataFilePath.getFileName();

        try {
            String jsonString = Files.readString(dataFilePath);
            JsonObject loaded = gson.fromJson(jsonString, JsonObject.class);
            System.out.println("Successfully loaded " + fileName + ".");
            return loaded != null ? loaded : defaultJson;
        } catch (IOException exception) {
            System.out.println("Failed to load " + fileName + ": " + exception.getMessage());
            actionBarNotification.sendMessage("Failed to load " + fileName + ".", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            return defaultJson;
        }
    }

    public boolean clearJson(String filePath, JsonObject defaultJson) {
        return saveJson(filePath, defaultJson);
    }
}
