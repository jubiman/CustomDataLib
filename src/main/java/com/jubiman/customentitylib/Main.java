package com.jubiman.customentitylib;

import com.jubiman.celtest.buff.InsanityIndicatorBuff;
import com.jubiman.celtest.command.SanityCommand;
import com.jubiman.celtest.sanity.SanityPlayersHandler;
import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.commands.CommandsManager;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.BuffRegistry;

/**
 * This class is the ModEntry so that Necesse loads this mod.
 * The reason this library is a mod is so the registry can live inside Necesse and all dependent mods have only one central Registry object,
 * instead of one Registry object per mod.
 */
@ModEntry
public class Main { // just so it's loaded to upload it to steam workshop
	public void init() {
		try {
			Class.forName("com.jubiman.customentitylib.player.CustomPlayerRegistry");

			// TODO: Human flesh mod
			CustomPlayerRegistry.registerClass(SanityPlayersHandler.name, SanityPlayersHandler.class);
			// Register indicator buff
			BuffRegistry.registerBuff("insanityindicatorbuff", new InsanityIndicatorBuff());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void postInit() {
		// TODO Register (debug) command
		CommandsManager.registerServerCommand(new SanityCommand());

		// Register events
		GameEvents.addListener(ServerStopEvent.class, new GameEventListener<ServerStopEvent>() {
			@Override
			public void onEvent(ServerStopEvent e) {
				CustomPlayerRegistry.INSTANCE.stopAll();
			}
		});
		GameEvents.addListener(ServerClientDisconnectEvent.class, new GameEventListener<ServerClientDisconnectEvent>() {
			@Override
			public void onEvent(ServerClientDisconnectEvent e) {
				CustomPlayerRegistry.INSTANCE.removeUser(e.client.authentication);
			}
		});
	}
}
