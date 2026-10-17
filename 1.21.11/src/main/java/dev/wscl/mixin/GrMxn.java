package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Mod;
import dev.wscl.Wc;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GrMxn {
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void zm(Camera c, float pt, boolean f, CallbackInfoReturnable<Float> ci) {
		if (Mod.live("zoom") && Wc.zk > 0.001f) ci.setReturnValue(ci.getReturnValueF() * (1f - Wc.zk * Cfg.c.zs / 100f));
	}
}
