package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Mod;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TotemParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class NpMxn {
	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void wsa(Particle p, CallbackInfo ci) {
		if (!Mod.live("noprt")) return;
		if (p instanceof TotemParticle && Cfg.gi("noprt.tot", 0) == 1) return;
		if (p instanceof net.minecraft.client.particle.CritParticle && Cfg.gi("noprt.crit", 1) == 1) return;
		ci.cancel();
	}
}
