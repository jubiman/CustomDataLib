package com.jubiman.customdatalib.player.client;

import com.jubiman.customdatalib.api.*;
import com.jubiman.customdatalib.util.Logger;
import necesse.engine.network.client.Client;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The Client registry. This is where mods can register their client-side CustomClients, used for drawing HUDs and other client-side stuff.
 */
public class CustomClientRegistry extends CustomDataRegistry<Long> {
	/**
	 * The main instance of the CustomClient registry, which resides on the client environment
	 */
	public static final CustomClientRegistry INSTANCE = new CustomClientRegistry();

	/**
	 * A HashMap containing all mods' custom clients
	 */
	private static final HashMap<String, Constructor<? extends ClientPlayersHandler<? extends CustomClient>>> clientCtorMap = new HashMap<>();

	/**
	 * Registers a {@link CustomClient} to the client-side registry.
	 * This could be the same class used on the server-side, but could also be a different class.
	 * Having the same object as the server-side {@link CustomClient} could lead to unnecessary memory usage, so it's recommended to use a different class.
	 *
	 * @param identifier the identifier of the {@link CustomClient}
	 * @param clazz    the class object of the {@link CustomClient} that implements {@link NeedsClientSideObject}
	 */
	public static void registerCustomClient(String identifier, Class<? extends ClientPlayersHandler<? extends CustomClient>> clazz) {
		try {
			Logger.debug("Registering client-side CustomClient for: " + identifier);
			Constructor<? extends ClientPlayersHandler<? extends CustomClient>> ctor = clazz.getDeclaredConstructor(Long.class);
			clientCtorMap.put(identifier, ctor);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Client ticks all registered CustomClient. Please do not call this function as it's called every tick when Necesse's client ticks.
	 *
	 * @param client the client to tick from
	 */
	public static void clientTickAll(Client client) {
		for (CustomDataHandler<Long, ? extends CustomData> cd : INSTANCE.registry.values()) {
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
		for (CustomDataHandler<Long, ? extends CustomData> cd : INSTANCE.registry.values()) {
			if (cd instanceof HUDDrawable)
				((HUDDrawable) cd).drawHUD(tickManager, player, renderBox);
			}
	}

	/**
	 * Registers all client-side players. Should not be called by mods.
	 */
	public void registerAll() {
		if (!registry.isEmpty()) {
			Logger.warn("Client-side players already registered, skipping registration");
			return;
		}
		for (Map.Entry<String, Constructor<? extends ClientPlayersHandler<?>>> entry : clientCtorMap.entrySet()) {
			try {
				Logger.debug("Registering client-side CustomClient: " + entry.getKey());
				register(entry.getKey(), entry.getValue().newInstance());
			} catch (Exception e) {
				Logger.error("Failed to register client-side CustomClient: " + entry.getKey());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Destroys all registered {@link CustomClient}s.
	 */
	void destroyClients() {
		for (CustomDataHandler<Long, ? extends CustomData> cps : registry.values())
			cps.stop();
	}

	/**
	 * Destroys a single client.
	 * @param authentication the authentication of the client to destroy
	 */
	public void destroyClient(long authentication) {
		for (CustomDataHandler<Long, ?> customDataHandler : registry.values())
			customDataHandler.remove(authentication);
	}
}
