// Copyright (c) 2026 naxemis. All rights reserved.
// Contact: contact@naxemis.dev

package dev.naxemis.roulettepaymenthandler.client;

import dev.naxemis.roulettepaymenthandler.client.commands.HelpCommand;
import dev.naxemis.roulettepaymenthandler.client.commands.PaymentCollectorCommands;
import dev.naxemis.roulettepaymenthandler.client.commands.PaymentDataCommands;
import dev.naxemis.roulettepaymenthandler.client.commands.RouletteStatusCommands;
import dev.naxemis.roulettepaymenthandler.client.commands.SendMessageCommands;
import dev.naxemis.roulettepaymenthandler.client.managers.PaymentDataManager;
import dev.naxemis.roulettepaymenthandler.client.managers.WinnerDataManager;
import dev.naxemis.roulettepaymenthandler.client.services.PaymentCollector;
import dev.naxemis.roulettepaymenthandler.client.services.SendMessageAfterDraw;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class RoulettePaymentHandlerClient implements ClientModInitializer {

    RouletteStatusCommands rouletteStatusCommands = new RouletteStatusCommands();
    PaymentCollectorCommands paymentCollectorCommands = new PaymentCollectorCommands();
    HelpCommand rouletteHelpCommand = new HelpCommand();
    PaymentDataCommands paymenDataCommands = new PaymentDataCommands();

    PaymentCollector paymentCollector = new PaymentCollector();
    PaymentDataManager paymentDataManager = new PaymentDataManager();

    SendMessageCommands sendMessageCommands = new SendMessageCommands();


    private int tickCounter = 0;
    private static final int updateWinnerDataTicks = 10; // 20 ticks == 1 second
    WinnerDataManager winnerDataManager = new WinnerDataManager();
    private void startWatchingWinnerData() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            if (tickCounter >= updateWinnerDataTicks) {
                tickCounter = 0;
                winnerDataManager.updateWinnerData().exceptionally(expection -> {
                    System.out.println("Something went wrong when trying to read winner data from JSON file async operation: " + expection.getMessage());
                    return null;
                });
            }
        });
    }

    @Override
    public void onInitializeClient() {
        paymentCollectorCommands.loadConfigFromJSON();
        SendMessageAfterDraw.loadConfig();

        rouletteStatusCommands.register();
        paymentCollectorCommands.register();
        rouletteHelpCommand.register();
        paymenDataCommands.register();
        SendMessageAfterDraw.register();
        sendMessageCommands.register();

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            rouletteStatusCommands.reset_roulette_status();
        });

        paymentDataManager.createEmptyDataFile();

        paymentCollector.registerListener(newPaymentData -> {
            paymentDataManager.saveData(newPaymentData).exceptionally(exception -> {
                System.out.println("Something went wrong when trying to run saving data to JSON file async operation: " + exception.getMessage());
                return null;
            });
        });

        startWatchingWinnerData();

        onClientShutdown();
    }

    public void onClientShutdown() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            paymentDataManager.asyncProcessShutdown();
            winnerDataManager.asyncProcessShutdown();
            rouletteStatusCommands.reset_roulette_status();
        });
    }
}
