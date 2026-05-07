// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.sound.SoundEvents;

import java.io.FileWriter;
import java.io.IOException;

import dev.naxemis.roulettepaymenthandler.client.utility.ActionBarNotification;
import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;
import dev.naxemis.roulettepaymenthandler.client.utility.PlaySoundEffect;

public class PaymentDataCommands {
    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();
    private static final ChatMessenger chatMessenger = new ChatMessenger();

    private static final String filePath = FileManager.resolveDataPath("paymentData.json");

    private void clearData() {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write("[]");
            System.out.println("paymentData.json file has been cleared properly.");

            actionBarNotification.sendMessage("Payment data cleared.", "§a");
            playSoundEffect.playSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);

            chatMessenger.sendSuccess("roulettepaymenthandler.payment_data.cleared");
        }
        catch (IOException exception) {
            System.out.println("Something went wrong when trying to clear paymentData.json file: " + exception);

            actionBarNotification.sendMessage("Failed to clear payment data.", "§4");
            playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);

            chatMessenger.sendError("roulettepaymenthandler.payment_data.clear_failed");
        }
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("roulette")
                            .then(ClientCommandManager.literal("paymentdata")
                                    .then(ClientCommandManager.literal("clear")
                                            .executes(context -> {
                                                clearData();
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}
