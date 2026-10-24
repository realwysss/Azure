package dev.wscl.mixin;

import dev.wscl.Tf;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(dev.wysl.ui.Panel.class)
public abstract class WpMxn {
	@Redirect(method = "text", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"))
	private void wst(GuiGraphics g, Font f, String s, int x, int y, int c, boolean sh) {
		g.drawString(f, Tf.c(s), x, y, c, sh);
	}

	@Redirect(method = "width", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I"))
	private int wsw(Font f, String s) {
		return Tf.fw(s);
	}
}
