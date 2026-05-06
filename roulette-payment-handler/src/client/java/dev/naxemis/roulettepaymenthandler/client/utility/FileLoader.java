package dev.naxemis.roulettepaymenthandler.client.utility;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

import dev.naxemis.roulettepaymenthandler.client.models.WinnerDataHolder;
import net.minecraft.sound.SoundEvents;

public class FileLoader {
    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();

    private static final Gson gson = new Gson();

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

    public boolean checkForDataJson(String filePath) {
        final Path dataFilePath = Paths.get(filePath);
        final Path fileName = dataFilePath.getFileName();

        try {
            if (!Files.exists(dataFilePath) || Files.size(dataFilePath) == 0) {
                System.out.println(fileName + " missing or empty, creating default.");

                WinnerDataHolder defaultData = new WinnerDataHolder("", 0);
                try (FileWriter writer = new FileWriter(dataFilePath.toFile())) {
                    gson.toJson(defaultData, writer);
                    System.out.println("Created an empty " + fileName + " file.");
                }
            }
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to create empty " + fileName + " file: " + exception.getMessage());
            actionBarNotification.sendMessage("Failed to create " + fileName + ".", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            return false;
        }
    }
}
