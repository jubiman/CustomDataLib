package com.jubiman.customdatalib.client;

import com.jubiman.customdatalib.util.Logger;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

/**
 * Packet sent from the server to the client to create a client-side player.
 * Sent only once after client has connected.
 */
public class PacketCreateClientSidePlayer extends Packet {
	/**
	 * Creates a new PacketCreateClientSidePlayer
	 */
	public PacketCreateClientSidePlayer() {
	}

	/**
	 * Creates a new PacketCreateClientSidePlayer from a byte array (received from the server), used by Necesse
	 * @param data the byte array to create the packet from
	 */
	public PacketCreateClientSidePlayer(byte[] data) {
		super(data);
	}

	/**
	 * Processes the packet on the client-side
	 * @param packet the packet to process
	 * @param client the client to process the packet on
	 */
	@Override
	public void processClient(NetworkPacket packet, Client client) {
		Logger.info("Creating client-side players");
		CustomClientRegistry.createClients(client);
	}
}
