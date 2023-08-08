package com.jubiman.customentitylib.patch;

import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import net.bytebuddy.asm.Advice;

/**
 * Loads all CustomPlayers
 */
@ModMethodPatch(target = ServerClient.class, name = "applySave", arguments = {LoadData.class})
public class PlayerLoadPatch {
	@Advice.OnMethodEnter
	static void onEnter(@Advice.This ServerClient self, @Advice.Argument(0) LoadData loadData) {
		try {
			CustomPlayerRegistry.INSTANCE.loadAllEnter(loadData, self.authentication);
			//ServerEnvironment.PLAYER_REGISTRY.loadAllEnter(loadData, self.authentication);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Advice.OnMethodExit
	static void onExit(@Advice.This ServerClient self, @Advice.Argument(0) LoadData loadData) {
		try {
			CustomPlayerRegistry.INSTANCE.loadAllExit(loadData, self.authentication);
			//ServerEnvironment.PLAYER_REGISTRY.loadAllExit(loadData, self.authentication);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
