package com.jubiman.customentitylib.api;

import necesse.engine.network.Packet;

/**
 * This interface defines that the client and server must sync their data, usually because the client needs it for
 * client side ticking, like HUD elements.
 */
public interface Syncable<T extends CustomData> {
	// TODO: do smth like this
	Packet getSyncPacket(T customData);
	T fromSyncPacket(Packet packet);
}
