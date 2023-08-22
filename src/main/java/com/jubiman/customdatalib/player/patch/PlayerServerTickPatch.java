package com.jubiman.customdatalib.player.patch;

import com.jubiman.customdatalib.player.CustomPlayerRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.Server;
import net.bytebuddy.asm.Advice;

/**
 * Server ticks all players
 */
@ModMethodPatch(target = Server.class, name = "tick", arguments = {})
public class PlayerServerTickPatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This Server server) {
		CustomPlayerRegistry.serverTickAll(server);
	}
}
