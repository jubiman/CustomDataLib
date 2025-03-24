package com.jubiman.customdatalib.player.client;

import com.jubiman.customdatalib.api.CustomDataHandler;

/**
 * The storage class for custom players on the client side
 * @param <T> the class extending CustomPlayer and NeedsClientSideObject
 */
public class ClientPlayersHandler<T extends CustomClient> extends CustomDataHandler<Long, T> {
	/**
	 * Constructs the storage class for custom players
	 *
	 * @param clazz      the class extending CustomPlayer
	 * @param identifier the name of the class, used for creating a save component
	 */
	public ClientPlayersHandler(Class<T> clazz, String identifier) {
		super(clazz, new Class[]{long.class}, identifier);
	}
}
