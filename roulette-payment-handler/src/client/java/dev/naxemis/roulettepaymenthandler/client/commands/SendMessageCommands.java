// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import dev.naxemis.roulettepaymenthandler.client.services.SendMessageAfterDraw;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SendMessageCommands {

    private static final FileManager fileManager = new FileManager();
    private static final String filePath = FileManager.resolveDataPath("sendMessageConfig.json");

    private JsonObject buildCurrentJson() {
        JsonObject json = new JsonObject();
        json.addProperty("messageFirst", SendMessageAfterDraw.getMessageFirst());
        json.addProperty("messageSecond", SendMessageAfterDraw.getMessageSecond());
        json.addProperty("delayTicks", SendMessageAfterDraw.getDelayTicks());
        return json;
    }

    public void saveConfig() {
        fileManager.saveJson(filePath, buildCurrentJson());
    }

    public void loadConfig() {
        JsonObject json = fileManager.loadJson(filePath, buildCurrentJson());

        if (json.has("messageFirst"))
            SendMessageAfterDraw.setMessageFirst(json.get("messageFirst").getAsString());

        if (json.has("messageSecond"))
            SendMessageAfterDraw.setMessageSecond(json.get("messageSecond").getAsString());

        if (json.has("delayTicks"))
            SendMessageAfterDraw.setDelayTicks(json.get("delayTicks").getAsInt());
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("roulette")
                            .then(literal("sendmessage")
                                    // Reload config
                                    .then(literal("reload")
                                            .executes(context -> {
                                                loadConfig();
                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                if (minecraftClient.player != null) {
                                                    minecraftClient.player.sendMessage(Text.literal("§aSendMessageAfterDraw config reloaded."), false);
                                                }
                                                return 1;
                                            })
                                    )
                                    // Show info
                                    .then(literal("info")
                                            .executes(context -> {
                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                if (minecraftClient.player != null) {
                                                    minecraftClient.player.sendMessage(Text.literal("§6[---- Send Message After Draw Config ----]"), false);
                                                    minecraftClient.player.sendMessage(Text.literal("First Message: §a" + SendMessageAfterDraw.getMessageFirst()), false);
                                                    minecraftClient.player.sendMessage(Text.literal("Second Message: §a" + SendMessageAfterDraw.getMessageSecond()), false);
                                                    minecraftClient.player.sendMessage(Text.literal("Delay Ticks: §a" + SendMessageAfterDraw.getDelayTicks()), false);
                                                }
                                                return 1;
                                            })
                                    )
                                    // Set subcommands
                                    .then(literal("set")
                                            .then(literal("firstmessage")
                                                    .then(argument("message", StringArgumentType.greedyString())
                                                            .executes(context -> {
                                                                String value = StringArgumentType.getString(context, "message");
                                                                SendMessageAfterDraw.setMessageFirst(value);
                                                                saveConfig();
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient.player != null) {
                                                                    minecraftClient.player.sendMessage(Text.literal("§aFirst message set to: " + value), false);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            .then(literal("secondmessage")
                                                    .then(argument("message", StringArgumentType.greedyString())
                                                            .executes(context -> {
                                                                String value = StringArgumentType.getString(context, "message");
                                                                SendMessageAfterDraw.setMessageSecond(value);
                                                                saveConfig();
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient.player != null) {
                                                                    minecraftClient.player.sendMessage(Text.literal("§aSecond message set to: " + value), false);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            .then(literal("delayticks")
                                                    .then(argument("ticks", IntegerArgumentType.integer(1))
                                                            .executes(context -> {
                                                                int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                                                SendMessageAfterDraw.setDelayTicks(ticks);
                                                                saveConfig();
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient.player != null) {
                                                                    minecraftClient.player.sendMessage(Text.literal("§aDelay ticks set to: " + ticks), false);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                                    // Test command
                                    .then(literal("test")
                                            .executes(context -> {
                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                if (minecraftClient.player != null) {
                                                    minecraftClient.player.sendMessage(Text.literal("§eTesting sending messages after a draw."), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§eTest message is visible for other players."), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§eMessage will be sent after " + SendMessageAfterDraw.getDelayTicks() + " ticks."), false);
                                                }
                                                SendMessageAfterDraw.start();

                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}
