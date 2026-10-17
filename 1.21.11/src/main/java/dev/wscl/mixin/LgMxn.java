package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Vis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public abstract class LgMxn {
	@Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", at = @At("HEAD"), cancellable = true)
	private void wsg(GuiGraphics g, int w, float a, int y, CallbackInfo ci) {
		if (!Cfg.c.ttl) return;
		Vis.logo(g, (Util.getMillis() % 1000000L) / 1000f, a, y + 26);
		ci.cancel();
	}
}
