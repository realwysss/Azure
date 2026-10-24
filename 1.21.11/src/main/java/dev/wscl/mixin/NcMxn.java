package dev.wscl.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.wscl.Na;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteContents.class)
public abstract class NcMxn {
	@Inject(method = "createAnimationState", at = @At("RETURN"))
	private void wsc(GpuBufferSlice b, int i, CallbackInfoReturnable<SpriteContents.AnimationState> ci) {
		if (ci.getReturnValue() != null) Na.M.put(ci.getReturnValue(), ((SpriteContents) (Object) this).name().getPath());
	}
}
