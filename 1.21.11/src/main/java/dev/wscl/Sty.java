package dev.wscl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class Sty {
	public static class C {
		public final String id;
		public final String nm;
		public final String ic;
		public final String[][] t;
		String[] md = {};
		int[] pc;
		public String lib;
		public String grp;
		int[] rg;
		String wf;
		public boolean own;

		C p(int x, int y, int w, int h) {
			pc = new int[]{x, y, w, h};
			return this;
		}

		C m(String... md) {
			this.md = md;
			return this;
		}

		C(String id, String nm, String ic, String[]... t) {
			this.id = id;
			this.nm = nm;
			this.ic = ic;
			this.t = t;
			this.lib = id;
		}

		C pc(String grp, String wf, int... rg) {
			this.grp = grp;
			this.lib = grp;
			this.wf = wf;
			this.rg = rg.length == 0 ? null : rg;
			return this;
		}
	}

	public static final C[] CS = {
			new C("crystal", "End Crystal", "item/end_crystal.png", new String[]{"entity/end_crystal/end_crystal.png", "entity/endercrystal/endercrystal.png"}, new String[]{"item/end_crystal.png", "items/end_crystal.png"}),
			new C("anchor", "Respawn Anchor", "block/respawn_anchor_side4.png", new String[]{"block/respawn_anchor_side0.png"}, new String[]{"block/respawn_anchor_side1.png"}, new String[]{"block/respawn_anchor_side2.png"}, new String[]{"block/respawn_anchor_side3.png"}, new String[]{"block/respawn_anchor_side4.png"}, new String[]{"block/respawn_anchor_top.png"}, new String[]{"block/respawn_anchor_top_off.png"}, new String[]{"block/respawn_anchor_bottom.png"}),
			new C("glowstone", "Glowstone", "block/glowstone.png", new String[]{"block/glowstone.png", "blocks/glowstone.png"}),
			new C("totem", "Totem", "item/totem_of_undying.png", new String[]{"item/totem_of_undying.png", "items/totem_of_undying.png", "items/totem.png"}),
			new C("obsidian", "Obsidian", "block/obsidian.png", new String[]{"block/obsidian.png", "blocks/obsidian.png"}),
			new C("pearl", "Ender Pearl", "item/ender_pearl.png", new String[]{"item/ender_pearl.png", "items/ender_pearl.png"}),
			new C("gapple", "Golden Apple", "item/golden_apple.png", new String[]{"item/golden_apple.png", "items/apple_golden.png", "items/golden_apple.png"}),
			new C("sword", "Netherite Sword", "item/netherite_sword.png", new String[]{"item/netherite_sword.png", "items/netherite_sword.png"}),
			new C("pick", "Netherite Pickaxe", "item/netherite_pickaxe.png", new String[]{"item/netherite_pickaxe.png", "items/netherite_pickaxe.png"}),
			new C("fire", "Fire", "block/fire_0.png", new String[]{"block/fire_0.png", "blocks/fire_layer_0.png"}, new String[]{"block/fire_1.png", "blocks/fire_layer_1.png"}).m("fire_floor0", "fire_floor1", "fire_side0", "fire_side1", "fire_side_alt0", "fire_side_alt1", "fire_up0", "fire_up1", "fire_up_alt0", "fire_up_alt1"),
			new C("cobweb", "Cobweb", "block/cobweb.png", new String[]{"block/cobweb.png", "blocks/web.png"}),
			new C("glint", "Enchant Glint", "misc/enchanted_glint_item.png", new String[]{"misc/enchanted_glint_item.png", "misc/enchanted_item_glint.png"}, new String[]{"misc/enchanted_glint_armor.png", "misc/enchanted_item_glint.png"}),
			new C("stone", "Stone", "block/stone.png", new String[]{"block/stone.png", "blocks/stone.png"}),
			new C("deepslate", "Deepslate", "block/deepslate.png", new String[]{"block/deepslate.png"}, new String[]{"block/deepslate_top.png"}),
			new C("snow", "Snow", "block/snow.png", new String[]{"block/snow.png", "blocks/snow.png"}),
			new C("shield", "Shield", "entity/shield_base_nopattern.png", new String[]{"entity/shield_base_nopattern.png"}, new String[]{"entity/shield/base.png", "entity/shield_base.png"}).p(1, 6, 12, 12),
			new C("bow", "Bow", "item/bow.png", new String[]{"item/bow.png", "items/bow_standby.png"}, new String[]{"item/bow_pulling_0.png", "items/bow_pulling_0.png"}, new String[]{"item/bow_pulling_1.png", "items/bow_pulling_1.png"}, new String[]{"item/bow_pulling_2.png", "items/bow_pulling_2.png"}),
			new C("crossbow", "Crossbow", "item/crossbow_standby.png", new String[]{"item/crossbow_standby.png"}, new String[]{"item/crossbow_pulling_0.png"}, new String[]{"item/crossbow_pulling_1.png"}, new String[]{"item/crossbow_pulling_2.png"}, new String[]{"item/crossbow_arrow.png"}, new String[]{"item/crossbow_firework.png"}),
			new C("darmor", "Diamond Armor", "item/diamond_chestplate.png", new String[]{"item/diamond_helmet.png", "items/diamond_helmet.png"}, new String[]{"item/diamond_chestplate.png", "items/diamond_chestplate.png"}, new String[]{"item/diamond_leggings.png", "items/diamond_leggings.png"}, new String[]{"item/diamond_boots.png", "items/diamond_boots.png"}, new String[]{"entity/equipment/humanoid/diamond.png", "models/armor/diamond_layer_1.png"}, new String[]{"entity/equipment/humanoid_leggings/diamond.png", "models/armor/diamond_layer_2.png"}),
			new C("narmor", "Netherite Armor", "item/netherite_chestplate.png", new String[]{"item/netherite_helmet.png"}, new String[]{"item/netherite_chestplate.png"}, new String[]{"item/netherite_leggings.png"}, new String[]{"item/netherite_boots.png"}, new String[]{"entity/equipment/humanoid/netherite.png", "models/armor/netherite_layer_1.png"}, new String[]{"entity/equipment/humanoid_leggings/netherite.png", "models/armor/netherite_layer_2.png"}),
			new C("daxe", "Diamond Axe", "item/diamond_axe.png", new String[]{"item/diamond_axe.png", "items/diamond_axe.png"}),
			new C("naxe", "Netherite Axe", "item/netherite_axe.png", new String[]{"item/netherite_axe.png"}),
			new C("dsword", "Diamond Sword", "item/diamond_sword.png", new String[]{"item/diamond_sword.png", "items/diamond_sword.png"}),
			new C("dpick", "Diamond Pickaxe", "item/diamond_pickaxe.png", new String[]{"item/diamond_pickaxe.png", "items/diamond_pickaxe.png"}),
			new C("trident", "Trident", "item/trident.png", new String[]{"item/trident.png"}, new String[]{"entity/trident.png"}),
			new C("spear", "Spears", "item/netherite_spear.png", new String[]{"item/netherite_spear.png"}, new String[]{"item/netherite_spear_in_hand.png"}, new String[]{"item/diamond_spear.png"}, new String[]{"item/diamond_spear_in_hand.png"}),
			new C("mace", "Mace", "item/mace.png", new String[]{"item/mace.png"}),
			new C("glitter", "Totem Particles", "particle/glitter_7.png", new String[]{"particle/glitter_0.png"}, new String[]{"particle/glitter_1.png"}, new String[]{"particle/glitter_2.png"}, new String[]{"particle/glitter_3.png"}, new String[]{"particle/glitter_4.png"}, new String[]{"particle/glitter_5.png"}, new String[]{"particle/glitter_6.png"}, new String[]{"particle/glitter_7.png"}),
			new C("sky", "Skies", "environment/celestial/sun.png"),
			new C("water", "Water", "block/water_still.png", new String[]{"block/water_still.png", "blocks/water_still.png"}, new String[]{"block/water_flow.png", "blocks/water_flow.png"})
	};
	static final String[] PN = {"Helmet", "Chestplate", "Leggings", "Boots"};
	static final String[] PK4 = {"helmet", "chestplate", "leggings", "boots"};
	static final String[] PS = {"h", "c", "l", "b"};

	static C[] pieces(String g, String mat, String nmat) {
		C[] o = new C[4];
		String l1 = "entity/equipment/humanoid/" + mat + ".png";
		String l2 = "entity/equipment/humanoid_leggings/" + mat + ".png";
		for (int i = 0; i < 4; i++) {
			String it = "item/" + mat + "_" + PK4[i] + ".png";
			String[][] t = i == 2 ? new String[][]{{it, "items/" + mat + "_" + PK4[i] + ".png"}, {l2, "models/armor/" + mat + "_layer_2.png"}} : new String[][]{{it, "items/" + mat + "_" + PK4[i] + ".png"}};
			o[i] = new C(g + "." + PS[i], nmat + " " + PN[i], it, t);
			if (i == 0) o[i].pc(g, l1, 0, 0, 64, 16);
			else if (i == 1) o[i].pc(g, l1, 16, 16, 40, 16);
			else if (i == 2) o[i].pc(g, null);
			else o[i].pc(g, l1, 0, 16, 16, 16);
		}
		return o;
	}

	static final C[] DP = pieces("darmor", "diamond", "Diamond");
	static final C[] NP = pieces("narmor", "netherite", "Netherite");
	static final List<C> CU = new ArrayList<>();
	static boolean cul;
	static C[] LS;
	static String lk = "";

	static Path cuf() {
		return Minecraft.getInstance().gameDirectory.toPath().resolve("config/azure/styles-custom.json");
	}

	@SuppressWarnings("unchecked")
	static void culd() {
		if (cul) return;
		cul = true;
		CU.clear();
		try {
			if (Files.exists(cuf())) {
				List<Map<String, String>> l = new com.google.gson.Gson().fromJson(Files.readString(cuf()), List.class);
				for (Map<String, String> e : l) CU.add(mk(e.get("n"), e.get("p")));
			}
		} catch (Throwable e) {
		}
	}

	public static void cureload() {
		cul = false;
		LS = null;
	}

	static void cusv() {
		List<Map<String, String>> l = new ArrayList<>();
		for (C c : CU) l.add(Map.of("n", c.id.substring(2), "p", c.ic));
		try {
			Files.createDirectories(cuf().getParent());
			Files.writeString(cuf(), new com.google.gson.Gson().toJson(l));
		} catch (Throwable e) {
		}
	}

	static C mk(String n, String path) {
		StringBuilder b = new StringBuilder();
		for (String w : n.split("_")) if (!w.isEmpty()) b.append(b.length() == 0 ? "" : " ").append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
		C c = new C("u:" + n, b.toString(), path, new String[]{path});
		c.own = true;
		return c;
	}

	public static String add(String raw) {
		String n = raw.trim().toLowerCase(java.util.Locale.ROOT).replace("minecraft:", "").replace(' ', '_');
		if (!n.matches("[a-z0-9_/]{2,60}")) return "Type a block or item name, like diamond_block";
		culd();
		for (C c : CU) if (c.id.equals("u:" + n)) return "You already added that one";
		for (C c : CS) for (String[] t : c.t) if (t[0].equals("item/" + n + ".png") || t[0].equals("block/" + n + ".png")) return "That one is already in the list as " + c.nm;
		String p = vanilla("block/" + n + ".png") != null ? "block/" + n + ".png" : vanilla("item/" + n + ".png") != null ? "item/" + n + ".png" : null;
		if (p == null) return "Minecraft has no block or item texture called " + n;
		CU.add(mk(n, p));
		cusv();
		LS = null;
		scanned = false;
		return "";
	}

	public static void rmc(C c) {
		CU.remove(c);
		sel().remove(c.id);
		save();
		cusv();
		LS = null;
		apply();
	}

	public static boolean sep() {
		return Cfg.gi("sty.arm", 0) == 1;
	}

	public static C[] list() {
		culd();
		String k = sep() + "|" + CU.size();
		if (LS != null && k.equals(lk)) return LS;
		List<C> o = new ArrayList<>();
		for (C c : CS) {
			if (sep() && c.id.equals("darmor")) o.addAll(java.util.Arrays.asList(DP));
			else if (sep() && c.id.equals("narmor")) o.addAll(java.util.Arrays.asList(NP));
			else o.add(c);
		}
		o.addAll(CU);
		LS = o.toArray(new C[0]);
		lk = k;
		return LS;
	}

	public static void setSep(boolean v) {
		if (v == sep()) return;
		if (v) for (String g : new String[]{"darmor", "narmor"}) {
			String gv = sel().getOrDefault(g, "");
			if (!gv.isEmpty()) for (String ps : PS) sel().putIfAbsent(g + "." + ps, gv);
		}
		Cfg.si("sty.arm", v ? 1 : 0);
		Cfg.sv();
		save();
		LS = null;
		redo();
	}

	public static boolean packWins(C c) {
		String k = c.grp != null ? c.grp : c.id;
		return Cfg.gi("sty.dom." + k, Cfg.gi("sty.dom", 0)) == 0;
	}

	public static void setWin(C c, boolean pack) {
		String k = c.grp != null ? c.grp : c.id;
		Cfg.si("sty.dom." + k, pack ? 0 : 1);
		Cfg.sv();
		redo();
	}

	public static void redo() {
		ap = new HashMap<>();
		for (String k : sel().keySet()) ap.put(k, "#");
		for (C c : list()) ap.put(c.id, "#");
		for (C c : DP) ap.put(c.id, "#");
		for (C c : NP) ap.put(c.id, "#");
		apply();
	}

	public static final String[] TN = {"Accent", "Red", "Pink", "Purple", "Blue", "Ice", "Lime", "Gold", "White", "Black"};
	static final int[] TC = {-1, 0xFF4848, 0xFF6EC7, 0xA05CFF, 0x3D7BFF, 0x8EE6FF, 0x7CFF4F, 0xFFC93C, 0xF0F0F0, 0x303030};
	static final String PK = "azure_styles";
	static Map<String, String> sel;
	public static final Map<String, List<String>> FOUND = new ConcurrentHashMap<>();
	public static volatile boolean scanning;
	public static volatile boolean scanned;
	public static volatile boolean busy;
	static final Map<String, Identifier> PRV = new HashMap<>();

	static Path cf() {
		return Minecraft.getInstance().gameDirectory.toPath().resolve("config/azure/styles.json");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> sel() {
		if (sel != null) return sel;
		sel = new HashMap<>();
		try {
			Path p = cf();
			if (Files.exists(p)) sel.putAll(new com.google.gson.Gson().fromJson(Files.readString(p), Map.class));
		} catch (Throwable e) {
		}
		ap = new HashMap<>(sel);
		return sel;
	}

	static void save() {
		try {
			Files.createDirectories(cf().getParent());
			Files.writeString(cf(), new com.google.gson.Gson().toJson(sel()));
		} catch (Throwable e) {
		}
	}

	public static String get(C c) {
		return sel().getOrDefault(c.id, "");
	}

	public static void set(C c, String v) {
		if (v.isEmpty()) sel().remove(c.id);
		else sel().put(c.id, v);
		save();
		apply();
	}

	static Path rp() {
		return Minecraft.getInstance().getResourcePackDirectory();
	}

	public static void scan() {
		if (scanning) return;
		scanning = true;
		Thread th = new Thread(() -> {
			Map<String, List<String>> o = new HashMap<>();
			C[] all = list();
			for (C c : all) o.put(c.id, new ArrayList<>());
			try (var ds = Files.list(rp())) {
				for (Path p : (Iterable<Path>) ds::iterator) {
					String n = p.getFileName().toString();
					if (n.equals(PK)) continue;
					boolean dir = Files.isDirectory(p);
					if (!dir && !n.toLowerCase().endsWith(".zip")) continue;
					java.util.Set<String> has = new java.util.HashSet<>();
					if (dir) {
						for (C c : all) for (String[] t : c.t) for (String a : t) {
							if (Files.exists(p.resolve("assets/minecraft/textures/" + a))) has.add(a);
							if (a.startsWith("item/") && Files.exists(p.resolve("assets/minecraft/models/" + a.substring(0, a.length() - 4) + ".json"))) has.add("m:" + a);
						}
					} else {
						try (ZipFile z = new ZipFile(p.toFile())) {
							var en = z.entries();
							while (en.hasMoreElements()) {
								String e = en.nextElement().getName();
								int i = e.indexOf("assets/minecraft/textures/");
								if (i >= 0) has.add(e.substring(i + 26));
								int j = e.indexOf("assets/minecraft/models/item/");
								if (j >= 0 && e.endsWith(".json")) has.add("m:item/" + e.substring(j + 29, e.length() - 5) + ".png");
							}
						} catch (Throwable e) {
						}
					}
					for (C c : all) {
						boolean ok = false;
						if (c.t.length > 0) for (String a : c.t[0]) if (has.contains(a) || has.contains("m:" + a)) ok = true;
						if (ok) o.get(c.id).add(n);
					}
				}
			} catch (Throwable e) {
			}
			FOUND.clear();
			FOUND.putAll(o);
			scanned = true;
			scanning = false;
		}, "azure-styles-scan");
		th.setDaemon(true);
		th.start();
	}

	static byte[] vanilla(String path) {
		try {
			var r = Minecraft.getInstance().getVanillaPackResources().getResource(PackType.CLIENT_RESOURCES, Identifier.withDefaultNamespace("textures/" + path));
			if (r == null) return null;
			try (InputStream in = r.get()) {
				return in.readAllBytes();
			}
		} catch (Throwable e) {
			return null;
		}
	}

	static Object[] cur(String path, String only) {
		try {
			var st = Minecraft.getInstance().getResourceManager().getResourceStack(Identifier.withDefaultNamespace(path));
			for (int i = st.size() - 1; i >= 0; i--) {
				var r = st.get(i);
				String id = r.sourcePackId();
				if (id.endsWith(PK) || (only == null && id.startsWith("file/")) || (only != null && !only.equals(id))) continue;
				try (InputStream in = r.open()) {
					return new Object[]{in.readAllBytes(), id};
				}
			}
		} catch (Throwable e) {
		}
		return null;
	}

	public static String top(String path) {
		try {
			var st = Minecraft.getInstance().getResourceManager().getResourceStack(Identifier.withDefaultNamespace(path));
			for (int i = st.size() - 1; i >= 0; i--) {
				String id = st.get(i).sourcePackId();
				if (id.endsWith(PK)) continue;
				return id.startsWith("file/") ? id.substring(5) : null;
			}
		} catch (Throwable e) {
		}
		return null;
	}

	public static String own(C c) {
		for (String[] t : c.t) {
			String o = ownp(t[0]);
			if (o != null) return o;
		}
		return null;
	}

	static String[] js(String p) {
		if (!p.endsWith(".png")) return new String[0];
		if (p.startsWith("item/")) {
			String nm = p.substring(5, p.length() - 4);
			return new String[]{"items/" + nm + ".json", "models/item/" + nm + ".json"};
		}
		if (p.startsWith("block/")) {
			String nm = p.substring(6, p.length() - 4);
			return new String[]{"blockstates/" + nm + ".json", "models/block/" + nm + ".json"};
		}
		return new String[0];
	}

	static String ownp(String p) {
		String o = top("textures/" + p);
		if (o != null) return o;
		for (String f : js(p)) {
			o = top(f);
			if (o != null) return o;
		}
		return null;
	}

	static void van(Path d, String p) {
		for (String f : js(p)) {
			if (top(f) == null) continue;
			Object[] r = cur(f, null);
			if (r == null) continue;
			try {
				Path o = d.resolve("assets/minecraft/" + f);
				Files.createDirectories(o.getParent());
				Files.write(o, (byte[]) r[0]);
			} catch (Throwable e) {
			}
		}
	}

	static List<String> xt(C c) {
		List<String> o = new ArrayList<>();
		for (String m : c.md) {
			Object[] r = cur("models/block/" + m + ".json", null);
			if (r == null) continue;
			try {
				var js = com.google.gson.JsonParser.parseString(new String((byte[]) r[0], java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
				if (!js.has("textures")) continue;
				for (var e : js.getAsJsonObject("textures").entrySet()) {
					String v = e.getValue().getAsString();
					if (v.startsWith("#")) continue;
					v = v.replace("minecraft:", "") + ".png";
					boolean kn = false;
					for (String[] t : c.t) if (t[0].equals(v)) kn = true;
					if (!kn && !o.contains(v)) o.add(v);
				}
			} catch (Throwable e) {
			}
		}
		return o;
	}

	static boolean tw(Path d, String path, int col) throws Exception {
		Object[] r = cur("textures/" + path, null);
		byte[] src = r == null ? vanilla(path) : (byte[]) r[0];
		if (src == null) return false;
		byte[] b = tint(src, col);
		if (b == null) return false;
		Path f = d.resolve("assets/minecraft/textures/" + path);
		Files.createDirectories(f.getParent());
		Files.write(f, b);
		Object[] mr = r == null ? null : cur("textures/" + path + ".mcmeta", (String) r[1]);
		byte[] mm = r == null ? vanilla(path + ".mcmeta") : mr == null ? null : (byte[]) mr[0];
		if (mm != null) Files.write(d.resolve("assets/minecraft/textures/" + path + ".mcmeta"), mm);
		return true;
	}

	static byte[] fromPack(String pk, String path) {
		Path p = rp().resolve(pk);
		try {
			if (Files.isDirectory(p)) {
				Path f = p.resolve("assets/minecraft/textures/" + path);
				return Files.exists(f) ? Files.readAllBytes(f) : null;
			}
			try (ZipFile z = new ZipFile(p.toFile())) {
				var en = z.entries();
				while (en.hasMoreElements()) {
					ZipEntry e = en.nextElement();
					if (e.getName().endsWith("assets/minecraft/textures/" + path)) {
						try (InputStream in = z.getInputStream(e)) {
							return in.readAllBytes();
						}
					}
				}
			}
		} catch (Throwable e) {
		}
		return null;
	}

	static byte[] raw(String pk, String ap) {
		Path p = rp().resolve(pk);
		try {
			if (Files.isDirectory(p)) {
				Path f = p.resolve(ap);
				return Files.exists(f) ? Files.readAllBytes(f) : null;
			}
			try (ZipFile z = new ZipFile(p.toFile())) {
				var en = z.entries();
				while (en.hasMoreElements()) {
					ZipEntry e = en.nextElement();
					if (e.getName().endsWith(ap) && (e.getName().length() == ap.length() || e.getName().charAt(e.getName().length() - ap.length() - 1) == '/')) {
						try (InputStream in = z.getInputStream(e)) {
							return in.readAllBytes();
						}
					}
				}
			}
		} catch (Throwable e) {
		}
		return null;
	}

	static String rf(String r, String kd) {
		int i = r.indexOf(':');
		return "assets/" + (i < 0 ? "minecraft" : r.substring(0, i)) + "/" + kd + "/" + (i < 0 ? r : r.substring(i + 1));
	}

	static void refs(com.google.gson.JsonElement e, String k, boolean tx, java.util.ArrayDeque<String> q) {
		if (e == null) return;
		if (e.isJsonObject()) {
			for (var en : e.getAsJsonObject().entrySet()) refs(en.getValue(), en.getKey(), tx || en.getKey().equals("textures"), q);
		} else if (e.isJsonArray()) {
			for (var x : e.getAsJsonArray()) refs(x, k, tx, q);
		} else if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
			String v = e.getAsString();
			if (v.startsWith("#") || v.contains("builtin/")) return;
			if (tx) {
				q.add(rf(v, "textures") + ".png");
				q.add(rf(v, "textures") + ".png.mcmeta");
			} else if (k.equals("model") || k.equals("parent")) q.add(rf(v, "models") + ".json");
		}
	}

	static boolean mdl(String pk, Path d, String p) {
		java.util.ArrayDeque<String> q = new java.util.ArrayDeque<>();
		java.util.Set<String> dn = new java.util.HashSet<>();
		for (String f : js(p)) q.add("assets/minecraft/" + f);
		if (q.isEmpty()) return false;
		boolean el = false;
		Map<String, byte[]> got = new java.util.LinkedHashMap<>();
		while (!q.isEmpty() && dn.size() < 80) {
			String f = q.poll();
			if (!dn.add(f)) continue;
			byte[] x = raw(pk, f);
			if (x == null) {
				if (f.endsWith(".png") && !(f.startsWith("assets/minecraft/textures/") && vanilla(f.substring(26)) != null)) return false;
				continue;
			}
			got.put(f, x);
			if (!f.endsWith(".json")) continue;
			try {
				String t = new String(x, java.nio.charset.StandardCharsets.UTF_8);
				if (t.contains("\"elements\"")) el = true;
				refs(com.google.gson.JsonParser.parseString(t), "", false, q);
			} catch (Throwable e) {
			}
		}
		if (got.isEmpty()) return false;
		for (var e : got.entrySet()) {
			try {
				Path f = d.resolve(e.getKey());
				Files.createDirectories(f.getParent());
				Files.write(f, e.getValue());
			} catch (Throwable x) {
			}
		}
		return true;
	}

	static String lsm(Path d) {
		StringBuilder o = new StringBuilder();
		try (var w = Files.walk(d)) {
			for (Path x : (Iterable<Path>) w.filter(Files::isRegularFile).filter(y -> y.toString().endsWith(".json")).sorted()::iterator) o.append(d.relativize(x)).append('#').append(java.util.Arrays.hashCode(Files.readAllBytes(x))).append(';');
		} catch (Throwable e) {
		}
		return o.toString();
	}

	static byte[] packAny(String pk, String[] alts) {
		if (alts.length > 0 && alts[0].startsWith("item/")) {
			byte[] m = mtx(pk, alts[0].substring(5, alts[0].length() - 4));
			if (m != null) return m;
		}
		for (String a : alts) {
			byte[] b = fromPack(pk, a);
			if (b != null) return b;
		}
		return null;
	}

	static com.google.gson.JsonObject pj(String pk, String f, boolean[] own) {
		byte[] b = raw(pk, f);
		if (b != null) own[0] = true;
		else if (f.startsWith("assets/minecraft/")) {
			Object[] r = cur(f.substring(17), null);
			b = r == null ? null : (byte[]) r[0];
		}
		if (b == null) return null;
		try {
			var e = com.google.gson.JsonParser.parseString(new String(b, java.nio.charset.StandardCharsets.UTF_8));
			return e.isJsonObject() ? e.getAsJsonObject() : null;
		} catch (Throwable x) {
			return null;
		}
	}

	static String fm(com.google.gson.JsonElement e) {
		if (e == null) return null;
		if (e.isJsonObject()) {
			var o = e.getAsJsonObject();
			if (o.has("fallback")) {
				String r = fm(o.get("fallback"));
				if (r != null) return r;
			}
			for (var en : o.entrySet()) {
				if (en.getKey().equals("model") && en.getValue().isJsonPrimitive()) return en.getValue().getAsString();
				String r = fm(en.getValue());
				if (r != null) return r;
			}
		} else if (e.isJsonArray()) {
			for (var x : e.getAsJsonArray()) {
				String r = fm(x);
				if (r != null) return r;
			}
		}
		return null;
	}

	static byte[] mtx(String pk, String nm) {
		try {
			boolean[] own = {false};
			var it = pj(pk, "assets/minecraft/items/" + nm + ".json", own);
			String mr = own[0] && it != null ? fm(it) : "minecraft:item/" + nm;
			if (mr == null) return null;
			Map<String, String> tx = new HashMap<>();
			String k = rf(mr, "models") + ".json";
			for (int i = 0; i < 8; i++) {
				var m = pj(pk, k, own);
				if (m == null) return null;
				if (m.has("textures")) for (var e : m.getAsJsonObject("textures").entrySet()) if (e.getValue().isJsonPrimitive()) tx.putIfAbsent(e.getKey(), e.getValue().getAsString());
				String pa = m.has("parent") ? m.get("parent").getAsString() : null;
				if (pa == null || pa.contains("builtin") || pa.contains("generated") || pa.contains("handheld")) break;
				k = rf(pa, "models") + ".json";
			}
			if (!own[0]) return null;
			String l0 = tx.get("layer0");
			if (l0 == null) {
				java.util.Set<String> vs = new java.util.HashSet<>();
				for (var e : tx.entrySet()) if (!e.getKey().equals("particle") && !e.getValue().startsWith("#")) vs.add(e.getValue());
				if (vs.size() == 1) l0 = vs.iterator().next();
			}
			if (l0 == null || l0.startsWith("#")) return null;
			String tf = rf(l0, "textures") + ".png";
			byte[] b = raw(pk, tf);
			if (b == null && tf.startsWith("assets/minecraft/textures/")) b = vanilla(tf.substring(26));
			return b;
		} catch (Throwable e) {
		}
		return null;
	}

	static int tc(int i) {
		return i == 0 ? Hud.ac() & 0xFFFFFF : TC[i];
	}

	static BufferedImage rd(byte[] b) throws java.io.IOException {
		try (var ni = com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(b))) {
			BufferedImage o = new BufferedImage(ni.getWidth(), ni.getHeight(), BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < ni.getHeight(); y++) for (int x = 0; x < ni.getWidth(); x++) o.setRGB(x, y, ni.getPixel(x, y));
			return o;
		}
	}

	static boolean emp(BufferedImage im, int x0, int y0, int cs) {
		int n = 0;
		for (int y = y0; y < y0 + cs; y++) for (int x = x0; x < x0 + cs; x++) if ((im.getRGB(x, y) >>> 24) > 0) n++;
		return n <= cs * cs / 12;
	}

	static boolean edg(BufferedImage im, int x, int y0, int cs) {
		for (int y = y0; y < y0 + cs; y++) if ((im.getRGB(x, y) >>> 24) > 0) return true;
		return false;
	}

	static byte[] cub(byte[] b) {
		try {
			BufferedImage im = rd(b);
			int w = im.getWidth();
			int h = im.getHeight();
			int cs;
			int cx;
			int cy;
			if (w == h * 2 && w >= 64) {
				cs = w / 8;
				cx = w / 2;
				cy = 0;
			} else {
				cs = w / 4;
				if (w < 16 || h < 2 * cs) return null;
				if (!emp(im, 0, 0, cs) || !emp(im, 3 * cs, 0, cs) || !edg(im, 0, cs, cs) || !edg(im, w - 1, cs, cs)) return null;
				cx = 0;
				cy = h >= 4 * cs ? 2 * cs : -1;
			}
			BufferedImage o = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			float g = 26f;
			fc(o, im, cs, 2 * cs, 0, g, new float[]{0, 0, 0}, new float[]{1, 0, 0}, new float[]{0, 0, 1}, 0.45f);
			fc(o, im, cs, 3 * cs, cs, g, new float[]{0, 1, 0}, new float[]{1, 0, 0}, new float[]{0, -1, 0}, 0.45f);
			fc(o, im, cs, 2 * cs, cs, g, new float[]{0, 1, 0}, new float[]{0, 0, 1}, new float[]{0, -1, 0}, 0.45f);
			if (cy >= 0) {
				float k = 15f;
				fc(o, im, cs, cx + cs, cy, k, new float[]{0, 1, 0}, new float[]{1, 0, 0}, new float[]{0, 0, 1}, 1f);
				fc(o, im, cs, cx + cs, cy + cs, k, new float[]{0, 1, 1}, new float[]{1, 0, 0}, new float[]{0, -1, 0}, 0.78f);
				fc(o, im, cs, cx, cy + cs, k, new float[]{1, 1, 1}, new float[]{0, 0, -1}, new float[]{0, -1, 0}, 0.62f);
			}
			fc(o, im, cs, cs, 0, g, new float[]{0, 1, 0}, new float[]{1, 0, 0}, new float[]{0, 0, 1}, 1f);
			fc(o, im, cs, cs, cs, g, new float[]{0, 1, 1}, new float[]{1, 0, 0}, new float[]{0, -1, 0}, 0.78f);
			fc(o, im, cs, 0, cs, g, new float[]{1, 1, 1}, new float[]{0, 0, -1}, new float[]{0, -1, 0}, 0.62f);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ImageIO.write(o, "png", bo);
			return bo.toByteArray();
		} catch (Throwable e) {
			return null;
		}
	}

	static float[] pj(float[] v, float a) {
		return new float[]{(v[0] - v[2]) * 0.866f * a, (v[0] + v[2]) * 0.5f * a - v[1] * a};
	}

	static void fc(BufferedImage o, BufferedImage im, int cs, int tx, int ty, float a, float[] org, float[] u, float[] v, float sh) {
		if (tx + cs > im.getWidth() || ty + cs > im.getHeight()) return;
		float[] c = pj(new float[]{0.5f, 0.5f, 0.5f}, a);
		float[] p0 = pj(org, a);
		float[] pu = pj(u, a);
		float[] pv = pj(v, a);
		float dt = pu[0] * pv[1] - pu[1] * pv[0];
		if (Math.abs(dt) < 1e-4f) return;
		for (int y = 0; y < 64; y++) {
			for (int x = 0; x < 64; x++) {
				float dx = x + 0.5f - 32f + c[0] - p0[0];
				float dy = y + 0.5f - 32f + c[1] - p0[1];
				float uu = (dx * pv[1] - dy * pv[0]) / dt;
				float vv = (pu[0] * dy - pu[1] * dx) / dt;
				if (uu < 0 || uu >= 1 || vv < 0 || vv >= 1) continue;
				int s = im.getRGB(tx + Math.min(cs - 1, (int) (uu * cs)), ty + Math.min(cs - 1, (int) (vv * cs)));
				int sa = s >>> 24;
				if (sa == 0) continue;
				int sr = Math.round((s >> 16 & 255) * sh);
				int sg = Math.round((s >> 8 & 255) * sh);
				int sb = Math.round((s & 255) * sh);
				int d = o.getRGB(x, y);
				int da = d >>> 24;
				float fa = sa / 255f;
				float oa = fa + da / 255f * (1 - fa);
				int r = Math.round((sr * fa + (d >> 16 & 255) * da / 255f * (1 - fa)) / oa);
				int gg = Math.round((sg * fa + (d >> 8 & 255) * da / 255f * (1 - fa)) / oa);
				int bb = Math.round((sb * fa + (d & 255) * da / 255f * (1 - fa)) / oa);
				o.setRGB(x, y, Math.round(oa * 255) << 24 | Math.min(255, r) << 16 | Math.min(255, gg) << 8 | Math.min(255, bb));
			}
		}
	}

	static byte[] pc(C c, String v, String[] t) {
		try {
			if (v.startsWith("l:")) return Lib.get(c.lib, v.substring(2), t[0]);
			if (v.startsWith("p:")) return packAny(v.substring(2), t);
			byte[] b = vanilla(t[0]);
			if (b != null && v.startsWith("c:")) return tint(b, Integer.parseInt(v.substring(2), 16));
			if (b != null && v.startsWith("t:")) return tint(b, tc(Integer.parseInt(v.substring(2))));
			return b;
		} catch (Throwable e) {
			return null;
		}
	}

	static byte[] mul(byte[] b, int c) {
		try {
			BufferedImage im = rd(b);
			for (int y = 0; y < im.getHeight(); y++) {
				for (int x = 0; x < im.getWidth(); x++) {
					int p = im.getRGB(x, y);
					im.setRGB(x, y, p & 0xFF000000 | (p >> 16 & 255) * (c >> 16 & 255) / 255 << 16 | (p >> 8 & 255) * (c >> 8 & 255) / 255 << 8 | (p & 255) * (c & 255) / 255);
				}
			}
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ImageIO.write(im, "png", bo);
			return bo.toByteArray();
		} catch (Throwable e) {
			return b;
		}
	}

	static void cpy(BufferedImage im, BufferedImage o, int k, int u, int v, int w, int h, int x, int y) {
		for (int yy = 0; yy < h * k; yy++) for (int xx = 0; xx < w * k; xx++) o.setRGB(x * k + xx, y * k + yy, im.getRGB(Math.min(im.getWidth() - 1, u * k + xx), Math.min(im.getHeight() - 1, v * k + yy)));
	}

	static byte[] trd(byte[] b) {
		try {
			BufferedImage im = rd(b);
			if (im.getWidth() != im.getHeight()) return null;
			int k = Math.max(1, im.getWidth() / 32);
			BufferedImage up = new BufferedImage(5 * k, 31 * k, BufferedImage.TYPE_INT_ARGB);
			cpy(im, up, k, 1, 1, 1, 4, 2, 0);
			cpy(im, up, k, 5, 4, 1, 4, 0, 1);
			cpy(im, up, k, 5, 4, 1, 4, 4, 1);
			cpy(im, up, k, 5, 1, 3, 2, 1, 4);
			cpy(im, up, k, 1, 7, 1, 25, 2, 6);
			int n = 28 * k;
			BufferedImage o = new BufferedImage(n, n, BufferedImage.TYPE_INT_ARGB);
			java.awt.Graphics2D g = o.createGraphics();
			g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.translate(n / 2.0, n / 2.0);
			g.rotate(Math.PI / 4);
			g.drawImage(up, -up.getWidth() / 2, -up.getHeight() / 2, null);
			g.dispose();
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ImageIO.write(o, "png", bo);
			return bo.toByteArray();
		} catch (Throwable e) {
			return null;
		}
	}

	static byte[] quad(C c, String v) {
		try {
			BufferedImage o = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < 4; i++) {
				byte[] b = pc(c, v, c.t[i]);
				if (b == null) b = vanilla(c.t[i][0]);
				if (b == null) continue;
				BufferedImage im = rd(b);
				int w = im.getWidth();
				for (int y = 0; y < 32; y++) for (int x = 0; x < 32; x++) o.setRGB(i % 2 * 32 + x, i / 2 * 32 + y, im.getRGB(Math.min(w - 1, x * w / 32), Math.min(im.getHeight() - 1, y * w / 32)));
			}
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ImageIO.write(o, "png", bo);
			return bo.toByteArray();
		} catch (Throwable e) {
			return null;
		}
	}

	static byte[] tint(byte[] src, int c) {
		try {
			BufferedImage im = rd(src);
			BufferedImage o = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
			float cr = (c >> 16 & 255) / 255f;
			float cg = (c >> 8 & 255) / 255f;
			float cb = (c & 255) / 255f;
			for (int y = 0; y < im.getHeight(); y++) {
				for (int x = 0; x < im.getWidth(); x++) {
					int p = im.getRGB(x, y);
					int a = p >>> 24;
					if (a == 0) continue;
					float l = (0.3f * (p >> 16 & 255) + 0.59f * (p >> 8 & 255) + 0.11f * (p & 255)) / 255f;
					l = Math.max(0f, Math.min(1f, (l - 0.5f) * 1.15f + 0.5f));
					float k = Math.min(1f, l * 1.5f);
					float w = Math.max(0f, (l - 0.72f) / 0.28f) * 0.8f;
					int r = Math.round(255 * Math.min(1f, cr * k * (1 - w) + w));
					int g = Math.round(255 * Math.min(1f, cg * k * (1 - w) + w));
					int b = Math.round(255 * Math.min(1f, cb * k * (1 - w) + w));
					o.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
				}
			}
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ImageIO.write(o, "png", bo);
			return bo.toByteArray();
		} catch (Throwable e) {
			return src;
		}
	}

	static byte[] mcmeta(String v, String pk, String[] alts) {
		if (pk != null) for (String a : alts) {
			byte[] b = fromPack(pk, a + ".mcmeta");
			if (b != null) return b;
		}
		return vanilla(alts[0] + ".mcmeta");
	}

	public static byte[] img(C c, String v) {
		if (v.startsWith("c:")) {
			byte[] b = vanilla(c.ic);
			return b == null ? null : tint(b, Integer.parseInt(v.substring(2), 16));
		}
		if (v.startsWith("s:")) return SkyL.thumb(Integer.parseInt(v.substring(2)));
		if (v.startsWith("t:")) {
			byte[] b = vanilla(c.ic);
			return b == null ? null : tint(b, tc(Integer.parseInt(v.substring(2))));
		}
		if (v.startsWith("p:")) {
			for (String[] t : c.t) for (String a : t) if (a.equals(c.ic)) return packAny(v.substring(2), t);
			return packAny(v.substring(2), c.t[0]);
		}
		if (v.startsWith("l:")) {
			for (String[] t : c.t) for (String a : t) if (a.equals(c.ic)) return Lib.get(c.lib, v.substring(2), t[0]);
			byte[] b = Lib.get(c.lib, v.substring(2), c.t[0][0]);
			return b != null ? b : vanilla(c.ic);
		}
		return vanilla(c.ic);
	}

	public static Identifier prv(C c, String v) {
		String key = c.id + "|" + v + (v.equals("t:0") ? "|" + (Hud.ac() & 0xFFFFFF) : "");
		Identifier id = PRV.get(key);
		if (id != null) return id;
		id = Identifier.fromNamespaceAndPath("azure", "sty/" + Integer.toHexString(key.hashCode()));
		if (PRV.size() >= 900) {
			var tm = Minecraft.getInstance().getTextureManager();
			for (Identifier o : PRV.values()) tm.release(o);
			PRV.clear();
		}
		PRV.put(key, id);
		try {
			byte[] b = img(c, v);
			if (b == null && (v.startsWith("l:") || v.startsWith("p:"))) {
				for (String[] t : c.t) {
					b = v.startsWith("l:") ? Lib.get(c.lib, v.substring(2), t[0]) : packAny(v.substring(2), t);
					if (b != null) {
						if (c.id.equals("trident") && t[0].startsWith("entity/")) {
							byte[] tb = trd(b);
							if (tb != null) b = tb;
						}
						break;
					}
				}
			}
			if (b == null) b = vanilla(c.ic);
			if (c.id.endsWith("armor") && !v.startsWith("s:")) {
				byte[] q = quad(c, v);
				if (q != null) b = q;
			}
			if (c.id.equals("crystal") && !v.startsWith("t:") && !v.startsWith("c:")) {
				byte[] cb = cub(b);
				if (cb != null) b = cb;
			}
			if (c.id.equals("water")) b = mul(b, 0x3F76E4);
			com.mojang.blaze3d.platform.NativeImage n0;
			try {
				n0 = com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(b));
			} catch (Throwable e) {
				n0 = com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(vanilla(c.ic)));
			}
			if (n0 != null) {
				var ni = n0;
				if (c.pc != null) {
					int k = Math.max(1, ni.getWidth() / 64);
					var sq = new com.mojang.blaze3d.platform.NativeImage(c.pc[2] * k, c.pc[3] * k, false);
					for (int y = 0; y < c.pc[3] * k; y++) for (int x = 0; x < c.pc[2] * k; x++) sq.setPixel(x, y, ni.getPixel(Math.min(ni.getWidth() - 1, c.pc[0] * k + x), Math.min(ni.getHeight() - 1, c.pc[1] * k + y)));
					ni.close();
					ni = sq;
				} else if (ni.getHeight() > ni.getWidth()) {
					var sq = new com.mojang.blaze3d.platform.NativeImage(ni.getWidth(), ni.getWidth(), false);
					for (int y = 0; y < ni.getWidth(); y++) for (int x = 0; x < ni.getWidth(); x++) sq.setPixel(x, y, ni.getPixel(x, y));
					ni.close();
					ni = sq;
				}
				Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "azure_sty", ni));
			}
		} catch (Throwable e) {
		}
		return id;
	}

	static void rm(Path p) {
		try {
			if (!Files.exists(p)) return;
			try (var w = Files.walk(p)) {
				w.sorted(java.util.Comparator.reverseOrder()).forEach(f -> {
					try {
						Files.delete(f);
					} catch (Throwable e) {
					}
				});
			}
		} catch (Throwable e) {
		}
	}

	static Map<String, String> ap = new HashMap<>();
	static volatile boolean again;
	static volatile boolean fcw;
	static volatile boolean sch;
	static final String SKD = "assets/minecraft/optifine/sky/world0/";

	static boolean skyw(Path d, int i, Map<String, byte[]> sp) throws Exception {
		Path z = SkyL.sky(i);
		if (z == null) return false;
		Path o = d.resolve(SKD);
		Files.createDirectories(o);
		java.util.Set<String> got = new java.util.HashSet<>();
		try (ZipFile zf = new ZipFile(z.toFile())) {
			var en = zf.entries();
			while (en.hasMoreElements()) {
				ZipEntry e = en.nextElement();
				if (e.isDirectory()) continue;
				byte[] b;
				try (InputStream in = zf.getInputStream(e)) {
					b = in.readAllBytes();
				}
				Path f = o.resolve(e.getName());
				Files.createDirectories(f.getParent());
				Files.write(f, b);
				got.add(e.getName().toLowerCase());
				if (e.getName().toLowerCase().endsWith(".png")) sp.put(e.getName(), b);
			}
		}
		BufferedImage bl = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ImageIO.write(bl, "png", bo);
		Files.write(o.resolve("azure_blank.png"), bo.toByteArray());
		sp.put("azure_blank.png", bo.toByteArray());
		for (int k = 1; k <= 48; k++) if (!got.contains("sky" + k + ".properties")) Files.writeString(o.resolve("sky" + k + ".properties"), "source=./azure_blank.png\n");
		return true;
	}

	static void skylive(Minecraft m, Map<String, byte[]> sp) {
		try {
			for (Map.Entry<String, byte[]> e : sp.entrySet()) {
				var ni = com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(e.getValue()));
				m.getTextureManager().register(Identifier.withDefaultNamespace("optifine/sky/world0/" + e.getKey().toLowerCase()), new DynamicTexture(() -> "azure_sky", ni));
			}
			var packs = m.getResourcePackRepository().openAllSelected();
			try (var rm = new net.minecraft.server.packs.resources.MultiPackResourceManager(PackType.CLIENT_RESOURCES, packs)) {
				Class<?> k = Class.forName("me.flashyreese.mods.nuit.resource.SkyboxResourceListener");
				Object l = k.getConstructor().newInstance();
				var rf = k.getDeclaredMethod("readFiles", net.minecraft.server.packs.resources.ResourceManager.class);
				rf.setAccessible(true);
				Object mp = rf.invoke(l, rm);
				var ap2 = k.getDeclaredMethod("applySkyboxes", Map.class);
				ap2.setAccessible(true);
				ap2.invoke(l, mp);
			}
			Sky.sig = "";
		} catch (Throwable e) {
			Sv.lg("sky style failed: " + e);
		}
	}

	static List<String> paths(C c) {
		List<String> o = new ArrayList<>();
		for (String[] t : c.t) o.add(t[0]);
		o.addAll(xt(c));
		return o;
	}

	static String[] mt(C c, String p) {
		for (String[] t : c.t) if (t[0].equals(p)) return t;
		char k = p.charAt(p.length() - 5);
		for (String[] t : c.t) if (t[0].charAt(t[0].length() - 5) == k) return t;
		return c.t[0];
	}

	static byte[][] base(String p) {
		Object[] r = cur("textures/" + p, null);
		if (r == null) return new byte[][]{vanilla(p), vanilla(p + ".mcmeta")};
		Object[] m = cur("textures/" + p + ".mcmeta", (String) r[1]);
		return new byte[][]{(byte[]) r[0], m == null ? null : (byte[]) m[0]};
	}

	static byte[][] src(C c, String v, String p) {
		if (v.startsWith("t:") || v.startsWith("c:")) {
			byte[][] b = base(p);
			if (b[0] == null) return null;
			return new byte[][]{tint(b[0], v.startsWith("c:") ? Integer.parseInt(v.substring(2), 16) : tc(Integer.parseInt(v.substring(2)))), b[1]};
		}
		String[] t = mt(c, p);
		if (v.startsWith("p:")) {
			String pk = v.substring(2);
			if (p.startsWith("item/") && !c.id.equals("crystal")) {
				byte[] mb = mtx(pk, p.substring(5, p.length() - 4));
				if (mb != null) return new byte[][]{mb, null};
			}
			for (String a : t) {
				byte[] b = fromPack(pk, a);
				if (b != null && c.id.equals("crystal") && p.startsWith("item/")) {
					byte[] cb = cub(b);
					if (cb != null) return new byte[][]{cb, fromPack(pk, a + ".mcmeta"), b};
				}
				if (b != null) return new byte[][]{b, fromPack(pk, a + ".mcmeta")};
			}
			return null;
		}
		if (v.startsWith("l:")) {
			byte[] b = Lib.get(c.lib, v.substring(2), t[0]);
			if (b != null && c.id.equals("crystal") && p.startsWith("item/")) {
				byte[] cb = cub(b);
				if (cb != null) return new byte[][]{cb, Lib.get(c.lib, v.substring(2), t[0] + ".mcmeta"), b};
			}
			return b == null ? null : new byte[][]{b, Lib.get(c.lib, v.substring(2), t[0] + ".mcmeta")};
		}
		return null;
	}

	static byte[] wsrc(C c, String v, String p) {
		if (v.startsWith("t:") || v.startsWith("c:")) {
			byte[][] b = base(p);
			return b[0] == null ? null : tint(b[0], v.startsWith("c:") ? Integer.parseInt(v.substring(2), 16) : tc(Integer.parseInt(v.substring(2))));
		}
		String alt = p.replace("entity/equipment/humanoid/", "models/armor/").replace(".png", "_layer_1.png");
		if (v.startsWith("p:")) {
			byte[] b = fromPack(v.substring(2), p);
			return b != null ? b : fromPack(v.substring(2), alt);
		}
		if (v.startsWith("l:")) return Lib.get(c.lib, v.substring(2), p);
		return null;
	}

	static BufferedImage fx(C c, BufferedImage im) {
		BufferedImage o = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
		float k = c.id.equals("cobweb") && Cfg.on("seecob") ? Cfg.gi("seecob.op", 35) / 100f : 1f;
		for (int y = 0; y < im.getHeight(); y++) {
			for (int x = 0; x < im.getWidth(); x++) {
				int p = im.getRGB(x, y);
				if (k < 1f) p = Math.round((p >>> 24) * k) << 24 | p & 0xFFFFFF;
				o.setRGB(x, y, p);
			}
		}
		return o;
	}

	public static void cob() {
		fcw = true;
		apply();
	}

	public static void apply() {
		if (busy) {
			again = true;
			return;
		}
		busy = true;
		again = false;
		boolean fc = fcw;
		fcw = false;
		Minecraft m = Minecraft.getInstance();
		Map<String, String> now = new HashMap<>(sel());
		Map<String, String> was = new HashMap<>(ap);
		Map<String, byte[]> sp = new java.util.LinkedHashMap<>();
		Thread th = new Thread(() -> {
			boolean any = false;
			boolean m0 = Files.exists(rp().resolve(PK).resolve("assets/minecraft/models/item/end_crystal.json"));
			md3 = false;
			Map<String, Object[]> lv = new java.util.LinkedHashMap<>();
			try {
				Path d = rp().resolve(PK);
				String mb0 = lsm(d);
				rm(d);
				for (C c : list()) {
					String v = now.getOrDefault(c.id, "");
					boolean ch = !v.equals(was.getOrDefault(c.id, "")) || fc && c.id.equals("cobweb");
					if (v.isEmpty() && !ch) continue;
					boolean pw = packWins(c);
					for (String p : paths(c)) {
						String ow = ownp(p);
						if (pw && ow != null) {
							if (ch) {
								Object[] r2 = cur("textures/" + p, "file/" + ow);
								if (r2 != null) {
									BufferedImage im2 = rd((byte[]) r2[0]);
									Object[] m2 = cur("textures/" + p + ".mcmeta", "file/" + ow);
									if (im2 != null) lv.put(p, new Object[]{fx(c, im2), ft(m2 == null ? null : (byte[]) m2[0])});
								}
							}
							continue;
						}
						byte[][] r = v.isEmpty() ? null : src(c, v, p);
						if (r != null && r[0] != null) {
							Path f = d.resolve("assets/minecraft/textures/" + p);
							Files.createDirectories(f.getParent());
							Files.write(f, r.length > 2 ? r[2] : r[0]);
							if (r.length > 2) {
								md3 = true;
								Path mf = d.resolve("assets/minecraft/models/item/end_crystal.json");
								Files.createDirectories(mf.getParent());
								Files.writeString(mf, cmdl(rd(r[2])));
							}
							if (r[1] != null) Files.write(d.resolve("assets/minecraft/textures/" + p + ".mcmeta"), r[1]);
							any = true;
							if (!c.id.equals("crystal") && !v.startsWith("p:")) van(d, p);
						}
						if (!ch) continue;
						byte[][] o = r != null && r[0] != null ? r : base(p);
						if (o[0] == null) continue;
						BufferedImage im = rd(o[0]);
						if (im != null) lv.put(p, new Object[]{fx(c, im), ft(o[1])});
					}
					if (v.startsWith("p:") && !c.id.equals("crystal")) for (String p : paths(c)) {
						if (pw && ownp(p) != null) continue;
						if (mdl(v.substring(2), d, p)) any = true;
						else van(d, p);
					}
				}
				if (sep()) for (C[] grp : new C[][]{DP, NP}) {
					String lf = grp[0].wf;
					boolean ch = false;
					boolean anyv = false;
					for (C pc : grp) {
						if (pc.rg == null) continue;
						String v = now.getOrDefault(pc.id, "");
						if (!v.equals(was.getOrDefault(pc.id, ""))) ch = true;
						if (!v.isEmpty()) anyv = true;
					}
					if (!ch && !anyv) continue;
					if (packWins(grp[0]) && top("textures/" + lf) != null) continue;
					byte[][] bs = base(lf);
					if (bs[0] == null) continue;
					BufferedImage b0 = rd(bs[0]);
					Map<C, BufferedImage> pim = new HashMap<>();
					int k = Math.max(1, b0.getWidth() / 64);
					for (C pc : grp) {
						if (pc.rg == null) continue;
						String v = now.getOrDefault(pc.id, "");
						if (v.isEmpty()) continue;
						byte[] pb = wsrc(pc, v, lf);
						BufferedImage pi = pb == null ? null : rd(pb);
						if (pi == null) continue;
						pim.put(pc, pi);
						k = Math.max(k, pi.getWidth() / 64);
					}
					BufferedImage out = new BufferedImage(64 * k, b0.getHeight() * 64 * k / b0.getWidth(), BufferedImage.TYPE_INT_ARGB);
					for (int y = 0; y < out.getHeight(); y++) for (int x = 0; x < out.getWidth(); x++) out.setRGB(x, y, b0.getRGB(x * b0.getWidth() / out.getWidth(), y * b0.getHeight() / out.getHeight()));
					for (C pc : grp) {
						BufferedImage pi = pim.get(pc);
						if (pi == null) continue;
						int pk = Math.max(1, pi.getWidth() / 64);
						for (int y = 0; y < pc.rg[3] * k; y++) for (int x = 0; x < pc.rg[2] * k; x++) {
							int sx = Math.min(pi.getWidth() - 1, (pc.rg[0] * k + x) * pk / k);
							int sy = Math.min(pi.getHeight() - 1, (pc.rg[1] * k + y) * pk / k);
							int dx = pc.rg[0] * k + x;
							int dy = pc.rg[1] * k + y;
							if (dx < out.getWidth() && dy < out.getHeight()) out.setRGB(dx, dy, pi.getRGB(sx, sy));
						}
					}
					if (anyv) {
						Path f = d.resolve("assets/minecraft/textures/" + lf);
						Files.createDirectories(f.getParent());
						ByteArrayOutputStream bo = new ByteArrayOutputStream();
						ImageIO.write(out, "png", bo);
						Files.write(f, bo.toByteArray());
						any = true;
					}
					lv.put(lf, new Object[]{out, 1});
				}
				String sv = now.getOrDefault("sky", "");
				sch = !sv.equals(was.getOrDefault("sky", ""));
				if (sv.startsWith("s:") && skyw(d, Integer.parseInt(sv.substring(2)), sp)) any = true;
				mch = !lsm(d).equals(mb0);
				if (any) Files.writeString(d.resolve("pack.mcmeta"), "{\"pack\":{\"description\":\"Azure Styles\",\"pack_format\":75,\"min_format\":65,\"max_format\":9999}}");
			} catch (Throwable e) {
			}
			final boolean on = any;
			if (m0 != md3) lq = true;
			m.execute(() -> {
				for (Map.Entry<String, Object[]> e : lv.entrySet()) {
					try {
						if (Lv.put(e.getKey(), (BufferedImage) e.getValue()[0], (Integer) e.getValue()[1])) {
							lq = true;
							rlt = System.currentTimeMillis();
						}
					} catch (Throwable x) {
					}
				}
				if (mch) {
					lq = true;
					rlt = System.currentTimeMillis();
				}
				Lv.gui();
				ap = now;
				en(m, on);
				if (sch) skylive(m, sp);
				busy = false;
				if (again) apply();
			});
		}, "azure-styles");
		th.setDaemon(true);
		th.start();
	}

	public static volatile boolean lq;
	static volatile boolean mch;
	static long rlt;
	static long lrl;
	static volatile boolean md3;

	static String cf(int u, int v) {
		return "{\"uv\":[" + u + "," + v + "," + (u + 4) + "," + (v + 4) + "],\"texture\":\"#0\"}";
	}

	static String cel(float a, float b, int v) {
		return "{\"from\":[" + a + "," + a + "," + a + "],\"to\":[" + b + "," + b + "," + b + "],\"faces\":{\"up\":" + cf(4, v) + ",\"down\":" + cf(8, v) + ",\"north\":" + cf(4, v + 4) + ",\"south\":" + cf(12, v + 4) + ",\"east\":" + cf(0, v + 4) + ",\"west\":" + cf(8, v + 4) + "}}";
	}

	static String cmdl(BufferedImage im) {
		boolean core = im.getHeight() >= im.getWidth();
		return "{\"parent\":\"minecraft:block/block\",\"textures\":{\"0\":\"minecraft:item/end_crystal\",\"particle\":\"minecraft:item/end_crystal\"},\"elements\":[" + cel(2f, 14f, 0) + (core ? "," + cel(4.5f, 11.5f, 8) : "") + "]}";
	}

	static int ft(byte[] mm) {
		if (mm == null) return 1;
		try {
			var o = com.google.gson.JsonParser.parseString(new String(mm, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("animation");
			return o != null && o.has("frametime") ? Math.max(1, o.get("frametime").getAsInt()) : 1;
		} catch (Throwable e) {
			return 1;
		}
	}

	static int low(List<String> l) {
		for (int i = 0; i < l.size(); i++) if (l.get(i).startsWith("file/")) return i;
		return l.size();
	}

	static void en(Minecraft m, boolean on) {
		try {
			var repo = m.getResourcePackRepository();
			String id = "file/" + PK;
			if (on && !repo.getAvailableIds().contains(id)) repo.reload();
			List<String> old = new ArrayList<>(repo.getSelectedIds());
			List<String> ids = new ArrayList<>(old);
			ids.remove(id);
			if (on) ids.add(id);
			if (ids.equals(old)) return;
			repo.setSelected(ids);
			lpk = String.valueOf(repo.getSelectedIds());
			List<String> op = m.options.resourcePacks;
			op.remove(id);
			if (on) op.add(id);
			m.options.save();
		} catch (Throwable e) {
		}
	}

	static Object lt;
	static int tk;
	static int lcw = -1;

	static String lpk;

	public static void tick() {
		if (++tk % 10 != 0 || busy) return;
		if (rlt > 0 && System.currentTimeMillis() - rlt > 800 && System.currentTimeMillis() - lrl > 5000) {
			var mc = Minecraft.getInstance();
			if (mc.getOverlay() == null && !(mc.screen instanceof Mnu)) {
				rlt = 0;
				lq = false;
				lrl = System.currentTimeMillis();
				mc.reloadResourcePacks();
				return;
			}
		}
		try {
			var mc = Minecraft.getInstance();
			if (mc.getOverlay() == null) {
				String pk = String.valueOf(mc.getResourcePackRepository().getSelectedIds());
				if (lpk != null && !pk.equals(lpk) && !sel().isEmpty()) {
					lpk = pk;
					scanned = false;
					redo();
					return;
				}
				lpk = pk;
			}
		} catch (Throwable e) {
		}
		int k = Cfg.on("seecob") ? Cfg.gi("seecob.op", 35) : 100;
		Object t = k < 100 ? Lv.tex("block/cobweb.png") : null;
		if (k != lcw) {
			boolean ly = (lcw < 100) != (k < 100);
			boolean fst = lcw < 0;
			lcw = k;
			lt = t;
			if (!fst || k < 100) cob();
			Minecraft m = Minecraft.getInstance();
			if (ly && !fst && m.levelRenderer != null && m.level != null) m.levelRenderer.allChanged();
		} else if (k < 100 && t != lt) {
			lt = t;
			cob();
		}
	}
}
