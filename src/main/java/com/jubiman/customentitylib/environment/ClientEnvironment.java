package com.jubiman.customentitylib.environment;

import com.jubiman.customentitylib.api.ClientTickable;
import com.jubiman.customentitylib.api.CustomData;
import com.jubiman.customentitylib.api.HUDDrawable;
import com.jubiman.customentitylib.player.CustomPlayer;
import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.GameLog;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The Client environment, which should only contain the client's own custom player.
 * Mainly used by a Syncable Client
 */
public class ClientEnvironment {
	/**
	 * A HashMap containing all mods' custom players that are ClientTickable or HUDDrawable
	 */
	private static final HashMap<String, CustomData> customDataHashMap = new HashMap<>();
	private static final HashMap<String, Function<Long, ? extends CustomPlayer>> registeredPlayers = new HashMap<>();
	// TODO: create a way for people to register a client side tick
	// TODO: maybe test with the mana bar? might be nice to have the mana cached on the client so you dont have to spam packets

	/**
	 * Registers a CustomPlayer to the client-side registry
	 * @param modName the mod name of the CustomPlayer
	 * @param ctor the constructor of the CustomPlayer to register
	 */
	public static void registerCustomPlayer(String modName, Function<Long, ? extends CustomPlayer> ctor) {
		GameLog.debug.println("Registering client-side CustomPlayer for: " + modName);
		registeredPlayers.put(modName, ctor);
	}

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

	/**
	 * Syncs all registered CustomPlayers' data.
	 * Will be approved upon in the future. For now, it's deprecated.
	 * @param identifier the identifier to sync
	 */
	@Deprecated
	public static void syncClientData(String identifier) {
		if (!customDataHashMap.containsKey(identifier)) {
			try {
				CustomPlayerRegistry.createNewPlayer(identifier);
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Draws all registered CustomPlayers' HUDs. Please do not call this function as it's called every tick when Necesse's HUD gets drawn.
	 * @param tickManager the tickManager to draw from
	 * @param player the player to draw on
	 * @param renderBox the renderBox to draw on
	 */
	public static void hudDrawAll(TickManager tickManager, PlayerMob player, Rectangle renderBox) {
		for (CustomData cd : customDataHashMap.values())
			if (cd instanceof HUDDrawable)
				((HUDDrawable) cd).drawHUD(tickManager, player, renderBox);
	}

	/**
	 * Creates a player for all registered CustomPlayers. Please do not call this function as it's called when Necesse's client gets created.
	 * @param auth the authentication of the player to create
	 */
	static void createPlayers(long auth) {
		for (Map.Entry<String, Function<Long, ? extends CustomPlayer>> entry : registeredPlayers.entrySet()) {
			GameLog.debug.println("Creating player for " + entry.getKey());
			customDataHashMap.put(entry.getKey(), entry.getValue().apply(auth));
		}
	}

	public static CustomData get(String name) {
		return customDataHashMap.get(name);
	}
}
