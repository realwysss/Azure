package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class AzSkin extends Screen {
	static class Cp {
		String id = "";
		String alias = "";
		String url = "";
		boolean active;
		byte[] png;
		Identifier tex;
	}

	static class Ht {
		int x;
		int y;
		int w;
		int h;
		boolean raw;
		Runnable r;
	}

	public static String api = "https://api.minecraftservices.com";
	public static String tk;
	final Screen par;
	final List<Ht> hits = new ArrayList<>();
	final List<Cp> capes = new ArrayList<>();
	Skn skn;
	PlayerSkin prev;
	byte[] png;
	Identifier tid;
	DynamicTexture dt;
	boolean slim;
	String nm = "";
	boolean nf;
	int tab;
	int sel = -2;
	int capeState;
	volatile String st = "";
	volatile boolean err;
	volatile boolean busy;
	long t0 = Util.getMillis();
	long ct;
	int sx;
	int sy;
	int ssh;
	int ssw;
	float f = 1f;
	int ph = 250;
	int pw = 300;
	int px;
	int py;
	float csc;
	boolean closing;
	java.util.function.Supplier<PlayerSkin> real;

	public AzSkin(Screen p) {
		super(Component.literal("Skin changer"));
		par = p;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	protected void init() {
		real = Acc.sk();
		int sh = Math.round(height * 0.6f);
		int sw = Math.round(sh * 0.7f);
		if (skn == null) {
			skn = new Skn(sw, sh, 0, 0, () -> prev != null ? prev : real.get());
			skn.mode = 1;
		}
		if (par instanceof AzTitle at && at.skin != null) {
			sx = at.skin.getX();
			sy = at.skin.getY();
			ssh = at.skin.getHeight();
			ssw = at.skin.getWidth();
		} else {
			sx = tx(sw) ;
			sy = ty();
			ssh = sh;
			ssw = sw;
		}
		if (capeState == 0) loadCapes();
	}

	int tx(int sw) {
		return Math.round(width * 0.27f - sw / 2f);
	}

	int ty() {
		return Math.round(height * 0.13f);
	}

	boolean in(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	void hit(int x, int y, int w, int h, boolean raw, Runnable r) {
		Ht t = new Ht();
		t.x = x;
		t.y = y;
		t.w = w;
		t.h = h;
		t.raw = raw;
		t.r = r;
		hits.add(t);
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri, boolean on, Runnable r) {
		boolean ov = in(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 9, Ui.al(pri || on ? ac : Ui.tn(0x120E26), pri ? 0.55f + (ov ? 0.25f : 0f) : on ? 0.35f : 0.8f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(pri || on || ov ? ac : 0xFFFFFF, pri ? 0.9f : on ? 0.85f : ov ? 0.8f : 0.16f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
		hit(x, y, w, h, false, r);
	}

	float q(float p) {
		p = Math.max(0f, Math.min(1f, p));
		return p * p * p * (p * (p * 6f - 15f) + 10f);
	}

	float eio(float p) {
		p = Math.max(0f, Math.min(1f, p));
		return p < 0.5f ? 4f * p * p * p : 1f - (float) Math.pow(-2f * p + 2f, 3) / 2f;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		hits.clear();
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		long now = Util.getMillis();
		if (closing && now - ct > 760) {
			minecraft.setScreen(par);
			return;
		}
		float k = closing ? 1f - q((now - ct) / 760f) : q((now - t0) / 950f);
		float pk = closing ? 1f - q((now - ct) / 460f) : q((now - t0 - 330) / 560f);
		int tsh = Math.round(height * 0.6f);
		int tsw = Math.round(tsh * 0.7f);
		int csh = Math.round(ssh + (tsh - ssh) * k);
		int csw = Math.round(csh * 0.7f);
		int cxm = Math.round((sx + ssw / 2f) * (1f - k) + (tx(tsw) + tsw / 2f) * k);
		int cyt = Math.round(sy + (ty() - sy) * k);
		skn.w.setWidth(csw);
		skn.w.setHeight(csh);
		skn.w.setPosition(cxm - csw / 2, cyt);
		skn.mode = !closing && tab == 1 ? 2 : 1;
		Skn.cape = !closing && (tab == 1 || sel >= 0) ? capeTex() : null;
		skn.render(g, mx, my, d);
		card(g, mx, my, ac, cxm, cyt + csh + 6, Math.max(0f, pk));
		f = Math.min(1f, (height - 8f) / ph);
		int rmx = (int) (mx / f);
		int rmy = (int) (my / f);
		int vw = Math.round(width / f);
		int vh = Math.round(height / f);
		pw = Math.max(230, Math.min(310, vw / 2 - 14));
		px = vw - pw - 22 + Math.round((1f - Math.max(0f, pk)) * 40);
		py = (vh - ph) / 2;
		Ui.GA = Math.max(0f, pk);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(f, f);
		panel(g, rmx, rmy, ac);
		ps.popMatrix();
		Ui.GA = 1f;
	}

	Identifier capeTex() {
		if (sel >= 0 && sel < capes.size()) return capes.get(sel).tex;
		if (sel == -2) for (Cp c : capes) if (c.active) return c.tex;
		return null;
	}

	void card(GuiGraphics g, int mx, int my, int ac, int cxm, int by, float a) {
		String n = minecraft.getUser().getName();
		boolean ias = true;
		String sub = "Switch account";
		int bw = Math.max(118, Math.max(Ui.w(n), Ui.w(sub, 8f)) + (ias ? 76 : 58));
		int bh = 30;
		int bx = cxm - bw / 2;
		boolean ov = ias && in(mx, my, bx, by, bw, bh);
		float old = Ui.GA;
		Ui.GA = a;
		Ui.rr(g, bx, by, bw, bh, 10, Ui.al(Vis.mix(Ui.tn(0x0E0B1E), ac, ov ? 0.3f : 0.1f), 0.72f));
		Ui.ro(g, bx, by, bw, bh, 10, Ui.al(ov ? ac : 0xFFFFFF, ov ? 0.85f : 0.14f));
		net.minecraft.client.gui.components.PlayerFaceRenderer.draw(g, real.get(), bx + 8, by + 7, 16, Ui.al(0xFFFFFF, 1f));
		Ui.tx(g, n, bx + 31, by + 6, 0xFFFFFF, 0.95f);
		Ui.ts(g, sub, bx + 31, by + 17, 8f, ias ? Vis.mix(0xFFFFFF, ac, 0.6f) : 0xFFFFFF, ias ? 0.9f : 0.45f);
		if (ias) Ic.dr(g, "chev", bx + bw - 14, by + bh / 2, 10, Ui.al(0xFFFFFF, 0.6f));
		Ui.GA = old;
		hit(bx, by, bw, bh, true, () -> minecraft.setScreen(new AzAcc(this)));
	}

	void panel(GuiGraphics g, int mx, int my, int ac) {
		Ui.glow(g, px, py, pw, ph, 14, ac, 0.3f);
		Ui.rr(g, px, py, pw, ph, 14, Ui.al(Ui.tn(0x090714), 0.94f));
		Ui.ro(g, px, py, pw, ph, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.rim(g, px, py, pw, ph, 14, ac, 1f);
		int tw = (pw - 36) / 2;
		tabBtn(g, mx, my, ac, px + 14, py + 14, tw, "Skin", "shirt", 0);
		tabBtn(g, mx, my, ac, px + 22 + tw, py + 14, tw, "Capes", "cape", 1);
		if (tab == 0) skinTab(g, mx, my, ac);
		else capeTab(g, mx, my, ac);
		btn(g, mx, my, ac, px + 14, py + ph - 36, 78, 22, "Back", false, false, () -> {
			closing = true;
			ct = Util.getMillis();
			if (tab == 1) skn.front();
			Skn.cape = null;
		});
		String s = busy ? "Working..." : st;
		if (!s.isEmpty()) {
			var l = font.split(Component.literal(s), pw - 28);
			for (int i = 0; i < Math.min(2, l.size()); i++) g.drawString(font, l.get(i), px + 14, py + ph - 70 + i * 11, err ? 0xFFFF6060 : 0xFF9CE8B0, false);
		}
	}

	void tabBtn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, String t, String ic, int i) {
		boolean on = tab == i;
		boolean ov = in(mx, my, x, y, w, 26);
		Ui.rr(g, x, y, w, 26, 9, Ui.al(on ? ac : 0xFFFFFF, on ? 0.35f : 0.05f + (ov ? 0.04f : 0f)));
		Ui.ro(g, x, y, w, 26, 9, Ui.al(on ? ac : 0xFFFFFF, on ? 0.85f : 0.12f));
		int tw = Ui.w(t, 9f);
		int ix = x + (w - tw - 18) / 2;
		Ic.dr(g, ic, ix + 7, y + 13, 14, Ui.al(0xFFFFFF, on ? 1f : 0.6f));
		Ui.ts(g, t, ix + 18, y + 9.5f, 9f, 0xFFFFFF, on ? 1f : 0.65f);
		hit(x, y, w, 26, false, () -> {
			if (tab != i && i == 0) skn.front();
			tab = i;
			nf = false;
			if (i == 1 && capeState == 0) loadCapes();
		});
	}

	void skinTab(GuiGraphics g, int mx, int my, int ac) {
		int rx = px + 14;
		int rw = pw - 28;
		btn(g, mx, my, ac, rx, py + 50, rw, 24, "Choose a PNG file", false, false, this::pick);
		Ui.tx(g, "Or copy the skin of a player", rx, py + 86, 0xFFFFFF, 0.55f);
		int bw = rw - 66;
		Ui.rr(g, rx, py + 100, bw, 24, 9, Ui.al(0xFFFFFF, nf ? 0.09f : 0.05f));
		Ui.ro(g, rx, py + 100, bw, 24, 9, Ui.al(nf ? ac : 0xFFFFFF, nf ? 0.9f : 0.12f));
		boolean car = nf && (Util.getMillis() / 500) % 2 == 0;
		if (nm.isEmpty() && !nf) Ui.tx(g, "Username", rx + 10, py + 108, 0xFFFFFF, 0.35f);
		else Ui.tx(g, nm + (car ? "|" : ""), rx + 10, py + 108, 0xFFFFFF, 1f);
		hit(rx, py + 100, bw, 24, false, () -> nf = true);
		btn(g, mx, my, ac, rx + bw + 6, py + 100, 60, 24, "Copy", false, false, this::copy);
		Ui.tx(g, "Model", rx, py + 136, 0xFFFFFF, 0.55f);
		int mw = (rw - 6) / 2;
		btn(g, mx, my, ac, rx, py + 150, mw, 22, "Classic", false, !slim, () -> setModel(false));
		btn(g, mx, my, ac, rx + mw + 6, py + 150, mw, 22, "Slim", false, slim, () -> setModel(true));
		btn(g, mx, my, ac, px + 14 + 86, py + ph - 36, pw - 28 - 86, 22, "Apply to account", true, false, this::apply);
	}

	void capeTab(GuiGraphics g, int mx, int my, int ac) {
		int rx = px + 14;
		int rw = pw - 28;
		if (capeState == 1) Ui.ts(g, "Loading your capes...", rx, py + 58, 9f, 0xFFFFFF, 0.6f);
		else if (capeState == 3) Ui.ts(g, "Could not load your capes", rx, py + 58, 9f, 0xFFFF7070, 1f);
		else {
			int cols = 3;
			int cw = (rw - 16) / cols;
			int chh = 88;
			int n = capes.size() + 1;
			int top = py + 50;
			int bot = py + ph - 76;
			float mxs = Math.max(0, ((n + cols - 1) / cols) * (chh + 8) - (bot - top));
			csc = Math.max(0f, Math.min(mxs, csc));
			g.enableScissor(rx, top, rx + rw, bot);
			for (int i = 0; i < n; i++) {
				int ci = i - 1;
				int x = rx + (i % cols) * (cw + 8);
				int y = top + (i / cols) * (chh + 8) - Math.round(csc);
				if (y + chh < top || y > bot) continue;
				boolean on = sel == ci || (sel == -2 && (ci < 0 ? activeIdx() < 0 : capes.get(ci).active));
				boolean ov = in(mx, my, x, y, cw, chh) && my >= top && my < bot;
				Ui.rr(g, x, y, cw, chh, 10, Ui.al(on ? ac : 0xFFFFFF, on ? 0.22f : 0.05f + (ov ? 0.05f : 0f)));
				Ui.ro(g, x, y, cw, chh, 10, Ui.al(on ? ac : 0xFFFFFF, on ? 0.9f : 0.12f));
				if (ci < 0) {
					Ic.dr(g, "x", x + cw / 2, y + 36, 18, Ui.al(0xFFFFFF, 0.5f));
					Ui.ts(g, "No cape", x + (cw - Ui.w("No cape", 8.5f)) / 2f, y + 72, 8.5f, 0xFFFFFF, 0.8f);
				} else {
					Cp c = capes.get(ci);
					if (c.tex != null) g.blit(RenderPipelines.GUI_TEXTURED, c.tex, x + (cw - 30) / 2, y + 10, 1f, 1f, 30, 48, 10, 16, 64, 32);
					String an = c.alias.isEmpty() ? "Cape" : c.alias;
					while (an.length() > 2 && Ui.w(an, 8f) > cw - 8) an = an.substring(0, an.length() - 1);
					Ui.ts(g, an, x + (cw - Ui.w(an, 8f)) / 2f, y + 66, 8f, 0xFFFFFF, 0.85f);
					if (c.active) Ui.ts(g, "Worn", x + (cw - Ui.w("Worn", 7.5f)) / 2f, y + 77, 7.5f, 0xFF9CE8B0, 0.9f);
				}
				if (my >= top && my < bot) {
					final int cc = ci;
					hit(x, y, cw, chh, false, () -> sel = cc);
				}
			}
			g.disableScissor();
			if (capes.isEmpty()) Ui.ts(g, "This account has no capes", rx + 2, top + chh + 14, 8.5f, 0xFFFFFF, 0.45f);
		}
		btn(g, mx, my, ac, px + 14 + 86, py + ph - 36, pw - 28 - 86, 22, "Wear this cape", true, false, this::equip);
	}

	int activeIdx() {
		for (int i = 0; i < capes.size(); i++) if (capes.get(i).active) return i;
		return -1;
	}

	void setModel(boolean sl) {
		slim = sl;
		if (png != null && tid != null) prev = new PlayerSkin(new ClientAsset.ResourceTexture(tid, tid), null, null, slim ? PlayerModelType.SLIM : PlayerModelType.WIDE, true);
	}

	void set(byte[] b, boolean sl, String msg) {
		try {
			BufferedImage bi = ImageIO.read(new ByteArrayInputStream(b));
			if (bi == null || bi.getWidth() != 64 || (bi.getHeight() != 64 && bi.getHeight() != 32)) {
				fail("The skin has to be a 64x64 or 64x32 PNG");
				return;
			}
			if (b.length > 1000000) {
				fail("That file is too big");
				return;
			}
			NativeImage ni = NativeImage.read(new ByteArrayInputStream(b));
			minecraft.execute(() -> {
				free();
				dt = new DynamicTexture(() -> "azure_skin", ni);
				tid = Identifier.fromNamespaceAndPath("azure", "skin_preview_" + UUID.randomUUID().toString().replace("-", ""));
				minecraft.getTextureManager().register(tid, dt);
				png = b;
				slim = sl;
				prev = new PlayerSkin(new ClientAsset.ResourceTexture(tid, tid), null, null, slim ? PlayerModelType.SLIM : PlayerModelType.WIDE, true);
				err = false;
				st = msg;
			});
		} catch (Throwable e) {
			fail("Could not read that file");
		}
	}

	void fail(String m) {
		err = true;
		st = m;
	}

	void free() {
		if (tid != null) {
			minecraft.getTextureManager().release(tid);
			tid = null;
			dt = null;
		}
	}

	public void removed() {
		free();
		Skn.cape = null;
		for (Cp c : capes) if (c.tex != null) minecraft.getTextureManager().release(c.tex);
		capes.clear();
	}

	String token() {
		return tk != null ? tk : minecraft.getUser().getAccessToken();
	}

	boolean online() {
		String t = token();
		return t != null && t.length() >= 30;
	}

	void pick() {
		if (busy) return;
		busy = true;
		Thread t = new Thread(() -> {
			try (MemoryStack ms = MemoryStack.stackPush()) {
				var pb = ms.mallocPointer(1);
				pb.put(ms.UTF8("*.png")).flip();
				String f = TinyFileDialogs.tinyfd_openFileDialog("Pick a skin", "", pb, "PNG skin", false);
				busy = false;
				if (f != null) set(Files.readAllBytes(Path.of(f)), slim, "Loaded. Pick the model and press Apply.");
			} catch (Throwable e) {
				busy = false;
				fail("Could not open the file");
			}
		}, "azure-skin-pick");
		t.setDaemon(true);
		t.start();
	}

	void copy() {
		String n = nm.trim();
		if (busy || n.isEmpty()) return;
		busy = true;
		err = false;
		Thread t = new Thread(() -> {
			try {
				HttpClient h = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(8)).build();
				HttpResponse<String> r = h.send(HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + n)).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString());
				if (r.statusCode() != 200) {
					busy = false;
					fail("No player called " + n);
					return;
				}
				String id = JsonParser.parseString(r.body()).getAsJsonObject().get("id").getAsString();
				r = h.send(HttpRequest.newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + id)).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString());
				if (r.statusCode() != 200) {
					busy = false;
					fail("Mojang is busy, try again in a minute");
					return;
				}
				JsonObject pr = JsonParser.parseString(r.body()).getAsJsonObject();
				String val = null;
				for (var e : pr.getAsJsonArray("properties")) if (e.getAsJsonObject().get("name").getAsString().equals("textures")) val = e.getAsJsonObject().get("value").getAsString();
				JsonObject sk = JsonParser.parseString(new String(Base64.getDecoder().decode(val))).getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN");
				boolean sl = sk.has("metadata") && sk.getAsJsonObject("metadata").has("model") && sk.getAsJsonObject("metadata").get("model").getAsString().equals("slim");
				HttpResponse<byte[]> img = h.send(HttpRequest.newBuilder(URI.create(sk.get("url").getAsString().replace("http://", "https://"))).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
				busy = false;
				set(img.body(), sl, "Copied the skin of " + n + ". Press Apply.");
			} catch (Throwable e) {
				busy = false;
				fail("Could not find that skin");
			}
		}, "azure-skin-copy");
		t.setDaemon(true);
		t.start();
	}

	void apply() {
		if (busy) return;
		if (png == null) {
			fail("Pick a skin first");
			return;
		}
		if (!online()) {
			fail("This account is offline, it has no Minecraft login to change");
			return;
		}
		busy = true;
		err = false;
		final byte[] b = png;
		final boolean sl = slim;
		final String tok = token();
		Thread t = new Thread(() -> {
			try {
				String bd = "----azure" + UUID.randomUUID().toString().replace("-", "");
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				o.write(("--" + bd + "\r\nContent-Disposition: form-data; name=\"variant\"\r\n\r\n" + (sl ? "slim" : "classic") + "\r\n").getBytes());
				o.write(("--" + bd + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\nContent-Type: image/png\r\n\r\n").getBytes());
				o.write(b);
				o.write(("\r\n--" + bd + "--\r\n").getBytes());
				HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
				HttpResponse<String> r = h.send(HttpRequest.newBuilder(URI.create(api + "/minecraft/profile/skins")).timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + tok).header("Content-Type", "multipart/form-data; boundary=" + bd).POST(HttpRequest.BodyPublishers.ofByteArray(o.toByteArray())).build(), HttpResponse.BodyHandlers.ofString());
				busy = false;
				answer(r.statusCode(), "Done. Your new skin shows up in game after you rejoin, others see it within a minute.");
			} catch (Throwable e) {
				busy = false;
				fail("Could not reach Mojang");
			}
		}, "azure-skin-apply");
		t.setDaemon(true);
		t.start();
	}

	void answer(int code, String okMsg) {
		if (code / 100 == 2) {
			err = false;
			st = okMsg;
		} else if (code == 401 || code == 403) fail("Mojang refused the login. Sign in to the account again in IAS.");
		else if (code == 429) fail("Too many changes, wait a minute.");
		else fail("Mojang said no (" + code + ")");
	}

	void loadCapes() {
		capeState = 1;
		if (!online()) {
			capeState = 2;
			return;
		}
		final String tok = token();
		Thread t = new Thread(() -> {
			try {
				HttpClient h = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(8)).build();
				HttpResponse<String> r = h.send(HttpRequest.newBuilder(URI.create(api + "/minecraft/profile")).timeout(Duration.ofSeconds(12)).header("Authorization", "Bearer " + tok).GET().build(), HttpResponse.BodyHandlers.ofString());
				if (r.statusCode() / 100 != 2) {
					capeState = 3;
					return;
				}
				JsonArray a = JsonParser.parseString(r.body()).getAsJsonObject().getAsJsonArray("capes");
				List<Cp> l = new ArrayList<>();
				if (a != null) for (JsonElement e : a) {
					JsonObject o = e.getAsJsonObject();
					Cp c = new Cp();
					c.id = o.get("id").getAsString();
					c.alias = o.has("alias") ? o.get("alias").getAsString() : "";
					c.url = o.get("url").getAsString().replace("http://", "https://");
					c.active = o.has("state") && o.get("state").getAsString().equals("ACTIVE");
					c.png = h.send(HttpRequest.newBuilder(URI.create(c.url)).timeout(Duration.ofSeconds(12)).GET().build(), HttpResponse.BodyHandlers.ofByteArray()).body();
					l.add(c);
				}
				minecraft.execute(() -> setCapes(l));
			} catch (Throwable e) {
				capeState = 3;
			}
		}, "azure-capes");
		t.setDaemon(true);
		t.start();
	}

	void setCapes(List<Cp> l) {
		for (Cp c : l) {
			try {
				NativeImage ni = NativeImage.read(new ByteArrayInputStream(c.png));
				c.tex = Identifier.fromNamespaceAndPath("azure", "cape_" + UUID.randomUUID().toString().replace("-", ""));
				minecraft.getTextureManager().register(c.tex, new DynamicTexture(() -> "azure_cape", ni));
			} catch (Throwable e) {
			}
		}
		capes.clear();
		capes.addAll(l);
		capeState = 2;
	}

	void equip() {
		if (busy) return;
		if (!online()) {
			fail("This account is offline, it has no Minecraft login to change");
			return;
		}
		if (sel == -2) {
			fail("Pick a cape first");
			return;
		}
		busy = true;
		err = false;
		final String tok = token();
		final String id = sel >= 0 ? capes.get(sel).id : "";
		Thread t = new Thread(() -> {
			try {
				HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
				HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(api + "/minecraft/profile/capes/active")).timeout(Duration.ofSeconds(15)).header("Authorization", "Bearer " + tok);
				HttpRequest rq = id.isEmpty() ? b.DELETE().build() : b.header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString("{\"capeId\":\"" + id + "\"}")).build();
				HttpResponse<String> r = h.send(rq, HttpResponse.BodyHandlers.ofString());
				busy = false;
				if (r.statusCode() / 100 == 2) {
					for (int i = 0; i < capes.size(); i++) capes.get(i).active = capes.get(i).id.equals(id);
					sel = -2;
				}
				answer(r.statusCode(), id.isEmpty() ? "Cape removed." : "Cape changed. It shows up after you rejoin.");
			} catch (Throwable e) {
				busy = false;
				fail("Could not reach Mojang");
			}
		}, "azure-cape-apply");
		t.setDaemon(true);
		t.start();
	}

	public boolean mouseScrolled(double mx, double my, double sx2, double sy2) {
		if (tab == 1) csc -= (float) sy2 * 28f;
		return true;
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		if (closing) return true;
		int mx = (int) e.x();
		int my = (int) e.y();
		int fx = (int) (e.x() / f);
		int fy = (int) (e.y() / f);
		nf = false;
		for (int i = hits.size() - 1; i >= 0; i--) {
			Ht h = hits.get(i);
			if (in(h.raw ? mx : fx, h.raw ? my : fy, h.x, h.y, h.w, h.h)) {
				Snd.p("click");
				h.r.run();
				return true;
			}
		}
		if (in(mx, my, skn.w.getX(), skn.w.getY(), skn.w.getWidth(), skn.w.getHeight())) skn.poke();
		return super.mouseClicked(e, dbl);
	}

	public boolean charTyped(CharacterEvent e) {
		if (nf && nm.length() < 16) {
			String s = e.codepointAsString();
			if (!s.isEmpty() && (Character.isLetterOrDigit(s.charAt(0)) || s.charAt(0) == '_')) {
				nm += s;
				return true;
			}
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (nf) {
			if (e.key() == GLFW.GLFW_KEY_BACKSPACE && !nm.isEmpty()) {
				nm = nm.substring(0, nm.length() - 1);
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_ENTER) {
				nf = false;
				copy();
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_ESCAPE) {
				nf = false;
				return true;
			}
		}
		if (e.key() == GLFW.GLFW_KEY_ESCAPE) {
			closing = true;
			ct = Util.getMillis();
			return true;
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		closing = true;
		ct = Util.getMillis();
	}

	public boolean isPauseScreen() {
		return false;
	}
}
