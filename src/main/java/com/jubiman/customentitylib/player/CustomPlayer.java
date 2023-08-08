package com.jubiman.customentitylib.player;

import com.jubiman.customentitylib.api.CustomData;

public class CustomPlayer extends CustomData {
	protected final long auth;

	public CustomPlayer(long auth) {
		this.auth = auth;
	}
}
