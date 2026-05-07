// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.services;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public final class SendMessageAfterDraw {

    private static int ticksRemaining = -1;

    private static String messageFirst = "First Message";
    private static String messageSecond = "Second Message";
    private static int delayTicks = 100;

    private static boolean sendFirstMessage = true;

    public static String getMessageFirst() { return messageFirst; }
    public static void setMessageFirst(String message) { messageFirst = message; }

    public static String getMessageSecond() { return messageSecond; }
    public static void setMessageSecond(String message) { messageSecond = message; }

    public static int getDelayTicks() { return delayTicks; }
    public static void setDelayTicks(int ticks) { delayTicks = ticks; }

    private SendMessageAfterDraw() {}

    private static void sendMessage(String message) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null || minecraftClient.player == null) return;
        minecraftClient.player.networkHandler.sendChatMessage(message);
    }

    public static void start() {
        if (ticksRemaining <= 0) {
            ticksRemaining = delayTicks;
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ticksRemaining > 0) {
                ticksRemaining--;
                if (ticksRemaining == 0) {
                    if (sendFirstMessage) {
                        sendMessage(messageFirst);
                    } else {
                        sendMessage(messageSecond);
                    }
                    sendFirstMessage = !sendFirstMessage;
                    ticksRemaining = -1;
                }
            }
        });
    }
}
