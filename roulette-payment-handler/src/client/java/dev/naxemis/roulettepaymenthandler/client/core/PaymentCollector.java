// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.core;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import dev.naxemis.roulettepaymenthandler.client.commands.PaymentCollectorCommands;
import dev.naxemis.roulettepaymenthandler.client.models.PaymentDataHolder;
import dev.naxemis.roulettepaymenthandler.client.utility.ActionBarNotification;
import dev.naxemis.roulettepaymenthandler.client.utility.PlaySoundEffect;

public class PaymentCollector {
    private static final ActionBarNotification actionBarNotification = new ActionBarNotification();
    private static final PlaySoundEffect playSoundEffect = new PlaySoundEffect();
    private static final PaymentCollectorCommands paymentCollectorCommands = new PaymentCollectorCommands();

    private enum Suffix {
        K("k", 1_000L),
        M("m", 1_000_000L),
        MLN("mln", 1_000_000L),
        B("b", 1_000_000_000L);

        final String token;
        final long multiplier;
        Suffix(String token, long multiplier) { this.token = token; this.multiplier = multiplier; }
    }

    private long parseAmount(String rawAmount) {
        String cleaned = rawAmount.replace("$", "").toLowerCase();
        for (Suffix suffix : Suffix.values()) {
            if (cleaned.endsWith(suffix.token)) {
                String numberPart = cleaned.substring(0, cleaned.length() - suffix.token.length());
                return (long) (Double.parseDouble(numberPart) * suffix.multiplier);
            }
        }
        return (long) Double.parseDouble(cleaned);
    }

    public void registerListener(Consumer<PaymentDataHolder> onPaymentReceived) {
        ClientReceiveMessageEvents.GAME.register((text, overlay) -> {
            List<Text> paymentComponents = new ArrayList<>();
            collectAllTextComponents(text, paymentComponents); // collects all components

            try {
                String[] componentArray = Arrays.stream(paymentComponents.getFirst().getString().split(" "))
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);

                for (int index = 0; index < componentArray.length; index++) {
                    if (index != paymentCollectorCommands.getPositionOfAmount()) {
                        componentArray[index] = componentArray[index].replaceAll("[!.:]", "");
                    }
                }

                String messageSpecifiedWord = componentArray[paymentCollectorCommands.getPositionOfSpecifiedWord()];
                String messageUsername = componentArray[paymentCollectorCommands.getPositionOfUsername()];
                long messageAmount = parseAmount(componentArray[paymentCollectorCommands.getPositionOfAmount()]);
                int messageSize = componentArray.length;

                // checks if the first word specified by player and first word, that have position specified by player, the same
                boolean isFirstWordMatching = messageSpecifiedWord.equals(paymentCollectorCommands.getSpecifiedComponentWord());
                boolean isSizeEqual = messageSize == paymentCollectorCommands.getPaymentMessageComponentSize();

                if(isFirstWordMatching && isSizeEqual) {
                    try {
                        PaymentDataHolder newPaymentData = new PaymentDataHolder(messageUsername, messageAmount);
                        onPaymentReceived.accept(newPaymentData);  // notify the callback
                    } catch (Exception exception) {
                        System.out.println("Failed to retrieve payment price and username: " + exception.getMessage());
                        actionBarNotification.sendMessage("Failed to retrieve payment price and username", "§4");
                        playSoundEffect.playSound(SoundEvents.ENTITY_ITEM_BREAK);
                    }
                }
            } catch (Exception exception) {
                // Ignore - it will spam the console too much
            }
        });
    }

    // collects all components from payment message and turns them into a List<Text>
    private void collectAllTextComponents(Text root, List<Text> collector) {
        collector.add(root);

        for (Text sibling : root.getSiblings()) {
            collectAllTextComponents(sibling, collector);
        }
    }
}