package com.jubiman.customentitylib.packet;

import com.jubiman.customentitylib.api.CustomData;
import com.jubiman.customentitylib.environment.ClientEnvironment;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

@Deprecated
public class CustomDataSyncPacket extends Packet {
	public final String identifier;

	public CustomDataSyncPacket(byte[] data) {
		super(data);
		PacketReader reader = new PacketReader(this);
		this.identifier = reader.getNextString();
	}

	public CustomDataSyncPacket(CustomData customPlayer, String identifier) {
		PacketWriter writer = new PacketWriter(this);
		this.identifier = identifier;

		writer.putNextString(this.identifier);
	}

	@Override
	public void processClient(NetworkPacket packet, Client client) {
		// TODO: ok maybe this class is a bit useless, or not?
		//ClientEnvironment.syncClientData(identifier, -1);

	}
}
