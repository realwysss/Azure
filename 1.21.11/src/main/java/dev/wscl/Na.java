package dev.wscl;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.texture.SpriteContents;

public class Na {
	public static final Map<SpriteContents.AnimationState, String> M = new WeakHashMap<>();

	public static boolean stop(SpriteContents.AnimationState s) {
		if (!Mod.live("noanim")) return false;
		String n = M.get(s);
		if (n == null) return false;
		String k = n.contains("water") ? "wat" : n.contains("lava") ? "lav" : n.contains("fire") ? "fir" : n.contains("portal") || n.contains("gateway") ? "por" : "oth";
		return Cfg.gi("noanim." + k, 1) == 1;
	}
}
