package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Ui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSliderButton.class)
public abstract class SlMxn {
	@Shadow protected double value;

	@Inject(method = "renderWidget", at = @At("HEAD"))
	private void wss(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		AbstractSliderButton b = (AbstractSliderButton) (Object) this;
		int ac = Hud.ac() & 0xFFFFFF;
		int x = b.getX();
		int y = b.getY();
		int w = b.getWidth();
		int h = b.getHeight();
		boolean hv = b.isHoveredOrFocused();
		Ui.rr(g, x, y, w, h, 7, Ui.al(0x0A0714, 0.82f));
		int hx = x + (int) (value * (w - 8));
		if (hx > x + 8) Ui.rr(g, x, y, hx - x + 4, h, 7, Ui.al(ac, 0.28f));
		Ui.ro(g, x, y, w, h, 7, Ui.al(hv ? ac : 0xFFFFFF, hv ? 0.9f : 0.14f));
		if (hv) Ui.glow(g, hx, y, 8, h, 4, ac, 0.8f);
		Ui.rr(g, hx, y, 8, h, 4, Ui.al(hv ? 0xFFFFFF : 0xE0D8FF, 1f));
	}
}
