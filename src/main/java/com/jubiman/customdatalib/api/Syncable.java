package com.jubiman.customdatalib.api;

import necesse.engine.network.Packet;

/**
 * This interface defines that the client and server must sync their data, usually because the client needs it for
 * client side ticking, like HUD elements.
 */
public interface Syncable {
	/**
	 * Returns a packet that contains all the data that needs to be synced.
	 * Should be a custom packet created by the mod that implements this interface.
	 * @return the packet
	 */
	Packet getSyncPacket();
}
