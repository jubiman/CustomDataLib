package com.jubiman.customentitylib.patch;

import com.jubiman.customentitylib.player.CustomPlayerRegistry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import net.bytebuddy.asm.Advice;

/**
 * Client ticks all players that implement ITickable
 */
@ModMethodPatch(target = Client.class, name = "tick", arguments = {})
public class ClientTickPatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.This Client client) {
		ClientClient clientClient = client.getClient();
		if (clientClient != null)
			CustomPlayerRegistry.clientTickAll(client);
	}
}