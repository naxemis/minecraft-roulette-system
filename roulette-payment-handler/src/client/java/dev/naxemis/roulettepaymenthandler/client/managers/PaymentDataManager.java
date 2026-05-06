// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.managers;

import com.google.gson.reflect.TypeToken;

import dev.naxemis.roulettepaymenthandler.client.models.PaymentDataHolder;
import dev.naxemis.roulettepaymenthandler.client.utility.ActionBarNotification;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;
import dev.naxemis.roulettepaymenthandler.client.utility.PlaySoundEffect;

import com.google.gson.Gson;
import net.minecraft.sound.SoundEvents;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import java.lang.reflect.Type;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentDataManager {
    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();
    private static final FileManager fileManager = new FileManager();

    private static final Gson gson = new Gson(); // creates Gson instance used for JSON serialization and deserialization
    private static final String paymentDataFilePath = System.getenv("APPDATA") + "/RoulettePaymentTracker/paymentData.json"; //file path to JSON file
    private static final Path paymentDataPath = Paths.get(paymentDataFilePath); // converts paymentDataFilePath string to a Path object

    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // thread pool for database operations

    public void createEmptyDataFile() {
        try {
            if (!fileManager.checkForDataDirectory(paymentDataFilePath)) return;
            String defaultJson = "[]";
            if (!fileManager.checkForDataJson(paymentDataFilePath, defaultJson)) return;

            Files.write(paymentDataPath, defaultJson.getBytes());

            System.out.println("Created an empty paymentData.json file.");
            actionBarNotification.sendMessage("Created empty paymentData.json.", "§a");
            playSoundEffect.playSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
        } catch (IOException exception) {
            System.out.println("Failed to create empty paymentData.json file: " + exception.getMessage());
            actionBarNotification.sendMessage("Failed to create empty paymentData.json.", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
        }
    }

    private List<PaymentDataHolder> readPaymentDataJson() {
        try (Reader fileReader = Files.newBufferedReader(paymentDataPath)) {
            Type listType = new TypeToken<List<PaymentDataHolder>>(){}.getType();
            List<PaymentDataHolder> list = gson.fromJson(fileReader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException exception) {
            System.out.println("Something went wrong reading existing data: " + exception.getMessage());
            actionBarNotification.sendMessage("Can't read data from JSON file.", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            return null;
        }
    }

    private boolean checkPlayerAlreadyExists(PaymentDataHolder player, String paymentUsername) {
        return player.username().equals(paymentUsername);
    }

    private void updatePaymentData(List<PaymentDataHolder> paymentDataList, PaymentDataHolder newPaymentData) {
        for (int index = 0; index < paymentDataList.size(); index++) {
            PaymentDataHolder player = paymentDataList.get(index);

            if (checkPlayerAlreadyExists(player, newPaymentData.username())) {
                long updatedAmount = player.amount() + newPaymentData.amount(); // updates payment amount
                PaymentDataHolder updatedPlayer = new PaymentDataHolder(newPaymentData.username(), updatedAmount); // creates object with updates payment data
                paymentDataList.set(index, updatedPlayer);
                return;
            }
        }

        paymentDataList.add(newPaymentData);
    }

    private void writeUpdatedList(List<PaymentDataHolder> paymentDataList) {
        try (FileWriter fileWriter = new FileWriter(paymentDataFilePath)) {
            gson.toJson(paymentDataList, fileWriter);
            System.out.println("Succesfully saved payment data to JSON file");
            actionBarNotification.sendMessage("Saved payment data to JSON file.", "§a");
            playSoundEffect.playSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
        } catch (IOException exception) {
            System.out.println("Something went wrong during saving data to JSON file: " + exception.getMessage());
            actionBarNotification.sendMessage("Couldn't save payment data to JSON file.", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
        }
    }

    private void processPaymentData(PaymentDataHolder newPaymentData) {
            if (Files.exists(paymentDataPath)) {
                List<PaymentDataHolder> paymentDataList = readPaymentDataJson();
                if (paymentDataList == null) return;
                updatePaymentData(paymentDataList, newPaymentData);
                writeUpdatedList(paymentDataList);
            }
    }

    public CompletableFuture<Void> saveData(PaymentDataHolder newPaymentData) {

        return CompletableFuture.runAsync(() -> { // runs the operation on background thread
            createEmptyDataFile();
            processPaymentData(newPaymentData);
        }, executorService); // makes the method use dedicated thread pool for execution
    }

    public void asyncProcessShutdown() {
        executorService.shutdown(); // closes the database connection thread
        System.out.println("Closing Payment Data Manager thread.");
    }
}
