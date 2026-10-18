package dev.wscl;

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
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class AzSkin extends Screen {
	public static String api = "https://api.minecraftservices.com";
	public static String tk;
	final Screen par;
	byte[] png;
	Identifier tid;
	DynamicTexture dt;
	int th = 64;
	int ft = 64;
	boolean slim;
	String nm = "";
	boolean nf;
	volatile String st = "";
	volatile boolean err;
	volatile boolean busy;
	long t0 = Util.getMillis();
	int cx;
	int cy;
	int cw = 430;
	int ch = 270;
	float f = 1f;

	public AzSkin(Screen p) {
		super(Component.literal("Skin changer"));
		par = p;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean in(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri, boolean on) {
		boolean ov = in(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 9, Ui.al(pri || on ? ac : 0x120E26, pri ? 0.55f + (ov ? 0.25f : 0f) : on ? 0.35f : 0.8f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(pri || on || ov ? ac : 0xFFFFFF, pri ? 0.9f : on ? 0.85f : ov ? 0.8f : 0.16f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
	}

	void part(GuiGraphics g, Identifier id, int x, int y, int s, int u, int v, int w, int h) {
		g.blit(RenderPipelines.GUI_TEXTURED, id, x, y, (float) u, (float) v, w * s, h * s, w, h, 64, ft);
	}

	void fig(GuiGraphics g, Identifier id, int x, int y, int s, boolean sl, int tall) {
		ft = tall;
		int aw = sl ? 3 : 4;
		part(g, id, x + 4 * s, y, s, 8, 8, 8, 8);
		part(g, id, x + 4 * s, y + 8 * s, s, 20, 20, 8, 12);
		part(g, id, x + (4 - aw) * s, y + 8 * s, s, 44, 20, aw, 12);
		part(g, id, x + 12 * s, y + 8 * s, s, tall == 64 ? 36 : 44, tall == 64 ? 52 : 20, aw, 12);
		part(g, id, x + 4 * s, y + 20 * s, s, 4, 20, 4, 12);
		part(g, id, x + 8 * s, y + 20 * s, s, tall == 64 ? 20 : 4, tall == 64 ? 52 : 20, 4, 12);
		if (tall == 64) {
			part(g, id, x + 4 * s, y, s, 40, 8, 8, 8);
			part(g, id, x + 4 * s, y + 8 * s, s, 20, 36, 8, 12);
			part(g, id, x + (4 - aw) * s, y + 8 * s, s, 44, 36, aw, 12);
			part(g, id, x + 12 * s, y + 8 * s, s, 52, 52, aw, 12);
			part(g, id, x + 4 * s, y + 20 * s, s, 4, 36, 4, 12);
			part(g, id, x + 8 * s, y + 20 * s, s, 4, 52, 4, 12);
		}
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		float k = Ui.ez((Util.getMillis() - t0) / 380f);
		Ui.GA = k;
		f = Math.min(1f, (height - 8f) / ch);
		mx = (int) (mx / f);
		my = (int) (my / f);
		var ps0 = g.pose();
		ps0.pushMatrix();
		ps0.scale(f, f);
		cx = (int) ((width / f - cw) / 2);
		cy = (int) ((height / f - ch) / 2) + Math.round((1f - k) * 12);
		Ui.glow(g, cx, cy, cw, ch, 14, ac, 0.35f);
		Ui.rr(g, cx, cy, cw, ch, 14, Ui.al(0x090714, 0.95f));
		g.fillGradient(cx + 14, cy + 1, cx + cw - 14, cy + 70, Ui.al(ac, 0.1f), Ui.al(ac, 0f));
		Ui.ro(g, cx, cy, cw, ch, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.ts(g, "Skin changer", cx + 24, cy + 20, 16f, 0xFFFFFF, 1f);
		Ui.tx(g, minecraft.getUser().getName(), cx + cw - 24 - Ui.w(minecraft.getUser().getName()), cy + 25, 0xFFFFFF, 0.45f);
		Ui.rr(g, cx + 24, cy + 56, 130, 180, 12, Ui.al(0xFFFFFF, 0.04f));
		Ui.ro(g, cx + 24, cy + 56, 130, 180, 12, Ui.al(0xFFFFFF, 0.1f));
		Identifier id = tid;
		boolean sl = slim;
		int tall = th;
		if (id == null) {
			var ps = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), false).get();
			id = ps.body().texturePath();
			sl = ps.model() == net.minecraft.world.entity.player.PlayerModelType.SLIM;
			tall = 64;
		}
		fig(g, id, cx + 24 + (130 - 16 * 4) / 2, cy + 56 + 14, 4, sl, tall);
		Ui.cx(g, png == null ? "Current skin" : "New skin", cx + 24 + 65, cy + 222, 0xFFFFFF, 0.5f);
		int rx = cx + 176;
		int rw = cw - 176 - 24;
		btn(g, mx, my, ac, rx, cy + 56, rw, 26, "Choose a PNG file", false, false);
		Ui.tx(g, "Or copy the skin of a player", rx, cy + 96, 0xFFFFFF, 0.55f);
		int bw = rw - 70;
		Ui.rr(g, rx, cy + 110, bw, 24, 9, Ui.al(0xFFFFFF, nf ? 0.09f : 0.05f));
		Ui.ro(g, rx, cy + 110, bw, 24, 9, Ui.al(nf ? ac : 0xFFFFFF, nf ? 0.9f : 0.12f));
		boolean car = nf && (Util.getMillis() / 500) % 2 == 0;
		if (nm.isEmpty() && !nf) Ui.tx(g, "Username", rx + 10, cy + 118, 0xFFFFFF, 0.35f);
		else Ui.tx(g, nm + (car ? "|" : ""), rx + 10, cy + 118, 0xFFFFFF, 1f);
		btn(g, mx, my, ac, rx + bw + 6, cy + 110, 64, 24, "Copy", false, false);
		Ui.tx(g, "Model", rx, cy + 150, 0xFFFFFF, 0.55f);
		int mw = (rw - 6) / 2;
		btn(g, mx, my, ac, rx, cy + 164, mw, 24, "Classic", false, !slim);
		btn(g, mx, my, ac, rx + mw + 6, cy + 164, mw, 24, "Slim", false, slim);
		String s = busy ? "Working..." : st;
		if (!s.isEmpty()) {
			var l = font.split(Component.literal(s), rw);
			for (int i = 0; i < Math.min(2, l.size()); i++) g.drawString(font, l.get(i), rx, cy + 198 + i * 11, err ? 0xFFFF6060 : 0xFF9CE8B0, false);
		}
		btn(g, mx, my, ac, cx + 24, cy + ch - 34 + 0, 90, 22, "Back", false, false);
		btn(g, mx, my, ac, cx + cw - 24 - 150, cy + ch - 34, 150, 22, "Apply to account", true, false);
		Ui.GA = 1f;
		ps0.popMatrix();
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
				th = ni.getHeight();
				png = b;
				slim = sl;
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
		String tok = tk != null ? tk : minecraft.getUser().getAccessToken();
		if (tok == null || tok.length() < 30) {
			fail("This account is offline, it has no Minecraft login to change");
			return;
		}
		busy = true;
		err = false;
		final byte[] b = png;
		final boolean sl = slim;
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
				if (r.statusCode() / 100 == 2) {
					err = false;
					st = "Done. Your new skin shows up in game after you rejoin, others see it within a minute.";
				} else if (r.statusCode() == 401 || r.statusCode() == 403) {
					fail("Mojang refused the login. Sign in to the account again in IAS.");
				} else if (r.statusCode() == 429) {
					fail("Too many changes, wait a minute.");
				} else {
					fail("Mojang said no (" + r.statusCode() + ")");
				}
			} catch (Throwable e) {
				busy = false;
				fail("Could not reach Mojang");
			}
		}, "azure-skin-apply");
		t.setDaemon(true);
		t.start();
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int mx = (int) (e.x() / f);
		int my = (int) (e.y() / f);
		int rx = cx + 176;
		int rw = cw - 176 - 24;
		int bw = rw - 70;
		nf = false;
		if (in(mx, my, cx + 24, cy + ch - 34, 90, 22)) {
			Snd.p("click");
			onClose();
			return true;
		}
		if (in(mx, my, cx + cw - 24 - 150, cy + ch - 34, 150, 22)) {
			Snd.p("click");
			apply();
			return true;
		}
		if (in(mx, my, rx, cy + 56, rw, 26)) {
			Snd.p("click");
			pick();
			return true;
		}
		if (in(mx, my, rx, cy + 110, bw, 24)) {
			nf = true;
			return true;
		}
		if (in(mx, my, rx + bw + 6, cy + 110, 64, 24)) {
			Snd.p("click");
			copy();
			return true;
		}
		int mw = (rw - 6) / 2;
		if (in(mx, my, rx, cy + 164, mw, 24)) {
			Snd.p("click");
			slim = false;
			return true;
		}
		if (in(mx, my, rx + mw + 6, cy + 164, mw, 24)) {
			Snd.p("click");
			slim = true;
			return true;
		}
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
		return super.keyPressed(e);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}

	public boolean isPauseScreen() {
		return false;
	}
}
