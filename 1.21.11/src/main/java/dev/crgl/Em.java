package dev.crgl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;

public class Em {
	public static void gather() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return;
		boolean in = Gk.li == 1;
		for (Entity e : m.level.entitiesForRendering()) {
			if (!(e instanceof EndCrystal) || e.isRemoved()) continue;
			Vec3 p = e.getPosition(Gk.pt);
			Gk.add(p.x, p.y + 1.0, p.z, in ? 1.7f : 3.4f, Gk.cur.col, 1f, e.getId());
		}
	}
}
