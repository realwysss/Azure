package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Ui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EbMxn {
	@Inject(method = "renderWidget", at = @At("HEAD"))
	private void wse(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn || net.minecraft.client.Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.ChatScreen) return;
		EditBox b = (EditBox) (Object) this;
		int ac = Hud.ac() & 0xFFFFFF;
		boolean f = b.isFocused();
		if (f) Ui.glow(g, b.getX(), b.getY(), b.getWidth(), b.getHeight(), 7, ac, 0.6f);
		Ui.rr(g, b.getX(), b.getY(), b.getWidth(), b.getHeight(), 7, Ui.al(0x0A0714, 0.85f));
		Ui.ro(g, b.getX(), b.getY(), b.getWidth(), b.getHeight(), 7, Ui.al(f ? ac : 0xFFFFFF, f ? 0.95f : 0.16f));
	}
}
