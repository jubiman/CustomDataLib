package com.jubiman.customdatalib.mob.patch;

import com.jubiman.customdatalib.mob.CustomMobRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

/**
 * Ticks all CustomMobs
 */
@ModMethodPatch(target = Mob.class, name = "serverTick", arguments = {})
public class MobServerTickPatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This Mob self) {
		if (self instanceof PlayerMob) return; // The player mob ticks separately
		CustomMobRegistry.serverTickAll(self);
	}
}
