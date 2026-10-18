package dev.snprb;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class img {
	static int ct;

	public record pre(NativeImage n, int gw, int gh) {
	}

	public final Identifier id;
	public final int gw, gh, pw, ph;

	public img(pre p) {
		gw = p.gw();
		gh = p.gh();
		pw = p.n().getWidth();
		ph = p.n().getHeight();
		id = Identifier.fromNamespaceAndPath("snprb", "t/" + ct++);
		Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "snprb", p.n()));
	}

	public static pre rd(File f, int bw, int bh, int sc, boolean dn) throws Exception {
		try (InputStream s = Files.newInputStream(f.toPath())) {
			NativeImage n = NativeImage.read(s);
			int ow = n.getWidth(), oh = n.getHeight();
			double r = Math.min((double) bw * sc / ow, (double) bh * sc / oh);
			int gw = Math.max(1, (int) (ow * r / sc)), gh = Math.max(1, (int) (oh * r / sc));
			if (dn && gw * sc < ow) {
				NativeImage o = new NativeImage(n.format(), gw * sc, gh * sc, false);
				n.resizeSubRectTo(0, 0, ow, oh, o);
				n.close();
				n = o;
			}
			return new pre(n, gw, gh);
		}
	}

	public void fr() {
		Minecraft.getInstance().getTextureManager().release(id);
	}
}
