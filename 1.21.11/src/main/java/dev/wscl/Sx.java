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
		if (!ps && m.getOverlay() == null) {
			ps = true;
			Tx.sync();
		}
		if (++tk % 200 == 0) {
			String j = Cfg.js();
			if (!j.equals(last)) {
				last = j;
				Cfg.sv();
			}
		}
	}

	public static void stop() {
		Cfg.sv();
	}
}
