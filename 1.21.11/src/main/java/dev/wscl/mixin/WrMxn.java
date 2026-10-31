package dev.wscl.mixin;

import dev.wscl.Rn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class WrMxn {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void rn(MultiBufferSource bs, Vec3 cam, WeatherRenderState st, CallbackInfo ci) {
		if (Rn.hide()) ci.cancel();
	}

	@Redirect(method = "tickRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private void rp(net.minecraft.client.multiplayer.ClientLevel l, net.minecraft.core.particles.ParticleOptions o, double x, double y, double z, double a, double b, double c) {
		if (o == net.minecraft.core.particles.ParticleTypes.RAIN && Rn.hide()) return;
		l.addParticle(o, x, y, z, a, b, c);
	}
}
