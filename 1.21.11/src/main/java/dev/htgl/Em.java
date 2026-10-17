package dev.htgl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Em {
	public static void gather() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return;
		Cfg c = Cfg.c;
		for (Entity e : m.level.entitiesForRendering()) {
			if (!(e instanceof LivingEntity l) || l.hurtTime <= 0 || l.deathTime > 0 || e.isRemoved()) continue;
			boolean me = e == m.player;
			if (me ? !c.sf : e instanceof Player ? !c.pl : !c.mb) continue;
			float k = Math.max(0f, l.hurtTime - Gk.pt) / Math.max(1, l.hurtDuration);
			Vec3 p = e.getPosition(Gk.pt);
			float h = e.getBbHeight();
			Gk.add(p.x, p.y + h * 0.5, p.z, h * 1.9f + 0.6f, Gk.cur.col, k, e.getId());
		}
	}
}
