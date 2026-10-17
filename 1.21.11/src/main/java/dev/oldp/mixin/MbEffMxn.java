package dev.oldp.mixin;

import dev.oldp.Cfg;
import dev.oldp.OldP;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffect.class)
public abstract class MbEffMxn {
	@Inject(method = "getColor", at = @At("RETURN"), cancellable = true)
	private void col(CallbackInfoReturnable<Integer> ci) {
		if (Cfg.c.col) ci.setReturnValue(OldP.old((MobEffect) (Object) this, ci.getReturnValue()));
	}
}
