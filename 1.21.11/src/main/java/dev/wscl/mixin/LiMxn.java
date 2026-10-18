package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Ui;
import dev.wscl.Vis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public abstract class LiMxn {
	private boolean ias() {
		String n = getClass().getName();
		return n.startsWith("ru.vidtu.ias.") || n.startsWith("com.terraformersmc.modmenu.");
	}

	@Inject(method = "renderWidget", at = @At("HEAD"))
	private void wsw(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn || !ias()) return;
		AbstractSelectionList<?> l = (AbstractSelectionList<?>) (Object) this;
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.rr(g, l.getX() - 2, l.getY() - 2, l.getWidth() + 4, l.getHeight() + 4, 10, Ui.al(0x0A0714, 0.7f));
		Ui.ro(g, l.getX() - 2, l.getY() - 2, l.getWidth() + 4, l.getHeight() + 4, 10, Ui.al(0xFFFFFF, 0.12f));
		g.fillGradient(l.getX() + 8, l.getY() - 1, l.getX() + l.getWidth() - 8, l.getY() + 30, Ui.al(ac, 0.08f), Ui.al(ac, 0f));
	}

	private boolean wsl() {
		Object o = this;
		return Cfg.c.btn && (o instanceof WorldSelectionList || ias());
	}

	@Inject(method = "renderSelection", at = @At("HEAD"), cancellable = true)
	private void wss(GuiGraphics g, @Coerce Object e, int c, CallbackInfo ci) {
		if (wsl()) ci.cancel();
	}

	@Inject(method = "renderItem", at = @At("HEAD"))
	private void wsr(GuiGraphics g, int mx, int my, float d, @Coerce Object e, CallbackInfo ci) {
		if (!wsl()) return;
		AbstractSelectionList<?> l = (AbstractSelectionList<?>) (Object) this;
		int ac = Hud.ac() & 0xFFFFFF;
		EnAcc en = (EnAcc) e;
		int x = en.ex() - 2;
		int y = en.ey() - 1;
		int w = en.ew() + 4;
		int h = en.eh();
		boolean se = l.getSelected() == e;
		boolean hv = mx >= x && mx < x + w && my >= y && my < y + h && my >= l.getY() && my < l.getBottom();
		Ui.rr(g, x, y, w, h, 8, Ui.al(se ? Vis.mix(0xFFFFFF, ac, 0.5f) : 0xFFFFFF, se ? 0.12f : hv ? 0.07f : 0f));
	}
}
