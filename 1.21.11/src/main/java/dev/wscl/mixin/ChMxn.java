package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Mod;
import dev.wscl.Ui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public abstract class ChMxn {
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
	private void wsf(GuiGraphics g, int x1, int y1, int x2, int y2, int c) {
		if (!Cfg.c.btn || !Mod.live("cinv")) {
			g.fill(x1, y1, x2, y2, c);
			return;
		}
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.glow(g, x1, y1, x2 - x1, y2 - y1, 6, ac, 0.3f);
		Ui.rr(g, x1, y1, x2 - x1, y2 - y1, 6, Ui.al(0x070F14, 0.88f));
		Ui.ro(g, x1, y1, x2 - x1, y2 - y1, 6, Ui.al(ac, 0.5f));
	}
}
