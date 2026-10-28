package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Intro;
import dev.wscl.Vis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoMxn {
	@Shadow private float currentProgress;
	@Shadow private long fadeOutStart;
	@Shadow private long fadeInStart;
	@Shadow private boolean fadeIn;
	private boolean azj;

	@Inject(method = "render", at = @At("RETURN"))
	private void wsl(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.lod) return;
		long n = Util.getMillis();
		if (Intro.on()) {
			if (fadeOutStart > -1L) {
				if (!azj) {
					azj = true;
					Intro.start(fadeOutStart);
					fadeOutStart -= 1500L;
				}
				Intro.draw(g);
			} else Intro.load(g, currentProgress);
			return;
		}
		float f = fadeOutStart > -1L ? (n - fadeOutStart) / 1000f : -1f;
		float k = fadeInStart > -1L ? (n - fadeInStart) / 120f : -1f;
		float a = f >= 1f ? 1f - Math.max(0f, Math.min(1f, f - 1f)) : (fadeIn ? Math.max(0f, Math.min(1f, k)) : 1f);
		if (a < 0.02f) return;
		float t = (n % 1000000L) / 1000f;
		int h = g.guiHeight();
		Vis.bg(g, t, a);
		int ey = h / 2 - 14;
		float rv = (System.currentTimeMillis() - Vis.t0) / 1000f;
		Vis.lmode = 1;
		Vis.logo(g, t, a, ey, rv);
		Vis.lmode = 0;
		Vis.bar(g, t, a, ey + 44, currentProgress);
		Vis.tip(g, t, a, h - 14);
	}
}
