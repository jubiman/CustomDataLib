package com.jubiman.customentitylib.patch;

import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.SaveData;
import net.bytebuddy.asm.Advice;

/**
 * Saves all mod's CustomPlayer data for this player.
 */
@ModMethodPatch(target = ServerClient.class, name = "getSave", arguments = {})
public class PlayerSavePatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This ServerClient self, @Advice.Return(readOnly = false) SaveData save) {
		CustomPlayerRegistry.INSTANCE.saveAll(save, self.authentication);
	}
}
