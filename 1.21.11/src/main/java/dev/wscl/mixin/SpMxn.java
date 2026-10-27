package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class SpMxn {
	private static boolean no(Entity e, ParticleOptions o) {
		return o.getType() == ParticleTypes.TOTEM_OF_UNDYING && e == Minecraft.getInstance().player && Mod.live("nstp");
	}

	@Inject(method = "createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;)V", at = @At("HEAD"), cancellable = true)
	private void wse(Entity e, ParticleOptions o, CallbackInfo ci) {
		if (no(e, o)) ci.cancel();
	}

	@Inject(method = "createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V", at = @At("HEAD"), cancellable = true)
	private void wsf(Entity e, ParticleOptions o, int n, CallbackInfo ci) {
		if (no(e, o)) ci.cancel();
	}
}
