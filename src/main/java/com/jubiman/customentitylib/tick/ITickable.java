package com.jubiman.customentitylib.tick;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

/**
 * Specifies that the CustomPlayer(sHandler) is tickable
 */
public interface ITickable {
	/**
	 * Performs a server tick
	 * @param server the server to tick on
	 */
	void serverTick(Server server);

	/**
	 * Performs a client tick
	 * @param client the client to tick on
	 */
	void clientTick(Client client);
}
