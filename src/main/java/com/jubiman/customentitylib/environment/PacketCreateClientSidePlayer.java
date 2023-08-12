package com.jubiman.customentitylib.environment;

import com.jubiman.customentitylib.environment.ClientEnvironment;
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
	public final long auth;

	public PacketCreateClientSidePlayer(long auth) {
		this.auth = auth;

		PacketWriter writer = new PacketWriter(this);
		writer.putNextLong(auth);
	}

	public PacketCreateClientSidePlayer(byte[] data) {
		super(data);
		PacketReader reader = new PacketReader(this);
		auth = reader.getNextLong();
	}

	@Override
	public void processClient(NetworkPacket packet, Client client) {
		GameLog.debug.println("PacketCreateClientSidePlayer.processClient: " + auth);
		ClientEnvironment.createPlayers(auth);
	}
}
