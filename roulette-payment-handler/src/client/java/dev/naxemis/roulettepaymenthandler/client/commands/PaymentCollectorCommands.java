// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PaymentCollectorCommands {
    private static final FileManager fileManager = new FileManager();
    private static final String filePath = System.getenv("APPDATA") + "/RoulettePaymentTracker/paymentCollectorConfig.json";

    private static int positionOfSpecifiedWord = 1; // where's located first word that player want to use for checking
    private static String specifiedComponentWord = "Otrzymałeś:"; // the word that will be checked with payment message
    private static int positionOfAmount = 2; // where's located amount that player has sent
    private static int positionOfUsername = 4; // where's located player's username
    private static int paymentMessageComponentSize = 5; // what's the size of the payment message
    public int getPositionOfSpecifiedWord() {
        return positionOfSpecifiedWord;
    }
    public String getSpecifiedComponentWord() {
        return specifiedComponentWord;
    }
    public int getPositionOfAmount() {
        return positionOfAmount;
    }
    public int getPositionOfUsername() {
        return positionOfUsername;
    }
    public int getPaymentMessageComponentSize() {
        return paymentMessageComponentSize;
    }

    private JsonObject buildCurrentJson() {
        JsonObject json = new JsonObject();
        json.addProperty("positionOfSpecifiedWord", positionOfSpecifiedWord);
        json.addProperty("specifiedComponentWord", specifiedComponentWord);
        json.addProperty("positionOfAmount", positionOfAmount);
        json.addProperty("positionOfUsername", positionOfUsername);
        json.addProperty("paymentMessageComponentsSize", paymentMessageComponentSize);
        return json;
    }

    private JsonObject buildDefaultJson() {
        JsonObject json = new JsonObject();
        json.addProperty("positionOfSpecifiedWord", 0);
        json.addProperty("specifiedComponentWord", "");
        json.addProperty("positionOfAmount", 0);
        json.addProperty("positionOfUsername", 0);
        json.addProperty("paymentMessageComponentsSize", 0);
        return json;
    }

    public void saveConfigToJSON() {
        fileManager.saveJson(filePath, buildCurrentJson());
    }

    public void loadConfigFromJSON() {
        boolean firstRun = !Files.exists(Paths.get(filePath));
        JsonObject json = fileManager.loadJson(filePath, buildDefaultJson());

        if (json.has("positionOfSpecifiedWord"))
            positionOfSpecifiedWord = json.get("positionOfSpecifiedWord").getAsInt();

        if (json.has("specifiedComponentWord"))
            specifiedComponentWord = json.get("specifiedComponentWord").getAsString();

        if (json.has("positionOfAmount"))
            positionOfAmount = json.get("positionOfAmount").getAsInt();

        if (json.has("positionOfUsername"))
            positionOfUsername = json.get("positionOfUsername").getAsInt();

        if (json.has("paymentMessageComponentsSize"))
            paymentMessageComponentSize = json.get("paymentMessageComponentsSize").getAsInt();

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient != null && minecraftClient.player != null) {
            if (firstRun) {
                minecraftClient.player.sendMessage(Text.literal("§ePayment collector config not found. Created config file with default values."), false);
            } else {
                minecraftClient.player.sendMessage(Text.literal("§aSuccessfully loaded payment collector config."), false);
            }
        }
    }

    // TODO (COLLECTOR COMMANDS): Move commands into different methods and execute them here.
    // I'm fr now. What the hell is this code. Did I wrote it on my lap or what?
    public void register() {
        String alreadyChangedText = "§eValue is already changed to: ";

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("roulette")
                            .then(literal("collectorconfig")
                                    .then(literal("reload")
                                            .executes(context -> {
                                                loadConfigFromJSON();

                                                return 1;
                                            })
                                    )
                                    .then(literal("info")
                                            .executes(context -> {
                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                if (minecraftClient != null && minecraftClient.player != null) {
                                                    minecraftClient.player.sendMessage(Text.literal("§6[---- Payment Collector Config Info ----]"), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§7Specified component word: §a" + specifiedComponentWord), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§7Position of specified word: §a" + positionOfSpecifiedWord), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§7Position of amount: §a" + positionOfAmount), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§7Position of username: §a" + positionOfUsername), false);
                                                    minecraftClient.player.sendMessage(Text.literal("§7Message components size: §a" + paymentMessageComponentSize), false);
                                                }
                                                return 1;
                                            })
                                    )
                                    .then(literal("set")
                                            // specified component word
                                            .then(literal("specifiedcomponentword")
                                                    .then(argument("word", StringArgumentType.greedyString())
                                                            .executes(context -> {
                                                                String newWord = StringArgumentType.getString(context, "word");
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient != null && minecraftClient.player != null) {
                                                                    if (!specifiedComponentWord.equals(newWord)) {
                                                                        specifiedComponentWord = newWord;
                                                                        saveConfigToJSON();
                                                                        minecraftClient.player.sendMessage(Text.literal("§aSpecified word set to: " + specifiedComponentWord), false);
                                                                    } else {
                                                                        minecraftClient.player.sendMessage(Text.literal(alreadyChangedText + specifiedComponentWord), false);
                                                                    }
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            // position of specified word
                                            .then(literal("positionofspecifiedword")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newPosition = IntegerArgumentType.getInteger(context, "position");
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient != null && minecraftClient.player != null) {
                                                                    if (positionOfSpecifiedWord != newPosition) {
                                                                        positionOfSpecifiedWord = newPosition;
                                                                        saveConfigToJSON();
                                                                        minecraftClient.player.sendMessage(Text.literal("§aPosition of specified word set to: " + positionOfSpecifiedWord), false);
                                                                    } else {
                                                                        minecraftClient.player.sendMessage(Text.literal(alreadyChangedText + positionOfSpecifiedWord), false);
                                                                    }
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            // position of amount
                                            .then(literal("positionofamount")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newPosition = IntegerArgumentType.getInteger(context, "position");
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient != null && minecraftClient.player != null) {
                                                                    if (positionOfAmount != newPosition) {
                                                                        positionOfAmount = newPosition;
                                                                        saveConfigToJSON();
                                                                        minecraftClient.player.sendMessage(Text.literal("§aPosition of payment amount set to: " + positionOfAmount), false);
                                                                    } else {
                                                                        minecraftClient.player.sendMessage(Text.literal(alreadyChangedText + positionOfAmount), false);
                                                                    }
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            // position of username
                                            .then(literal("positionofusername")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newPosition = IntegerArgumentType.getInteger(context, "position");
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient != null && minecraftClient.player != null) {
                                                                    if (positionOfUsername != newPosition) {
                                                                        positionOfUsername = newPosition;
                                                                        saveConfigToJSON();
                                                                        minecraftClient.player.sendMessage(Text.literal("§aPosition of username set to: " + positionOfUsername), false);
                                                                    } else {
                                                                        minecraftClient.player.sendMessage(Text.literal(alreadyChangedText + positionOfUsername), false);
                                                                    }
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            // message component size
                                            .then(literal("messagecomponentsize")
                                                    .then(argument("size", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newSize = IntegerArgumentType.getInteger(context, "size");
                                                                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                                                                if (minecraftClient != null && minecraftClient.player != null) {
                                                                    if (paymentMessageComponentSize != newSize) {
                                                                        paymentMessageComponentSize = newSize;
                                                                        saveConfigToJSON();
                                                                        minecraftClient.player.sendMessage(Text.literal("§aSize of message component array set to: " + paymentMessageComponentSize), false);
                                                                    } else {
                                                                        minecraftClient.player.sendMessage(Text.literal(alreadyChangedText + paymentMessageComponentSize), false);
                                                                    }
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                        )
                                )
            );
        });
    }
}
