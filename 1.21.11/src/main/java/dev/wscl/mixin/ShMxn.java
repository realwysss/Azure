package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class ShMxn {
	@Inject(method = "getShadowRadius", at = @At("HEAD"), cancellable = true)
	private void wsh(EntityRenderState s, CallbackInfoReturnable<Float> cir) {
		if (Mod.live("noshadow")) cir.setReturnValue(0f);
	}
}
