// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.managers;

import com.google.gson.Gson;

import dev.naxemis.roulettepaymenthandler.client.core.PaymentConfirmation;
import dev.naxemis.roulettepaymenthandler.client.addon.SendMessageAfterDraw;
import dev.naxemis.roulettepaymenthandler.client.utility.ActionBarNotification;
import dev.naxemis.roulettepaymenthandler.client.utility.PlaySoundEffect;
import dev.naxemis.roulettepaymenthandler.client.models.WinnerDataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WinnerDataManager {

    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();

    private static WinnerDataHolder winnerData = new WinnerDataHolder("", 0);

    // TODO (SYSTEM): System.getenv("APPDATA") = null for Linux/Mac => "null/RoulettePaymentTracker/..."
    private static final String filePath = System.getenv("APPDATA") + "/RoulettePaymentTracker/winnerData.json";
    private static final Path winnerDataFilePath = Paths.get(filePath);

    private static final Gson gson = new Gson();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // TODO (DATA): Move this method into DataLoader class. Make it reusable for different values.
    private boolean checkForDataDirectory() {
        try {
            if (!Files.exists(winnerDataFilePath.getParent())) {
                System.out.println("Creating directories for winnerData.json.");
                Files.createDirectories(winnerDataFilePath.getParent());
            }
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to create directories for winnerData.json: " + exception.getMessage());
            return false;
        }
    }

    // TODO (DATA): Move this method into DataLoader class. Make it reusable for different values.
    private boolean checkForDataJson() {
        try {
            if (!Files.exists(winnerDataFilePath) || Files.size(winnerDataFilePath) == 0) {
                System.out.println("winnerData.json missing or empty, creating default.");

                WinnerDataHolder defaultData = new WinnerDataHolder("", 0);
                try (FileWriter writer = new FileWriter(winnerDataFilePath.toFile())) {
                    gson.toJson(defaultData, writer);
                    System.out.println("Created an empty winnerData.json file.");
                }
            }
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to create empty winnerData.json file: " + exception.getMessage());
            actionBarNotification.sendMessage("Failed to create winnerData.json.", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            return false;
        }
    }

    private WinnerDataHolder readWinnerData() {
    try (Reader reader = new FileReader(filePath)) {
        return gson.fromJson(reader, WinnerDataHolder.class);
    } catch (IOException exception) {
        System.out.println("Failed to read winnerData.json: " + exception.getMessage());
        actionBarNotification.sendMessage("Failed to read winner data.", "§4");
        playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
        return null;
    }
    }

    private CompletableFuture<Boolean> sendPayment(MinecraftClient minecraftClient, WinnerDataHolder winnerData) {
        try {
            String command = String.format("pay %s %d", winnerData.username(), winnerData.amount());
            minecraftClient.player.networkHandler.sendChatCommand(command);
            return PaymentConfirmation.confirm();
        } catch (Exception exception) {
            System.out.println("Payment send failed: " + exception.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    private void notifyUser() {
        System.out.println("Winner: " + winnerData.username() + " Amount: " + winnerData.amount());
        actionBarNotification.sendMessage(String.format("Winner: %s | Amount: %d$.", winnerData.username(), winnerData.amount()),"§a");
        playSoundEffect.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    private void processWinnerData() {
        WinnerDataHolder parsed = readWinnerData();
        if (parsed == null || parsed.equals(winnerData)) return;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null || minecraftClient.player == null) return;
        
        WinnerDataHolder previous = winnerData;
        winnerData = parsed;

        sendPayment(minecraftClient, parsed).thenAcceptAsync(success -> {
            if (!success) {
                winnerData = previous;
                actionBarNotification.sendMessage("Payment failed!", "§4");
                playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
                return;
            }
        
            notifyUser();
            SendMessageAfterDraw.start();
        }, executorService);
    }

    public CompletableFuture<Void> updateWinnerData() {
        return CompletableFuture.runAsync(() -> {
            if (!checkForDataDirectory()) return;
            if (!checkForDataJson()) return;
            processWinnerData();
        }, executorService);
    }

    public void asyncProcessShutdown() {
        executorService.shutdown();
        System.out.println("Closing Winner Data Manager thread.");
    }
}