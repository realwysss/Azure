package dev.wscl.mixin;

import dev.wscl.Mod;
import dev.wscl.Crs;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class CrMxn {
	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void wsx(GuiGraphics g, DeltaTracker d, CallbackInfo ci) {
		if (!Mod.live("cross")) return;
		Crs.draw(g);
		ci.cancel();
	}
}
