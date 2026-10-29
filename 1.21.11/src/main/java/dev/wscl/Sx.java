package dev.wscl;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class Sx {
	static int tk;
	static String last;
	static boolean ps;
	static final String[] MK = {"key.azure.", "key.blurreborn", "key.frametrim", "key.glowlib", "key.hitcolor", "key.hitglow", "key.itemsopacity", "key.klfx", "key.nobounce", "key.noeb", "key.ogfog", "key.oldpotions", "key.snprb", "key.sprswp", "key.tuffbright", "key.tuffglint", "key.totemparticleglow", "key.anchorglow", "key.crystalglow", "key.xpglow"};

	static boolean mine(String n) {
		if (n.equals("key.azure.open")) return false;
		for (String p : MK) if (n.startsWith(p)) return true;
		return false;
	}

	public static void tick(Minecraft m) {
		if (m.options == null) return;
		if (last == null) last = Cfg.js();
		if (Cfg.gi("kbreset", 0) == 0) {
			for (KeyMapping k : m.options.keyMappings) if (mine(k.getName())) k.setKey(InputConstants.UNKNOWN);
			KeyMapping.resetMapping();
			m.options.save();
			Cfg.si("kbreset", 1);
			Cfg.sv();
		}
		if (Cfg.gi("fx108", 0) == 0) {
			java.util.Set<String> rs = java.util.Set.of("nopd", "nosd", "wcrys", "wanch");
			for (Mod d : Mod.ALL) {
				try {
					if ((d.ct == 4 || rs.contains(d.id)) && d.on.getAsBoolean()) d.set.accept(false);
				} catch (Throwable e) {
				}
			}
			dev.tpgl.Cfg.c.pc = false;
			dev.tpgl.Cfg.sv();
			Cfg.si("fx108", 1);
			Cfg.sv();
		}
		if (Cfg.gi("tg0", 0) == 0) {
			Mods.tgSet(false);
			Cfg.si("tg0", 1);
			Cfg.sv();
		}
		if (!ps && m.getOverlay() == null) {
			ps = true;
			Tx.sync();
			Mods.init();
			Ic.ld();
		}
		if (++tk % 200 == 0) {
			Cfg.si("acc", dev.wysl.Thm.get() & 0xFFFFFF);
			String j = Cfg.js();
			if (!j.equals(last)) {
				last = j;
				Cfg.sv();
			}
			all();
		}
	}

	static final com.google.gson.Gson G = new com.google.gson.Gson();
	static final java.util.Map<Class<?>, String> SNAP = new java.util.HashMap<>();

	static void sv(Class<?> k, String f, Runnable save, boolean force) {
		String j = "";
		try {
			java.lang.reflect.Field fd = k.getDeclaredField(f);
			fd.setAccessible(true);
			j = G.toJson(fd.get(null));
		} catch (Throwable e) {
			j = "";
		}
		if (!force && !j.isEmpty() && j.equals(SNAP.get(k))) return;
		SNAP.put(k, j);
		save.run();
	}

	static void all() {
		all(false);
	}

	static void all(boolean force) {
		sv(dev.angl.Cfg.class, "c", dev.angl.Cfg::sv, force);
		sv(dev.blrb.Cfg.class, "c", dev.blrb.Cfg::sv, force);
		sv(dev.crgl.Cfg.class, "c", dev.crgl.Cfg::sv, force);
		sv(dev.frbs.Cfg.class, "c", dev.frbs.Cfg::save, force);
		sv(dev.frtr.Cfg.class, "c", dev.frtr.Cfg::sv, force);
		sv(dev.htcl.Cfg.class, "c", dev.htcl.Cfg::sv, force);
		sv(dev.htgl.Cfg.class, "c", dev.htgl.Cfg::sv, force);
		sv(dev.itop.Cfg.class, "c", dev.itop.Cfg::sv, force);
		sv(dev.klfx.Cfg.class, "c", dev.klfx.Cfg::sv, force);
		sv(dev.nobn.Cfg.class, "c", dev.nobn.Cfg::sv, force);
		sv(dev.noeb.Cfg.class, "c", dev.noeb.Cfg::sv, force);
		sv(dev.ogfg.Cfg.class, "c", dev.ogfg.Cfg::sv, force);
		sv(dev.oldp.Cfg.class, "c", dev.oldp.Cfg::sv, force);
		sv(dev.tfbr.Cfg.class, "c", dev.tfbr.Cfg::sv, force);
		sv(dev.tfgl.Cfg.class, "c", dev.tfgl.Cfg::sv, force);
		sv(dev.tpgl.Cfg.class, "c", dev.tpgl.Cfg::sv, force);
		sv(dev.xpgl.Cfg.class, "c", dev.xpgl.Cfg::sv, force);
		sv(dev.snprb.cfg.class, "c", dev.snprb.cfg::sv, force);
		sv(hits.fire.Config.class, "now", hits.fire.Config::save, force);
	}

	public static void stop() {
		Cfg.sv();
		all(true);
	}
}
