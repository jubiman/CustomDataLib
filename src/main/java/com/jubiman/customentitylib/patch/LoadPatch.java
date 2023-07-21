package com.jubiman.customentitylib.patch;

import com.jubiman.customentitylib.CustomEntityRegistry;
import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import net.bytebuddy.asm.Advice;

/**
 * Loads all CustomPlayers
 */
@ModMethodPatch(target = ServerClient.class, name = "applySave", arguments = {LoadData.class})
public class LoadPatch {
	@Advice.OnMethodEnter
	static void onEnter(@Advice.Argument(0) LoadData loadData) {
		try {
			CustomPlayerRegistry.loadAllEnter(loadData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Advice.OnMethodExit
	static void onExit(@Advice.Argument(0) LoadData loadData) {
		try {
			CustomPlayerRegistry.loadAllExit(loadData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
