package dev.wscl.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.wscl.Lv;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteContents.class)
public abstract class AniMxn {
	@Inject(method = "createAnimationState", at = @At("RETURN"))
	private void azani(GpuBufferSlice s, int i, CallbackInfoReturnable<SpriteContents.AnimationState> ci) {
		if (ci.getReturnValue() != null) Lv.ANI.put((SpriteContents) (Object) this, ci.getReturnValue());
	}
}
