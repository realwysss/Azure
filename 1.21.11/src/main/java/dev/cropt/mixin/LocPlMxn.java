package dev.cropt.mixin;

import dev.cropt.Co;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocPlMxn {
	@Inject(method = "tick", at = @At("HEAD"))
	private void tk(CallbackInfo ci) {
		Co.tick();
	}
}
