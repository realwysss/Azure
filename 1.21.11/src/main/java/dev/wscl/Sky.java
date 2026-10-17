package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class Sky {
	static final List<Identifier> ids = new ArrayList<>();
	static String sig = "";
	static Object mgr;
	static Method add;
	static Method rem;
	static int tk;
	static final double[][] EUL = {{55, 0, 0}, {-55, 0, 0}, {0, 0, 55}, {0, 0, -55}};
	public static String last = "";

	static boolean ready() {
		if (mgr != null) return true;
		if (!FabricLoader.getInstance().isModLoaded("nuit")) return false;
		try {
			Class<?> k = Class.forName("me.flashyreese.mods.nuit.SkyboxManager");
			mgr = k.getMethod("getInstance").invoke(null);
			add = k.getMethod("addSkybox", Identifier.class, JsonObject.class);
			rem = k.getMethod("removeSkybox", Identifier.class);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	static int gi(String k, int d) {
		return Cfg.gi("sky." + k, d);
	}

	static String signature() {
		StringBuilder b = new StringBuilder();
		for (String k : new String[]{"col", "cc", "au", "am", "as", "st", "sa", "fs", "ff", "bg"}) b.append(gi(k, -1)).append(',');
		if (gi("col", 0) == 0) b.append(Hud.ac() & 0xFFFFFF);
		return b.toString();
	}

	public static void tick() {
		if (tk++ % 20 != 0) return;
		Minecraft m = Minecraft.getInstance();
		boolean on = Mod.live("sky") && m.level != null && ready();
		String s = on ? signature() : "";
		if (s.equals(sig)) return;
		sig = s;
		clear();
		if (on) build();
	}

	static void clear() {
		if (!ready()) {
			ids.clear();
			return;
		}
		for (Identifier i : ids) {
			try {
				rem.invoke(mgr, i);
			} catch (Throwable e) {
			}
		}
		ids.clear();
	}

	static JsonObject fade(int dur, int step, java.util.function.IntToDoubleFunction f) {
		JsonObject kf = new JsonObject();
		for (int t = 0; t < dur; t += step) kf.addProperty(String.valueOf(t), Math.round(f.applyAsDouble(t) * 1000) / 1000.0);
		JsonObject o = new JsonObject();
		o.addProperty("duration", dur);
		o.add("keyFrames", kf);
		return o;
	}

	static JsonObject props(int layer, JsonObject fade, double speed) {
		return props(layer, fade, speed, null);
	}

	static JsonObject props(int layer, JsonObject fade, double speed, double[] eul) {
		JsonObject p = new JsonObject();
		p.addProperty("layer", layer);
		if (fade != null) p.add("fade", fade);
		if (speed != 0) {
			JsonObject r = new JsonObject();
			r.addProperty("speed", speed);
			if (eul != null) {
				JsonObject ax = new JsonObject();
				JsonArray av = new JsonArray();
				for (double q : eul) av.add(q);
				ax.add("0", av);
				r.add("axis", ax);
			}
			p.add("rotation", r);
		}
		return p;
	}

	static JsonObject bl(String s) {
		JsonObject b = new JsonObject();
		b.addProperty("type", s);
		return b;
	}

	static JsonObject cond() {
		JsonObject c = new JsonObject();
		JsonArray w = new JsonArray();
		w.add("minecraft:overworld");
		JsonObject d = new JsonObject();
		d.add("entries", w);
		c.add("dimensions", d);
		return c;
	}

	static void put(String name, JsonObject o) {
		Identifier id = Identifier.fromNamespaceAndPath("azure", "sky_" + name);
		try {
			add.invoke(mgr, id, o);
			ids.add(id);
		} catch (Throwable e) {
			last = name + " " + e;
		}
	}

	static JsonObject color(int layer, float r, float g, float b, String blend) {
		JsonObject o = new JsonObject();
		o.addProperty("schemaVersion", 1);
		o.addProperty("type", "monocolor");
		JsonObject c = new JsonObject();
		c.addProperty("red", r);
		c.addProperty("green", g);
		c.addProperty("blue", b);
		c.addProperty("alpha", 1.0);
		o.add("color", c);
		o.add("blend", bl(blend));
		o.add("properties", props(layer, null, 0));
		o.add("conditions", cond());
		return o;
	}

	static JsonObject tex(String file, int layer, String blend, JsonObject fade, double speed) {
		return tex(file, layer, blend, fade, speed, null);
	}

	static JsonObject tex(String file, int layer, String blend, JsonObject fade, double speed, double[] eul) {
		JsonObject o = new JsonObject();
		o.addProperty("schemaVersion", 1);
		o.addProperty("type", "square-textured");
		o.addProperty("texture", "azure:textures/sky/" + file + ".png");
		o.add("blend", bl(blend));
		o.add("properties", props(layer, fade, speed, eul));
		o.add("conditions", cond());
		return o;
	}

	static void build() {
		int rgb = gi("col", 0) == 0 ? Hud.ac() & 0xFFFFFF : gi("cc", 0x8B5CF6) & 0xFFFFFF;
		float r = ((rgb >> 16) & 255) / 255f;
		float g = ((rgb >> 8) & 255) / 255f;
		float b = (rgb & 255) / 255f;
		float mx = Math.max(0.05f, Math.max(r, Math.max(g, b)));
		float tr = r / mx * 0.85f + 0.15f;
		float tg = g / mx * 0.85f + 0.15f;
		float tb = b / mx * 0.85f + 0.15f;
		float bgs = gi("bg", 22) / 100f;
		put("bg", color(0, Math.min(1f, r / mx * bgs + 0.02f), Math.min(1f, g / mx * bgs + 0.01f), Math.min(1f, b / mx * bgs + 0.05f), "normal"));
		double as = gi("as", 60) / 100.0;
		boolean mv = gi("am", 1) == 1;
		if (gi("au", 1) == 1) {
			put("au1", tex("aurora_a", 1, "add", fade(1200, 60, t -> as * (0.5 + 0.5 * (0.5 + 0.5 * Math.sin(t / 1200.0 * 6.2832 * 2)))), mv ? 0.6 : 0));
			put("au2", tex("aurora_b", 2, "add", fade(1800, 60, t -> as * (0.45 + 0.55 * (0.5 + 0.5 * Math.sin(t / 1800.0 * 6.2832 * 3 + 1.7)))), mv ? -0.4 : 0));
		}
		put("tint", color(3, tr, tg, tb, "multiply"));
		if (gi("st", 1) == 1) {
			int n = Math.max(1, Math.min(3, gi("sa", 3)));
			if (n >= 3) put("sta", tex("stars_a", 4, "add", fade(500, 20, t -> 0.55 + 0.45 * Math.sin(t / 500.0 * 6.2832 * 3)), 0));
			if (n >= 2) put("stb", tex("stars_b", 5, "add", fade(700, 20, t -> 0.6 + 0.4 * Math.sin(t / 700.0 * 6.2832 * 2 + 1.1)), 0));
			put("stc", tex("stars_c", 6, "add", fade(900, 30, t -> 0.75 + 0.25 * Math.sin(t / 900.0 * 6.2832 * 3 + 2.3)), 0));
		}
		if (gi("fs", 1) == 1) {
			int dur = Math.max(100, gi("ff", 12) * 20);
			double[] sp = {41, 41, 38, 38};
			for (int k = 0; k < 4; k++) {
				int off = (int) (dur * (k / 4.0));
				int o0 = off;
				JsonObject f = fadeP(dur, o0);
				put("fall" + k, tex("streak_" + k, 7 + k, "add", f, sp[k], EUL[k]));
			}
		}
	}

	static JsonObject fadeP(int dur, int off) {
		JsonObject kf = new JsonObject();
		java.util.TreeMap<Integer, Double> m = new java.util.TreeMap<>();
		m.put(0, 0.0);
		m.put(dur - 1, 0.0);
		int a = off;
		int[] ts = {a, a + 3, a + 20, a + 34};
		double[] vs = {0, 1, 0.7, 0};
		for (int i = 0; i < 4; i++) {
			int t = ts[i];
			if (t < dur) m.put(t, vs[i]);
			else if (i == 3) m.put(dur - 1, 0.0);
		}
		for (var e : m.entrySet()) kf.addProperty(String.valueOf(e.getKey()), e.getValue());
		JsonObject o = new JsonObject();
		o.addProperty("duration", dur);
		o.add("keyFrames", kf);
		return o;
	}
}
