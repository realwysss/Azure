package dev.itop;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class Wr extends WrapperBlockStateModel {
	public Wr(BlockStateModel w) {
		super(w);
	}

	@Override
	public void emitQuads(QuadEmitter e, BlockAndTintGetter v, BlockPos p, BlockState s, RandomSource r, Predicate<Direction> cull) {
		int o = Op.of(s);
		if (o >= 100) {
			wrapped.emitQuads(e, v, p, s, r, cull);
			return;
		}
		if (o <= 0) return;
		e.pushTransform(q -> {
			for (int i = 0; i < 4; i++) {
				int c = q.color(i);
				q.color(i, ((c >>> 24) * o / 100) << 24 | (c & 0xffffff));
			}
			q.renderLayer(ChunkSectionLayer.TRANSLUCENT);
			return true;
		});
		wrapped.emitQuads(e, v, p, s, r, d -> false);
		e.popTransform();
	}
}
