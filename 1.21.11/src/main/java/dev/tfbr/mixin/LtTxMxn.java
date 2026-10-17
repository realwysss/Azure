package dev.tfbr.mixin;

import com.mojang.blaze3d.buffers.Std140Builder;
import dev.tfbr.Cfg;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LtTxMxn {
	private static final String PF = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;";

	@Shadow
	private boolean updateLightTexture;

	private float lb = -1f;

	@Inject(method = "updateLightTexture", at = @At("HEAD"))
	private void chk(float f, CallbackInfo ci) {
		float b = Cfg.on() ? Cfg.bst() : 0f;
		if (b != lb) {
			lb = b;
			updateLightTexture = true;
		}
	}

	@ModifyArg(method = "updateLightTexture", at = @At(value = "INVOKE", target = PF, ordinal = 4), index = 0)
	private float drk(float v) {
		return Cfg.on() ? 0f : v;
	}

	@ModifyArg(method = "updateLightTexture", at = @At(value = "INVOKE", target = PF, ordinal = 5), index = 0)
	private float wrld(float v) {
		return Cfg.on() ? 0f : v;
	}

	@ModifyArg(method = "updateLightTexture", at = @At(value = "INVOKE", target = PF, ordinal = 6), index = 0)
	private float brt(float v) {
		return Cfg.on() ? Math.max(v, Cfg.bst()) : v;
	}
}
