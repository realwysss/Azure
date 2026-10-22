package dev.wscl;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class Tf {
	static final Style ST = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("azure", "azure")));
	static final Map<String, Component> CC = new LinkedHashMap<>(256, 0.75f, true) {
		protected boolean removeEldestEntry(Map.Entry<String, Component> e) {
			return size() > 600;
		}
	};

	public static boolean on() {
		return Cfg.gi("afont", 1) == 1;
	}

	public static Component c(String s) {
		if (!on()) return Component.literal(s);
		Component c = CC.get(s);
		if (c == null) {
			c = Component.literal(s).withStyle(ST);
			CC.put(s, c);
		}
		return c;
	}

	public static int fw(String s) {
		return Minecraft.getInstance().font.width(c(s));
	}

	static void put(GuiGraphics g, String s, int color, boolean sh) {
		var f = Minecraft.getInstance().font;
		Component c = c(s);
		if (sh && on() && (((color >> 16) & 255) + ((color >> 8) & 255) + (color & 255)) > 384) g.drawString(f, c, 1, 1, (color & 0xFF000000) | ((color & 0xFCFCFC) >> 2), false);
		g.drawString(f, c, 0, 0, color, sh && !on());
	}

	public static void ds(GuiGraphics g, String s, int x, int y, int color, boolean sh) {
		var p = g.pose();
		p.pushMatrix();
		p.translate(x, y);
		put(g, s, color, sh);
		p.popMatrix();
	}

	public static float wf(String s, float size) {
		return fw(s) * size / 9f;
	}

	public static int w(String s, float size) {
		return Math.round(wf(s, size));
	}

	public static void dr(GuiGraphics g, String s, float x, float y, float size, int color) {
		if (s.isEmpty() || (color >>> 24) == 0) return;
		float k = size / 9f;
		var p = g.pose();
		p.pushMatrix();
		p.translate(x, y);
		p.scale(k, k);
		put(g, s, color, size < 14f);
		p.popMatrix();
	}
}
