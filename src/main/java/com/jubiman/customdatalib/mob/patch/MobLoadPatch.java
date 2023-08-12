package com.jubiman.customdatalib.mob.patch;

import com.jubiman.customdatalib.mob.CustomMobRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

/**
 * Loads all CustomMobs
 */
@ModMethodPatch(target = Mob.class, name = "applyLoadData", arguments = {LoadData.class})
public class MobLoadPatch {
	@Advice.OnMethodEnter
	static void onEnter(@Advice.This Mob self, @Advice.Argument(0) LoadData loadData) {
		try {
			CustomMobRegistry.INSTANCE.loadAllEnter(loadData, self.getID());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Advice.OnMethodExit
	static void onExit(@Advice.This Mob self, @Advice.Argument(0) LoadData loadData) {
		try {
			CustomMobRegistry.INSTANCE.loadAllExit(loadData, self.getID());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
