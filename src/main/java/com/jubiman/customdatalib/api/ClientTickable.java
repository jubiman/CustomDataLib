package com.jubiman.customdatalib.api;

import necesse.engine.network.client.Client;

/**
 * Specifies that the CustomPlayer(sHandler) is tickable on the client-side
 */
public interface ClientTickable extends NeedsClientSideObject {
	/**
	 * Performs a client tick
	 *
	 * @param client the client to tick on
	 */
	void clientTick(Client client);
}
