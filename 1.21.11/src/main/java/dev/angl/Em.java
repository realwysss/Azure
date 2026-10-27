package dev.angl;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Em {
	static final java.util.ArrayList<double[]> gh = new java.util.ArrayList<>();

	static float kk(int c) {
		return c == 0 ? 0.25f : Cfg.c.chg ? 0.4f + 0.6f * c / 4f : 1f;
	}

	public static void ghost(BlockPos p, int c) {
		if (Cfg.c.dur > 0) gh.add(new double[]{p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, System.currentTimeMillis(), c, p.hashCode() ^ 0x3c6ef35f});
	}

	public static void gather() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return;
		if (Anc.lvl != m.level) {
			Anc.clear();
			Anc.lvl = m.level;
		}
		Iterator<Map.Entry<Long, Integer>> it = Anc.map.entrySet().iterator();
		while (it.hasNext()) {
			long l = it.next().getKey();
			BlockState b = m.level.getBlockState(BlockPos.of(l));
			if (!b.is(Blocks.RESPAWN_ANCHOR)) {
				it.remove();
				continue;
			}
			int c = b.getValue(RespawnAnchorBlock.CHARGE);
			if (c == 0 && !Cfg.c.emp) continue;
			float k = c == 0 ? 0.25f : Cfg.c.chg ? 0.4f + 0.6f * c / 4f : 1f;
			Gk.add(BlockPos.getX(l) + 0.5, BlockPos.getY(l) + 0.5, BlockPos.getZ(l) + 0.5, 2.8f, Gk.cur.col, k, (int) l);
		}
		long now = System.currentTimeMillis();
		long life = Cfg.c.dur * 100L;
		for (int i = gh.size() - 1; i >= 0; i--) {
			double[] h = gh.get(i);
			float f = 1f - (now - (long) h[3]) / (float) Math.max(1L, life);
			if (f <= 0f || life == 0) {
				gh.remove(i);
				continue;
			}
			int c = (int) h[4];
			if (c == 0 && !Cfg.c.emp) continue;
			Gk.add(h[0], h[1], h[2], 2.8f, Gk.cur.col, kk(c) * f * f, (int) h[5]);
		}
	}
}
