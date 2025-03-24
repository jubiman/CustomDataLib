package com.jubiman.customdatalib.player;

import com.jubiman.customdatalib.api.CustomData;
import necesse.engine.network.server.Server;

/**
 * The base for all custom player data classes
 */
public abstract class CustomPlayer extends CustomData {
	/**
	 * The player's auth
	 */
	public final long auth;

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
	@Deprecated
	public long getAuth() {
		return auth;
	}

	/**
	 * Called every server tick
	 * @param server the server
	 */
	public abstract void serverTick(Server server);
}
