// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import dev.naxemis.roulettepaymenthandler.client.services.SendMessageAfterDraw;
import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SendMessageCommands {

    private static final FileManager fileManager = new FileManager();
    private static final ChatMessenger chatMessenger = new ChatMessenger();
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
                                    .then(literal("reload")
                                            .executes(context -> {
                                                loadConfig();
                                                chatMessenger.sendSuccess("roulettepaymenthandler.send_message.reloaded");
                                                return 1;
                                            })
                                    )
                                    .then(literal("info")
                                            .executes(context -> {
                                                chatMessenger.sendHeader("roulettepaymenthandler.send_message.info.header");
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.send_message.info.first_message", SendMessageAfterDraw.getMessageFirst());
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.send_message.info.second_message", SendMessageAfterDraw.getMessageSecond());
                                                chatMessenger.sendInfoRow("roulettepaymenthandler.send_message.info.delay_ticks", SendMessageAfterDraw.getDelayTicks());
                                                return 1;
                                            })
                                    )
                                    .then(literal("set")
                                            .then(literal("firstmessage")
                                                    .then(argument("message", StringArgumentType.greedyString())
                                                            .executes(context -> {
                                                                String value = StringArgumentType.getString(context, "message");
                                                                SendMessageAfterDraw.setMessageFirst(value);
                                                                saveConfig();
                                                                chatMessenger.sendSuccess("roulettepaymenthandler.send_message.set.first_message", value);
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
                                                                chatMessenger.sendSuccess("roulettepaymenthandler.send_message.set.second_message", value);
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
                                                                chatMessenger.sendSuccess("roulettepaymenthandler.send_message.set.delay_ticks", ticks);
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                                    .then(literal("test")
                                            .executes(context -> {
                                                chatMessenger.sendWarning("roulettepaymenthandler.send_message.test.intro");
                                                chatMessenger.sendWarning("roulettepaymenthandler.send_message.test.visibility");
                                                chatMessenger.sendWarning("roulettepaymenthandler.send_message.test.delay", SendMessageAfterDraw.getDelayTicks());
                                                SendMessageAfterDraw.start();
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}
