package dev.wscl;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.level.ParticleStatus;

public class Fps {
	static String sig = "";
	static boolean act;
	static int pr = -1;
	static int tk;
	static final int[][] PS = {
		{12, 8, 1, 1, 0, 1, 100, 1, 0, 1, 100, 1, 1, 0, 0, 0},
		{8, 6, 2, 1, 1, 1, 75, 1, 1, 1, 80, 1, 1, 1, 0, 0},
		{5, 5, 2, 1, 1, 1, 50, 1, 1, 1, 60, 1, 1, 1, 0, 0}
	};
	static final String[] K = {"rd", "sd", "pt", "cl", "ao", "bb", "ed", "sh", "mm", "ft", "ftd", "ftp", "ftb", "bk", "fb", "fy"};

	static int g(String k, int d) {
		return Cfg.gi("fpsboost." + k, d);
	}

	static void sv(String k, int v) {
		Cfg.si("fpsboost." + k, v);
	}

	public static void tick(boolean force) {
		if (!force && tk++ % 20 != 0) return;
		Minecraft m = Minecraft.getInstance();
		boolean on = Cfg.on("fpsboost");
		int p = g("pr", 1);
		if (on && p != pr && p > 0) {
			int[] v = PS[p - 1];
			for (int i = 0; i < K.length; i++) sv(K[i], v[i]);
		}
		pr = p;
		StringBuilder b = new StringBuilder(on ? "1" : "0");
		for (String k : K) b.append(',').append(g(k, -1));
		String s = b.toString();
		if (s.equals(sig)) return;
		sig = s;
		restore(m);
		if (on) apply(m);
		sync(on);
	}

	static void save(Options o) {
		if (Cfg.gi("fpsboost.o.saved", 0) == 1) return;
		sv("o.rd", o.renderDistance().get());
		sv("o.sd", o.simulationDistance().get());
		sv("o.pt", o.particles().get().ordinal());
		sv("o.cl", o.cloudStatus().get().ordinal());
		sv("o.ao", o.ambientOcclusion().get() ? 1 : 0);
		sv("o.bb", o.biomeBlendRadius().get());
		sv("o.ed", (int) Math.round(o.entityDistanceScaling().get() * 100));
		sv("o.sh", o.entityShadows().get() ? 1 : 0);
		sv("o.mm", o.mipmapLevels().get());
		sv("o.saved", 1);
	}

	static void restore(Minecraft m) {
		if (Cfg.gi("fpsboost.o.saved", 0) != 1) return;
		Options o = m.options;
		o.renderDistance().set(g("o.rd", 12));
		o.simulationDistance().set(g("o.sd", 12));
		o.particles().set(ParticleStatus.values()[Math.max(0, Math.min(2, g("o.pt", 0)))]);
		o.cloudStatus().set(CloudStatus.values()[Math.max(0, Math.min(CloudStatus.values().length - 1, g("o.cl", 2)))]);
		o.ambientOcclusion().set(g("o.ao", 1) == 1);
		o.biomeBlendRadius().set(g("o.bb", 2));
		o.entityDistanceScaling().set(g("o.ed", 100) / 100.0);
		o.entityShadows().set(g("o.sh", 1) == 1);
		o.mipmapLevels().set(g("o.mm", 4));
		sv("o.saved", 0);
		o.save();
	}

	static void apply(Minecraft m) {
		Options o = m.options;
		save(o);
		int rd = g("rd", 12);
		int sd = g("sd", 8);
		if (rd > 0 && rd < o.renderDistance().get()) o.renderDistance().set(rd);
		if (sd > 0 && sd < o.simulationDistance().get()) o.simulationDistance().set(sd);
		int pt = g("pt", 1);
		if (pt == 1) o.particles().set(ParticleStatus.DECREASED);
		if (pt == 2) o.particles().set(ParticleStatus.MINIMAL);
		if (g("cl", 1) == 1) o.cloudStatus().set(CloudStatus.OFF);
		if (g("ao", 0) == 1) o.ambientOcclusion().set(false);
		if (g("bb", 1) == 1) o.biomeBlendRadius().set(0);
		int ed = g("ed", 100);
		if (ed < 100) o.entityDistanceScaling().set(ed / 100.0);
		if (g("sh", 1) == 1) o.entityShadows().set(false);
		if (g("mm", 0) == 1) o.mipmapLevels().set(0);
		o.save();
	}

	static void sync(boolean on) {
		try {
			var c = dev.frtr.Cfg.c;
			boolean ft = on && g("ft", 1) == 1;
			c.on = ft;
			c.ent = ft;
			c.prt = on && g("ftp", 1) == 1;
			c.blk = on && g("ftb", 1) == 1;
			float f = g("ftd", 100) / 100f;
			c.di = Math.round(32 * f);
			c.dx = Math.round(24 * f);
			c.dp = Math.round(64 * f);
			c.dd = Math.round(64 * f);
			c.dm = Math.round(80 * f);
			c.dpl = Math.round(256 * f);
			c.dot = Math.round(128 * f);
			c.pd = Math.round(48 * f);
			c.bd = Math.round(48 * f);
			dev.frtr.Cfg.sv();
			var b = dev.frbs.Cfg.c;
			b.rthr = on && g("fb", 0) == 1;
			b.noyld = on && g("fy", 0) == 1;
			b.prio = b.rthr ? 1 : 0;
			dev.frbs.Cfg.save();
			dev.frbs.FrBs.prio(b.prio);
			Thread.currentThread().setPriority(b.rthr ? Thread.MAX_PRIORITY : Thread.NORM_PRIORITY);
		} catch (Throwable e) {
		}
	}

	public static boolean noBreak() {
		return Cfg.on("fpsboost") && g("bk", 0) == 1;
	}
}
