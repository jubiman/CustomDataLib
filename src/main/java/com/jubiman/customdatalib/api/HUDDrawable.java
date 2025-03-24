package com.jubiman.customdatalib.api;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

import java.awt.*;

/**
 * Specifies that the CustomPlayer is drawable on the HUD
 */
public interface HUDDrawable extends NeedsClientSideObject {
	/**
	 * Draws the HUD.
	 *
	 * @param tickManager the tickManager
	 * @param playerMob   the playerMob
	 * @param renderBox   the renderBox
	 */
	void drawHUD(TickManager tickManager, PlayerMob playerMob, Rectangle renderBox);
}
