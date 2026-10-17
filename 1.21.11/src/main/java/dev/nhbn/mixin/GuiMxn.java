package dev.nhbn.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMxn {
	@Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void nm(GuiGraphics g, CallbackInfo ci) {
		if (dev.wscl.Mod.live("nohotbarname")) ci.cancel();
	}
}
