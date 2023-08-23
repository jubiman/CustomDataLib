package com.jubiman.customdatalib;

import com.jubiman.customdatalib.environment.PacketCreateClientSidePlayer;
import com.jubiman.customdatalib.player.CustomPlayerRegistry;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.PacketRegistry;

/**
 * This class is the ModEntry so that Necesse loads this mod.
 * The reason this library is a mod is so the registry can live inside Necesse and all dependent mods have only one central Registry object,
 * instead of one Registry object per mod.
 */
@ModEntry
public class Main { // just so it's loaded to upload it to steam workshop
	/**
	 * Initializes the mod, called by Necesse
	 */
	public void init() {
		try {
			// Load registries
			Class.forName("com.jubiman.customdatalib.player.CustomPlayerRegistry");
			Class.forName("com.jubiman.customdatalib.mob.CustomMobRegistry");

			// Register packets
			PacketRegistry.registerPacket(PacketCreateClientSidePlayer.class);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Called after all mods have been initialized, called by Necesse
	 */
	public void postInit() {
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
