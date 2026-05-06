// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class HelpCommand {
    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("roulette").then(ClientCommandManager.literal("help").executes(context -> {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();

                if (minecraftClient == null || minecraftClient.player == null) {
                    return 0;
                }

                minecraftClient.player.sendMessage(Text.literal("§6[---- Roulette Help ----]"), false);

                // TODO (CHAT MESSAGE SEPARATOR): Create a method "message_separator() and use it to apply separators between the chat messages"
                minecraftClient.player.sendMessage(Text.literal(""), false);

                // TODO (SHOWING HELP): Instead of typing all the funciton, create help-messages.yml file and get .command and .description and display them in a for loop.
                /*
                for (iterable_type iterable_element : iterable) {
                    minecraftClient.player.sendMessage(Text.literal(iterable_element.command), false);
                    minecraftClient.player.sendMessage(Text.literal(iterable_element.description), false);
                    minecraftClient.player.sendMessage(Text.literal(""), false);
                }
                */

                minecraftClient.player.sendMessage(Text.literal("§a/roulette status <start/stop>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7SChanges roulette status value to true or false."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette collectorconfig info"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Shows the current payment collector configuration."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette collectorconfig set specifiedcomponentword <word>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the keyword that will be detected in payment messages."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette collectorconfig set positionofspecifiedword <number>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the position of the keyword in the message, used to identify payment messages."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette collectorconfig set positionofamount <number>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the position of the payment amount in the message."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette collectorconfig set positionofusername <number>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the position of the username in the message."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette collectorconfig set messagecomponentsize <number>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the number of words in the payment message."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                // SendMessageAfterDraw commands
                minecraftClient.player.sendMessage(Text.literal("§a/roulette sendmessage reload"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Reloads the SendMessageAfterDraw configuration from JSON."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette sendmessage info"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Shows the current SendMessageAfterDraw configuration."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette sendmessage test"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sends message that is used after a winner is drawn. To check both messages use it twice."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette sendmessage set firstmessage <text>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the first message that will be sent after a draw."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette sendmessage set secondmessage <text>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the second message that will be sent after a draw."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                minecraftClient.player.sendMessage(Text.literal("§a/roulette sendmessage set delayticks <number>"), false);
                minecraftClient.player.sendMessage(Text.literal("§7Sets the delay in ticks before sending the next message."), false);
                minecraftClient.player.sendMessage(Text.literal(""), false);

                return 1;
                }))
            );
        });
    }
}
