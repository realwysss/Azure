package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;

public class Ic {
	public static void ln(GuiGraphics g, int x1, int y1, int x2, int y2, int c) {
		int n = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
		if (n == 0) {
			g.fill(x1, y1, x1 + 1, y1 + 1, c);
			return;
		}
		for (int i = 0; i <= n; i++) {
			int x = x1 + Math.round((x2 - x1) * i / (float) n);
			int y = y1 + Math.round((y2 - y1) * i / (float) n);
			g.fill(x, y, x + 1, y + 1, c);
		}
	}

	static void ring(GuiGraphics g, int cx, int cy, int r, int c) {
		Ui.ro(g, cx - r, cy - r, r * 2 + 1, r * 2 + 1, r, c);
	}

	static void disc(GuiGraphics g, int cx, int cy, int r, int c) {
		Ui.rr(g, cx - r, cy - r, r * 2 + 1, r * 2 + 1, r, c);
	}

	static void box(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
		Ui.ro(g, x, y, w, h, r, c);
	}

	static java.util.List<String> NM;
	static java.util.Map<String, Integer> IX;
	static int IC = 8;
	static int AH = 640;
	static final net.minecraft.resources.Identifier TX = net.minecraft.resources.Identifier.fromNamespaceAndPath("azure", "textures/gui/icons.png");

