package dev.wscl.mixin;

import dev.wscl.Skn;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerSkinWidget.class)
public abstract class PwMxn {
	@Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerSkinWidget;getX()I"))
	private int wsx(PlayerSkinWidget w) {
		return w.getX() - (Skn.cur == w ? Skn.pw : 0);
	}

	@Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerSkinWidget;getY()I"))
	private int wsy(PlayerSkinWidget w) {
		return w.getY() - (Skn.cur == w ? Skn.ph : 0);
	}

	@Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerSkinWidget;getRight()I"))
	private int wsr(PlayerSkinWidget w) {
		return w.getRight() + (Skn.cur == w ? Skn.pw : 0);
	}

	@Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;submitSkinRenderState(Lnet/minecraft/client/model/player/PlayerModel;Lnet/minecraft/resources/Identifier;FFFFIIII)V"))
	private void wsc(net.minecraft.client.gui.GuiGraphics g, net.minecraft.client.model.player.PlayerModel m, net.minecraft.resources.Identifier t, float a, float b, float c, float d, int x0, int y0, int x1, int y1) {
		g.submitSkinRenderState(m, t, a, b, c, d, x0, y0, x1, y1);
		if (Skn.cur != null && Skn.cape != null && Skn.cur == (Object) this) g.submitSkinRenderState(Skn.cm(), Skn.cape, a, b, c, d, x0, y0, x1, y1);
	}
}
