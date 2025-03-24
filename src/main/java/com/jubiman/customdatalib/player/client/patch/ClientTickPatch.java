package com.jubiman.customdatalib.player.client.patch;

import com.jubiman.customdatalib.player.CustomPlayerRegistry;
import com.jubiman.customdatalib.player.client.CustomClientRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import net.bytebuddy.asm.Advice;

/**
 * Client ticks all players that implement ClientTickable
 */
@ModMethodPatch(target = Client.class, name = "tick", arguments = {})
public class ClientTickPatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This Client client) {
		CustomClientRegistry.clientTickAll(client);
	}
}