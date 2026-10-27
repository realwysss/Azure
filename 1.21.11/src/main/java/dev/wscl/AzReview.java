package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class AzReview extends Screen {
	final Screen par;
	int rt;
	String tx = "";
	String msg = "";
	boolean bad;
	boolean busy;
	boolean done;
	int px;
	int py;
	int pw;
	int ph;

	public AzReview(Screen p) {
		super(Component.literal("Review"));
		par = p;
	}

	protected void init() {
		Api.url();
		pw = Math.min(380, width - 40);
		ph = 236;
		px = (width - pw) / 2;
		py = Math.max(10, (height - ph) / 2);
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (net.minecraft.util.Util.getMillis() % 1000000L) / 1000f, 1f);
	}

	List<String> wrap(String s, int w) {
		List<String> o = new ArrayList<>();
		StringBuilder cur = new StringBuilder();
		for (String word : s.split(" ", -1)) {
			String t = cur.length() == 0 ? word : cur + " " + word;
			if (Ui.w(t) <= w) {
				cur = new StringBuilder(t);
				continue;
			}
			if (cur.length() > 0) o.add(cur.toString());
			cur = new StringBuilder(word);
			while (Ui.w(cur.toString()) > w && cur.length() > 1) {
				int k = cur.length() - 1;
				while (k > 1 && Ui.w(cur.substring(0, k)) > w) k--;
				o.add(cur.substring(0, k));
				cur = new StringBuilder(cur.substring(k));
			}
		}
		o.add(cur.toString());
		return o;
	}

	int sx(int i) {
		return px + 16 + i * 26;
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		renderBackground(g, mx, my, d);
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.glow(g, px, py, pw, ph, 14, ac, 0.35f);
		Ui.rr(g, px, py, pw, ph, 14, Ui.al(Ui.tn(0x0C0A1A), 0.94f));
		Ui.ro(g, px, py, pw, ph, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.ts(g, "Write a review", px + 16, py + 14, 14f, 0xFFFFFF, 1f);
		String nm = minecraft.getUser().getName();
		Ui.tx(g, "as " + nm, px + 16, py + 36, 0xFFFFFF, 0.5f);
		for (int i = 0; i < 5; i++) {
			boolean on = i < rt;
			boolean ov = in(mx, my, sx(i), py + 54, 22, 22);
			Ic.dr(g, on ? "starf" : "star", sx(i) + 11, py + 65, 18 + (ov ? 2 : 0), Ui.al(on ? 0xFFD24A : 0xFFFFFF, on ? 1f : ov ? 0.7f : 0.3f));
		}
		int bx = px + 16;
		int by = py + 84;
		int bw = pw - 32;
		int bh = 90;
		Ui.rr(g, bx, by, bw, bh, 8, Ui.al(0x000000, 0.4f));
		Ui.ro(g, bx, by, bw, bh, 8, Ui.al(0xFFFFFF, 0.14f));
		List<String> ls = wrap(tx, bw - 16);
		boolean blink = !busy && !done && (net.minecraft.util.Util.getMillis() / 500) % 2 == 0;
		if (tx.isEmpty()) Ui.tx(g, "Type what you think of Azure", bx + 8, by + 8, 0xFFFFFF, 0.3f);
		int first = Math.max(0, ls.size() - 7);
		for (int i = first; i < ls.size(); i++) Ui.tx(g, ls.get(i), bx + 8, by + 8 + (i - first) * 11, 0xFFFFFF, 1f);
		if (blink) {
			String last = ls.get(ls.size() - 1);
			g.fill(bx + 8 + Ui.w(last), by + 7 + (ls.size() - 1 - first) * 11, bx + 9 + Ui.w(last), by + 17 + (ls.size() - 1 - first) * 11, Ui.al(ac, 1f));
		}
		Ui.tx(g, tx.length() + "/400", bx + bw - Ui.w(tx.length() + "/400") - 2, by + bh + 4, 0xFFFFFF, 0.4f);
		int ok = rt > 0 && tx.trim().length() >= 10 && !busy && !done ? 1 : 0;
		btn(g, mx, my, ac, px + 16, py + ph - 38, 96, 22, done ? "Posted" : busy ? "Posting..." : "Post", ok == 1);
		btn(g, mx, my, ac, px + 120, py + ph - 38, 70, 22, "Back", true);
		if (!msg.isEmpty()) Ui.tx(g, msg, px + 200, py + ph - 33, bad ? 0xFF8080 : 0x8BE9B0, 1f);
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean on) {
		boolean ov = on && in(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 10, Ui.al(Vis.mix(Ui.tn(0x0C0A1A), ac, ov ? 0.55f : on ? 0.3f : 0.05f), 0.9f));
		Ui.ro(g, x, y, w, h, 10, Ui.al(ov || on ? ac : 0xFFFFFF, on ? 0.8f : 0.15f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 4f, 9f, 0xFFFFFF, on ? 1f : 0.4f);
	}

	void post() {
		if (Api.base.isEmpty()) {
			bad = true;
			msg = "Not connected yet, try again";
			Api.url();
			return;
		}
		busy = true;
		bad = false;
		msg = "";
		String body = "{\"n\":" + Api.q(minecraft.getUser().getName()) + ",\"r\":" + rt + ",\"t\":" + Api.q(tx.trim()) + ",\"s\":\"game\"}";
		Thread t = new Thread(() -> {
			try {
				Api.post("/review", body);
				minecraft.execute(() -> {
					busy = false;
					done = true;
					msg = "Thanks, it shows once approved";
				});
			} catch (Throwable e) {
				minecraft.execute(() -> {
					busy = false;
					bad = true;
					msg = e.getMessage() != null && e.getMessage().contains("429") ? "Too many reviews, try later" : "Check your text, no links";
				});
			}
		}, "azure-review");
		t.setDaemon(true);
		t.start();
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		for (int i = 0; i < 5; i++) if (in(e.x(), e.y(), sx(i), py + 54, 22, 22)) {
			rt = i + 1;
			Snd.p("click");
			return true;
		}
		if (in(e.x(), e.y(), px + 16, py + ph - 38, 96, 22) && rt > 0 && tx.trim().length() >= 10 && !busy && !done) {
			Snd.p("click");
			post();
			return true;
		}
		if (in(e.x(), e.y(), px + 120, py + ph - 38, 70, 22)) {
			onClose();
			return true;
		}
		return super.mouseClicked(e, dbl);
	}

	public boolean charTyped(CharacterEvent e) {
		String s = e.codepointAsString();
		if (!busy && !done && !s.isEmpty() && s.charAt(0) >= 32 && tx.length() < 400) {
			tx += s;
			return true;
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (e.key() == GLFW.GLFW_KEY_BACKSPACE && !tx.isEmpty() && !busy && !done) {
			tx = tx.substring(0, tx.length() - 1);
			return true;
		}
		if (e.key() == GLFW.GLFW_KEY_V && e.hasControlDown() && !busy && !done) {
			String c = minecraft.keyboardHandler.getClipboard().replace("\n", " ").replace("\r", " ");
			tx = (tx + c).substring(0, Math.min(400, (tx + c).length()));
			return true;
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
