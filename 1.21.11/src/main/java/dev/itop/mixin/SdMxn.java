package dev.itop.mixin;

import dev.itop.Op;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext", remap = false)
public abstract class SdMxn {
	@Shadow(remap = false)
	protected BlockAndTintGetter level;
	@Shadow(remap = false)
	protected BlockState state;
	@Shadow(remap = false)
	protected BlockPos pos;

	@Inject(method = "isFaceCulled", at = @At("RETURN"), cancellable = true, remap = false)
	private void fc(Direction d, CallbackInfoReturnable<Boolean> ci) {
		if (Op.any && ci.getReturnValueZ() && state != null && (Op.of(state) < 100 || Op.oc && Op.of(level.getBlockState(pos.relative(d))) < 100)) ci.setReturnValue(false);
	}
}
