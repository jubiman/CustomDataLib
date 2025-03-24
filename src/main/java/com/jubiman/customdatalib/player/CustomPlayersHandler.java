package com.jubiman.customdatalib.player;

import com.jubiman.customdatalib.api.CustomDataHandler;
import com.jubiman.customdatalib.api.Savable;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

/**
 * Handler for handling custom players in your mod
 *
 * @param <T> your CustomPlayer class
 */
public abstract class CustomPlayersHandler<T extends CustomPlayer> extends CustomDataHandler<Long, T> {
	/**
	 * Constructs the storage class for custom players
	 *
	 * @param clazz      the class extending CustomPlayer
	 * @param identifier the name of the class, used for creating a save component
	 */
	public CustomPlayersHandler(Class<T> clazz, String identifier) {
		super(clazz, new Class[]{long.class}, identifier);
	}

	/**
	 * Save players' data.
	 *
	 * @param saveData       the parent save object (usually ServerClient)
	 * @param authentication the authentication of the player to save
	 */
	@Override
	public void save(SaveData saveData, Long authentication) {
		T p = get(authentication);
		if (!(p instanceof Savable)) return;

		SaveData save = new SaveData(handlerName);
		((Savable) p).addSaveData(save);
		saveData.addSaveData(save);
	}

	/**
	 * Load player from saved data. Gets called before the rest of the player is loaded.
	 *
	 * @param loadData data to load from (should be the same as where you save, usually ServerClient)
	 * @param auth     the authentication of the player to load
	 */
	public void loadEnter(LoadData loadData, long auth) {
		T p = get(auth);
		if (p instanceof Savable)
			((Savable) p).loadEnter(loadData);
	}

	/**
	 * Load player from saved data. Gets called after the rest of the player is loaded.
	 *
	 * @param loadData data to load from (should be the same as where you save, usually ServerClient)
	 * @param auth     the authentication of the player to load
	 */
	public void loadExit(LoadData loadData, long auth) {
		T p = get(auth);
		if (p instanceof Savable)
			((Savable) p).loadExit(loadData);
	}

	/**
	 * Called every tick on the server. Override this to add your own logic.
	 *
	 * @param server the server instance
	 */
	public void serverTick(Server server) {
		// Tick all players
		for (T p : values())
			p.serverTick(server);

		// Send sync packet every second
		// TODO: improve this and probably delete it as it is very inefficient like this
//		if (server.tickManager().isFirstGameTickInSecond()) {
//			for (T p : values()) {
//				if (p instanceof Syncable && ((Syncable) p).isContinuousSync()) {
//					server.network.sendPacket(((Syncable) p).getSyncPacket(), server.getClientByAuth(p.auth));
//				}
//			}
//		}
	}
}
