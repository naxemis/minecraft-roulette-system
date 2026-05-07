// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PaymentCollectorCommands {
    private static final FileManager fileManager = new FileManager();
    private static final ChatMessenger chatMessenger = new ChatMessenger();
    private static final String filePath = FileManager.resolveDataPath("paymentCollectorConfig.json");

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

        if (firstRun) {
            chatMessenger.sendWarning("roulettepaymenthandler.collector.first_run");
        } else {
            chatMessenger.sendSuccess("roulettepaymenthandler.collector.loaded");
        }
    }

    // TODO (COLLECTOR COMMANDS): Move commands into different methods and execute them here.
    // I'm fr now. What the hell is this code. Did I wrote it on my lap or what?
    public void register() {
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
                                                chatMessenger.sendHeader("roulettepaymenthandler.collector.info.header");
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.collector.info.specified_component_word", specifiedComponentWord);
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.collector.info.position_of_specified_word", positionOfSpecifiedWord);
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.collector.info.position_of_amount", positionOfAmount);
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.collector.info.position_of_username", positionOfUsername);
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.collector.info.message_components_size", paymentMessageComponentSize);
                                                return 1;
                                            })
                                    )
                                    .then(literal("set")
                                            .then(literal("specifiedcomponentword")
                                                    .then(argument("word", StringArgumentType.greedyString())
                                                            .executes(context -> {
                                                                String newWord = StringArgumentType.getString(context, "word");
                                                                if (!specifiedComponentWord.equals(newWord)) {
                                                                    specifiedComponentWord = newWord;
                                                                    saveConfigToJSON();
                                                                    chatMessenger.sendSuccess("roulettepaymenthandler.collector.set.specified_word", specifiedComponentWord);
                                                                } else {
                                                                    chatMessenger.sendWarning("roulettepaymenthandler.collector.already_changed", specifiedComponentWord);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            .then(literal("positionofspecifiedword")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newPosition = IntegerArgumentType.getInteger(context, "position");
                                                                if (positionOfSpecifiedWord != newPosition) {
                                                                    positionOfSpecifiedWord = newPosition;
                                                                    saveConfigToJSON();
                                                                    chatMessenger.sendSuccess("roulettepaymenthandler.collector.set.position_of_specified_word", positionOfSpecifiedWord);
                                                                } else {
                                                                    chatMessenger.sendWarning("roulettepaymenthandler.collector.already_changed", positionOfSpecifiedWord);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            .then(literal("positionofamount")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newPosition = IntegerArgumentType.getInteger(context, "position");
                                                                if (positionOfAmount != newPosition) {
                                                                    positionOfAmount = newPosition;
                                                                    saveConfigToJSON();
                                                                    chatMessenger.sendSuccess("roulettepaymenthandler.collector.set.position_of_amount", positionOfAmount);
                                                                } else {
                                                                    chatMessenger.sendWarning("roulettepaymenthandler.collector.already_changed", positionOfAmount);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            .then(literal("positionofusername")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newPosition = IntegerArgumentType.getInteger(context, "position");
                                                                if (positionOfUsername != newPosition) {
                                                                    positionOfUsername = newPosition;
                                                                    saveConfigToJSON();
                                                                    chatMessenger.sendSuccess("roulettepaymenthandler.collector.set.position_of_username", positionOfUsername);
                                                                } else {
                                                                    chatMessenger.sendWarning("roulettepaymenthandler.collector.already_changed", positionOfUsername);
                                                                }
                                                                return 1;
                                                            })
                                                    )
                                            )
                                            .then(literal("messagecomponentsize")
                                                    .then(argument("size", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int newSize = IntegerArgumentType.getInteger(context, "size");
                                                                if (paymentMessageComponentSize != newSize) {
                                                                    paymentMessageComponentSize = newSize;
                                                                    saveConfigToJSON();
                                                                    chatMessenger.sendSuccess("roulettepaymenthandler.collector.set.message_components_size", paymentMessageComponentSize);
                                                                } else {
                                                                    chatMessenger.sendWarning("roulettepaymenthandler.collector.already_changed", paymentMessageComponentSize);
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
