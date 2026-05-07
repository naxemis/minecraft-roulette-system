package dev.naxemis.roulettepaymenthandler.client.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatMessenger {

    public void sendText(Text text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        client.player.sendMessage(text, false);
    }

    public void sendMessage(String text) {
        sendText(Text.literal(text));
    }

    public void sendSeparator() {
        sendMessage("");
    }

    public void sendTranslatable(String key, Object... args) {
        sendText(Text.translatable(key, args));
    }

    public void sendSuccess(String key, Object... args) {
        sendText(Text.translatable(key, args).formatted(Formatting.GREEN));
    }

    public void sendError(String key, Object... args) {
        sendText(Text.translatable(key, args).formatted(Formatting.DARK_RED));
    }

    public void sendWarning(String key, Object... args) {
        sendText(Text.translatable(key, args).formatted(Formatting.YELLOW));
    }

    public void sendInfo(String key, Object... args) {
        sendText(Text.translatable(key, args).formatted(Formatting.GRAY));
    }

    public void sendHeader(String key, Object... args) {
        sendText(Text.literal("[---- ")
                .append(Text.translatable(key, args))
                .append(Text.literal(" ----]"))
                .formatted(Formatting.GOLD));
    }

    public void sendInfoRow(String labelKey, Object value) {
        sendText(Text.translatable(labelKey).formatted(Formatting.GRAY)
                .append(Text.literal(": ").formatted(Formatting.GRAY))
                .append(Text.literal(String.valueOf(value)).formatted(Formatting.GREEN)));
    }

    public void sendCommandHelp(String commandKey, String descriptionKey) {
        sendText(Text.translatable(commandKey).formatted(Formatting.GREEN));
        sendText(Text.translatable(descriptionKey).formatted(Formatting.GRAY));
        sendSeparator();
    }
}
