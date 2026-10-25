package dev.wscl.mixin;

import dev.wscl.Ns;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public abstract class NsMxn {
	@Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
	private void wsp(SoundInstance s, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
		if (Ns.mute(s)) cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
	}
}
