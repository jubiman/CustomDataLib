package com.jubiman.customdatalib.player.client;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;

/**
 * Packet sent from the server to the client to destroy a client-side player.
 */
public class PacketDestroyClient extends Packet {
	/**
	 * The authentication of the client to destroy
	 */
	public final long authentication;

	/**
	 * Creates a new PacketDestroyClient from a byte array (received from the server), used by Necesse
	 * @param client the {@link ServerClient} to destroy
	 */
	public PacketDestroyClient(ServerClient client) {
		super();
		this.authentication = client.authentication;

		PacketWriter writer = new PacketWriter(this);
		writer.putNextLong(authentication);
	}

	/**
	 * Creates a new PacketDestroyClient from a byte array (received from the server), used by Necesse
	 * @param data the byte array to create the packet from
	 */
	public PacketDestroyClient(byte[] data) {
		super(data);
		PacketReader reader = new PacketReader(this);
		this.authentication = reader.getNextLong();
	}

	@Override
	public void processClient(NetworkPacket packet, Client client) {
		CustomClientRegistry.INSTANCE.destroyClient(authentication);
	}
}
