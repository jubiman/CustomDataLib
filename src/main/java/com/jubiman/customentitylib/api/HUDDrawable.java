package com.jubiman.customentitylib.api;

import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

import java.awt.Rectangle;

/**
 * Specifies that the CustomPlayer is drawable on the HUD
 */
public interface HUDDrawable {
	/**
	 * Draws the HUD.
	 * @param tickManager the tickManager
	 * @param playerMob the playerMob
	 * @param renderBox the renderBox
	 */
	void drawHUD(TickManager tickManager, PlayerMob playerMob, Rectangle renderBox);
}
