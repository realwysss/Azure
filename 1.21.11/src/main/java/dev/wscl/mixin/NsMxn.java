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
public abstract class NsMxn {
	@Inject(method = "createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V", at = @At("HEAD"), cancellable = true)
	private void wsp(Entity e, ParticleOptions p, int n, CallbackInfo ci) {
		if (!Mod.live("nstp") || p != ParticleTypes.TOTEM_OF_UNDYING) return;
		var pl = Minecraft.getInstance().player;
		if (pl != null && e.getId() == pl.getId()) ci.cancel();
	}

	@Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
	private void wsd(net.minecraft.core.BlockPos p, net.minecraft.world.level.block.state.BlockState s, CallbackInfo ci) {
		if (dev.wscl.Fps.noBreak()) ci.cancel();
	}

	@Inject(method = "crack", at = @At("HEAD"), cancellable = true)
	private void wsk(net.minecraft.core.BlockPos p, net.minecraft.core.Direction d, CallbackInfo ci) {
		if (dev.wscl.Fps.noBreak()) ci.cancel();
	}
}
