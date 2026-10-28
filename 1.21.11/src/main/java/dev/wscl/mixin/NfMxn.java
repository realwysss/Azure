package dev.wscl.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.wscl.Cfg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class NfMxn {
	@Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
	private static void nofr(PoseStack p, MultiBufferSource b, TextureAtlasSprite s, CallbackInfo ci) {
		if (Cfg.on("nofire")) ci.cancel();
	}
}
