package com.jubiman.celtest.packet;

import com.jubiman.celtest.sanity.SanityPlayer;
import com.jubiman.celtest.sanity.SanityPlayersHandler;
import com.jubiman.celtest.sanity.mana.Mana;
import com.jubiman.customentitylib.environment.ClientEnvironment;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketSyncPlayer extends Packet {
	public final int mana;
	public final int maxMana;

	public PacketSyncPlayer(SanityPlayer player) {
		mana = player.mana.getMana();
		maxMana = player.mana.getMaxMana();

		PacketWriter writer = new PacketWriter(this);
		writer.putNextInt(mana);
		writer.putNextInt(maxMana);
	}

	public PacketSyncPlayer(byte[] data) {
		super(data);
		PacketReader reader = new PacketReader(this);
		mana = reader.getNextInt();
		maxMana = reader.getNextInt();
	}

	@Override
	public void processClient(NetworkPacket packet, Client client) {
		((SanityPlayer)ClientEnvironment.get(SanityPlayersHandler.name)).mana = new Mana(mana, maxMana);
	}
}
