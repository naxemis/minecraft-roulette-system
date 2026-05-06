package dev.naxemis.roulettepaymenthandler.client.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.sound.SoundEvents;

public class FileLoader {
    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();

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
}
