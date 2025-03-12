package com.jubiman.customdatalib.player;

import com.jubiman.customdatalib.api.*;
import com.jubiman.customdatalib.environment.ClientEnvironment;
import com.jubiman.customdatalib.util.Logger;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry where mods can register CustomPlayers
 */
public class CustomPlayerRegistry extends CustomDataRegistry<Long> {
	/**
	 * The main instance of the CustomPlayer registry, which resides on the server environment
	 */
	public static final CustomPlayerRegistry INSTANCE = new CustomPlayerRegistry();

	/**
	 * A HashMap containing all registered CustomPlayers
 	 */
	private static final HashMap<String, Constructor<? extends CustomPlayersHandler<?>>> classHashMap = new HashMap<>();

	static {
		GameEvents.addListener(ServerStopEvent.class, new GameEventListener<ServerStopEvent>() {
			@Override
			public void onEvent(ServerStopEvent e) {
				INSTANCE.stopAll();
				Logger.debug("Stopped all CustomPlayersHandler classes");
			}
		});
		GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
			@Override
			public void onEvent(ServerStartEvent e) {
				Logger.info("Registering all CustomPlayersHandler classes: " + Arrays.toString(classHashMap.keySet().toArray()));
				INSTANCE.registerAll();
			}
		});
	}

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
			classHashMap.put(identifier, ctor);
			// WHY DID I DO THIS???? DOES THIS EVEN WORK?????
			// Check if the player (generic type) is syncable
			Type superclass = clazz.getGenericSuperclass();
			// Check if superclass is a parameterized type
			if (superclass instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) superclass;

				// Get the actual type arguments used for the superclass
				Type[] typeArgs = parameterizedType.getActualTypeArguments();
				// Check if there are type arguments
				if (typeArgs.length > 0) {
					// Get the first type argument (CustomPlayer)
					Type typeArg = typeArgs[0];
					// Check if the type argument is a class
					if (typeArg instanceof Class) {
						Class<? extends CustomPlayer> playerClass = (Class<? extends CustomPlayer>) typeArg;
						// Check if the playerClass implements the Syncable interface
						if (ClientSide.class.isAssignableFrom(playerClass)) {
							// Finally register the CustomPlayer
							ClientEnvironment.registerCustomPlayer(identifier, (id) -> {
								try {
									// Get the constructor of playerClass that accepts a Long parameter
									java.lang.reflect.Constructor<? extends CustomPlayer> constructor = playerClass.getConstructor(Long.class);
									// Invoke the constructor to create an instance
									return constructor.newInstance(id);
								} catch (Exception ex) {
									ex.printStackTrace();
									return null;
								}
							});
						}
					}
				}
			}
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
		for (CustomDataHandler<Long, ? extends CustomData> cps : INSTANCE.registry.values()) {
			((CustomPlayersHandler<? extends CustomPlayer>) cps).serverTick(server);
		}
	}

	/**
	 * Saves player data from all registered CustomPlayers classes
	 *
	 * @param save           the SaveData to save to
	 * @param authentication the authentication of the player to save
	 */
	public void saveAll(SaveData save, Object authentication) {
		SaveData customPlayerSave = new SaveData("CustomPlayerData");
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values()) {
			Logger.info("Saving data for " + cps.handlerName);
			cps.save(customPlayerSave, (Long) authentication);
			save.addSaveData(customPlayerSave);
		}
	}

	/**
	 * Loads all registered CustomPlayers, is called before the rest of the player is loaded
	 *
	 * @param data the LoadData to load from
	 * @param authentication the authentication of the player to load
	 */
	public void loadAllEnter(LoadData data, long authentication) {
		LoadData playerData = data.getFirstLoadDataByName("CustomPlayerData");
		for (Map.Entry<String, CustomDataHandler<Long, ? extends CustomData>> entry : registry.entrySet()) {
			// Check if the mod has valid data
			LoadData modData = playerData.getFirstLoadDataByName(entry.getKey());
			if (modData == null) {
				Logger.info("No data found for " + entry.getKey());
				continue;
			}
			// Load the data
			Logger.info("Loading data for " + entry.getKey());
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
		for (Map.Entry<String, CustomDataHandler<Long, ? extends CustomData>> entry : registry.entrySet()) {
			// Check if the mod has valid data
			LoadData modData = playerData.getFirstLoadDataByName(entry.getKey());
			if (modData == null) {
				Logger.info("No data found for " + entry.getKey());
				continue;
			}
			// Load the data
			Logger.info("Loading data for " + entry.getKey());
			((CustomPlayersHandler<? extends CustomPlayer>) entry.getValue()).loadExit(modData, authentication);
		}
	}

	/**
	 * Creates a new Handler instance for every declared class
	 */
	private void registerAll() {
		// TODO: call this class some time
		for (Map.Entry<String, Constructor<? extends CustomPlayersHandler<?>>> entry : classHashMap.entrySet()) {
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
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values())
			((CustomPlayersHandler<? extends CustomPlayer>) cps).stop();
	}

	/**
	 * Removes a player from all registered CustomPlayers. Please do not call this function as it's called when Necesse's server stops.
	 * @param authentication the authentication of the player to remove
	 */
	public void removeUser(long authentication) {
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values())
			((CustomPlayersHandler<? extends CustomPlayer>) cps).remove(authentication);
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
}

