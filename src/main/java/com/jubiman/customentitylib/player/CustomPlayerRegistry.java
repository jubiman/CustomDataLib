package com.jubiman.customentitylib.player;

import com.jubiman.customentitylib.api.*;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry where mods can register CustomPlayers
 */
public class CustomPlayerRegistry extends CustomDataRegistry<Long> {
	// TODO: I think this can be replaced with a separate client-side registry?
	@Deprecated
	private static final CustomPlayerRegistry CLIENT_INSTANCE = new CustomPlayerRegistry();

	/**
	 * The main instance of the CustomPlayer registry, which resides on the server environment
	 */
	public static final CustomPlayerRegistry INSTANCE = new CustomPlayerRegistry();
	//private final HashMap<String, CustomPlayersHandler<? extends CustomData>> registry = new HashMap<>();
	private static final HashMap<String, Constructor<? extends CustomPlayersHandler<?>>> classHashMap = new HashMap<>();

	static {
		GameEvents.addListener(ServerStopEvent.class, new GameEventListener<ServerStopEvent>() {
			@Override
			public void onEvent(ServerStopEvent e) {
				// TODO
			}
		});
		GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
			@Override
			public void onEvent(ServerStartEvent e) {
				System.out.println("Registering all CustomPlayersHandler classes: " + Arrays.toString(classHashMap.keySet().toArray()));
				INSTANCE.registerAll();
				GameLog.debug.println("Registered all CustomPlayersHandler classes");
			}
		});
	}

	@Deprecated
	public static CustomPlayerRegistry getAppropriateInstance() {
		// TODO: figure out if we are on the client or on the server
		return null;
	}

	@Deprecated
	public static CustomPlayerRegistry getAppropriateInstance(NetworkClient networkClient) {
		// TODO: this might work 9/10 times?
		if (networkClient instanceof ClientClient) {
			return CLIENT_INSTANCE;
		} else if (networkClient instanceof ServerClient) {
			return INSTANCE;
		}
		return null;
	}

	/**
	 * Register a new CustomPlayersHandler class. Used by individual mods
	 * @param identifier the name of the class
	 * @param clazz a reference to the class
	 */
	public static void registerClass(String identifier, Class<? extends CustomPlayersHandler<?>> clazz) {
		try {
			GameLog.debug.println("Registering CustomPlayersHandler class: " + identifier);
			Constructor<? extends CustomPlayersHandler<?>> ctor = clazz.getDeclaredConstructor();
			classHashMap.put(identifier, ctor);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public static CustomData createNewPlayer(String identifier) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		return classHashMap.get(identifier).newInstance().createNew();
	}

	/**
	 * Saves player data from all registered CustomPlayers classes
	 * @param save the SaveData to save to
	 * @param authentication the authentication of the player to save
	 */
	public void saveAll(SaveData save, Object authentication) {
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values()) {
			SaveData customPlayerSave = new SaveData("CustomPlayerData");
			cps.save(customPlayerSave, (Long) authentication);
			save.addSaveData(customPlayerSave);
		}
	}

	/**
	 * Loads all registered CustomPlayers, is called before the rest of the player is loaded
	 * @param data the LoadData to load from
	 */
	public void loadAllEnter(LoadData data, long authentication) {
		LoadData playerData = data.getFirstLoadDataByName("CustomPlayerData");
		for (Map.Entry<String, CustomDataHandler<Long, ? extends CustomData>> entry : registry.entrySet())
			((CustomPlayersHandler<? extends CustomPlayer>) entry.getValue()).loadEnter(playerData.getFirstLoadDataByName(entry.getKey()), authentication);
	}

	/**
	 * Loads all registered CustomPlayers, is called after the rest of the player is loaded
	 * @param data the LoadData to load from
	 */
	public void loadAllExit(LoadData data, long authentication) {
		LoadData playerData = data.getFirstLoadDataByName("CustomPlayerData");
		for (Map.Entry<String, CustomDataHandler<Long, ? extends CustomData>> entry : registry.entrySet())
			((CustomPlayersHandler<? extends CustomPlayer>) entry.getValue()).loadExit(playerData.getFirstLoadDataByName(entry.getKey()), authentication);
	}

	/**
	 * Server ticks all registered CustomPlayers. Please do not call this function as it's called every tick when Necesse's server ticks.
	 * @param server the server to tick from
	 */
	public static void serverTickAll(Server server) {
		for (CustomDataHandler<Long, ? extends CustomData> cps : INSTANCE.registry.values()) {
				((CustomPlayersHandler<? extends CustomPlayer>) cps).serverTick(server);
		}
	}

	/**
	 * Client ticks all registered CustomPlayers. Please do not call this function as it's called every tick when Necesse's client ticks.
	 * @param client the client to tick from
	 */
	@Deprecated
	public static void clientTickAll(Client client) {
		for (CustomDataHandler<Long, ? extends CustomData> cps : CLIENT_INSTANCE.registry.values())
			if (cps instanceof ClientTickable)
				((ClientTickable) cps).clientTick(client);
	}

	/**
	 * Creates a new Handler instance for every declared class
	 */
	private void registerAll() {
		// TODO: call this class some time
		for (Map.Entry<String, Constructor<? extends CustomPlayersHandler<?>>> entry : classHashMap.entrySet()) {
			try {
				GameLog.debug.println("Instantiating a new object for " + entry.getValue().getDeclaringClass().getName());
				CustomPlayersHandler<?> instance = entry.getValue().newInstance();
				register(entry.getKey(), instance);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				System.err.println("Failed to instantiate a new object for " + entry.getValue().getDeclaringClass().getName());
				e.printStackTrace();
				// TODO: Decide if we should throw an exception here to crash the game
			}
			GameLog.debug.println("Pls rubber duckie help me");
		}
	}

	public void stopAll() {
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values())
			((CustomPlayersHandler<? extends CustomPlayer>) cps).stop();
	}

	public void removeUser(long authentication) {
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values())
			((CustomPlayersHandler<? extends CustomPlayer>) cps).remove(authentication);
	}

	public void sendSyncPackets(long authentication, ServerClient serverClient) {
		for (CustomDataHandler<Long, ? extends CustomData> handler : registry.values()) {
			CustomData player = handler.get(authentication);
			if (player instanceof Syncable) {
				serverClient.sendPacket(((Syncable) player).getSyncPacket());
			}
		}
	}
}

