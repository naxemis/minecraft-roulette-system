// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;

import dev.naxemis.roulettepaymenthandler.client.utility.ChatMessenger;
import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import java.util.concurrent.atomic.AtomicBoolean;

public class RouletteStatusCommands {
    private static final FileManager fileManager = new FileManager();
    private static final ChatMessenger chatMessenger = new ChatMessenger();
    private static final String filePath = FileManager.resolveDataPath("rouletteStatus.json");

    private final AtomicBoolean rouletteStatus = new AtomicBoolean(false);

    private JsonObject buildCurrentJson() {
        JsonObject json = new JsonObject();
        json.addProperty("rouletteStatus", rouletteStatus.get());
        return json;
    }

    private void saveSatusToJSON() {
        fileManager.saveJson(filePath, buildCurrentJson());
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("roulette")
                    .then(literal("status")
                            .then(literal("start")
                                    .executes(context -> {
                                        if (!rouletteStatus.get()) {
                                            rouletteStatus.set(true);
                                            saveSatusToJSON();
                                            chatMessenger.sendSuccess("roulettepaymenthandler.status.changed_to_true");
                                        } else {
                                            chatMessenger.sendWarning("roulettepaymenthandler.status.already_changed", rouletteStatus.get());
                                        }
                                        return 1;
                                    })
                            )
                            .then(literal("stop")
                                    .executes(context -> {
                                        if (rouletteStatus.get()) {
                                            rouletteStatus.set(false);
                                            saveSatusToJSON();
                                            chatMessenger.sendError("roulettepaymenthandler.status.changed_to_false");
                                        } else {
                                            chatMessenger.sendWarning("roulettepaymenthandler.status.already_changed", rouletteStatus.get());
                                        }
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }

    public void reset_roulette_status() {
        rouletteStatus.set(false);
        saveSatusToJSON();
    }
}
