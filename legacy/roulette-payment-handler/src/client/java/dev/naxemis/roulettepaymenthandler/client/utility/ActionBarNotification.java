// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ActionBarNotification {
    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public void sendMessage(String messageText, String messageColour) {
        if (minecraftClient == null || minecraftClient.inGameHud == null) {
            return;
        }

        minecraftClient.inGameHud.setOverlayMessage(Text.literal(messageColour + messageText), true);
    }
}
