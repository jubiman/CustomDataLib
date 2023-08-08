package com.jubiman.customentitylib.player;

import com.jubiman.customentitylib.api.CustomDataHandler;
import com.jubiman.customentitylib.api.Savable;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

/**
 * Handler for handling custom players in your mod
 * @param <T> your CustomPlayer class
 */
public abstract class CustomPlayersHandler<T extends CustomPlayer> extends CustomDataHandler<Long, T> {
	/**
	 * Constructs the storage class for custom players
	 *
	 * @param clazz the class extending CustomPlayer
	 * @param identifier the name of the class, used for creating a save component
	 */
	public CustomPlayersHandler(Class<T> clazz, String identifier) {
		super(clazz, new Class[]{long.class}, identifier);
	}

	/**
	 * Creates a new instance of the custom player
	 * @return the new custom player instance
	 */
	public T createNew() throws InvocationTargetException, InstantiationException, IllegalAccessException {
		return ctor.newInstance();
	}

	/**
	 * Iterate through the keys (player auths).
	 * @return a set of all keys (all player auths )
	 */
	public Set<Long> keyIterator() {
		return userMap.keySet();
	}

	/**
	 * Returns the values
	 * @return a collection of all values (all CustomPlayers)
	 */
	public Collection<T> values() {
		return userMap.values();
	}

	/**
	 * Save player's data.
	 * @param saveData the parent save object (usually ServerClient)
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
	 * @param loadData data to load from (should be the same as where you save, usually ServerClient)
	 * @param auth the authentication of the player to load
	 */
	public void loadEnter(LoadData loadData, long auth) {
		LoadData data = loadData.getLoadData().get(0);
		T p = get(auth);
		if (p instanceof Savable) // TODO: should always be true
			((Savable) p).loadEnter(data);
	}

	/**
	 * Load player from saved data. Gets called after the rest of the player is loaded.
	 * @param loadData data to load from (should be the same as where you save, usually ServerClient)
	 * @param auth the authentication of the player to load
	 */
	public void loadExit(LoadData loadData, long auth) {
		LoadData data = loadData.getLoadData().get(0);
		T p = get(auth);
		if (p instanceof Savable)
			((Savable) p).loadExit(data);
	}

	/**
	 * When switching worlds or on server stop this will be called to avoid overwriting data in other (older) worlds
	 */
	public void stop() {
		userMap.clear(); // avoid overwriting other worlds
	}

	/**
	 * Removes a player from the map
	 * @param authentication the authentication of the player to remove
	 */
	public void remove(long authentication) {
		userMap.remove(authentication);
	}

	/**
	 * Called every tick on the server. Override this to add your own logic.
	 * @param server the server instance
	 */
	public void serverTick(Server server) {
	}
}
