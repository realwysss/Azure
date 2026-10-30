package dev.wscl.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.wscl.Bhl;
import dev.wscl.Mod;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class BhMxn {
	@Inject(method = "renderBlockOutline", at = @At("HEAD"), cancellable = true)
	private void bhl(MultiBufferSource.BufferSource bs, PoseStack ps, boolean tr, LevelRenderState st, CallbackInfo ci) {
		if (!Mod.live("bhl")) return;
		ci.cancel();
		BlockOutlineRenderState o = st.blockOutlineRenderState;
		if (o == null || o.isTranslucent() != tr) return;
		Bhl.draw(bs, ps, o, st.cameraRenderState.pos);
	}
}
