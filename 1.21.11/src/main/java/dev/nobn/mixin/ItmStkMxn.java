package dev.nobn.mixin;

import dev.nobn.Cfg;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItmStkMxn {
	@Inject(method = "getPopTime", at = @At("HEAD"), cancellable = true)
	private void pop(CallbackInfoReturnable<Integer> ci) {
		if (Cfg.c.nb) ci.setReturnValue(0);
	}
}
