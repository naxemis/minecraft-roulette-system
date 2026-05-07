// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client.commands;

import com.google.gson.JsonObject;

import dev.naxemis.roulettepaymenthandler.client.utility.FileManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;

public class RouletteStatusCommands {
    private static final FileManager fileManager = new FileManager();
    private static final String filePath = System.getenv("APPDATA") + "/RoulettePaymentTracker/rouletteStatus.json";

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
        String alreadyChangedText = "§eRoulette status is already changed to: ";

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("roulette")
                            .then(literal("status")
                                .then(literal("start")
                                        .executes(context -> {
                                            if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
                                                if (!rouletteStatus.get()) {
                                                    rouletteStatus.set(true);
                                                    saveSatusToJSON();
                                                    MinecraftClient.getInstance().player.sendMessage(Text.literal("§aRoulette status changed to: true."), false);
                                                }
                                                else {
                                                    MinecraftClient.getInstance().player.sendMessage(Text.literal(alreadyChangedText + rouletteStatus.get()), false);
                                                }
                                            }
                                            return 1;
                                        })
                                )
                                .then(literal("stop")
                                        .executes(context -> {
                                            if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
                                                if (rouletteStatus.get()) {
                                                    rouletteStatus.set(false);
                                                    saveSatusToJSON();
                                                    MinecraftClient.getInstance().player.sendMessage(Text.literal("§4Roulette status changed to: false."), false);
                                                }
                                                else {
                                                    MinecraftClient.getInstance().player.sendMessage(Text.literal(alreadyChangedText + rouletteStatus.get()), false);
                                                }
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

