package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class Prf {
	static final Map<String, Runnable> F = new LinkedHashMap<>();
	static final String[] PERS = {"uid", "fs", "ver", "ft", "api", "lv", "useen"};
	static final String[] KEEP = {"kbreset", "fx108", "hdc"};

	static {
		F.put("anchorglow.json", dev.angl.Cfg::ld);
		F.put("blurreborn.json", dev.blrb.Cfg::ld);
		F.put("crystalglow.json", dev.crgl.Cfg::ld);
		F.put("frameboost.json", dev.frbs.Cfg::load);
		F.put("frametrim.json", dev.frtr.Cfg::ld);
		F.put("hitcolor.json", dev.htcl.Cfg::ld);
		F.put("hitglow.json", dev.htgl.Cfg::ld);
		F.put("itemsopacity.json", dev.itop.Cfg::ld);
		F.put("klfx.json", dev.klfx.Cfg::ld);
		F.put("nobounce.json", dev.nobn.Cfg::ld);
		F.put("noeb.json", dev.noeb.Cfg::ld);
		F.put("ogfog.json", dev.ogfg.Cfg::ld);
		F.put("oldpotions.json", dev.oldp.Cfg::ld);
		F.put("snprb.json", dev.snprb.cfg::ld);
		F.put("tuffbright.json", dev.tfbr.Cfg::ld);
		F.put("tuffglint.json", dev.tfgl.Cfg::ld);
		F.put("totemparticleglow.json", dev.tpgl.Cfg::ld);
		F.put("xpglow.json", dev.xpgl.Cfg::ld);
		F.put(hits.fire.Glow.ID + ".json", () -> hits.fire.Config.load(FabricLoader.getInstance().getConfigDir().resolve(hits.fire.Glow.ID + ".json")));
		F.put("totemglow.json", () -> {
			try {
				Class<?> k = Class.forName("hits.glow.Config");
				try {
					k.getMethod("load").invoke(null);
				} catch (NoSuchMethodException e) {
					k.getMethod("load", Path.class).invoke(null, FabricLoader.getInstance().getConfigDir().resolve("totemglow.json"));
				}
			} catch (Throwable e) {
				Sv.lg("profile totemglow reload failed: " + e);
			}
		});
		F.put("azure/styles.json", () -> {
			Sty.sel = null;
			Sty.sel();
			Sty.apply();
		});
	}

	public static String out() {
		try {
			Sx.all();
			Cfg.sv();
		} catch (Throwable e) {
			Sv.lg("profile flush failed: " + e);
		}
		JsonObject o = new JsonObject();
		o.addProperty("v", 2);
		JsonObject az = Cfg.G.toJsonTree(Cfg.c).getAsJsonObject();
		for (String k : PERS) az.remove(k);
		o.add("az", az);
		o.addProperty("acc", dev.wysl.Thm.get() & 0xFFFFFF);
		JsonArray t = new JsonArray();
		try {
			for (int i = 0; i < Tx.CI.length; i++) t.add(Tx.get(i));
		} catch (Throwable e) {
			Sv.lg("profile packs failed: " + e);
		}
		o.add("tx", t);
		JsonObject m = new JsonObject();
		Path d = FabricLoader.getInstance().getConfigDir();
		for (String f : F.keySet()) {
			try {
				Path p = d.resolve(f);
				if (Files.isRegularFile(p)) m.add(f, JsonParser.parseString(Files.readString(p)));
			} catch (Throwable e) {
				Sv.lg("profile read " + f + " failed: " + e);
			}
		}
		o.add("m", m);
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			try (DeflaterOutputStream z = new DeflaterOutputStream(b)) {
				z.write(o.toString().getBytes(StandardCharsets.UTF_8));
			}
			return "WC2" + Base64.getUrlEncoder().withoutPadding().encodeToString(b.toByteArray());
		} catch (Throwable e) {
			Sv.lg("profile pack failed: " + e);
			return "";
		}
	}

	static JsonObject parse(String s) throws Exception {
		s = s.trim();
		if (s.startsWith("WC2")) {
			byte[] b = Base64.getUrlDecoder().decode(s.substring(3));
			try (InflaterInputStream z = new InflaterInputStream(new java.io.ByteArrayInputStream(b))) {
				return JsonParser.parseString(new String(z.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
			}
		}
		if (s.startsWith("WC1")) {
			JsonObject o = JsonParser.parseString(new String(Base64.getUrlDecoder().decode(s.substring(3)), StandardCharsets.UTF_8)).getAsJsonObject();
			JsonObject n = new JsonObject();
			n.addProperty("v", 1);
			n.add("tx", o.get("tx"));
			o.remove("tx");
			n.add("az", o);
			return n;
		}
		return null;
	}

	public static boolean ok(String s) {
		try {
			return s != null && parse(s) != null;
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean in(String s) {
		JsonObject o;
		try {
			o = parse(s);
		} catch (Throwable e) {
			Sv.lg("profile parse failed: " + e);
			return false;
		}
		if (o == null) return false;
		Minecraft mc = Minecraft.getInstance();
		mc.execute(() -> app(o));
		return true;
	}

	static void app(JsonObject o) {
		try {
			if (o.has("az") && o.get("az").isJsonObject()) {
				JsonObject az = o.getAsJsonObject("az");
				JsonObject cur = Cfg.G.toJsonTree(Cfg.c).getAsJsonObject();
				for (String k : PERS) {
					az.remove(k);
					if (cur.has(k)) az.add(k, cur.get(k));
				}
				if (az.has("v") && az.get("v").isJsonObject()) {
					JsonObject nv = az.getAsJsonObject("v");
					for (String k : KEEP) {
						nv.remove(k);
						Integer x = Cfg.c.v == null ? null : Cfg.c.v.get(k);
						if (x != null) nv.addProperty(k, x);
					}
				} else if (Cfg.c.v != null) az.add("v", Cfg.G.toJsonTree(Cfg.c.v));
				Cfg n = Cfg.G.fromJson(az, Cfg.class);
				if (n != null) {
					Cfg.c = n;
					Cfg.fx();
					Cfg.sv();
				}
			}
		} catch (Throwable e) {
			Sv.lg("profile azure apply failed: " + e);
		}
		try {
			if (o.has("acc")) dev.wysl.Thm.set(o.get("acc").getAsInt() & 0xFFFFFF);
		} catch (Throwable e) {
			Sv.lg("profile accent failed: " + e);
		}
		try {
			if (o.has("m") && o.get("m").isJsonObject()) {
				Path d = FabricLoader.getInstance().getConfigDir();
				for (Map.Entry<String, JsonElement> en : o.getAsJsonObject("m").entrySet()) {
					Runnable ld = F.get(en.getKey());
					if (ld == null) continue;
					try {
						Sv.fw(d.resolve(en.getKey()), en.getValue().toString());
						ld.run();
					} catch (Throwable e) {
						Sv.lg("profile apply " + en.getKey() + " failed: " + e);
					}
				}
			}
		} catch (Throwable e) {
			Sv.lg("profile mods apply failed: " + e);
		}
		try {
			if (o.has("tx") && o.get("tx").isJsonArray()) {
				JsonArray t = o.getAsJsonArray("tx");
				int[] v = new int[Tx.CI.length];
				boolean df = false;
				for (int i = 0; i < v.length && i < t.size(); i++) {
					v[i] = t.get(i).getAsInt();
					if (v[i] != Tx.get(i)) df = true;
				}
				if (df) Tx.set(v);
			}
		} catch (Throwable e) {
			Sv.lg("profile packs apply failed: " + e);
		}
	}

	public static void copy() {
		Minecraft.getInstance().keyboardHandler.setClipboard(out());
	}

	public static void paste() {
		in(Minecraft.getInstance().keyboardHandler.getClipboard());
	}
}
