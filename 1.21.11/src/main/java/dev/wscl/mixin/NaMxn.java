package dev.wscl.mixin;

import dev.wscl.Na;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteContents.AnimationState.class)
public abstract class NaMxn {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void wst(CallbackInfo ci) {
		if (Na.stop((SpriteContents.AnimationState) (Object) this)) ci.cancel();
	}
}
