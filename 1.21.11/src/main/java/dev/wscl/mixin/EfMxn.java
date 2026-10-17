package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class EfMxn {
	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void wse(GuiGraphics g, DeltaTracker d, CallbackInfo ci) {
		if (Mod.live("pots")) ci.cancel();
	}
}
