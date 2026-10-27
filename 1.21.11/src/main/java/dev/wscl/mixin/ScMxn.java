package dev.wscl.mixin;

import dev.wscl.AzTitle;
import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Mnu;
import dev.wscl.Ui;
import dev.wscl.Vis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScMxn {
	@Unique private long azt = -1;

	@Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
	private void wsp(GuiGraphics g, float d, CallbackInfo ci) {
		if (!Cfg.c.ttl) return;
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		ci.cancel();
	}

	@Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
	private void wsb(GuiGraphics g, CallbackInfo ci) {
		if (Cfg.c.ttl && Minecraft.getInstance().level == null) ci.cancel();
	}

	@Unique
	private float fk() {
		if (azt < 0) azt = Util.getMillis();
		return Ui.ez((Util.getMillis() - azt) / 240f);
	}

	@Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
	private void wsm(GuiGraphics g, CallbackInfo ci) {
		if (!Cfg.c.ttl) return;
		if (Minecraft.getInstance().level != null) {
			int ac = Hud.ac() & 0xFFFFFF;
			g.fillGradient(0, 0, g.guiWidth(), g.guiHeight(), Ui.al(Vis.mix(0x03090C, ac, 0.12f), 0.62f * fk()), Ui.al(0x03090C, 0.78f * fk()));
		}
		ci.cancel();
	}

	@Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;IIII)V", at = @At("HEAD"), cancellable = true)
	private void wsl(GuiGraphics g, int x, int y, int w, int h, CallbackInfo ci) {
		if (Cfg.c.ttl) ci.cancel();
	}

	@Inject(method = "renderWithTooltipAndSubtitles", at = @At("HEAD"))
	private void wsh(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		Object o = this;
		if (o instanceof Mnu || o instanceof AzTitle) return;
		Ui.GA = fk();
	}

	@Inject(method = "renderWithTooltipAndSubtitles", at = @At("RETURN"))
	private void wst(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		Ui.GA = 1f;
	}
}
