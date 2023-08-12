package com.jubiman.customdatalib.mob;

import com.jubiman.customdatalib.api.CustomData;

public class CustomMob extends CustomData {
	protected final int id;

	public CustomMob(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
