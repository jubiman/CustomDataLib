package com.jubiman.customdatalib.api;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

/**
 * Defines whether to save the custom data
 */
public interface Savable {
	/**
	 * Save the player's data
	 *
	 * @param save save parent object to add to
	 */
	void addSaveData(SaveData save);

	/**
	 * Load player's data from saved data. Gets called before the rest of the player is loaded.
	 *
	 * @param data the data to load
	 */
	void loadEnter(LoadData data);

	/**
	 * Load player's data from saved data. Gets called before the rest of the player is loaded.
	 *
	 * @param data the data to load
	 */
	void loadExit(LoadData data);
}
