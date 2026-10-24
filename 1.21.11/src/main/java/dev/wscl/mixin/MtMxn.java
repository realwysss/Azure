package dev.wscl.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MtMxn {
	@Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
	private void wst(CallbackInfoReturnable<String> ci) {
		ci.setReturnValue("Azure Client");
	}
}
