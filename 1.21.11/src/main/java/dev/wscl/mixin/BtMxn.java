package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Tf;
import dev.wscl.Ui;
import dev.wscl.Vis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButton.class)
public abstract class BtMxn {
	@Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
	private void wsb(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		Object o = this;
		if (!Cfg.c.btn || !(o instanceof Button || o instanceof CycleButton) || o instanceof SpriteIconButton || o instanceof net.minecraft.client.gui.components.ImageButton) return;
		AbstractButton b = (AbstractButton) o;
		int x = b.getX();
		int y = b.getY();
		int w = b.getWidth();
		int h = b.getHeight();
		boolean hv = b.active && b.isHoveredOrFocused();
		int ac = Hud.ac() & 0xFFFFFF;
		float hk = b.active ? 1f : 0.4f;
		int r = Math.min(10, h / 2);
		if (hv) Ui.glow(g, x, y, w, h, r, ac, 0.8f);
		Ui.rr(g, x, y, w, h, r, Ui.al(hv ? Vis.mix(0x120E26, ac, 0.35f) : 0x0C0A1A, (hv ? 0.92f : 0.7f) * hk));
		Ui.ro(g, x, y, w, h, r, Ui.al(hv ? ac : 0xFFFFFF, hv ? 0.95f : 0.16f));
		String t = b.getMessage().getString();
		Tf.dr(g, t, x + (w - Tf.w(t, 9f)) / 2f, y + h / 2f - 4f, 9f, b.active ? 0xFFFFFFFF : 0xFF707A8A);
		ci.cancel();
	}
}
