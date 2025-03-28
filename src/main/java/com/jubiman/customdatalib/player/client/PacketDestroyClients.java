package com.jubiman.customdatalib.player.client;

import com.jubiman.customdatalib.util.Logger;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;

/**
 * Packet sent from the server to the client to destroy client-side players.
 */
public class PacketDestroyClients extends Packet {
	/**
	 * Creates a new PacketDestroyClient from a byte array (received from the server), used by Necesse
	 * @param data the byte array to create the packet from
	 */
	public PacketDestroyClients(byte[] data) {
		super(data);
	}

	/**
	 * Creates a new PacketDestroyClient
	 */
	public PacketDestroyClients() {
	}

	/**
	 * Processes the packet on the client-side
	 * @param packet the packet to process
	 * @param client the client to process the packet on
	 */
	@Override
	public void processClient(NetworkPacket packet, Client client) {
		Logger.info("Destroying client-side players");
		CustomClientRegistry.INSTANCE.destroyClients();
	}
}
