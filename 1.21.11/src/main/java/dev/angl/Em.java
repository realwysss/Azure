package dev.angl;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Em {
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
	}
}
