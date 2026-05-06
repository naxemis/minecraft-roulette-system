// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.services;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import dev.naxemis.roulettepaymenthandler.client.commands.PaymentCollectorCommands;
import dev.naxemis.roulettepaymenthandler.client.models.PaymentDataHolder;

public class PaymentCollector {
    private static final PaymentCollectorCommands paymentCollectorCommands = new PaymentCollectorCommands();

    private enum Suffix {
        K("k", 1_000L),
        M("m", 1_000_000L),
        MLN("mln", 1_000_000L),
        B("b", 1_000_000_000L);

        final String symbol;
        final long multiplier;
        Suffix(String symbol, long multiplier) { this.symbol = symbol; this.multiplier = multiplier; }
    }

    private long parseAmount(String rawAmount) {
        String cleaned = rawAmount.replace("$", "").toLowerCase();
        for (Suffix suffix : Suffix.values()) {
            if (cleaned.endsWith(suffix.symbol)) {
                String numberPart = cleaned.substring(0, cleaned.length() - suffix.symbol.length());
                return (long) (Double.parseDouble(numberPart) * suffix.multiplier);
            }
        }
        return (long) Double.parseDouble(cleaned);
    }

    private String[] extractWords(Text text) {
        List<Text> components = new ArrayList<>();
        collectAllTextComponents(text, components);
        return Arrays.stream(components.getFirst().getString().split(" "))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    private void sanitizeWords(String[] words) {
        int amountPosition = paymentCollectorCommands.getPositionOfAmount();
        for (int index = 0; index < words.length; index++) {
            if (index != amountPosition) {
                words[index] = words[index].replaceAll("[!.:]", "");
            }
        }
    }

    private boolean isValidPaymentMessage(String[] words) {
        if (words.length != paymentCollectorCommands.getPaymentMessageComponentSize()) return false;
        String specifiedWord = words[paymentCollectorCommands.getPositionOfSpecifiedWord()];
        return specifiedWord.equals(paymentCollectorCommands.getSpecifiedComponentWord());
    }

    private PaymentDataHolder buildPaymentData(String[] words) {
        String username = words[paymentCollectorCommands.getPositionOfUsername()];
        long amount = parseAmount(words[paymentCollectorCommands.getPositionOfAmount()]);
        return new PaymentDataHolder(username, amount);
    }

    public void registerListener(Consumer<PaymentDataHolder> onPaymentReceived) {
        ClientReceiveMessageEvents.GAME.register((text, overlay) -> {
            try {
                String[] words = extractWords(text);
                sanitizeWords(words);
                if (!isValidPaymentMessage(words)) return;
                onPaymentReceived.accept(buildPaymentData(words));
            } catch (Exception exception) {
                // ignore — non-payment messages spam too much
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