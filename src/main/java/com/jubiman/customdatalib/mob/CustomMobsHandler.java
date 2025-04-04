package com.jubiman.customdatalib.mob;

import com.jubiman.customdatalib.api.CustomDataHandler;
import com.jubiman.customdatalib.api.Savable;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;

/**
 * Handler for handling custom mobs in your mod
 *
 * @param <T> your CustomMob class
 */
public class CustomMobsHandler<T extends CustomMob> extends CustomDataHandler<Integer, T> {
	/**
	 * Constructs the storage class for custom mobs
	 * @param clazz the class extending CustomMob
	 * @param handlerName the name of the class, used for creating a save component
	 */
	public CustomMobsHandler(Class<T> clazz, String handlerName) {
		super(clazz, new Class[]{int.class}, handlerName);
	}

	/**
	 * Save mobs' data.
	 *
	 * @param saveData the parent save object
	 * @param id       the id of the player to save
	 */
	@Override
	public void save(SaveData saveData, Integer id) {
		T p = get(id);
		if (!(p instanceof Savable)) return;

		SaveData save = new SaveData(handlerName);
		((Savable) p).addSaveData(save);
		saveData.addSaveData(save);
	}

	/**
	 * Load player from saved data. Gets called before the rest of the player is loaded.
	 *
	 * @param loadData data to load from (should be the same as where you save, usually ServerClient)
	 * @param id       the id of the player to load
	 */
	public void loadEnter(LoadData loadData, int id) {
		T p = get(id);
		if (p instanceof Savable) // TODO: should always be true?
			((Savable) p).loadEnter(loadData);
	}

	/**
	 * Load player from saved data. Gets called after the rest of the player is loaded.
	 *
	 * @param loadData data to load from (should be the same as where you save, usually ServerClient)
	 * @param id       the id of the player to load
	 */
	public void loadExit(LoadData loadData, int id) {
		T p = get(id);
		if (p instanceof Savable)
			((Savable) p).loadExit(loadData);
	}

	/**
	 * When switching worlds or on server stop this will be called to avoid overwriting data in other (older) worlds
	 */
	public void stop() {
		dataMap.clear(); // avoid overwriting other worlds
	}

	/**
	 * Removes a player from the map
	 *
	 * @param id the id of the player to remove
	 */
	public void remove(int id) {
		dataMap.remove(id);
	}

	/**
	 * Called every tick on the server. Override this to add your own logic.
	 *
	 * @param mob the mob to tick
	 */
	public void serverTick(Mob mob) {
		// TODO: Might need some stuff, maybe not?
		for (T p : values())
			p.serverTick(mob);
	}
}
