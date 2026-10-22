package dev.wscl;

import net.minecraft.client.Minecraft;

public class Tc {
	static double cyc;
	public static long now = 6000;
	static final int[] PR = {0, 23000, 1000, 6000, 12500, 13500, 18000};

	public static void tick(Minecraft m) {
		if (!Mod.live("time") || m.level == null) return;
		int p = Cfg.gi("time.pr", 2);
		long base = p > 0 && p < PR.length ? PR[p] : Cfg.gi("time.t", 6000);
		if (Cfg.gi("time.mode", 0) == 1) cyc += Cfg.gi("time.sp", 20) / 20.0;
		else cyc = 0;
		long t = ((base + (long) cyc) % 24000L + 24000L) % 24000L;
		now = t;
		m.level.setTimeFromServer(m.level.getGameTime(), t, false);
	}
}
