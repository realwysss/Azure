package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Ui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.TabButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabButton.class)
public abstract class TbMxn {
	@Inject(method = "renderWidget", at = @At("HEAD"))
	private void wst(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		TabButton b = (TabButton) (Object) this;
		int ac = Hud.ac() & 0xFFFFFF;
		boolean on = b.isSelected();
		boolean hv = b.isHoveredOrFocused();
		int x = b.getX() + 2;
		int y = b.getY() + 3;
		int w = b.getWidth() - 4;
		int h = b.getHeight() - 5;
		if (on) Ui.glow(g, x, y, w, h, 8, ac, 0.6f);
		Ui.rr(g, x, y, w, h, 8, Ui.al(on ? ac : 0x0A0714, on ? 0.35f : hv ? 0.7f : 0.55f));
		Ui.ro(g, x, y, w, h, 8, Ui.al(on || hv ? ac : 0xFFFFFF, on ? 0.95f : hv ? 0.7f : 0.14f));
	}
}
