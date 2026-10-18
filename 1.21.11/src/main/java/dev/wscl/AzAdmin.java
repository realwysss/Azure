package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class AzAdmin extends Screen {
	static class F {
		String v = "";
		boolean mask;
		int max;

		F(boolean m, int x) {
			mask = m;
			max = x;
		}
	}

	final Screen par;
	final F pass = new F(true, 64);
	final F ttl = new F(false, 80);
	final F msg = new F(false, 400);
	F foc = pass;
	volatile boolean in;
	volatile String err = "";
	volatile String ok = "";
	volatile boolean busy;
	volatile int onlineN;
	volatile int total;
	volatile int today;
	volatile List<String[]> online = new ArrayList<>();
	volatile List<News.It> sent = new ArrayList<>();
	long t0 = Util.getMillis();
	long lastPoll;
	float sc;
	int cx;
	int cy;
	int cw = 520;
	int ch = 300;
	float f = 1f;
	String name = "";

	public AzAdmin(Screen p) {
		super(Component.literal("Admin"));
		par = p;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean at(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri) {
		boolean ov = at(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 9, Ui.al(pri ? ac : 0x120E26, pri ? 0.55f + (ov ? 0.25f : 0f) : 0.8f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(pri || ov ? ac : 0xFFFFFF, pri ? 0.9f : ov ? 0.8f : 0.16f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
	}

	void field(GuiGraphics g, int ac, F fl, String hint, int x, int y, int w, int h) {
		boolean fo = foc == fl;
		Ui.rr(g, x, y, w, h, 9, Ui.al(0xFFFFFF, fo ? 0.09f : 0.05f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(fo ? ac : 0xFFFFFF, fo ? 0.9f : 0.12f));
		String s = fl.mask ? "*".repeat(fl.v.length()) : fl.v;
		boolean car = fo && (Util.getMillis() / 500) % 2 == 0;
		if (s.isEmpty() && !fo) {
			Ui.ts(g, hint, x + 9, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 0.35f);
			return;
		}
		String shown = s;
		while (shown.length() > 1 && Ui.w(shown + "|", 9f) > w - 18) shown = shown.substring(1);
		Ui.ts(g, shown + (car ? "|" : ""), x + 9, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
	}

	String ago(long s) {
		long d = System.currentTimeMillis() / 1000 - s;
		if (d < 60) return "now";
		return (d / 60) + " min";
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		name = minecraft.getUser().getName();
		float k = Ui.ez((Util.getMillis() - t0) / 380f);
		Ui.GA = k;
		f = Math.min(1f, (height - 8f) / ch);
		mx = (int) (mx / f);
		my = (int) (my / f);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(f, f);
		int w = in ? cw : 330;
		int h = in ? ch : 190;
		cx = (int) ((width / f - w) / 2);
		cy = (int) ((height / f - h) / 2) + Math.round((1f - k) * 12);
		Ui.glow(g, cx, cy, w, h, 14, ac, 0.35f);
		Ui.rr(g, cx, cy, w, h, 14, Ui.al(0x090714, 0.96f));
		Ui.ro(g, cx, cy, w, h, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.ts(g, "Admin", cx + 22, cy + 18, 15f, 0xFFFFFF, 1f);
		Ui.ts(g, name, cx + w - 22 - Ui.w(name, 8.5f), cy + 22, 8.5f, 0xFFFFFF, 0.45f);
		if (!in) login(g, mx, my, ac, w, h);
		else dash(g, mx, my, ac, w, h);
		Ui.GA = 1f;
		ps.popMatrix();
		if (in && Util.getMillis() - lastPoll > 30000L && !busy) poll();
	}

	void login(GuiGraphics g, int mx, int my, int ac, int w, int h) {
		if (Api.url().isEmpty()) {
			Ui.ts(g, "The Azure server is not set up yet.", cx + 22, cy + 60, 9f, 0xFFFF9090, 1f);
			Ui.ts(g, "Deploy it first, then come back here.", cx + 22, cy + 74, 8.5f, 0xFFFFFF, 0.5f);
		} else {
			Ui.ts(g, "Sign in with the admin password", cx + 22, cy + 52, 9f, 0xFFFFFF, 0.6f);
			field(g, ac, pass, "Password", cx + 22, cy + 70, w - 44, 26);
			String s = busy ? "Checking..." : err;
			if (!s.isEmpty()) Ui.ts(g, s, cx + 22, cy + 104, 8.5f, busy ? 0xFFFFFF : 0xFFFF7070, busy ? 0.6f : 1f);
			btn(g, mx, my, ac, cx + w - 22 - 110, cy + h - 40, 110, 24, "Sign in", true);
		}
		btn(g, mx, my, ac, cx + 22, cy + h - 40, 90, 24, "Back", false);
	}

	void stat(GuiGraphics g, int ac, int x, int y, int w, String v, String l) {
		Ui.rr(g, x, y, w, 46, 10, Ui.al(0xFFFFFF, 0.05f));
		Ui.ts(g, v, x + 12, y + 8, 17f, 0xFFFFFF, 1f);
		Ui.ts(g, l, x + 12, y + 31, 8f, ac, 0.9f);
	}

	void dash(GuiGraphics g, int mx, int my, int ac, int w, int h) {
		int cwid = (w - 44 - 16) / 3;
		stat(g, ac, cx + 22, cy + 44, cwid, String.valueOf(onlineN), "online now");
		stat(g, ac, cx + 22 + cwid + 8, cy + 44, cwid, String.valueOf(total), "players ever");
		stat(g, ac, cx + 22 + (cwid + 8) * 2, cy + 44, cwid, "+" + today, "new in 24 hours");
		int lx = cx + 22;
		int ly = cy + 100;
		int lw = 238;
		int lh = h - 100 - 18;
		Ui.rr(g, lx, ly, lw, lh, 10, Ui.al(0xFFFFFF, 0.04f));
		Ui.ts(g, "Online now", lx + 12, ly + 9, 9f, 0xFFFFFF, 0.8f);
		g.enableScissor(lx, ly + 26, lx + lw, ly + lh - 4);
		float y = ly + 28 - sc;
		for (String[] r : online) {
			Ui.ts(g, r[0], lx + 12, y, 8.5f, 0xFFFFFF, 1f);
			Ui.ts(g, r[1], lx + 124, y, 8f, ac, 0.9f);
			Ui.ts(g, r[2], lx + lw - 12 - Ui.w(r[2], 8f), y, 8f, 0xFFFFFF, 0.4f);
			y += 12;
		}
		g.disableScissor();
		int rx = cx + 22 + lw + 12;
		int rw = w - 44 - lw - 12;
		Ui.ts(g, "Message for everyone", rx, ly + 2, 9f, 0xFFFFFF, 0.8f);
		field(g, ac, ttl, "Title", rx, ly + 18, rw, 24);
		field(g, ac, msg, "Message", rx, ly + 48, rw, 24);
		btn(g, mx, my, ac, rx + rw - 90, ly + 78, 90, 22, "Send", true);
		String s = busy ? "Working..." : !err.isEmpty() ? err : ok;
		if (!s.isEmpty()) Ui.ts(g, fitS(s, rw - 100), rx, ly + 84, 8f, !err.isEmpty() ? 0xFFFF7070 : 0xFF9CE8B0, 1f);
		Ui.ts(g, "Sent", rx, ly + 108, 8.5f, 0xFFFFFF, 0.5f);
		int sy = ly + 120;
		int n = 0;
		for (News.It it : sent) {
			if (n++ >= 3) break;
			Ui.rr(g, rx, sy, rw, 18, 7, Ui.al(0xFFFFFF, 0.05f));
			Ui.ts(g, fitS(it.t, rw - 40), rx + 8, sy + 5, 8f, 0xFFFFFF, 0.85f);
			boolean ov = at(mx, my, rx + rw - 20, sy, 18, 18);
			Ic.dr(g, "x", rx + rw - 11, sy + 9, 8, Ui.al(ov ? 0xFF6060 : 0xFFFFFF, ov ? 1f : 0.45f));
			sy += 22;
		}
		Ui.ts(g, "Esc to close", cx + w - 22 - Ui.w("Esc to close", 7.5f), cy + h - 14, 7.5f, 0xFFFFFF, 0.3f);
	}

	String fitS(String s, int max) {
		String o = s;
		while (o.length() > 1 && Ui.w(o, 8f) > max) o = o.substring(0, o.length() - 1);
		return o;
	}

	String body(String extra) {
		return "{\"name\":" + Api.q(name) + ",\"pass\":" + Api.q(pass.v) + extra + "}";
	}

	void poll() {
		busy = true;
		lastPoll = Util.getMillis();
		Thread t = new Thread(() -> {
			try {
				JsonObject o = JsonParser.parseString(Api.post("/a/stats", body(""))).getAsJsonObject();
				onlineN = o.getAsJsonArray("online").size();
				total = o.get("total").getAsInt();
				today = o.get("today").getAsInt();
				List<String[]> l = new ArrayList<>();
				for (JsonElement e : o.getAsJsonArray("online")) {
					JsonObject u = e.getAsJsonObject();
					l.add(new String[]{u.get("n").getAsString().isEmpty() ? "unknown" : u.get("n").getAsString(), u.get("v").getAsString(), ago(u.get("s").getAsLong())});
				}
				online = l;
				JsonArray ni = JsonParser.parseString(Api.get("/news")).getAsJsonObject().getAsJsonArray("items");
				List<News.It> s = new ArrayList<>();
				for (JsonElement e : ni) {
					JsonObject x = e.getAsJsonObject();
					News.It it = new News.It();
					it.id = x.get("id").getAsInt();
					it.t = x.get("t").getAsString();
					s.add(it);
				}
				sent = s;
				in = true;
				err = "";
			} catch (Exception e) {
				if (!in) err = e.getMessage() != null && e.getMessage().contains("403") ? "Wrong password or this account is not allowed" : "Could not reach the server";
				else err = "Could not refresh";
			} finally {
				busy = false;
			}
		}, "azure-admin");
		t.setDaemon(true);
		t.start();
	}

	void send() {
		if (busy || ttl.v.isBlank()) return;
		busy = true;
		ok = "";
		err = "";
		final String a = ttl.v.trim();
		final String b = msg.v.trim();
		Thread t = new Thread(() -> {
			try {
				Api.post("/a/post", body(",\"t\":" + Api.q(a) + ",\"b\":" + Api.q(b)));
				ttl.v = "";
				msg.v = "";
				ok = "Sent. Everyone sees it soon.";
				News.refresh();
				lastPoll = 0;
			} catch (Exception e) {
				err = "Could not send";
			} finally {
				busy = false;
			}
		}, "azure-admin-send");
		t.setDaemon(true);
		t.start();
	}

	void del(int id) {
		if (busy) return;
		busy = true;
		Thread t = new Thread(() -> {
			try {
				Api.post("/a/del", body(",\"id\":" + id));
				News.refresh();
				lastPoll = 0;
			} catch (Exception e) {
				err = "Could not delete";
			} finally {
				busy = false;
			}
		}, "azure-admin-del");
		t.setDaemon(true);
		t.start();
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int mx = (int) (e.x() / f);
		int my = (int) (e.y() / f);
		int w = in ? cw : 330;
		int h = in ? ch : 190;
		if (!in) {
			if (at(mx, my, cx + 22, cy + h - 40, 90, 24)) {
				onClose();
				return true;
			}
			if (at(mx, my, cx + 22, cy + 70, w - 44, 26)) {
				foc = pass;
				return true;
			}
			if (at(mx, my, cx + w - 22 - 110, cy + h - 40, 110, 24) && !busy) {
				err = "";
				poll();
				return true;
			}
			return super.mouseClicked(e, dbl);
		}
		int lw = 238;
		int rx = cx + 22 + lw + 12;
		int rw = w - 44 - lw - 12;
		int ly = cy + 100;
		foc = null;
		if (at(mx, my, rx, ly + 18, rw, 24)) foc = ttl;
		else if (at(mx, my, rx, ly + 48, rw, 24)) foc = msg;
		else if (at(mx, my, rx + rw - 90, ly + 78, 90, 22)) send();
		int sy = ly + 120;
		int n = 0;
		for (News.It it : sent) {
			if (n++ >= 3) break;
			if (at(mx, my, rx + rw - 20, sy, 18, 18)) {
				del(it.id);
				return true;
			}
			sy += 22;
		}
		return true;
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		sc = Math.max(0f, Math.min(Math.max(0, online.size() * 12 - 150), sc - (float) sy * 18f));
		return true;
	}

	public boolean charTyped(CharacterEvent e) {
		if (foc != null) {
			String s = e.codepointAsString();
			if (!s.isEmpty() && s.charAt(0) >= 32 && foc.v.length() < foc.max) foc.v += s;
			return true;
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (e.key() == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}
		if (foc != null) {
			if (e.key() == GLFW.GLFW_KEY_BACKSPACE && !foc.v.isEmpty()) {
				foc.v = foc.v.substring(0, foc.v.length() - 1);
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_V && e.hasControlDown()) {
				String c = minecraft.keyboardHandler.getClipboard();
				if (c != null) {
					String nv = foc.v + c.replace("\n", " ");
					foc.v = nv.substring(0, Math.min(foc.max, nv.length()));
				}
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_ENTER) {
				if (!in && foc == pass) poll();
				else if (in && foc == ttl) foc = msg;
				else if (in) send();
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_TAB && in) {
				foc = foc == ttl ? msg : ttl;
				return true;
			}
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		pass.v = "";
		minecraft.setScreen(par);
	}

	public boolean isPauseScreen() {
		return false;
	}
}
