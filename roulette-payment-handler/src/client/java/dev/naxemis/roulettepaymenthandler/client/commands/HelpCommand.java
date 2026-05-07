// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import java.util.List;

import com.mojang.brigadier.context.CommandContext;

import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class HelpCommand {
    private static final ChatMessenger chatMessenger = new ChatMessenger();

    private static final String KEY_PREFIX = "roulettepaymenthandler.help.entries.";

    private static final List<String> HELP_KEYS = List.of(
            "status",
            "collector_info",
            "collector_specified_word",
            "collector_position_word",
            "collector_position_amount",
            "collector_position_username",
            "collector_message_size",
            "send_message_reload",
            "send_message_info",
            "send_message_test",
            "send_message_first",
            "send_message_second",
            "send_message_delay"
    );

    public static void showFullHelp() {
        chatMessenger.sendHeader("roulettepaymenthandler.help.header");
        chatMessenger.sendSeparator();
        for (String key : HELP_KEYS) {
            chatMessenger.sendCommandHelp(
                    KEY_PREFIX + key + ".command",
                    KEY_PREFIX + key + ".description"
            );
        }
    }

    private int help(CommandContext<FabricClientCommandSource> context) {
        showFullHelp();
        return 1;
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("roulette")
                    .then(ClientCommandManager.literal("help").executes(context -> help(context)))
            );
        });
    }
}
