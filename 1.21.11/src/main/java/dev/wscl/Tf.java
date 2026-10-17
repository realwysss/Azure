package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class Tf {
	public static float wf(String s, float size) {
		return Minecraft.getInstance().font.width(s) * size / 9f;
	}

	public static int w(String s, float size) {
		return Math.round(wf(s, size));
	}

	public static void dr(GuiGraphics g, String s, float x, float y, float size, int color) {
		if (s.isEmpty() || (color >>> 24) == 0) return;
		var f = Minecraft.getInstance().font;
		float k = size / 9f;
		var p = g.pose();
		p.pushMatrix();
		p.translate(x, y);
		p.scale(k, k);
		g.drawString(f, s, 0, 0, color, size < 14f);
		p.popMatrix();
	}
}
