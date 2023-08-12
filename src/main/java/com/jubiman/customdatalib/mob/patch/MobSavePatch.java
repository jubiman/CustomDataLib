package com.jubiman.customdatalib.mob.patch;

import com.jubiman.customdatalib.mob.CustomMobRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = Mob.class, name = "addSaveData", arguments = {SaveData.class})
public class MobSavePatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This Mob self, @Advice.Argument(0) SaveData save) {
		CustomMobRegistry.INSTANCE.saveAll(save, self.getID());
	}
}
