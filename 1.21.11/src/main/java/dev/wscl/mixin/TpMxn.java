package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Mod;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ScreenEffectRenderer.class)
public abstract class TpMxn {
	@ModifyArgs(method = "renderItemActivationAnimation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
	private void sz(Args a) {
		if (!Mod.live("totpop")) return;
		float k = Cfg.gi("totpop.sz", 60) / 100f;
		a.set(0, (float) a.get(0) * k);
		a.set(1, (float) a.get(1) * k);
		a.set(2, (float) a.get(2) * k);
	}
}
