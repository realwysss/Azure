package dev.htgl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Em {
	static final java.util.Map<Integer, long[]> hit = new java.util.HashMap<>();

	public static void gather() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return;
		Cfg c = Cfg.c;
		long now = System.currentTimeMillis();
		long ex = c.dur * 100L;
		if (hit.size() > 64) hit.clear();
		for (Entity e : m.level.entitiesForRendering()) {
			if (!(e instanceof LivingEntity l) || l.hurtTime <= 0 || l.deathTime > 0 || e.isRemoved()) continue;
			boolean me = e == m.player;
			if (me ? !c.sf : e instanceof Player ? !c.pl : !c.mb) continue;
			float k = Math.max(0f, l.hurtTime - Gk.pt) / Math.max(1, l.hurtDuration);
			if (ex > 0) {
				long[] t = hit.computeIfAbsent(e.getId(), z -> new long[]{now, 0});
				if (l.hurtTime > t[1]) t[0] = now;
				t[1] = l.hurtTime;
				float r = 1f - (now - t[0]) / (float) (500L + ex);
				k = (float) Math.pow(Math.max(0f, r), 1.5);
			}
			Vec3 p = e.getPosition(Gk.pt);
			float h = e.getBbHeight();
			Gk.add(p.x, p.y + h * 0.5, p.z, h * 1.9f + 0.6f, Gk.cur.col, k, e.getId());
		}
		if (ex > 0) {
			for (Entity e : m.level.entitiesForRendering()) {
				long[] t = hit.get(e.getId());
				if (t == null || !(e instanceof LivingEntity l) || l.hurtTime > 0 || l.deathTime > 0 || e.isRemoved()) continue;
				boolean me = e == m.player;
				if (me ? !c.sf : e instanceof Player ? !c.pl : !c.mb) continue;
				float r = 1f - (now - t[0]) / (float) (500L + ex);
				if (r <= 0f) {
					hit.remove(e.getId());
					continue;
				}
				Vec3 p = e.getPosition(Gk.pt);
				float h = e.getBbHeight();
				Gk.add(p.x, p.y + h * 0.5, p.z, h * 1.9f + 0.6f, Gk.cur.col, (float) Math.pow(r, 1.5), e.getId());
			}
		}
	}
}
