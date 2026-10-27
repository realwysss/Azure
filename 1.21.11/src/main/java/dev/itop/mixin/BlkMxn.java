package dev.itop.mixin;

import dev.itop.Op;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlkMxn {
	@Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
	private static void sf(BlockState a, BlockState b, Direction d, CallbackInfoReturnable<Boolean> ci) {
		if (Op.any && (Op.of(a) < 100 || Op.oc && Op.of(b) < 100)) ci.setReturnValue(true);
	}
}
