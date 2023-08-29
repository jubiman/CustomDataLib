package com.jubiman.customdatalib.mob;

import com.jubiman.customdatalib.api.CustomData;
import necesse.entity.mobs.Mob;

/**
 * The base for all custom mob data classes
 */
public abstract class CustomMob extends CustomData {
	/**
	 * The mob's id
	 */
	protected final int id;

	/**
	 * Create a new CustomMob
	 * @param id the mob's id
	 */
	public CustomMob(int id) {
		this.id = id;
	}

	/**
	 * Get the mob's id
	 * @return the mob's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Perform a server tick on the mob
	 * @param mob the mob
	 */
	public abstract void serverTick(Mob mob);
}
