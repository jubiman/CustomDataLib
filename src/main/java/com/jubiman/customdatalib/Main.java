package com.jubiman.customdatalib;

import com.jubiman.customdatalib.player.client.CustomClientRegistry;
import com.jubiman.customdatalib.player.client.PacketDestroyClient;
import com.jubiman.customdatalib.player.client.PacketDestroyClients;
import com.jubiman.customdatalib.player.CustomPlayerRegistry;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
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
			Class.forName("com.jubiman.customdatalib.player.client.CustomClientRegistry");
			Class.forName("com.jubiman.customdatalib.mob.CustomMobRegistry");

			// Register packets
			PacketRegistry.registerPacket(PacketDestroyClient.class);
			PacketRegistry.registerPacket(PacketDestroyClients.class);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static Server server;

	/**
	 * Get the server instance
	 * @return the server instance
	 */
	public static Server getServer() {
		return server;
	}

	/**
	 * Called after all mods have been initialized, called by Necesse
	 */
	public void postInit() {
		// Register event listeners
		CustomPlayerRegistry.INSTANCE.registerListeners();
		CustomClientRegistry.INSTANCE.registerAll();
		GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
			@Override
			public void onEvent(ServerStartEvent e) {
				server = e.server;
			}
		});
		GameEvents.addListener(ServerStopEvent.class, new GameEventListener<ServerStopEvent>() {
			@Override
			public void onEvent(ServerStopEvent e) {
				for (ServerClient client : e.server.getClients()) {
					client.sendPacket(new PacketDestroyClients());
				}
				CustomPlayerRegistry.INSTANCE.stopAll();
				server = null;
			}
		});
		GameEvents.addListener(ServerClientDisconnectEvent.class, new GameEventListener<ServerClientDisconnectEvent>() {
			@Override
			public void onEvent(ServerClientDisconnectEvent e) {
				CustomPlayerRegistry.INSTANCE.removeUser(e.client.authentication);
				server.network.sendToAllClientsExcept(new PacketDestroyClient(e.client), e.client);
				e.client.sendPacket(new PacketDestroyClients());
			}
		});
		GameEvents.addListener(ServerClientConnectedEvent.class, new GameEventListener<ServerClientConnectedEvent>() {
			@Override
			public void onEvent(ServerClientConnectedEvent e) {
				// Tell the client to create a new player
//				e.client.sendPacket(new PacketCreateClientSidePlayer(e.client.authentication));

				// Sync the saved data with the client
				CustomPlayerRegistry.INSTANCE.sendSyncPackets(e.client.authentication, e.client);
			}
		});
	}
}
