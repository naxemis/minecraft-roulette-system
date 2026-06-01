// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;

import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.concurrent.atomic.AtomicBoolean;

public class RouletteStatusCommands {
    private static final FileManager fileManager = new FileManager();
    private static final ChatMessenger chatMessenger = new ChatMessenger();
    private static final String filePath = FileManager.resolveDataPath("rouletteStatus.json");

    private static final String KEY_PREFIX = "roulettepaymenthandler.status.";

    private final AtomicBoolean rouletteStatus = new AtomicBoolean(false);

    private JsonObject buildCurrentJson() {
        JsonObject json = new JsonObject();
        json.addProperty("rouletteStatus", rouletteStatus.get());
        return json;
    }

    private void saveSatusToJSON() {
        fileManager.saveJson(filePath, buildCurrentJson());
    }

    private int start(CommandContext<FabricClientCommandSource> context) {
        if (!rouletteStatus.get()) {
            rouletteStatus.set(true);
            saveSatusToJSON();
            chatMessenger.sendSuccess(KEY_PREFIX + "changed_to_true");
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", rouletteStatus.get());
        }
        return 1;
    }

    private int stop(CommandContext<FabricClientCommandSource> context) {
        if (rouletteStatus.get()) {
            rouletteStatus.set(false);
            saveSatusToJSON();
            chatMessenger.sendError(KEY_PREFIX + "changed_to_false");
        } else {
            chatMessenger.sendWarning(KEY_PREFIX + "already_changed", rouletteStatus.get());
        }
        return 1;
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("roulette")
                    .then(literal("status")
                            .executes(context -> { HelpCommand.showSectionHelp("status"); return 1; })
                            .then(literal("start").executes(context -> start(context)))
                            .then(literal("stop").executes(context -> stop(context)))
                    )
            );
        });
    }

    public void reset_roulette_status() {
        rouletteStatus.set(false);
        saveSatusToJSON();
    }
}
