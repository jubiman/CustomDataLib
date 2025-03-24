package com.jubiman.customdatalib.player.client;

import com.jubiman.customdatalib.api.CustomData;
import com.jubiman.customdatalib.api.NeedsClientSideObject;

/**
 * Effectively a {@link com.jubiman.customdatalib.player.CustomPlayer} that does not require a serverTick method
 */
public class CustomClient extends CustomData implements NeedsClientSideObject {
	/**
	 * The player's auth
	 */
	public final long auth;

	/**
	 * Create a new CustomPlayer
	 * @param auth the player's auth
	 */
	public CustomClient(long auth) {
		this.auth = auth;
	}
}
