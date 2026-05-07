// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import dev.naxemis.roulettepaymenthandler.client.services.SendMessageAfterDraw;
import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SendMessageCommands {

    private static final FileManager fileManager = new FileManager();
    private static final ChatMessenger chatMessenger = new ChatMessenger();
    private static final String filePath = FileManager.resolveDataPath("sendMessageConfig.json");

    private static final String KEY_PREFIX = "roulettepaymenthandler.send_message.";

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

    private int reload(CommandContext<FabricClientCommandSource> context) {
        loadConfig();
        chatMessenger.sendSuccess(KEY_PREFIX + "reloaded");
        return 1;
    }

    private int showInfo(CommandContext<FabricClientCommandSource> context) {
        chatMessenger.sendHeader(KEY_PREFIX + "info.header");
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.first_message", SendMessageAfterDraw.getMessageFirst());
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.second_message", SendMessageAfterDraw.getMessageSecond());
        chatMessenger.sendInfoRow(KEY_PREFIX + "info.delay_ticks", SendMessageAfterDraw.getDelayTicks());
        return 1;
    }

    private int setFirstMessage(CommandContext<FabricClientCommandSource> context) {
        String value = StringArgumentType.getString(context, "message");
        SendMessageAfterDraw.setMessageFirst(value);
        saveConfig();
        chatMessenger.sendSuccess(KEY_PREFIX + "set.first_message", value);
        return 1;
    }

    private int setSecondMessage(CommandContext<FabricClientCommandSource> context) {
        String value = StringArgumentType.getString(context, "message");
        SendMessageAfterDraw.setMessageSecond(value);
        saveConfig();
        chatMessenger.sendSuccess(KEY_PREFIX + "set.second_message", value);
        return 1;
    }

    private int setDelayTicks(CommandContext<FabricClientCommandSource> context) {
        int ticks = IntegerArgumentType.getInteger(context, "ticks");
        SendMessageAfterDraw.setDelayTicks(ticks);
        saveConfig();
        chatMessenger.sendSuccess(KEY_PREFIX + "set.delay_ticks", ticks);
        return 1;
    }

    private int test(CommandContext<FabricClientCommandSource> context) {
        chatMessenger.sendWarning(KEY_PREFIX + "test.intro");
        chatMessenger.sendWarning(KEY_PREFIX + "test.visibility");
        chatMessenger.sendWarning(KEY_PREFIX + "test.delay", SendMessageAfterDraw.getDelayTicks());
        SendMessageAfterDraw.start();
        return 1;
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("roulette")
                            .then(literal("sendmessage")
                                    .then(literal("reload").executes(context -> reload(context)))
                                    .then(literal("info").executes(context -> showInfo(context)))
                                    .then(literal("set")
                                            .then(literal("firstmessage")
                                                    .then(argument("message", StringArgumentType.greedyString())
                                                            .executes(context -> setFirstMessage(context))))
                                            .then(literal("secondmessage")
                                                    .then(argument("message", StringArgumentType.greedyString())
                                                            .executes(context -> setSecondMessage(context))))
                                            .then(literal("delayticks")
                                                    .then(argument("ticks", IntegerArgumentType.integer(1))
                                                            .executes(context -> setDelayTicks(context))))
                                    )
                                    .then(literal("test").executes(context -> test(context)))
                            )
            );
        });
    }
}
