package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class ScbMxn {
	@Unique private boolean azp;

	@Unique
	private static int g(String k, int d) {
		return Mod.get("sb").i(k, d);
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
	private void wsa(GuiGraphics gg, Objective o, CallbackInfo ci) {
		azp = false;
		if (!Mod.live("sb")) return;
		if (g("hide", 0) == 1) {
			ci.cancel();
			return;
		}
		float s = g("sc", 100) / 100f;
		float ax = gg.guiWidth();
		float ay = gg.guiHeight() / 2f;
		gg.pose().pushMatrix();
		gg.pose().translate(ax + g("ox", 0), ay + g("oy", 0));
		gg.pose().scale(s, s);
		gg.pose().translate(-ax, -ay);
		azp = true;
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
	private void wsb(GuiGraphics gg, Objective o, CallbackInfo ci) {
		if (azp) gg.pose().popMatrix();
		azp = false;
	}

	@Unique
	private static void bg(GuiGraphics gg, int a, int b, int c, int d, int col) {
		if (!Mod.live("sb")) {
			gg.fill(a, b, c, d, col);
			return;
		}
		if (g("nobg", 0) == 1) return;
		int al = Math.round(g("bgo", 40) * 2.55f);
		gg.fill(a, b, c, d, al << 24);
	}

	@Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0))
	private void wsf0(GuiGraphics gg, int a, int b, int c, int d, int col) {
		bg(gg, a, b, c, d, col);
	}

	@Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 1))
	private void wsf1(GuiGraphics gg, int a, int b, int c, int d, int col) {
		bg(gg, a, b, c, d, col);
	}

	@Unique
	private static void tx(GuiGraphics gg, Font f, Component c, int x, int y, int col, boolean sh) {
		if (!Mod.live("sb")) {
			gg.drawString(f, c, x, y, col, sh);
			return;
		}
		if (g("bw", 0) == 1) {
			c = Component.literal(c.getString());
			col = 0xFFFFFFFF;
		}
		gg.drawString(f, c, x, y, col, g("sh", 0) == 1);
	}

	@Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V", ordinal = 0))
	private void wst(GuiGraphics gg, Font f, Component c, int x, int y, int col, boolean sh) {
		tx(gg, f, c, x, y, col, sh);
	}

	@Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V", ordinal = 1))
	private void wsn(GuiGraphics gg, Font f, Component c, int x, int y, int col, boolean sh) {
		tx(gg, f, c, x, y, col, sh);
	}

	@Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V", ordinal = 2))
	private void wsv(GuiGraphics gg, Font f, Component c, int x, int y, int col, boolean sh) {
		if (Mod.live("sb") && g("nums", 1) == 0) return;
		tx(gg, f, c, x, y, col, sh);
	}
}
