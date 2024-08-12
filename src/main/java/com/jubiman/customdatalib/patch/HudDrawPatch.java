package com.jubiman.customdatalib.patch;

import com.jubiman.customdatalib.environment.ClientEnvironment;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormBuffHud;
import net.bytebuddy.asm.Advice;

import java.awt.Rectangle;

/**
 * Draws all HUDDrawable players' HUD elements
 */
@ModMethodPatch(target = FormBuffHud.class, name = "draw", arguments = {TickManager.class, PlayerMob.class, Rectangle.class})
public class HudDrawPatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.Argument(0) TickManager tickManager, @Advice.Argument(1) PlayerMob player, @Advice.Argument(2) Rectangle rectangle) {
		ClientEnvironment.hudDrawAll(tickManager, player, rectangle);
	}
}
