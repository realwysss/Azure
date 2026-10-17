package dev.cropt.mixin;

import dev.cropt.Co;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItmStkMxn {
	@Inject(method = "getPopTime", at = @At("HEAD"), cancellable = true)
	private void pop(CallbackInfoReturnable<Integer> ci) {
		if (Co.active()) ci.setReturnValue(0);
	}
}
