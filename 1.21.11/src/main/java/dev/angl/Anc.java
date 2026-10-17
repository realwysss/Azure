package dev.angl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class Anc {
	public static final Map<Long, Integer> map = new HashMap<>();
	public static Object lvl;
	static final Map<Long, ArrayList<Long>> by = new HashMap<>();

	static long ck(int x, int z) {
		return (long) (x >> 4) & 0xffffffffL | ((long) (z >> 4) & 0xffffffffL) << 32;
	}

	public static void load(ClientLevel l, LevelChunk c) {
		ChunkPos cp = c.getPos();
		LevelChunkSection[] ss = c.getSections();
		ArrayList<Long> ls = null;
		for (int i = 0; i < ss.length; i++) {
			LevelChunkSection s = ss[i];
			if (s == null || s.hasOnlyAir() || !s.maybeHas(b -> b.is(Blocks.RESPAWN_ANCHOR))) continue;
			int y0 = (l.getMinSectionY() + i) << 4;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					for (int z = 0; z < 16; z++) {
						BlockState b = s.getBlockState(x, y, z);
						if (!b.is(Blocks.RESPAWN_ANCHOR)) continue;
						long k = BlockPos.asLong(cp.getMinBlockX() + x, y0 + y, cp.getMinBlockZ() + z);
						map.put(k, b.getValue(RespawnAnchorBlock.CHARGE));
						if (ls == null) ls = new ArrayList<>();
						ls.add(k);
					}
				}
			}
		}
		if (ls != null) by.put(ck(cp.getMinBlockX(), cp.getMinBlockZ()), ls);
	}

	public static void unload(ClientLevel l, LevelChunk c) {
		ArrayList<Long> ls = by.remove(ck(c.getPos().getMinBlockX(), c.getPos().getMinBlockZ()));
		if (ls != null) for (long k : ls) map.remove(k);
	}

	public static void upd(BlockPos p, BlockState s) {
		long k = p.asLong();
		if (s.is(Blocks.RESPAWN_ANCHOR)) {
			if (map.put(k, s.getValue(RespawnAnchorBlock.CHARGE)) == null) by.computeIfAbsent(ck(p.getX(), p.getZ()), z -> new ArrayList<>()).add(k);
		} else if (map.remove(k) != null) {
			ArrayList<Long> ls = by.get(ck(p.getX(), p.getZ()));
			if (ls != null) ls.remove(k);
		}
	}

	public static void clear() {
		map.clear();
		by.clear();
	}
}
