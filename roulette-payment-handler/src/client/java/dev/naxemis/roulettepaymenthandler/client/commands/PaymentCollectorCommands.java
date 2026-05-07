// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PaymentCollectorCommands {
    private static final FileManager fileManager = new FileManager();
    private static final ChatMessenger chatMessenger = new ChatMessenger();
    private static final String filePath = FileManager.resolveDataPath("paymentCollectorConfig.json");

    private static final String KEY_PREFIX = "roulettepaymenthandler.collector.";

    private static int positionOfSpecifiedWord = 1;
    private static String specifiedComponentWord = "Otrzymałeś:";
    private static int positionOfAmount = 2;
    private static int positionOfUsername = 4;
    private static int paymentMessageComponentSize = 5;

    public int getPositionOfSpecifiedWord() { return positionOfSpecifiedWord; }
    public String getSpecifiedComponentWord() { return specifiedComponentWord; }
    public int getPositionOfAmount() { return positionOfAmount; }
    public int getPositionOfUsername() { return positionOfUsername; }
    public int getPaymentMessageComponentSize() { return paymentMessageComponentSize; }

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
            chatMessenger.sendWarning(KEY_PREFIX + "first_run");
        } else {
            chatMessenger.sendSuccess(KEY_PREFIX + "loaded");
        }
    }

    private int reload(CommandContext<FabricClientCommandSource> context) {
        loadConfigFromJSON();
        return 1;
    }

    private int showInfo(CommandContext<FabricClientCommandSource> context) {
        chatMessenger.sendHeader(KEY_PREFIX + "info.header");
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.specified_component_word", specifiedComponentWord);
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.position_of_specified_word", positionOfSpecifiedWord);
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.position_of_amount", positionOfAmount);
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.position_of_username", positionOfUsername);
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.message_components_size", paymentMessageComponentSize);
        return 1;
    }

    private int setSpecifiedWord(CommandContext<FabricClientCommandSource> context) {
        String newWord = StringArgumentType.getString(context, "word");
        if (!specifiedComponentWord.equals(newWord)) {
            specifiedComponentWord = newWord;
            saveConfigToJSON();
            chatMessenger.sendSuccess(KEY_PREFIX + "set.specified_word", specifiedComponentWord);
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", specifiedComponentWord);
        }
        return 1;
    }

    private int setPositionOfSpecifiedWord(CommandContext<FabricClientCommandSource> context) {
        int newPosition = IntegerArgumentType.getInteger(context, "position");
        if (positionOfSpecifiedWord != newPosition) {
            positionOfSpecifiedWord = newPosition;
            saveConfigToJSON();
            chatMessenger.sendSuccess(KEY_PREFIX + "set.position_of_specified_word", positionOfSpecifiedWord);
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", positionOfSpecifiedWord);
        }
        return 1;
    }

    private int setPositionOfAmount(CommandContext<FabricClientCommandSource> context) {
        int newPosition = IntegerArgumentType.getInteger(context, "position");
        if (positionOfAmount != newPosition) {
            positionOfAmount = newPosition;
            saveConfigToJSON();
            chatMessenger.sendSuccess(KEY_PREFIX + "set.position_of_amount", positionOfAmount);
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", positionOfAmount);
        }
        return 1;
    }

    private int setPositionOfUsername(CommandContext<FabricClientCommandSource> context) {
        int newPosition = IntegerArgumentType.getInteger(context, "position");
        if (positionOfUsername != newPosition) {
            positionOfUsername = newPosition;
            saveConfigToJSON();
            chatMessenger.sendSuccess(KEY_PREFIX + "set.position_of_username", positionOfUsername);
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", positionOfUsername);
        }
        return 1;
    }

    private int setMessageComponentSize(CommandContext<FabricClientCommandSource> context) {
        int newSize = IntegerArgumentType.getInteger(context, "size");
        if (paymentMessageComponentSize != newSize) {
            paymentMessageComponentSize = newSize;
            saveConfigToJSON();
            chatMessenger.sendSuccess(KEY_PREFIX + "set.message_components_size", paymentMessageComponentSize);
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", paymentMessageComponentSize);
        }
        return 1;
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("roulette")
                            .then(literal("collectorconfig")
                                    .executes(context -> { HelpCommand.showSectionHelp("collector_"); return 1; })
                                    .then(literal("reload").executes(context -> reload(context)))
                                    .then(literal("info").executes(context -> showInfo(context)))
                                    .then(literal("set")
                                            .executes(context -> { HelpCommand.showSectionHelp("collector_"); return 1; })
                                            .then(literal("specifiedcomponentword")
                                                    .then(argument("word", StringArgumentType.greedyString())
                                                            .executes(context -> setSpecifiedWord(context))))
                                            .then(literal("positionofspecifiedword")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> setPositionOfSpecifiedWord(context))))
                                            .then(literal("positionofamount")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> setPositionOfAmount(context))))
                                            .then(literal("positionofusername")
                                                    .then(argument("position", IntegerArgumentType.integer())
                                                            .executes(context -> setPositionOfUsername(context))))
                                            .then(literal("messagecomponentsize")
                                                    .then(argument("size", IntegerArgumentType.integer())
                                                            .executes(context -> setMessageComponentSize(context))))
                                    )
                            )
            );
        });
    }
}
