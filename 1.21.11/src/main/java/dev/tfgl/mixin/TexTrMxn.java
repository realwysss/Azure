package dev.tfgl.mixin;

import dev.tfgl.Cfg;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureTransform.class)
public abstract class TexTrMxn {
	@Inject(method = "setupGlintTexturing", at = @At("HEAD"), cancellable = true)
	private static void glint(float f, CallbackInfoReturnable<Matrix4f> ci) {
		if (!Cfg.c.on) return;
		long l = (long) ((double) Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0 * Cfg.c.spd / 100.0);
		float g = (float) (l % 110000L) / 110000.0f;
		float h = (float) (l % 30000L) / 30000.0f;
		Matrix4f m = new Matrix4f().translation(-g, h, 0.0f);
		m.rotateZ((float) Math.toRadians(Cfg.c.ang)).scale(f * 100f / Cfg.c.siz);
		ci.setReturnValue(m);
	}
}
