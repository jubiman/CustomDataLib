package com.jubiman.customdatalib.client;

import com.jubiman.customdatalib.api.ClientSide;
import com.jubiman.customdatalib.api.CustomData;
import necesse.engine.network.client.Client;

/**
 * The base for all custom client data classes
 */
public abstract class CustomClient extends CustomData implements ClientSide {
	/**
	 * The player's client
	 */
	protected final Client client;

	/**
	 * Create a new CustomPlayer
	 * @param client the player's auth
	 */
	public CustomClient(Client client) {
		this.client = client;
	}

	/**
	 * Get the player's client
	 * @return the player's client
	 */
	public Client getClient() {
		return client;
	}
}
