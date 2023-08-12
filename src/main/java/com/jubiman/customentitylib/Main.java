package com.jubiman.customentitylib;

import com.jubiman.celtest.buff.InsanityIndicatorBuff;
import com.jubiman.celtest.command.SanityCommand;
import com.jubiman.celtest.packet.PacketSyncPlayer;
import com.jubiman.celtest.sanity.SanityPlayer;
import com.jubiman.celtest.sanity.SanityPlayersHandler;
import com.jubiman.celtest.sanity.mana.Mana;
import com.jubiman.customentitylib.environment.ClientEnvironment;
import com.jubiman.customentitylib.environment.PacketCreateClientSidePlayer;
import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.commands.CommandsManager;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.PacketRegistry;

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

			// Register packets
			PacketRegistry.registerPacket(PacketCreateClientSidePlayer.class);

			// TODO: Remove (example code)
			PacketRegistry.registerPacket(PacketSyncPlayer.class);
			// Register players handler
			CustomPlayerRegistry.registerClass(SanityPlayersHandler.name, SanityPlayersHandler.class);

			// Register client-side player
			ClientEnvironment.registerCustomPlayer(SanityPlayersHandler.name, SanityPlayer::new);

			// Register indicator buff
			BuffRegistry.registerBuff("insanityindicatorbuff", new InsanityIndicatorBuff());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: remove this
	public void initResources() {
		Mana.loadTextures();
	}

	public void postInit() {
		// TODO Register (debug) command
		CommandsManager.registerServerCommand(new SanityCommand());

		// Register event listeners
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
		GameEvents.addListener(ServerClientConnectedEvent.class, new GameEventListener<ServerClientConnectedEvent>() {
			@Override
			public void onEvent(ServerClientConnectedEvent e) {
				// Tell the client to create a new player
				e.client.sendPacket(new PacketCreateClientSidePlayer(e.client.authentication));

				// Sync the saved data with the client
				CustomPlayerRegistry.INSTANCE.sendSyncPackets(e.client.authentication, e.client);
			}
		});
	}
}
