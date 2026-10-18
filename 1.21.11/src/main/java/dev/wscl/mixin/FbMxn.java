package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionInstance.class)
public abstract class FbMxn {
	@Inject(method = "get", at = @At("RETURN"), cancellable = true)
	private void wsf(CallbackInfoReturnable<Object> ci) {
		Minecraft m = Minecraft.getInstance();
		if (m == null || m.options == null || !Mod.live("tuffbright") || dev.wscl.Cfg.gi("tuffbright.fb", 0) != 1) return;
		if ((Object) this == m.options.gamma()) ci.setReturnValue(15.0);
	}
}
