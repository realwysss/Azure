package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Vis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TiMxn {
	@Shadow private SplashRenderer splash;

	@Inject(method = "render", at = @At("HEAD"))
	private void wss(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (Cfg.c.ttl) splash = null;
	}

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void wst(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.ttl) return;
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		ci.cancel();
	}
}
