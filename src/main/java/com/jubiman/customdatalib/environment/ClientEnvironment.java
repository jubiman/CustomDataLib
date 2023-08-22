package com.jubiman.customdatalib.environment;

import com.jubiman.customdatalib.api.ClientTickable;
import com.jubiman.customdatalib.api.CustomData;
import com.jubiman.customdatalib.api.HUDDrawable;
import com.jubiman.customdatalib.player.CustomPlayer;
import necesse.engine.GameLog;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

import java.awt.*;
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

	/**
	 * A HashMap containing all registered CustomPlayers
	 */
	private static final HashMap<String, Function<Long, ? extends CustomPlayer>> registeredPlayers = new HashMap<>();

	/**
	 * Registers a CustomPlayer to the client-side registry
	 *
	 * @param modName the mod name of the CustomPlayer
	 * @param ctor    the constructor of the CustomPlayer to register
	 */
	public static void registerCustomPlayer(String modName, Function<Long, ? extends CustomPlayer> ctor) {
		GameLog.debug.println("Registering client-side CustomPlayer for: " + modName);
		registeredPlayers.put(modName, ctor);
	}

	/**
	 * Client ticks all registered CustomPlayers. Please do not call this function as it's called every tick when Necesse's client ticks.
	 *
	 * @param client the client to tick from
	 */
	public static void clientTickAll(Client client) {
		for (CustomData cd : customDataHashMap.values()) {
			if (cd instanceof ClientTickable)
				((ClientTickable) cd).clientTick(client);
		}
	}

	/**
	 * Draws all registered CustomPlayers' HUDs. Please do not call this function as it's called every tick when Necesse's HUD gets drawn.
	 *
	 * @param tickManager the tickManager to draw from
	 * @param player      the player to draw on
	 * @param renderBox   the renderBox to draw on
	 */
	public static void hudDrawAll(TickManager tickManager, PlayerMob player, Rectangle renderBox) {
		for (CustomData cd : customDataHashMap.values())
			if (cd instanceof HUDDrawable)
				((HUDDrawable) cd).drawHUD(tickManager, player, renderBox);
	}

	/**
	 * Creates a player for all registered CustomPlayers. Please do not call this function as it's called when Necesse's client gets created.
	 *
	 * @param auth the authentication of the player to create
	 */
	static void createPlayers(long auth) {
		for (Map.Entry<String, Function<Long, ? extends CustomPlayer>> entry : registeredPlayers.entrySet()) {
			GameLog.debug.println("Creating player for " + entry.getKey());
			customDataHashMap.put(entry.getKey(), entry.getValue().apply(auth));
		}
	}

	/**
	 * Gets a CustomPlayer from the client-side registry
	 * @param name the name of the CustomPlayer to get
	 * @return the CustomPlayer
	 */
	public static CustomData get(String name) {
		return customDataHashMap.get(name);
	}
}
