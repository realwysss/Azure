package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class AzNote extends Screen {
	final Screen par;
	final String ttl;
	final String tag;
	final String date;
	final String url;
	final int col;
	final List<String[]> rows = new ArrayList<>();
	final String body;
	float sc;
	float tsc;
	int px;
	int py;
	int pw;
	int ph;
	int bx0;
	int by0;
	int bx1;
	int by1;
	int tot;
	long t0 = Util.getMillis();
	int lw = -1;

	public AzNote(Screen p, String title, String tag, String date, String body, String url, int col) {
		super(Component.literal(title));
		par = p;
		ttl = title;
		this.tag = tag;
		this.date = date;
		this.body = body == null ? "" : body;
		this.url = url == null ? "" : url;
		this.col = col;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	void build(int w) {
		rows.clear();
		tot = 0;
		for (String raw : body.split("\n")) {
			String l = raw.replace("**", "").replace("`", "").replace("\t", "  ");
			if (l.isBlank()) {
				rows.add(new String[]{"g", ""});
				tot += 6;
				continue;
			}
			String tp = "p";
			String x = l.trim();
			int ind = 0;
			if (x.startsWith("#")) {
				tp = "h";
				x = x.replaceFirst("^#+\s*", "");
			} else if (x.startsWith("- ") || x.startsWith("* ")) {
				tp = "b";
				x = x.substring(2);
				ind = 12;
			}
			float z = tp.equals("h") ? 10f : 8.5f;
			String cur = "";
			boolean first = true;
			for (String wd : x.split(" ")) {
				String t = cur.isEmpty() ? wd : cur + " " + wd;
				if (!cur.isEmpty() && Ui.w(t, z) > w - ind) {
					rows.add(new String[]{first ? tp : "c", cur, String.valueOf(ind)});
					first = false;
					tot += tp.equals("h") ? 14 : 11;
					cur = wd;
				} else cur = t;
			}
			if (!cur.isEmpty()) {
				rows.add(new String[]{first ? tp : "c", cur, String.valueOf(ind)});
				tot += tp.equals("h") ? 14 : 11;
			}
		}
		lw = w;
	}

	boolean in(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri) {
		boolean ov = in(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 9, Ui.al(pri ? ac : Ui.tn(0x120E26), pri ? 0.55f + (ov ? 0.25f : 0f) : 0.8f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(pri || ov ? ac : 0xFFFFFF, pri ? 0.9f : ov ? 0.8f : 0.16f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		float k = Ui.ez((Util.getMillis() - t0) / 320f);
		Ui.GA = k;
		pw = Math.min(width - 24, 470);
		ph = Math.min(height - 20, 330);
		px = (width - pw) / 2;
		py = (height - ph) / 2 + Math.round((1f - k) * 10);
		Ui.glow(g, px, py, pw, ph, 14, col, 0.28f);
		Ui.rr(g, px, py, pw, ph, 14, Ui.al(Ui.tn(0x090714), 0.96f));
		Ui.ro(g, px, py, pw, ph, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.rim(g, px, py, pw, ph, 14, col, 1f);
		Ui.rr(g, px + 16, py + 18, 3, 30, 1, Ui.al(col, 1f));
		Ui.ts(g, ttl, px + 28, py + 17, 14f, 0xFFFFFF, 1f);
		String sub = (tag.isEmpty() ? "" : tag + "   ") + date;
		Ui.ts(g, sub, px + 28, py + 36, 8.5f, 0xFFFFFF, 0.5f);
		Ui.rr(g, px + 16, py + 58, pw - 32, 1, 0, Ui.al(0xFFFFFF, 0.08f));
		bx0 = px + 18;
		bx1 = px + pw - 18;
		by0 = py + 68;
		by1 = py + ph - 48;
		int w = bx1 - bx0 - 10;
		if (w != lw) build(w);
		float mxs = Math.max(0, tot - (by1 - by0));
		tsc = Math.max(0f, Math.min(mxs, tsc));
		sc += (tsc - sc) * 0.35f;
		g.enableScissor(bx0 - 2, by0, bx1 + 2, by1);
		float y = by0 - sc;
		for (String[] r : rows) {
			switch (r[0]) {
				case "g":
					y += 6;
					break;
				case "h":
				case "p":
				case "b":
				case "c":
					boolean h = r[0].equals("h");
					int ind = Integer.parseInt(r[2]);
					float z = h ? 10f : 8.5f;
					if (r[0].equals("b")) Ui.rr(g, bx0 + 2, Math.round(y) + 3, 4, 4, 2, Ui.al(col, 1f));
					if (y > by0 - 14 && y < by1) Ui.ts(g, r[1], bx0 + (r[0].equals("c") && ind > 0 ? ind : r[0].equals("b") ? ind : 0), y, z, h ? Vis.mix(col, 0xFFFFFF, 0.7f) : 0xFFFFFF, h ? 1f : 0.78f);
					y += h ? 14 : 11;
					break;
				default:
					break;
			}
		}
		g.disableScissor();
		if (tot > by1 - by0) {
			int th = Math.max(16, (by1 - by0) * (by1 - by0) / tot);
			int ty = by0 + Math.round((by1 - by0 - th) * (sc / mxs));
			Ui.rr(g, bx1 + 4, by0, 3, by1 - by0, 1, Ui.al(0xFFFFFF, 0.05f));
			Ui.rr(g, bx1 + 4, ty, 3, th, 1, Ui.al(ac, 0.7f));
		}
		if (rows.isEmpty()) Ui.ts(g, "No details for this one.", bx0, by0 + 4, 9f, 0xFFFFFF, 0.45f);
		btn(g, mx, my, ac, px + 18, py + ph - 38, 90, 22, "Back", false);
		if (!url.isEmpty()) btn(g, mx, my, ac, px + pw - 18 - 130, py + ph - 38, 130, 22, "Open on GitHub", true);
		Ui.GA = 1f;
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		tsc -= (float) sy * 26f;
		return true;
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int mx = (int) e.x();
		int my = (int) e.y();
		if (in(mx, my, px + 18, py + ph - 38, 90, 22)) {
			Snd.p("click");
			onClose();
			return true;
		}
		if (!url.isEmpty() && in(mx, my, px + pw - 18 - 130, py + ph - 38, 130, 22)) {
			Snd.p("click");
			Util.getPlatform().openUri(url);
			return true;
		}
		return super.mouseClicked(e, dbl);
	}

	public boolean keyPressed(KeyEvent e) {
		if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}

	public boolean isPauseScreen() {
		return false;
	}
}
