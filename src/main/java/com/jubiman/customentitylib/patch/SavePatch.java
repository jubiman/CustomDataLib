package com.jubiman.customentitylib.patch;

import com.jubiman.customentitylib.CustomEntityRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.SaveData;
import net.bytebuddy.asm.Advice;

/**
 * Saves all CustomPlayers
 */
@ModMethodPatch(target = ServerClient.class, name = "getSave", arguments = {})
public class SavePatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This ServerClient self, @Advice.Return(readOnly = false) SaveData save) {
		CustomEntityRegistry.saveAll(save, self);
	}
}
