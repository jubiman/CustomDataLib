package com.jubiman.customentitylib.patch;

import com.jubiman.customentitylib.environment.ClientEnvironment;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormBuffHud;
import net.bytebuddy.asm.Advice;

import java.awt.Rectangle;

@ModMethodPatch(target = FormBuffHud.class, name = "draw", arguments = {TickManager.class, PlayerMob.class, Rectangle.class})
public class HudDrawPatch {
	@Advice.OnMethodExit
	static void onExit(@Advice.Argument(0) TickManager tickManager, @Advice.Argument(1) PlayerMob player, @Advice.Argument(2) Rectangle rectangle) {
		ClientEnvironment.hudDrawAll(tickManager, player, rectangle);
	}
}
