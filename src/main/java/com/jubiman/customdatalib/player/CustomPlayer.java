package com.jubiman.customdatalib.player;

import com.jubiman.customdatalib.api.CustomData;

/**
 * The base for all custom player data classes
 */
public class CustomPlayer extends CustomData {
	/**
	 * The player's auth
	 */
	protected final long auth;

	/**
	 * Create a new CustomPlayer
	 * @param auth the player's auth
	 */
	public CustomPlayer(long auth) {
		this.auth = auth;
	}

	/**
	 * Get the player's auth
	 * @return the player's auth
	 */
	public long getAuth() {
		return auth;
	}
}
