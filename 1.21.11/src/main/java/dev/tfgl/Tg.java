package dev.tfgl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public class Tg {
	static final String[] PN = {"", "aqua", "kitty", "cry"};
	static DynamicTexture ti;
	static DynamicTexture ta;
	static DynamicTexture no;
	static String key = "";
	public static int swaps;

	static class T extends DynamicTexture {
		T(String n, NativeImage i) {
			super(() -> n, i);
			sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR);
		}
	}

	static NativeImage read(Identifier id) {
		try {
			Optional<Resource> r = Minecraft.getInstance().getResourceManager().getResource(id);
			if (r.isEmpty()) return null;
			try (InputStream s = r.get().open()) {
				return NativeImage.read(s);
			}
		} catch (Exception e) {
			return null;
		}
	}

	static NativeImage src(boolean item) {
		Cfg c = Cfg.c;
		NativeImage im = null;
		if (c.pre >= 1 && c.pre <= 3) {
			String n = PN[c.pre];
			im = read(Identifier.fromNamespaceAndPath("tuffglint", "textures/" + n + (item ? "_item" : "_armor") + ".png"));
			if (im == null) im = read(Identifier.fromNamespaceAndPath("tuffglint", "textures/" + n + "_item.png"));
		} else if (c.pre == 4) {
			try {
				Path p = Cfg.D.resolve(item ? "item.png" : "armor.png");
				if (!Files.exists(p)) p = Cfg.D.resolve("item.png");
				if (Files.exists(p)) im = NativeImage.read(Files.newInputStream(p));
			} catch (Exception e) {
				im = null;
			}
		}
		if (im == null) im = read(item ? ItemRenderer.ENCHANTED_GLINT_ITEM : ItemRenderer.ENCHANTED_GLINT_ARMOR);
		if (im == null) im = new NativeImage(1, 1, true);
		if (c.col != 0xffffff) {
			int tr = c.col >> 16 & 255, tg = c.col >> 8 & 255, tb = c.col & 255;
			for (int y = 0; y < im.getHeight(); y++) {
				for (int x = 0; x < im.getWidth(); x++) {
					int p = im.getPixel(x, y);
					int a = p >>> 24, r = p >> 16 & 255, g = p >> 8 & 255, b = p & 255;
					int m = Math.max(r, Math.max(g, b));
					im.setPixel(x, y, a << 24 | (m * tr / 255) << 16 | (m * tg / 255) << 8 | (m * tb / 255));
				}
			}
		}
		return im;
	}

	static void kill(DynamicTexture t) {
		if (t != null) t.close();
	}

	public static AbstractTexture tex(Identifier id, TextureManager tm) {
		boolean item = id.equals(ItemRenderer.ENCHANTED_GLINT_ITEM);
		boolean arm = id.equals(ItemRenderer.ENCHANTED_GLINT_ARMOR);
		if (!item && !arm) return tm.getTexture(id);
		Cfg c = Cfg.c;
		if (!c.on) return tm.getTexture(id);
		String k = c.pre + "|" + c.col + "|" + System.identityHashCode(Minecraft.getInstance().getResourceManager());
		if (!k.equals(key)) {
			key = k;
			kill(ti);
			kill(ta);
			ti = null;
			ta = null;
		}
		if (item ? !c.itm : !c.arm) {
			if (no == null) no = new T("tuff glint none", new NativeImage(1, 1, true));
			swaps++;
			return no;
		}
		if (c.pre == 0 && c.col == 0xffffff) return tm.getTexture(id);
		swaps++;
		if (item) {
			if (ti == null) ti = new T("tuff glint item", src(true));
			return ti;
		}
		if (ta == null) ta = new T("tuff glint armor", src(false));
		return ta;
	}
}
