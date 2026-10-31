package dev.wscl.mixin;

import dev.wscl.Plc;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class PlMxn {
	@Inject(method = "useItemOn", at = @At("HEAD"))
	private void a(LocalPlayer p, InteractionHand h, BlockHitResult r, CallbackInfoReturnable<InteractionResult> ci) {
		Plc.pre(r);
	}

	@Inject(method = "useItemOn", at = @At("RETURN"))
	private void b(LocalPlayer p, InteractionHand h, BlockHitResult r, CallbackInfoReturnable<InteractionResult> ci) {
		Plc.post();
	}
}
