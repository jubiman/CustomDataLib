package com.jubiman.customdatalib.player;

import com.jubiman.customdatalib.api.CustomData;

public class CustomPlayer extends CustomData {
	protected final long auth;

	public CustomPlayer(long auth) {
		this.auth = auth;
	}

	public long getAuth() {
		return auth;
	}
}
