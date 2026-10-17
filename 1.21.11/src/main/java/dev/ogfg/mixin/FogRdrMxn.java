package dev.ogfg.mixin;

import dev.ogfg.Fg;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRdrMxn {
	@Inject(method = "setupFog", at = @At("HEAD"))
	private void a(Camera cam, int rd, DeltaTracker dt, float f, ClientLevel lvl, CallbackInfoReturnable<Vector4f> ci) {
		Fg.st(cam, rd, lvl);
	}

	@Inject(method = "setupFog", at = @At("RETURN"))
	private void b(Camera cam, int rd, DeltaTracker dt, float f, ClientLevel lvl, CallbackInfoReturnable<Vector4f> ci) {
		Fg.live = false;
		Vector4f v = ci.getReturnValue();
		if (v != null) {
			Fg.fr = v.x;
			Fg.fg = v.y;
			Fg.fb = v.z;
		}
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float d(float x) {
		return Fg.off(x, 0);
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	private float e(float x) {
		return Fg.off(x, 1);
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private float g(float x) {
		return Fg.off(x, 2);
	}

	@ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 3)
	private float h(float x) {
		return Fg.off(x, 3);
	}
}
