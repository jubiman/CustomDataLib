package com.jubiman.celtest.buff;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class InsanityIndicatorBuff extends Buff {
	@Override
	public void init(ActiveBuff activeBuff) {
		this.canCancel = false;
		this.overrideSync = true;
	}

	@Override
	public ListGameTooltips getTooltip(ActiveBuff ab) {
		ListGameTooltips tooltips = super.getTooltip(ab);
		tooltips.add(Localization.translate("buff", "insanityindicatorbufftooltip"));
		return tooltips;
	}
}
