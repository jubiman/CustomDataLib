package com.jubiman.celtest.sanity;

import com.jubiman.customdatalib.player.CustomPlayerRegistry;
import com.jubiman.customdatalib.player.CustomPlayersHandler;
import necesse.engine.network.server.Server;
import necesse.level.maps.biomes.MobSpawnTable;

public class SanityPlayersHandler extends CustomPlayersHandler<SanityPlayer> {
	public static final String name = "SANITYPLAYERS";
	public static final MobSpawnTable spawnTable = new MobSpawnTable();

	public SanityPlayersHandler() {
		super(SanityPlayer.class, name);
	}

	public static SanityPlayersHandler getInstance() {
		return (SanityPlayersHandler) CustomPlayerRegistry.INSTANCE.get(name);
	}

	/**
	 * A null safe way to get a player from the map, adds player if they don't exist yet
	 * @param auth the authentication of the player's ServerClient
	 * @return the SanityPlayer object belonging to the player
	 */
	public static SanityPlayer getPlayer(long auth) {
		return getInstance().get(auth);
	}

	@Override
	public void serverTick(Server server) {
		super.serverTick(server);
		for (SanityPlayer player : userMap.values())
			player.serverTick(server);
	}
}
