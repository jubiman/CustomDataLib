package com.jubiman.celtest.sanity;

import com.jubiman.celtest.packet.PacketSyncPlayer;
import com.jubiman.celtest.sanity.mana.Mana;
import com.jubiman.customdatalib.api.ClientTickable;
import com.jubiman.customdatalib.api.HUDDrawable;
import com.jubiman.customdatalib.api.Savable;
import com.jubiman.customdatalib.api.Syncable;
import com.jubiman.customdatalib.player.CustomPlayer;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.pirates.PirateMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobChance;

import java.awt.*;
import java.util.Random;

public class SanityPlayer extends CustomPlayer implements ClientTickable, Savable, HUDDrawable, Syncable {
	public int ticksSinceLastHallucination;
	public int nextHallucination;
	public int nextSanityIncrease;
	public Mana mana = new Mana(new Random().nextInt(500) + 1);
	private byte sanity;

	public SanityPlayer(long auth) {
		super(auth);
		ticksSinceLastHallucination = 0;
		nextHallucination = 3333;
		nextSanityIncrease = 1200;
		sanity = 100;
	}

	public void serverTick(Server server) {
		PlayerMob player = server.getPlayerByAuth(auth);
		if (sanity < 33) {
			if (!player.buffManager.hasBuff("insanityindicatorbuff")) {
				player.addBuff(new ActiveBuff("insanityindicatorbuff", player, ((33 - sanity) * 1200 - 1200 + nextSanityIncrease) / 20f, null), true);
			} else {
				player.buffManager.getBuff("insanityindicatorbuff").setDurationLeftSeconds(((33 - sanity) * 1200 - 1200 + nextSanityIncrease) / 20f);
				server.network.sendToAllClients(new PacketMobBuff(player.getUniqueID(), player.buffManager.getBuff("insanityindicatorbuff")));
			}

			if (ticksSinceLastHallucination >= nextHallucination) {
				ticksSinceLastHallucination = 0;
				nextHallucination = GameRandom.globalRandom.getIntBetween(12000, 60000); // 60s -> 300s (assuming 20 TPS)
				for (int times = GameRandom.globalRandom.getIntBetween(1, (32 - sanity / 6) + 1); times > 0; --times) {
					Point tile = player.getMapPos();
					tile.x += GameRandom.globalRandom.getIntBetween(-100, 100);
					tile.y += GameRandom.globalRandom.getIntBetween(-100, 100);
					Level level = player.getLevel();
					MobChance randomMob = SanityPlayersHandler.spawnTable.getRandomMob(level, null, tile, GameRandom.globalRandom);
					if (randomMob != null) {
						Mob mob = randomMob.getMob(level, null, tile);
						mob.setLevel(level);
						level.entityManager.addMob(mob, tile.x, tile.y);
						if (level.isServerLevel())
							level.getServer().network.sendToClientsAt(new PacketSpawnMob(mob), level);
						if (mob instanceof PirateMob) break; // don't want more mobs after a boss lmao
					}
				}
			} else ++ticksSinceLastHallucination;
		} else {
			if (player.buffManager.hasBuff("insanityindicatorbuff")) {
				player.buffManager.removeBuff("insanityindicatorbuff", true);
			}
		}
		if (nextSanityIncrease == 0) {
			nextSanityIncrease = 1200;
			++sanity;
			if (sanity > 100) sanity = 100;
		} else --nextSanityIncrease;
	}

	@Override
	public void clientTick(Client client) {
	}

	public void addSanity(int amount) {
		sanity = (byte) Math.min(sanity + amount, 100);
	}

	public void removeSanity(int amount) {
		sanity = (byte) Math.max(sanity - amount, 0);
	}

	public byte getSanity() {
		return sanity;
	}

	public void setSanity(int amount) {
		sanity = (byte) amount;
		if (sanity < 0) sanity = 0;
		else if (sanity > 100) sanity = 100;
	}

	@Override
	public void addSaveData(SaveData save) {
		save.addByte("sanity", sanity);
		save.addInt("nextHallucination", nextHallucination);
		save.addInt("nextSanityIncrease", nextSanityIncrease);
		save.addInt("ticksSinceLastHallucination", ticksSinceLastHallucination);
		mana.save(save);
	}

	@Override
	public void loadEnter(LoadData data) {
	}

	@Override
	public void loadExit(LoadData data) {
		sanity = data.getByte("sanity");
		nextHallucination = data.getInt("nextHallucination");
		nextSanityIncrease = data.getInt("nextSanityIncrease");
		ticksSinceLastHallucination = data.getInt("ticksSinceLastHallucination");
		mana.load(data.getFirstLoadDataByName("mana"));
	}

	public int getIntelligence() {
		return 10;
	}

	@Override
	public void drawHUD(TickManager tickManager, PlayerMob playerMob, Rectangle renderBox) {
		mana.draw();
	}

	@Override
	public Packet getSyncPacket() {
		return new PacketSyncPlayer(this);
	}
}
