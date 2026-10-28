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
			new C("fire", "Fire", "block/fire_0.png", new String[]{"block/fire_0.png", "blocks/fire_layer_0.png"}, new String[]{"block/fire_1.png", "blocks/fire_layer_1.png"}),
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

	static byte[] tint(byte[] src, int c) {
		try {
			BufferedImage im = ImageIO.read(new ByteArrayInputStream(src));
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

	public static void apply() {
		if (busy) return;
		busy = true;
		Minecraft m = Minecraft.getInstance();
		Thread th = new Thread(() -> {
			boolean any = false;
			try {
				Path d = rp().resolve(PK);
				rm(d);
				for (C c : CS) {
					String v = get(c);
					if (v.isEmpty()) continue;
					for (String[] t : c.t) {
						byte[] b;
						String pk = null;
						if (v.startsWith("t:")) {
							byte[] src = vanilla(t[0]);
							b = src == null ? null : tint(src, tc(Integer.parseInt(v.substring(2))));
						} else {
							pk = v.substring(2);
							b = packAny(pk, t);
						}
						if (b == null) continue;
						Path f = d.resolve("assets/minecraft/textures/" + t[0]);
						Files.createDirectories(f.getParent());
						Files.write(f, b);
						byte[] mm = mcmeta(v, pk, t);
						if (mm != null) Files.write(d.resolve("assets/minecraft/textures/" + t[0] + ".mcmeta"), mm);
						any = true;
					}
				}
				if (any) {
					Files.createDirectories(d);
					Files.writeString(d.resolve("pack.mcmeta"), "{\"pack\":{\"description\":\"Azure Styles\",\"pack_format\":75,\"min_format\":65,\"max_format\":9999}}");
				}
			} catch (Throwable e) {
			}
			final boolean on = any;
			m.execute(() -> {
				try {
					var repo = m.getResourcePackRepository();
					repo.reload();
					List<String> ids = new ArrayList<>(repo.getSelectedIds());
					String id = "file/" + PK;
					List<String> old = new ArrayList<>(ids);
					ids.remove(id);
					if (on) ids.add(id);
					repo.setSelected(ids);
					m.options.updateResourcePacks(repo);
					if (old.equals(ids)) m.reloadResourcePacks();
				} catch (Throwable e) {
				}
				busy = false;
			});
		}, "azure-styles");
		th.setDaemon(true);
		th.start();
	}
}
