package dev.crgl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;

public class Em {
	static final Map<Integer, double[]> seen = new HashMap<>();
	static final ArrayList<double[]> gh = new ArrayList<>();
	static Object lvl;

	public static void gather() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return;
		if (lvl != m.level) {
			lvl = m.level;
			seen.clear();
			gh.clear();
		}
		boolean in = Gk.li == 1;
		long now = System.currentTimeMillis();
		long life = Cfg.c.dur * 100L;
		Map<Integer, double[]> cur = new HashMap<>();
		for (Entity e : m.level.entitiesForRendering()) {
			if (!(e instanceof EndCrystal) || e.isRemoved()) continue;
			Vec3 p = e.getPosition(Gk.pt);
			cur.put(e.getId(), new double[]{p.x, p.y + 1.0, p.z});
			Gk.add(p.x, p.y + 1.0, p.z, in ? 1.7f : 3.4f, Gk.cur.col, 1f, e.getId());
		}
		if (life > 0) {
			for (Iterator<Map.Entry<Integer, double[]>> it = seen.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<Integer, double[]> en = it.next();
				if (!cur.containsKey(en.getKey())) gh.add(new double[]{en.getValue()[0], en.getValue()[1], en.getValue()[2], now, en.getKey() ^ 0x2f0b9a3});
			}
		}
		seen.clear();
		seen.putAll(cur);
		for (int i = gh.size() - 1; i >= 0; i--) {
			double[] h = gh.get(i);
			float f = 1f - (now - (long) h[3]) / (float) Math.max(1L, life);
			if (f <= 0f || life == 0) {
				gh.remove(i);
				continue;
			}
			Gk.add(h[0], h[1], h[2], in ? 1.7f : 3.4f, Gk.cur.col, f * f, (int) h[4]);
		}
	}
}
