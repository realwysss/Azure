package dev.wscl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public class Lv {
	public static final Map<SpriteContents, SpriteContents.AnimationState> ANI = new WeakHashMap<>();

	public static void put(String path, BufferedImage im) {
		Minecraft m = Minecraft.getInstance();
		Identifier sid = Identifier.withDefaultNamespace(path.substring(0, path.length() - 4));
		boolean[] hit = {false};
		m.getAtlasManager().forEach((id, at) -> {
			TextureAtlasSprite s = at.getSprite(sid);
			if (s == null || s == at.missingSprite()) return;
			hit[0] = true;
			spr(at, s, im);
		});
		if (hit[0]) return;
		Identifier tid = Identifier.withDefaultNamespace("textures/" + path);
		AbstractTexture old = m.getTextureManager().getTexture(tid);
		GpuSampler sp = old.getSampler();
		NativeImage ni = nat(im, 0, 0, im.getWidth(), im.getHeight(), im.getWidth(), im.getHeight(), 0);
		m.getTextureManager().register(tid, new DynamicTexture(() -> "azure_style", ni) {
			{
				sampler = sp;
			}
		});
	}

	static GpuTexture tex(String path) {
		Identifier sid = Identifier.withDefaultNamespace(path.substring(0, path.length() - 4));
		GpuTexture[] o = {null};
		Minecraft.getInstance().getAtlasManager().forEach((id, at) -> {
			TextureAtlasSprite s = at.getSprite(sid);
			if (s != null && s != at.missingSprite() && o[0] == null) o[0] = at.getTexture();
		});
		return o[0];
	}

	static void spr(TextureAtlas at, TextureAtlasSprite s, BufferedImage im) {
		SpriteContents c = s.contents();
		int w = c.width();
		int h = c.height();
		int fw = im.getWidth();
		int fh = Math.max(1, fw * h / w);
		int n = Math.max(1, im.getHeight() / fh);
		SpriteContents.AnimationState st = ANI.get(c);
		CommandEncoder enc = RenderSystem.getDevice().createCommandEncoder();
		if (st == null) {
			GpuTexture t = at.getTexture();
			if (t == null || t.isClosed()) return;
			int ix = Math.round(s.getU0() * t.getWidth(0));
			int pd = Math.max(0, ix - s.getX());
			wr(enc, t, nat(im, 0, 0, fw, fh, w, h, pd), s.getX(), s.getY());
			return;
		}
		Map<Integer, GpuTextureView> fr = ((dev.wscl.mixin.AsAcc) st).azfr();
		for (Map.Entry<Integer, GpuTextureView> e : fr.entrySet()) {
			GpuTexture t = e.getValue().texture();
			if (t.isClosed()) continue;
			int k = e.getKey() % n;
			wr(enc, t, nat(im, 0, k * fh, fw, fh, w, h, 0), 0, 0);
		}
	}

	static void wr(CommandEncoder enc, GpuTexture t, NativeImage lv, int x, int y) {
		int pw = lv.getWidth();
		int ph = lv.getHeight();
		for (int mp = 0; mp < t.getMipLevels(); mp++) {
			if (pw >> mp < 1 || ph >> mp < 1) break;
			if (mp > 0) {
				NativeImage sm = half(lv);
				lv.close();
				lv = sm;
			}
			enc.writeToTexture(t, lv, mp, 0, x >> mp, y >> mp, pw >> mp, ph >> mp, 0, 0);
		}
		lv.close();
	}

	static NativeImage nat(BufferedImage im, int fx, int fy, int fw, int fh, int w, int h, int pd) {
		NativeImage o = new NativeImage(w + 2 * pd, h + 2 * pd, false);
		for (int y = 0; y < h + 2 * pd; y++) {
			for (int x = 0; x < w + 2 * pd; x++) {
				int sx = Math.min(w - 1, Math.max(0, x - pd));
				int sy = Math.min(h - 1, Math.max(0, y - pd));
				int ix = Math.min(im.getWidth() - 1, fx + sx * fw / w);
				int iy = Math.min(im.getHeight() - 1, fy + sy * fh / h);
				o.setPixel(x, y, im.getRGB(ix, iy));
			}
		}
		return o;
	}

	static NativeImage half(NativeImage s) {
		int w = Math.max(1, s.getWidth() / 2);
		int h = Math.max(1, s.getHeight() / 2);
		NativeImage o = new NativeImage(w, h, false);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int a = 0, r = 0, g = 0, b = 0;
				for (int dy = 0; dy < 2; dy++) {
					for (int dx = 0; dx < 2; dx++) {
						int c = s.getPixel(Math.min(s.getWidth() - 1, x * 2 + dx), Math.min(s.getHeight() - 1, y * 2 + dy));
						a += c >>> 24;
						r += c >> 16 & 255;
						g += c >> 8 & 255;
						b += c & 255;
					}
				}
				o.setPixel(x, y, a / 4 << 24 | r / 4 << 16 | g / 4 << 8 | b / 4);
			}
		}
		return o;
	}
}
