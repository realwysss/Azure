package dev.tpgl.mixin;

import dev.tpgl.Tt;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TotemParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class PrtEngMxn {
	@Inject(method = "add", at = @At("HEAD"))
	private void add(Particle p, CallbackInfo ci) {
		if (p instanceof TotemParticle) Tt.add(p);
	}
}
