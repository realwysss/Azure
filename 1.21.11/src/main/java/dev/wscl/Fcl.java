package dev.wscl;

import hits.fire.Config;

public class Fcl {
	static Config sv;
	static boolean gl, lg;

	public static boolean glow() {
		return sv != null ? gl : Config.get().enabled;
	}

	public static void glow(boolean v) {
		if (sv != null) {
			gl = v;
			sv.enabled = v;
		} else Config.get().enabled = v;
	}

	public static Config saved() {
		return sv != null ? sv : Config.get();
	}

	public static void tick() {
		Config c = Config.get();
		if (Cfg.on("firecolor")) {
			int f = Cfg.gi("firecolor.fc", 0xFF7A1F) & 0xFFFFFF;
			int s = Cfg.gi("firecolor.sc", 0x3FD8FF) & 0xFFFFFF;
			if (sv == null) {
				sv = c.copy();
				gl = c.enabled;
				lg = !gl;
			}
			c.enabled = true;
			c.recolor = true;
			c.fire.on = true;
			c.soul.on = true;
			c.fire.color = f;
			c.soul.color = s;
			sv.fire.color = f;
			sv.soul.color = s;
			if (!gl) {
				c.screen = true;
				c.fire.radius = 0f;
				c.soul.radius = 0f;
				c.fire.strength = 1f;
				c.soul.strength = 1f;
			} else if (!lg) {
				c.screen = sv.screen;
				c.fire.radius = sv.fire.radius;
				c.soul.radius = sv.soul.radius;
				c.fire.strength = sv.fire.strength;
				c.soul.strength = sv.soul.strength;
			}
			lg = gl;
		} else if (sv != null) {
			Config.set(sv);
			sv = null;
		}
	}
}
