package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class AzFriends extends Screen {
	final Screen par;
	String nm = "";
	boolean typing;
	String msg = "";
	boolean bad;
	String rm = "";
	long rmT;
	float sc;
	float tsc;
	int px;
	int py;
	int pw;
	int ph;
	final List<Object[]> hits = new ArrayList<>();
	final java.util.Map<String, Float> an = new java.util.HashMap<>();

	public AzFriends(Screen p) {
		super(Component.literal("Friends"));
		par = p;
	}

	protected void init() {
		Api.url();
		Frn.refresh(true);
		pw = Math.min(460, width - 24);
		ph = Math.min(height - 24, 340);
		px = (width - pw) / 2;
		py = (height - ph) / 2;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (net.minecraft.util.Util.getMillis() % 1000000L) / 1000f, 1f);
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	float a(String k, boolean on) {
		float v = an.getOrDefault(k, 0f);
		v += ((on ? 1f : 0f) - v) * 0.3f;
		an.put(k, v);
		return v;
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri, int c0, int c1, Runnable r) {
		boolean ov = in(mx, my, x, y, w, h) && my >= c0 && my < c1;
		float k = a("b" + t + x + "_" + y, ov);
		Th.box(g, x, y, w, h, Vis.mix(Th.pn(), ac, pri ? 0.3f + 0.3f * k : 0.06f + 0.2f * k), 0.92f, pri || k > 0.05f ? ac : 0xFFFFFF, pri ? 0.85f : 0.15f + 0.6f * k);
		Th.ct(g, t, x + w / 2f, y + h / 2f, 8.5f, 0xFFFFFF, 1f);
		hits.add(new Object[]{x, Math.max(y, c0), w, Math.min(y + h, c1) - Math.max(y, c0), r});
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		renderBackground(g, mx, my, d);
		hits.clear();
		Frn.fast = System.currentTimeMillis();
		Frn.tick();
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.glow(g, px, py, pw, ph, 14, ac, 0.3f);
		Th.box(g, px, py, pw, ph, 0f);
		Ui.ts(g, "Friends", px + 16, py + 14, 14f, 0xFFFFFF, 1f);
		if (Frn.in() && Frn.ld) {
			String s = Frn.onl() + " online, " + Frn.fr.size() + (Frn.fr.size() == 1 ? " friend" : " friends");
			Ui.tx(g, s, px + 16 + Ui.w("Friends", 14f) + 10, py + 17, 0xFFFFFF, 0.5f);
		}
		btn(g, mx, my, ac, px + pw - 66, py + 10, 54, 20, "Back", false, 0, 9999, this::onClose);
		int y = py + 44;
		int x0 = px + 16;
		int w = pw - 32;
		if (!Frn.premium()) {
			note(g, x0, y, w, "Friends need a Microsoft account.", "Offline accounts can not be checked, so nobody could be sure it is really you.");
			return;
		}
		if (!Frn.in()) {
			note(g, x0, y, w, "Verify your account once", "Azure asks Mojang to confirm this account is yours, the same way a server does. Your password and token never leave this pc.");
			btn(g, mx, my, ac, x0, y + 62, 150, 24, Frn.busy ? "Checking..." : "Verify with Mojang", true, 0, 9999, () -> {
				if (!Frn.busy) Frn.login(null);
			});
			if (!Frn.err.isEmpty()) Ui.tx(g, Frn.err, x0 + 162, y + 70, 0xFF8080, 1f);
			return;
		}
		Ui.tx(g, "Add a friend", x0, y, 0xFFFFFF, 0.7f);
		int fy = y + 12;
		int fw = w - 92;
		boolean fov = in(mx, my, x0, fy, fw, 22);
		Th.box(g, x0, fy, fw, 22, Vis.mix(Th.pn(), 0x000000, 0.3f), 0.95f, typing || fov ? ac : 0xFFFFFF, typing ? 0.8f : 0.15f);
		boolean blink = typing && (net.minecraft.util.Util.getMillis() / 500) % 2 == 0;
		if (nm.isEmpty() && !typing) Ui.tx(g, "Player name", x0 + 8, fy + 7, 0xFFFFFF, 0.35f);
		else Ui.tx(g, nm + (blink ? "_" : ""), x0 + 8, fy + 7, 0xFFFFFF, 1f);
		hits.add(new Object[]{x0, fy, fw, 22, (Runnable) () -> typing = true, "field"});
		btn(g, mx, my, ac, x0 + fw + 8, fy, 84, 22, "Send request", true, 0, 9999, this::send);
		if (!msg.isEmpty()) Ui.tx(g, msg, x0, fy + 28, bad ? 0xFF8080 : 0x8BE9B0, 1f);
		int ly0 = fy + 42;
		int ly1 = py + ph - 12;
		List<Object[]> rows = new ArrayList<>();
		for (String s : Frn.in) rows.add(new Object[]{0, s});
		for (String s : Frn.out) rows.add(new Object[]{1, s});
		for (Frn.F f : Frn.fr) rows.add(new Object[]{2, f});
		int rh = 34;
		int tot = rows.size() * (rh + 4) + 14 * 3;
		tsc = Math.max(0, Math.min(Math.max(0, tot - (ly1 - ly0)), tsc));
		sc += (tsc - sc) * 0.3f;
		g.enableScissor(x0 - 2, ly0, x0 + w + 2, ly1);
		int ry = ly0 - Math.round(sc);
		int last = -1;
		if (rows.isEmpty()) {
			Ui.tx(g, Frn.ld ? "No friends yet. Type a name above and send a request." : Frn.err.isEmpty() ? "Loading..." : Frn.err, x0, ry + 6, 0xFFFFFF, 0.5f);
		}
		for (Object[] r : rows) {
			int kind = (Integer) r[0];
			if (kind != last) {
				String h = kind == 0 ? "Requests for you" : kind == 1 ? "Sent requests" : "Your friends";
				Ui.tx(g, h, x0, ry + 2, ac, 0.9f);
				ry += 14;
				last = kind;
			}
			if (ry + rh >= ly0 && ry <= ly1) row(g, mx, my, ac, x0, ry, w, rh, kind, r[1], ly0, ly1);
			ry += rh + 4;
		}
		g.disableScissor();
	}

	void note(GuiGraphics g, int x, int y, int w, String t, String s) {
		Ui.ts(g, t, x, y + 4, 10f, 0xFFFFFF, 1f);
		List<String> l = wrap(s, w);
		for (int i = 0; i < l.size(); i++) Ui.tx(g, l.get(i), x, y + 22 + i * 11, 0xFFFFFF, 0.6f);
	}

	List<String> wrap(String s, int w) {
		List<String> o = new ArrayList<>();
		StringBuilder cur = new StringBuilder();
		for (String word : s.split(" ")) {
			String t = cur.length() == 0 ? word : cur + " " + word;
			if (Ui.w(t) > w && cur.length() > 0) {
				o.add(cur.toString());
				cur = new StringBuilder(word);
			} else cur = new StringBuilder(t);
		}
		o.add(cur.toString());
		return o;
	}

	void row(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, int kind, Object o, int c0, int c1) {
		String n = kind == 2 ? ((Frn.F) o).n : (String) o;
		boolean ov = in(mx, my, x, y, w, h);
		float k = a("r" + n + kind, ov);
		Th.box(g, x, y, w, h, Vis.mix(Th.pn(), ac, 0.05f + 0.08f * k), 0.85f, k > 0.05f ? ac : 0xFFFFFF, 0.08f + 0.3f * k);
		net.minecraft.resources.Identifier hid = Rv.head(n);
		if (hid != null) g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, hid, x + 7, y + 7, 0f, 0f, 20, 20, 64, 64, 64, 64);
		else Th.box(g, x + 7, y + 7, 20, 20, 0f);
		Ui.ts(g, n, x + 34, y + 8, 9.5f, 0xFFFFFF, 1f);
		int bx = x + w - 8;
		if (kind == 0) {
			Ui.tx(g, "Wants to be your friend", x + 34, y + 20, 0xFFFFFF, 0.45f);
			bx -= 58;
			btn(g, mx, my, ac, bx, y + 7, 58, 20, "Decline", false, c0, c1, () -> act("f_decline", n));
			bx -= 62;
			btn(g, mx, my, ac, bx, y + 7, 58, 20, "Accept", true, c0, c1, () -> act("f_accept", n));
		} else if (kind == 1) {
			boolean na = Frn.noaz.contains(n.toLowerCase(java.util.Locale.ROOT));
			Ui.tx(g, na ? "Has not opened Azure 1.7 yet, they get it then" : "Waiting for an answer", x + 34, y + 20, 0xFFFFFF, 0.45f);
			bx -= 58;
			btn(g, mx, my, ac, bx, y + 7, 58, 20, "Cancel", false, c0, c1, () -> act("f_decline", n));
		} else {
			Frn.F f = (Frn.F) o;
			String st = f.on ? (f.sv.isEmpty() ? "Online, in the menus" : "Online on " + f.sv) : Frn.seen(f.seen);
			if (f.on) Ui.rr(g, x + 23, y + 23, 6, 6, 3, Ui.al(0x5CE08A, 1f));
			Ui.tx(g, Ui.fit(st, w - 190), x + 34, y + 20, f.on ? 0x8BE9B0 : 0xFFFFFF, f.on ? 0.95f : 0.45f);
			boolean sure = rm.equals(n) && System.currentTimeMillis() - rmT < 4000;
			bx -= 62;
			btn(g, mx, my, ac, bx, y + 7, 62, 20, sure ? "Sure?" : "Remove", false, c0, c1, () -> {
				if (sure) {
					act("f_remove", n);
					rm = "";
				} else {
					rm = n;
					rmT = System.currentTimeMillis();
				}
			});
			String cur = minecraft.getCurrentServer() == null ? "" : minecraft.getCurrentServer().ip;
			if (f.on && !f.sv.isEmpty() && !f.sv.equalsIgnoreCase(cur)) {
				bx -= 50;
				btn(g, mx, my, ac, bx, y + 7, 46, 20, "Join", true, c0, c1, () -> join(f));
			}
		}
	}

	void act(String a, String n) {
		Snd.p("click");
		Frn.act(a, n, r -> {
			bad = !r.equals("friends") && !r.equals("none") && !r.equals("sent") && !r.equals("ok");
			msg = a.equals("f_accept") ? (bad ? "Could not accept, try again" : "You are now friends with " + n) : bad ? "Something went wrong, try again" : "";
		});
	}

	void send() {
		String n = nm.trim();
		if (!n.matches("[A-Za-z0-9_]{3,16}")) {
			bad = true;
			msg = "Names are 3 to 16 letters, numbers or _";
			return;
		}
		if (n.equalsIgnoreCase(minecraft.getUser().getName())) {
			bad = true;
			msg = "That is you";
			return;
		}
		Snd.p("click");
		bad = false;
		msg = "Sending...";
		Frn.act("f_add", n, r -> {
			bad = r.equals("bad") || r.equals("error") || r.equals("slow") || r.equals("login") || r.equals("nouser");
			msg = switch (r) {
				case "sent" -> "Request sent to " + n;
				case "friends" -> "You are now friends with " + n;
				case "slow" -> "Too many requests, wait a little";
				case "login" -> "Please verify your account again";
				case "nouser" -> "No Minecraft player is called " + n;
				default -> "Could not send the request";
			};
			if (!bad) nm = "";
		});
	}

	void join(Frn.F f) {
		Snd.p("click");
		ServerData sd = new ServerData(f.n, f.sv, ServerData.Type.OTHER);
		if (minecraft.level != null) minecraft.disconnectWithSavingScreen();
		ConnectScreen.startConnecting(new net.minecraft.client.gui.screens.TitleScreen(), minecraft, ServerAddress.parseString(f.sv), sd, false, null);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		boolean onField = false;
		for (int i = hits.size() - 1; i >= 0; i--) {
			Object[] h = hits.get(i);
			if (in(e.x(), e.y(), (Integer) h[0], (Integer) h[1], (Integer) h[2], (Integer) h[3])) {
				boolean was = typing;
				typing = false;
				((Runnable) h[4]).run();
				if (h.length > 5) typing = true;
				else if (was && !typing) onField = false;
				return true;
			}
		}
		typing = false;
		return super.mouseClicked(e, dbl);
	}

	public boolean mouseScrolled(double mx, double my, double h, double v) {
		tsc -= (float) v * 30f;
		return true;
	}

	public boolean charTyped(CharacterEvent e) {
		String s = e.codepointAsString();
		if (typing && s.matches("[A-Za-z0-9_]") && nm.length() < 16) {
			nm += s;
			return true;
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (typing) {
			if (e.key() == GLFW.GLFW_KEY_BACKSPACE && !nm.isEmpty()) {
				nm = nm.substring(0, nm.length() - 1);
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_ENTER || e.key() == GLFW.GLFW_KEY_KP_ENTER) {
				send();
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_V && e.hasControlDown()) {
				String c = minecraft.keyboardHandler.getClipboard().replaceAll("[^A-Za-z0-9_]", "");
				nm = (nm + c).substring(0, Math.min(16, (nm + c).length()));
				return true;
			}
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
