package com.jubiman.customdatalib.environment;

import necesse.engine.GameLog;
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
	 * The auth of the player to create
	 */
	public final long auth;

	/**
	 * Creates a new PacketCreateClientSidePlayer
	 * @param auth the auth of the player to create
	 */
	public PacketCreateClientSidePlayer(long auth) {
		this.auth = auth;

		PacketWriter writer = new PacketWriter(this);
		writer.putNextLong(auth);
	}

	/**
	 * Creates a new PacketCreateClientSidePlayer from a byte array (received from the server), used by Necesse
	 * @param data the byte array to create the packet from
	 */
	public PacketCreateClientSidePlayer(byte[] data) {
		super(data);
		PacketReader reader = new PacketReader(this);
		auth = reader.getNextLong();
	}

	/**
	 * Processes the packet on the client-side
	 * @param packet the packet to process
	 * @param client the client to process the packet on
	 */
	@Override
	public void processClient(NetworkPacket packet, Client client) {
		GameLog.debug.println("PacketCreateClientSidePlayer.processClient: " + auth);
		ClientEnvironment.createPlayers(auth);
	}
}
