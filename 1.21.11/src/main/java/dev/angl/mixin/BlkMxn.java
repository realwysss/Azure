package dev.angl.mixin;

import dev.angl.Anc;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class BlkMxn {
	@Inject(method = "sendBlockUpdated", at = @At("HEAD"))
	private void blk(BlockPos p, BlockState o, BlockState n, int f, CallbackInfo ci) {
		Anc.upd(p, n);
	}
}
