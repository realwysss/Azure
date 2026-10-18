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

}
