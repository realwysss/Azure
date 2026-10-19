package dev.wscl;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class AzDc extends Screen {
	static final Identifier PF = Identifier.fromNamespaceAndPath("azure", "icon_256.png");
	static volatile int mem = -1;
	static volatile int onl = -1;
	static long lst;
	final Screen par;
	long t0 = Util.getMillis();
	int px;
	int py;
	int pw = 260;
	int ph = 220;

	public AzDc(Screen p) {
		super(Component.literal("Discord"));
		par = p;
		long n = Util.getMillis();
		if (n - lst > 60000L) {
			lst = n;
			Thread t = new Thread(() -> {
				try {
					String c = Api.dcl.isEmpty() ? Api.DC : Api.dcl;
					c = c.substring(c.lastIndexOf('/') + 1);
					String b = Api.H.send(HttpRequest.newBuilder(URI.create("https://discord.com/api/v9/invites/" + c + "?with_counts=true")).timeout(Duration.ofSeconds(8)).GET().build(), HttpResponse.BodyHandlers.ofString()).body();
					var o = com.google.gson.JsonParser.parseString(b).getAsJsonObject();
					mem = o.get("approximate_member_count").getAsInt();
					onl = o.get("approximate_presence_count").getAsInt();
				} catch (Throwable e) {
					lst = 0;
				}
			}, "azure-dc");
			t.setDaemon(true);
			t.start();
		}
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean in(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		int bl = 0x5865F2;
		float k = Ui.ez((Util.getMillis() - t0) / 320f);
		Ui.GA = k;
		px = (width - pw) / 2;
		py = (height - ph) / 2 + Math.round((1f - k) * 10);
		Ui.glow(g, px, py, pw, ph, 14, bl, 0.25f);
		Ui.rr(g, px, py, pw, ph, 14, Ui.al(0x090714, 0.96f));
		Ui.ro(g, px, py, pw, ph, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.rim(g, px, py, pw, ph, 14, bl, 1f);
		int is = 64;
		int ix = px + (pw - is) / 2;
		Ui.glow(g, ix, py + 22, is, is, 16, ac, 0.3f);
		g.blit(RenderPipelines.GUI_TEXTURED, PF, ix, py + 22, 0f, 0f, is, is, 256, 256, 256, 256);
		Ui.ro(g, ix, py + 22, is, is, 16, Ui.al(0xFFFFFF, 0.2f));
		String nm = "Azure Client";
		Ui.ts(g, nm, px + (pw - Ui.w(nm, 15f)) / 2f, py + 98, 15f, 0xFFFFFF, 1f);
		String ct = mem < 0 ? "counting members..." : mem + " members";
		Ui.ts(g, ct, px + (pw - Ui.w(ct, 9f)) / 2f, py + 120, 9f, 0xFFFFFF, 0.65f);
		if (onl >= 0) {
			String on = onl + " online";
			int ow = Ui.w(on, 8.5f);
			int ox = px + (pw - ow - 12) / 2;
			Ui.rr(g, ox, py + 139, 6, 6, 3, Ui.al(0x3FD07A, 1f));
			Ui.ts(g, on, ox + 12, py + 138, 8.5f, 0xFFFFFF, 0.5f);
		}
		int bw = pw - 48;
		boolean ov = in(mx, my, px + 24, py + 160, bw, 26);
		Ui.rr(g, px + 24, py + 160, bw, 26, 10, Ui.al(bl, 0.7f + (ov ? 0.25f : 0f)));
		Ui.ro(g, px + 24, py + 160, bw, 26, 10, Ui.al(0xFFFFFF, ov ? 0.5f : 0.25f));
		String j = "Join server";
		int gw = 14 + 6 + Ui.w(j, 9f);
		int gx = px + 24 + (bw - gw) / 2;
		Ic.dr(g, "discord", gx + 7, py + 173, 14, 0xFFFFFFFF);
		Ui.ts(g, j, gx + 20, py + 169.3f, 9f, 0xFFFFFF, 1f);
		boolean ob = in(mx, my, px + 24, py + 192, bw, 18);
		String bk = "Back";
		Ui.ts(g, bk, px + (pw - Ui.w(bk, 8.5f)) / 2f, py + 197, 8.5f, 0xFFFFFF, ob ? 0.9f : 0.45f);
		Ui.GA = 1f;
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int mx = (int) e.x();
		int my = (int) e.y();
		if (in(mx, my, px + 24, py + 160, pw - 48, 26)) {
			Snd.p("click");
			Util.getPlatform().openUri(Api.dcl.isEmpty() ? Api.DC : Api.dcl);
			return true;
		}
		if (in(mx, my, px + 24, py + 192, pw - 48, 18)) {
			Snd.p("click");
			onClose();
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
