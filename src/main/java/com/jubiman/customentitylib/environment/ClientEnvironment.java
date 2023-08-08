package com.jubiman.customentitylib.environment;

import com.jubiman.customentitylib.api.CustomData;
import com.jubiman.customentitylib.api.ClientTickable;
import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.network.client.Client;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * The Client environment, which should only contain the client's own custom player.
 * Mainly used by a Syncable Client
 */
public class ClientEnvironment {
	/**
	 * A HashMap containing all mods' custom players that are tickable
	 */
	private static final HashMap<String, CustomData> customDataHashMap = new HashMap<>();
	// TODO: create a way for people to register a client side tick
	// TODO: maybe test with the mana bar? might be nice to have the mana cached on the client so you dont have to spam packets

	/**
	 * Client ticks all registered CustomPlayers. Please do not call this function as it's called every tick when Necesse's client ticks.
	 * @param client the client to tick from
	 */
	public static void clientTickAll(Client client) {
		for (CustomData cd : customDataHashMap.values()) {
			if (cd instanceof ClientTickable)
				((ClientTickable) cd).clientTick(client);
		}
	}

	public static void syncClientData(String identifier) {
		if (!customDataHashMap.containsKey(identifier)) {
			try {
				CustomPlayerRegistry.createNewPlayer(identifier);
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
