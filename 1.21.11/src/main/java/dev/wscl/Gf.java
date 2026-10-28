package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class Gf {
	static final Identifier TX = Identifier.fromNamespaceAndPath("azure", "textures/font/pig_ascii.png");
	static final int[] AW = new int[128];
	static int tw = 128;
	static int th = 128;
	static int cs = 8;
	static boolean ld;

	static void ld() {
		if (ld) return;
		ld = true;
		java.util.Arrays.fill(AW, 6);
		try (var in = Gf.class.getResourceAsStream("/assets/azure/textures/font/pig_ascii.png")) {
			var im = javax.imageio.ImageIO.read(in);
			tw = im.getWidth();
			th = im.getHeight();
			cs = tw / 16;
			for (int c = 33; c < 127; c++) {
				int gx = (c % 16) * cs;
				int gy = (c / 16) * cs;
				int m = 0;
				for (int x = 0; x < cs; x++) for (int y = 0; y < cs; y++) if ((im.getRGB(gx + x, gy + y) >>> 24) > 0) m = Math.max(m, x + 1);
				AW[c] = m + 1;
			}
		} catch (Throwable e) {
		}
		AW[32] = 4;
	}

	public static boolean ok(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < 32 || c > 126) return false;
		}
		return true;
	}

	public static int w(String s) {
		ld();
		int n = 0;
		for (int i = 0; i < s.length(); i++) n += AW[s.charAt(i)];
		return n;
	}

	public static void dr(GuiGraphics g, String s, int x, int y, int c) {
		ld();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch != ' ') g.blit(RenderPipelines.GUI_TEXTURED, TX, x, y, (ch % 16) * cs, (ch / 16) * cs, 8, 8, cs, cs, tw, th, c);
			x += AW[ch];
		}
	}
}
