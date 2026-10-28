package dev.wscl.mixin;

import dev.wscl.Cfg;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public abstract class CwMxn {
	@Inject(method = "getChunkRenderType", at = @At("HEAD"), cancellable = true)
	private static void azcw(BlockState s, CallbackInfoReturnable<ChunkSectionLayer> ci) {
		if (s.is(Blocks.COBWEB) && Cfg.on("seecob")) ci.setReturnValue(ChunkSectionLayer.TRANSLUCENT);
	}
}
