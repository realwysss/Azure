package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Ic;
import dev.wscl.Ui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Checkbox.class)
public abstract class CkMxn {
	@Inject(method = "renderContents", at = @At("HEAD"))
	private void wsk(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		Checkbox b = (Checkbox) (Object) this;
		int ac = Hud.ac() & 0xFFFFFF;
		boolean on = b.selected();
		boolean hv = b.isHoveredOrFocused();
		int x = b.getX();
		int y = b.getY();
		if (on) Ui.glow(g, x, y, 17, 17, 5, ac, 0.7f);
		Ui.rr(g, x, y, 17, 17, 5, Ui.al(on ? ac : dev.wscl.Ui.tn(0x0A0714), 0.85f));
		Ui.ro(g, x, y, 17, 17, 5, Ui.al(hv || on ? ac : 0xFFFFFF, hv || on ? 0.95f : 0.18f));
		if (on) {
			Ic.ln(g, x + 4, y + 8, x + 7, y + 11, 0xFFFFFFFF);
			Ic.ln(g, x + 7, y + 11, x + 13, y + 5, 0xFFFFFFFF);
		}
	}
}
