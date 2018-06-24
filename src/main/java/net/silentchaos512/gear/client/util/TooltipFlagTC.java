package net.silentchaos512.gear.client.util;

import net.minecraft.client.util.ITooltipFlag;

public class TooltipFlagTC implements ITooltipFlag {

  public final boolean ctrlDown, altDown, shiftDown, advanced;

  public TooltipFlagTC(boolean ctrlDown, boolean altDown, boolean shiftDown, boolean advanced) {

    this.ctrlDown = ctrlDown;
    this.altDown = altDown;
    this.shiftDown = shiftDown;
    this.advanced = advanced;
  }

  @Override
  public boolean isAdvanced() {

    return advanced;
  }
}