	static void ld() {
		if (NM != null) return;
		NM = new java.util.ArrayList<>();
		try (var in = Ic.class.getResourceAsStream("/assets/azure/icons.json")) {
			var o = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(in, "UTF-8")).getAsJsonObject();
			for (var e : o.getAsJsonArray("names")) NM.add(e.getAsString());
			IX = new java.util.HashMap<>();
			for (int i = 0; i < NM.size(); i++) IX.putIfAbsent(NM.get(i), i);
			if (o.has("h")) AH = o.get("h").getAsInt();
		} catch (Exception e) {
		}
	}

	public static boolean has(String id) {
		ld();
		return IX != null && IX.containsKey(id);
	}

	public static void dr(GuiGraphics g, String id, int cx, int cy, int size, int c) {
		ld();
		Integer ix = IX == null ? null : IX.get(id);
		if (ix == null) return;
		int i = ix;
		g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TX, cx - size / 2, cy - size / 2, (i % IC) * 64f, (i / IC) * 64f, size, size, 64, 64, 512, AH, c);
	}

	public static void draw(GuiGraphics g, String id, int cx, int cy, int c) {
		if (has(id)) {
			dr(g, id, cx, cy, 14, c);
			return;
		}
		switch (id) {
			case "drop":
				disc(g, cx, cy + 2, 4, c);
				ln(g, cx, cy - 6, cx + 4, cy, c);
				ln(g, cx, cy - 6, cx - 4, cy, c);
				break;
			case "eye":
				box(g, cx - 7, cy - 4, 15, 9, 4, c);
				disc(g, cx, cy, 2, c);
				break;
			case "cross":
				ln(g, cx - 6, cy, cx - 2, cy, c);
				ln(g, cx + 2, cy, cx + 6, cy, c);
				ln(g, cx, cy - 6, cx, cy - 2, c);
				ln(g, cx, cy + 2, cx, cy + 6, c);
				break;
			case "box":
				box(g, cx - 5, cy - 5, 11, 11, 2, c);
				ln(g, cx - 5, cy - 1, cx + 5, cy - 1, c);
				break;
			case "keys":
				box(g, cx - 3, cy - 6, 6, 5, 1, c);
				box(g, cx - 7, cy, 6, 5, 1, c);
				box(g, cx + 1, cy, 6, 5, 1, c);
				break;
			case "gauge":
				box(g, cx - 7, cy - 7, 15, 15, 7, c);
				ln(g, cx, cy + 1, cx + 3, cy - 3, c);
				disc(g, cx, cy + 1, 1, c);
				break;
			case "pin":
				ring(g, cx, cy - 2, 4, c);
				ln(g, cx - 3, cy + 1, cx, cy + 6, c);
				ln(g, cx + 3, cy + 1, cx, cy + 6, c);
				break;
			case "wifi":
				g.fill(cx - 6, cy + 3, cx - 4, cy + 6, c);
				g.fill(cx - 2, cy, cx, cy + 6, c);
				g.fill(cx + 2, cy - 3, cx + 4, cy + 6, c);
				g.fill(cx + 6, cy - 6, cx + 8, cy + 6, c);
				break;
			case "flask":
				ln(g, cx - 2, cy - 6, cx - 2, cy - 1, c);
				ln(g, cx + 2, cy - 6, cx + 2, cy - 1, c);
				ln(g, cx - 2, cy - 1, cx - 6, cy + 5, c);
				ln(g, cx + 2, cy - 1, cx + 6, cy + 5, c);
				ln(g, cx - 6, cy + 5, cx + 6, cy + 5, c);
				ln(g, cx - 3, cy - 6, cx + 3, cy - 6, c);
				break;
			case "sun":
				disc(g, cx, cy, 3, c);
				ln(g, cx, cy - 7, cx, cy - 5, c);
				ln(g, cx, cy + 5, cx, cy + 7, c);
				ln(g, cx - 7, cy, cx - 5, cy, c);
				ln(g, cx + 5, cy, cx + 7, cy, c);
				ln(g, cx - 5, cy - 5, cx - 4, cy - 4, c);
				ln(g, cx + 4, cy + 4, cx + 5, cy + 5, c);
				ln(g, cx + 4, cy - 4, cx + 5, cy - 5, c);
				ln(g, cx - 5, cy + 5, cx - 4, cy + 4, c);
				break;
			case "clock":
				ring(g, cx, cy, 6, c);
				ln(g, cx, cy, cx, cy - 4, c);
				ln(g, cx, cy, cx + 3, cy, c);
				break;
			case "cloud":
				disc(g, cx - 3, cy + 1, 3, c);
				disc(g, cx + 3, cy + 1, 3, c);
				disc(g, cx, cy - 2, 4, c);
				break;
			case "rocket":
				box(g, cx - 2, cy - 7, 5, 11, 2, c);
				ln(g, cx - 2, cy + 1, cx - 5, cy + 5, c);
				ln(g, cx + 2, cy + 1, cx + 5, cy + 5, c);
				disc(g, cx, cy - 3, 1, c);
				break;
			case "shield":
				ln(g, cx - 5, cy - 5, cx + 5, cy - 5, c);
				ln(g, cx - 5, cy - 5, cx - 5, cy + 1, c);
				ln(g, cx + 5, cy - 5, cx + 5, cy + 1, c);
				ln(g, cx - 5, cy + 1, cx, cy + 6, c);
				ln(g, cx + 5, cy + 1, cx, cy + 6, c);
				break;
			case "tag":
				box(g, cx - 6, cy - 3, 13, 7, 2, c);
				disc(g, cx - 3, cy, 1, c);
				break;
			case "crown":
				ln(g, cx - 6, cy + 4, cx - 6, cy - 3, c);
				ln(g, cx - 6, cy - 3, cx - 2, cy + 1, c);
				ln(g, cx - 2, cy + 1, cx, cy - 5, c);
				ln(g, cx, cy - 5, cx + 2, cy + 1, c);
				ln(g, cx + 2, cy + 1, cx + 6, cy - 3, c);
				ln(g, cx + 6, cy - 3, cx + 6, cy + 4, c);
				ln(g, cx - 6, cy + 4, cx + 6, cy + 4, c);
				break;
			case "hand":
				box(g, cx - 4, cy - 1, 9, 7, 2, c);
				for (int i = 0; i < 4; i++) ln(g, cx - 3 + i * 2, cy - 1, cx - 3 + i * 2, cy - 6 + (i == 1 || i == 2 ? 0 : 2), c);
				break;
			case "ghost":
				box(g, cx - 5, cy - 6, 11, 12, 5, c);
				disc(g, cx - 2, cy - 2, 1, c);
				disc(g, cx + 2, cy - 2, 1, c);
				break;
			case "zoom":
				ring(g, cx - 1, cy - 1, 5, c);
				ln(g, cx + 3, cy + 3, cx + 7, cy + 7, c);
				break;
			case "chev":
				ln(g, cx - 6, cy - 5, cx - 1, cy, c);
				ln(g, cx - 1, cy, cx - 6, cy + 5, c);
				ln(g, cx, cy - 5, cx + 5, cy, c);
				ln(g, cx + 5, cy, cx, cy + 5, c);
				break;
			case "user":
				disc(g, cx, cy - 3, 3, c);
				box(g, cx - 5, cy + 2, 11, 5, 2, c);
				break;
			case "chat":
				box(g, cx - 7, cy - 5, 15, 10, 3, c);
				g.fill(cx - 4, cy - 1, cx - 3, cy, c);
				g.fill(cx, cy - 1, cx + 1, cy, c);
				g.fill(cx + 4, cy - 1, cx + 5, cy, c);
				break;
			case "music":
				ln(g, cx + 2, cy - 6, cx + 2, cy + 3, c);
				ln(g, cx + 2, cy - 6, cx + 6, cy - 4, c);
				disc(g, cx - 1, cy + 4, 2, c);
				break;
			case "bars":
				g.fill(cx - 6, cy + 1, cx - 4, cy + 6, c);
				g.fill(cx - 2, cy - 3, cx, cy + 6, c);
				g.fill(cx + 2, cy - 6, cx + 4, cy + 6, c);
				break;
			case "star":
				ln(g, cx, cy - 6, cx + 2, cy - 1, c);
				ln(g, cx + 2, cy - 1, cx + 7, cy - 1, c);
				ln(g, cx + 7, cy - 1, cx + 3, cy + 2, c);
				ln(g, cx + 3, cy + 2, cx + 5, cy + 7, c);
				ln(g, cx + 5, cy + 7, cx, cy + 4, c);
				ln(g, cx, cy + 4, cx - 5, cy + 7, c);
				ln(g, cx - 5, cy + 7, cx - 3, cy + 2, c);
				ln(g, cx - 3, cy + 2, cx - 7, cy - 1, c);
				ln(g, cx - 7, cy - 1, cx - 2, cy - 1, c);
				ln(g, cx - 2, cy - 1, cx, cy - 6, c);
				break;
			case "heart":
				disc(g, cx - 3, cy - 2, 3, c);
				disc(g, cx + 3, cy - 2, 3, c);
				ln(g, cx - 6, cy, cx, cy + 6, c);
				ln(g, cx + 6, cy, cx, cy + 6, c);
				break;
			case "palette":
				ring(g, cx, cy, 6, c);
				disc(g, cx - 2, cy - 2, 1, c);
				disc(g, cx + 2, cy - 2, 1, c);
				disc(g, cx, cy + 2, 1, c);
				break;
			case "sliders":
				ln(g, cx - 6, cy - 3, cx + 6, cy - 3, c);
				ln(g, cx - 6, cy + 3, cx + 6, cy + 3, c);
				disc(g, cx - 2, cy - 3, 2, c);
				disc(g, cx + 3, cy + 3, 2, c);
				break;
			case "bolt":
				ln(g, cx + 2, cy - 7, cx - 3, cy + 1, c);
				ln(g, cx - 3, cy + 1, cx + 2, cy + 1, c);
				ln(g, cx + 2, cy + 1, cx - 2, cy + 7, c);
				break;
			case "layers":
				ln(g, cx - 7, cy - 1, cx, cy - 5, c);
				ln(g, cx, cy - 5, cx + 7, cy - 1, c);
				ln(g, cx + 7, cy - 1, cx, cy + 3, c);
				ln(g, cx, cy + 3, cx - 7, cy - 1, c);
				ln(g, cx - 7, cy + 3, cx, cy + 7, c);
				ln(g, cx, cy + 7, cx + 7, cy + 3, c);
				break;
			case "grid":
				box(g, cx - 6, cy - 6, 5, 5, 1, c);
				box(g, cx + 1, cy - 6, 5, 5, 1, c);
				box(g, cx - 6, cy + 1, 5, 5, 1, c);
				box(g, cx + 1, cy + 1, 5, 5, 1, c);
				break;
			case "gear":
				ring(g, cx, cy, 4, c);
				ln(g, cx, cy - 7, cx, cy - 5, c);
				ln(g, cx, cy + 5, cx, cy + 7, c);
				ln(g, cx - 7, cy, cx - 5, cy, c);
				ln(g, cx + 5, cy, cx + 7, cy, c);
				break;
			case "globe":
				ring(g, cx, cy, 6, c);
				ln(g, cx - 6, cy, cx + 6, cy, c);
				box(g, cx - 3, cy - 6, 7, 13, 3, c);
				break;
			case "access":
				disc(g, cx, cy - 5, 1, c);
				ln(g, cx - 6, cy - 2, cx + 6, cy - 2, c);
				ln(g, cx, cy - 2, cx, cy + 2, c);
				ln(g, cx, cy + 2, cx - 3, cy + 7, c);
				ln(g, cx, cy + 2, cx + 3, cy + 7, c);
				break;
			case "power":
				ring(g, cx, cy + 1, 5, c);
				ln(g, cx, cy - 7, cx, cy - 1, c);
				break;
			case "search":
				ring(g, cx - 1, cy - 1, 4, c);
				ln(g, cx + 2, cy + 2, cx + 6, cy + 6, c);
				break;
			case "sword":
				ln(g, cx - 5, cy + 5, cx + 5, cy - 5, c);
				ln(g, cx - 5, cy + 1, cx - 1, cy + 5, c);
				break;
			case "gem":
				ln(g, cx - 5, cy - 2, cx - 2, cy - 6, c);
				ln(g, cx - 2, cy - 6, cx + 2, cy - 6, c);
				ln(g, cx + 2, cy - 6, cx + 5, cy - 2, c);
				ln(g, cx + 5, cy - 2, cx, cy + 6, c);
				ln(g, cx, cy + 6, cx - 5, cy - 2, c);
				ln(g, cx - 5, cy - 2, cx + 5, cy - 2, c);
				break;
			case "cube":
				box(g, cx - 5, cy - 5, 11, 11, 1, c);
				ln(g, cx - 5, cy - 5, cx - 2, cy - 7, c);
				ln(g, cx + 6, cy - 5, cx + 8, cy - 7, c);
				ln(g, cx - 2, cy - 7, cx + 8, cy - 7, c);
				break;
			case "flame":
				ring(g, cx, cy + 2, 4, c);
				ln(g, cx, cy - 7, cx - 3, cy - 1, c);
				ln(g, cx, cy - 7, cx + 3, cy - 1, c);
				break;
			case "skull":
				ring(g, cx, cy - 1, 5, c);
				disc(g, cx - 2, cy - 1, 1, c);
				disc(g, cx + 2, cy - 1, 1, c);
				ln(g, cx - 2, cy + 5, cx + 2, cy + 5, c);
				break;
			case "cape":
				ln(g, cx - 4, cy - 6, cx + 4, cy - 6, c);
				ln(g, cx - 4, cy - 6, cx - 6, cy + 6, c);
				ln(g, cx + 4, cy - 6, cx + 6, cy + 6, c);
				ln(g, cx - 6, cy + 6, cx + 6, cy + 6, c);
				break;
			default:
				ring(g, cx, cy, 5, c);
		}
	}
}
