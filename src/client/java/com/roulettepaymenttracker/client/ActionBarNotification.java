// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package com.roulettepaymenttracker.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ActionBarNotification {
    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public void sendMessage(String messageText, String messageColour) {
        if (minecraftClient != null && minecraftClient.inGameHud != null) {
            minecraftClient.inGameHud.setOverlayMessage(Text.literal(messageColour + messageText), true);
        }
    }
}
