package com.jubiman.customdatalib.player;

import com.jubiman.customdatalib.api.*;
import com.jubiman.customdatalib.player.client.ClientPlayersHandler;
import com.jubiman.customdatalib.util.Logger;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry where mods can register CustomPlayers
 */
public class CustomPlayerRegistry extends CustomDataRegistry<Long> {
	/**
	 * The main instance of the CustomPlayer registry, which resides on the server environment
	 */
	public static final CustomPlayerRegistry INSTANCE = new CustomPlayerRegistry();

	/**
	 * A HashMap containing all registered CustromPlayersHandlers
 	 */
	private static final HashMap<String, Constructor<? extends CustomPlayersHandler<?>>> serverCtorMap = new HashMap<>();

	/**
	 * Register a new CustomPlayersHandler class. Used by individual mods
	 *
	 * @param identifier the name of the class
	 * @param clazz      a reference to the class
	 */
	public static void registerClass(String identifier, Class<? extends CustomPlayersHandler<? extends CustomPlayer>> clazz) {
		try {
			Logger.info("Registering CustomPlayersHandler class: " + identifier);
			Constructor<? extends CustomPlayersHandler<?>> ctor = clazz.getDeclaredConstructor();
			serverCtorMap.put(identifier, ctor);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Server ticks all registered CustomPlayers. Please do not call this function as it's called every tick when Necesse's server ticks.
	 *
	 * @param server the server to tick from
	 */
	public static void serverTickAll(Server server) {
		for (CustomDataHandler<Long, ? extends CustomData> customDataHandler : INSTANCE.registry.values()) {
			((CustomPlayersHandler<? extends CustomPlayer>) customDataHandler).serverTick(server);
		}
	}

	/**
	 * Saves player data from all registered CustomPlayers classes
	 *
	 * @param save           the SaveData to save to
	 * @param authentication the authentication of the player to save
	 */
	public void saveAll(SaveData save, long authentication) {
		SaveData customPlayerSave = new SaveData("CustomPlayerData");
		Logger.info("Saving data for %d", authentication);
		for (CustomDataHandler<Long, ? extends CustomData> customDataHandler : registry.values()) {
			Logger.info("\t - Saving data for %s", customDataHandler.handlerName);
			customDataHandler.save(customPlayerSave, authentication);
		}
		save.addSaveData(customPlayerSave);
	}

	/**
	 * Loads all registered CustomPlayers, is called before the rest of the player is loaded
	 *
	 * @param data the LoadData to load from
	 * @param authentication the authentication of the player to load
	 */
	public void loadAllEnter(LoadData data, long authentication) {
		LoadData playerData = data.getFirstLoadDataByName("CustomPlayerData");
		Logger.info("Loading data for %d", authentication);
		for (Map.Entry<String, CustomDataHandler<Long, ? extends CustomData>> entry : registry.entrySet()) {
			// Check if the mod has valid data
			LoadData modData = playerData.getFirstLoadDataByName(entry.getKey());
			if (modData == null) {
				Logger.info("\t - No data found for " + entry.getKey());
				continue;
			}
			// Load the data
			Logger.info("\t - Loading data for %s", entry.getKey());
			((CustomPlayersHandler<? extends CustomPlayer>) entry.getValue()).loadEnter(modData, authentication);
		}
	}

	/**
	 * Loads all registered CustomPlayers, is called after the rest of the player is loaded
	 *
	 * @param data the LoadData to load from
	 * @param authentication the authentication of the player to load
	 */
	public void loadAllExit(LoadData data, long authentication) {
		LoadData playerData = data.getFirstLoadDataByName("CustomPlayerData");
		Logger.info("Loading data for %d", authentication);
		for (Map.Entry<String, CustomDataHandler<Long, ? extends CustomData>> entry : registry.entrySet()) {
			// Check if the mod has valid data
			LoadData modData = playerData.getFirstLoadDataByName(entry.getKey());
			if (modData == null) {
				Logger.info("\t - No data found for " + entry.getKey());
				continue;
			}
			// Load the data
			Logger.info("\t - Loading data for %s", entry.getKey());
			Logger.info("Loading data for " + entry.getKey());
			((CustomPlayersHandler<? extends CustomPlayer>) entry.getValue()).loadExit(modData, authentication);
		}
	}

	/**
	 * Creates a new Handler instance for every declared class
	 */
	private void registerAll() {
		// TODO: call this class some time
		for (Map.Entry<String, Constructor<? extends CustomPlayersHandler<?>>> entry : serverCtorMap.entrySet()) {
			try {
				Logger.debug("Instantiating a new object for " + entry.getValue().getDeclaringClass().getName());
				CustomPlayersHandler<?> instance = entry.getValue().newInstance();
				register(entry.getKey(), instance);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				Logger.error("Failed to instantiate a new object for " + entry.getValue().getDeclaringClass().getName());
				e.printStackTrace();
				// TODO: Decide if we should throw an exception here to crash the game
			}
			Logger.info("Pls rubber duckie help me");
		}
	}

	/**
	 * Stops all registered CustomPlayers. Please do not call this function as it's called when Necesse's server stops.
	 */
	public void stopAll() {
		for (CustomDataHandler<Long, ? extends CustomData> customDataHandler : registry.values())
			customDataHandler.stop();
	}

	/**
	 * Removes a player from all registered CustomPlayers. Please do not call this function as it's called when Necesse's server stops.
	 * @param authentication the authentication of the player to remove
	 */
	public void removeUser(long authentication) {
		for (CustomDataHandler<Long, ? extends CustomData> customDataHandler : registry.values())
			customDataHandler.remove(authentication);
	}

	/**
	 * Sends all sync packets from all registered CustomPlayers
	 * @param authentication the authentication of the player to send the sync packets to
	 * @param serverClient the serverClient to send the sync packets to
	 */
	public void sendSyncPackets(long authentication, ServerClient serverClient) {
		for (CustomDataHandler<Long, ? extends CustomData> handler : registry.values()) {
			CustomData player = handler.get(authentication);
			if (player instanceof Syncable) {
				Logger.debug("Sending sync packet for " + player);
				serverClient.sendPacket(((Syncable) player).getSyncPacket());
			}
		}
	}

	/**
	 * Register all event listeners, should not be called by mods
	 */
	public void registerListeners() {
		GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
			@Override
			public void onEvent(ServerStartEvent e) {
				Logger.info("Registering all CustomPlayersHandler classes: " + Arrays.toString(serverCtorMap.keySet().toArray()));
				INSTANCE.registerAll();
			}
		});
	}
}

