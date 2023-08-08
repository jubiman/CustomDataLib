package com.jubiman.customentitylib.api;

import necesse.engine.network.client.Client;

/**
 * Specifies that the CustomPlayer(sHandler) is tickable
 */
public interface ClientTickable {
	/**
	 * Performs a client tick
	 * @param client the client to tick on
	 */
	void clientTick(Client client);
}
