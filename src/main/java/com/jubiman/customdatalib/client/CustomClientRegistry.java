package com.jubiman.customdatalib.client;

import com.jubiman.customdatalib.api.ClientTickable;
import com.jubiman.customdatalib.api.CustomData;
import com.jubiman.customdatalib.api.HUDDrawable;
import com.jubiman.customdatalib.player.CustomPlayersHandler;
import com.jubiman.customdatalib.util.Logger;
import necesse.engine.network.client.Client;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The Client environment, which should only contain the client's own custom player.
 * Mainly used by a Syncable Client
 */
public class CustomClientRegistry {
	/**
	 * A HashMap containing all mods' custom client
	 */
	private static final HashMap<String, CustomData> customDataHashMap = new HashMap<>();

	/**
	 * A HashMap containing all registered CustomClients' constructors
	 */
	private static final HashMap<String, Constructor<? extends CustomClient>> classHashMap = new HashMap<>();

	/**
	 * Registers a CustomClient to the client-side registry
	 *
	 * @param identifier the identifier of the CustomClient
	 * @param clazz    the constructor of the CustomClient to register
	 */
	public static void registerCustomClient(String identifier, Class<? extends CustomClient> clazz) {
		try {
			Logger.debug("Registering client-side CustomClient for: " + identifier);
			Constructor<? extends CustomClient> ctor = clazz.getDeclaredConstructor(Client.class);
			classHashMap.put(identifier, ctor);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a CustomClient from the client-side registry
	 * @param name the name of the CustomClient to get
	 * @return the CustomClient
	 */
	public static CustomData get(String name) {
		return customDataHashMap.get(name);
	}

	/**
	 * Client ticks all registered CustomClient. Please do not call this function as it's called every tick when Necesse's client ticks.
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
	 * Draws all registered CustomClient' HUDs. Please do not call this function as it's called every tick when Necesse's HUD gets drawn.
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
	 * Creates a client for all registered CustomClients. Please do not call this function as it's called when Necesse's client gets created.
	 *
	 * @param client the client to create from
	 */
	static void createClients(Client client) {
		for (Map.Entry<String, Constructor<? extends CustomClient>> entry : classHashMap.entrySet()) {
			try {
				Logger.debug("Creating client for " + entry.getKey());
				customDataHashMap.put(entry.getKey(), entry.getValue().newInstance(client));
			} catch (Exception e) {
				Logger.error("Failed to create client for " + entry.getKey());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Destroys all registered CustomClients. Please do not call this function as it's called when Necesse's client gets destroyed.
	 */
	static void destroyClients() {
		customDataHashMap.clear();
	}
}
