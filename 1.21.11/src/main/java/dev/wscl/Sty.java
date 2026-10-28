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

		C m(String... md) {
			this.md = md;
			return this;
		}

		C(String id, String nm, String ic, String[]... t) {
			this.id = id;
			this.nm = nm;
			this.ic = ic;
			this.t = t;
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
			new C("snow", "Snow", "block/snow.png", new String[]{"block/snow.png", "blocks/snow.png"})
	};
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
			for (C c : CS) o.put(c.id, new ArrayList<>());
			try (var ds = Files.list(rp())) {
				for (Path p : (Iterable<Path>) ds::iterator) {
					String n = p.getFileName().toString();
					if (n.equals(PK)) continue;
					boolean dir = Files.isDirectory(p);
					if (!dir && !n.toLowerCase().endsWith(".zip")) continue;
					java.util.Set<String> has = new java.util.HashSet<>();
					if (dir) {
						for (C c : CS) for (String[] t : c.t) for (String a : t) if (Files.exists(p.resolve("assets/minecraft/textures/" + a))) has.add(a);
					} else {
						try (ZipFile z = new ZipFile(p.toFile())) {
							var en = z.entries();
							while (en.hasMoreElements()) {
								String e = en.nextElement().getName();
								int i = e.indexOf("assets/minecraft/textures/");
								if (i >= 0) has.add(e.substring(i + 26));
							}
						} catch (Throwable e) {
						}
					}
					for (C c : CS) {
						boolean ok = false;
						for (String a : c.t[0]) if (has.contains(a)) ok = true;
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
				if (id.endsWith(PK) || (only != null && !only.equals(id))) continue;
				try (InputStream in = r.open()) {
					return new Object[]{in.readAllBytes(), id};
				}
			}
		} catch (Throwable e) {
		}
		return null;
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

	static byte[] packAny(String pk, String[] alts) {
		for (String a : alts) {
			byte[] b = fromPack(pk, a);
			if (b != null) return b;
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
		if (v.startsWith("t:")) {
			byte[] b = vanilla(c.ic);
			return b == null ? null : tint(b, tc(Integer.parseInt(v.substring(2))));
		}
		if (v.startsWith("p:")) {
			for (String[] t : c.t) for (String a : t) if (a.equals(c.ic)) return packAny(v.substring(2), t);
			return packAny(v.substring(2), c.t[0]);
		}
		if (v.startsWith("l:")) {
			for (String[] t : c.t) for (String a : t) if (a.equals(c.ic)) return Lib.get(c.id, v.substring(2), t[0]);
			byte[] b = Lib.get(c.id, v.substring(2), c.t[0][0]);
			return b != null ? b : vanilla(c.ic);
		}
		return vanilla(c.ic);
	}

	public static Identifier prv(C c, String v) {
		String key = c.id + "|" + v + (v.equals("t:0") ? "|" + (Hud.ac() & 0xFFFFFF) : "");
		Identifier id = PRV.get(key);
		if (id != null) return id;
		id = Identifier.fromNamespaceAndPath("azure", "sty/" + Integer.toHexString(key.hashCode()));
		PRV.put(key, id);
		try {
			byte[] b = img(c, v);
			if (b != null) {
				var ni = com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(b));
				if (ni.getHeight() > ni.getWidth()) {
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
		if (v.startsWith("t:")) {
			byte[][] b = base(p);
			if (b[0] == null) return null;
			return new byte[][]{tint(b[0], tc(Integer.parseInt(v.substring(2)))), b[1]};
		}
		String[] t = mt(c, p);
		if (v.startsWith("p:")) {
			String pk = v.substring(2);
			for (String a : t) {
				byte[] b = fromPack(pk, a);
				if (b != null) return new byte[][]{b, fromPack(pk, a + ".mcmeta")};
			}
			return null;
		}
		if (v.startsWith("l:")) {
			byte[] b = Lib.get(c.id, v.substring(2), t[0]);
			return b == null ? null : new byte[][]{b, Lib.get(c.id, v.substring(2), t[0] + ".mcmeta")};
		}
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
		Thread th = new Thread(() -> {
			boolean any = false;
			Map<String, BufferedImage> lv = new java.util.LinkedHashMap<>();
			try {
				Path d = rp().resolve(PK);
				rm(d);
				for (C c : CS) {
					String v = now.getOrDefault(c.id, "");
					boolean ch = !v.equals(was.getOrDefault(c.id, "")) || fc && c.id.equals("cobweb");
					if (v.isEmpty() && !ch) continue;
					for (String p : paths(c)) {
						byte[][] r = v.isEmpty() ? null : src(c, v, p);
						if (r != null && r[0] != null) {
							Path f = d.resolve("assets/minecraft/textures/" + p);
							Files.createDirectories(f.getParent());
							Files.write(f, r[0]);
							if (r[1] != null) Files.write(d.resolve("assets/minecraft/textures/" + p + ".mcmeta"), r[1]);
							any = true;
						}
						if (!ch) continue;
						byte[][] o = r != null && r[0] != null ? r : base(p);
						if (o[0] == null) continue;
						BufferedImage im = rd(o[0]);
						if (im != null) lv.put(p, fx(c, im));
					}
				}
				if (any) Files.writeString(d.resolve("pack.mcmeta"), "{\"pack\":{\"description\":\"Azure Styles\",\"pack_format\":75,\"min_format\":65,\"max_format\":9999}}");
			} catch (Throwable e) {
			}
			final boolean on = any;
			m.execute(() -> {
				for (Map.Entry<String, BufferedImage> e : lv.entrySet()) {
					try {
						Lv.put(e.getKey(), e.getValue());
					} catch (Throwable x) {
					}
				}
				ap = now;
				en(m, on);
				busy = false;
				if (again) apply();
			});
		}, "azure-styles");
		th.setDaemon(true);
		th.start();
	}

	static void en(Minecraft m, boolean on) {
		try {
			var repo = m.getResourcePackRepository();
			String id = "file/" + PK;
			if (on && !repo.getAvailableIds().contains(id)) repo.reload();
			List<String> ids = new ArrayList<>(repo.getSelectedIds());
			if (ids.contains(id) == on) return;
			ids.remove(id);
			if (on) ids.add(id);
			repo.setSelected(ids);
			m.options.resourcePacks.remove(id);
			if (on) m.options.resourcePacks.add(id);
			m.options.save();
		} catch (Throwable e) {
		}
	}

	static Object lt;
	static int tk;
	static int lcw = -1;

	public static void tick() {
		if (++tk % 10 != 0 || busy) return;
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
