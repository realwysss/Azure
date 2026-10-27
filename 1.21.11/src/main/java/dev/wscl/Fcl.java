package dev.wscl;

import hits.fire.Config;

public class Fcl {
	static Config sv;

	public static void tick() {
		Config c = Config.get();
		if (Cfg.on("firecolor")) {
			int f = Cfg.gi("firecolor.fc", 0xFF7A1F) & 0xFFFFFF;
			int s = Cfg.gi("firecolor.sc", 0x3FD8FF) & 0xFFFFFF;
			if (sv == null) sv = c.copy();
			c.enabled = true;
			c.recolor = true;
			c.screen = true;
			c.fire.on = true;
			c.soul.on = true;
			c.fire.color = f;
			c.soul.color = s;
			c.fire.radius = 0f;
			c.soul.radius = 0f;
			c.fire.strength = 1f;
			c.soul.strength = 1f;
		} else if (sv != null) {
			Config.set(sv);
			sv = null;
		}
	}
}
