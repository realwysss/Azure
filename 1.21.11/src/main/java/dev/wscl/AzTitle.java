package dev.wscl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class AzTitle extends Screen {
	static class B {
		String lb;
		String ic;
		Runnable r;
		int x;
		int y;
		int w;
		int h;
	}

	final List<B> bs = new ArrayList<>();
	final Map<Object, float[]> an = new IdentityHashMap<>();
	long t0 = Util.getMillis();
	boolean ini;
	boolean mods;
	PlayerSkinWidget skin;
	Skn skn;
	java.util.function.Supplier<net.minecraft.world.entity.player.PlayerSkin> sk;
	int lmx = -1;
	int lmy = -1;
	long lmv = Util.getMillis();
	long lft = Util.getMillis();
	float yaw = 20f;
	float pit;
	int[] ab;

	public AzTitle() {
		super(Component.literal("Azure"));
	}

	protected void init() {
		if (ini) t0 = Util.getMillis() - 420L;
		ini = true;
		bs.clear();
		mods = false;
		int sh = Math.round(height * 0.5f);
		sk = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), false);
		skn = new Skn(Math.round(sh * 0.7f), sh, 0, 0);
		skin = skn.w;
		int sw = Math.round(sh * 0.7f);
		int cxs = Math.round(width * 0.74f);
		skin.setPosition(cxs - sw / 2, Math.round(height * 0.2f));
		try {
			Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
			mods = true;
		} catch (Throwable t) {
		}
		add("Singleplayer", "solo", () -> minecraft.setScreen(new SelectWorldScreen(this)));
		add("Multiplayer", "multi", () -> minecraft.setScreen(new JoinMultiplayerScreen(this)));
		if (mods) add("Mods", "mods", () -> {
			try {
				minecraft.setScreen((Screen) Class.forName("com.terraformersmc.modmenu.gui.ModsScreen").getConstructor(Screen.class).newInstance(this));
			} catch (Throwable t) {
			}
		});
		add("Options", "gear", () -> minecraft.setScreen(new OptionsScreen(this, minecraft.options)));
		add("Quit", "power", () -> minecraft.stop());
	}

	void add(String l, String i, Runnable r) {
		B b = new B();
		b.lb = l;
		b.ic = i;
		b.r = r;
		bs.add(b);
	}

	float an(Object o, boolean on, float sp) {
		float[] f = an.computeIfAbsent(o, k -> new float[]{0});
		f[0] += ((on ? 1f : 0f) - f[0]) * sp;
		return f[0];
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		float tt = (Util.getMillis() % 1000000L) / 1000f;
		if (minecraft.getOverlay() == null && Intro.t0 == 0) Intro.start(Util.getMillis() - 900L);
		float rv = (Util.getMillis() - Math.max(t0, Intro.busy() ? Intro.ui() : 0L)) / 1000f;
		int ac = Hud.ac() & 0xFFFFFF;
		Vis.bgx(g, tt, 1f);
		int n = bs.size();
		int lx = Math.max(26, Math.round(width * 0.09f));
		int cy = Math.max(48, Math.round(height * 0.26f));
		Vis.logoAt(g, tt, 1f, lx, cy, 0.4f + rv);
		int y0 = cy + 40;
		int rh = Math.max(20, Math.min(30, (height - y0 - 30) / n - 2));
		int rw = Math.min(150, width - lx - 20);
		for (int i = 0; i < n; i++) {
			B b = bs.get(i);
			float k = Ui.ez((rv - 0.25f - i * 0.07f) / 0.45f);
			b.w = rw;
			b.h = rh;
			b.x = lx - 10;
			b.y = y0 + i * (rh + 3);
			boolean ov = mx >= b.x && mx < b.x + rw && my >= b.y && my < b.y + rh;
			float h = an(b, ov, 0.3f);
			int sx = b.x + Math.round((1f - k) * -14 + 4 * h);
			if (h > 0.03f) {
				Ui.rr(g, sx, b.y, rw, rh, 9, Ui.al(Vis.mix(0x0E0B1E, ac, 0.25f), 0.7f * h * k));
				Ui.ro(g, sx, b.y, rw, rh, 9, Ui.al(ac, 0.7f * h * k));
				Ui.rr(g, sx + 1, b.y + (int) (rh * (0.5f - 0.25f * h)), 2, Math.max(2, (int) (rh * 0.5f * h)), 1, Ui.al(ac, h * k));
			}
			float z = rh >= 26 ? 11f : 10f;
			Ic.dr(g, b.ic, sx + 20, b.y + rh / 2, 15, Ui.al(Vis.mix(0xFFFFFF, ac, 0.4f * h), (0.6f + 0.4f * h) * k));
			Ui.ts(g, b.lb, sx + 38, b.y + rh / 2f - z * 0.44f, z, 0xFFFFFF, (0.62f + 0.38f * h) * k);
		}
		float kk = Ui.ez((rv - 0.2f) / 0.4f);
		if (skin != null && width >= 380) {
			float cxm = skin.getX() + skin.getWidth() / 2f;
			Ui.GA = kk;
			skn.render(g, mx, my, d);
			Ui.GA = 1f;
			String nm = minecraft.getUser().getName();
			boolean ias = ias();
			String sub = ias ? "Click to switch account" : "Signed in";
			int bw = Math.max(118, Math.max(Ui.w(nm), Ui.w(sub, 8f)) + (ias ? 76 : 58));
			int bh = 30;
			int bx = (int) cxm - bw / 2;
			int by = skin.getY() + skin.getHeight() + 6;
			boolean aov = ias && in(mx, my, bx, by, bw, bh);
			float ah = an("acc2", aov, 0.3f);
			if (ah > 0.03f) Ui.glow(g, bx, by, bw, bh, 10, ac, ah * kk);
			Ui.rr(g, bx, by, bw, bh, 10, Ui.al(Vis.mix(0x0E0B1E, ac, 0.28f * ah), 0.72f * kk));
			Ui.ro(g, bx, by, bw, bh, 10, Ui.al(ah > 0.05f ? ac : 0xFFFFFF, (0.14f + 0.7f * ah) * kk));
			Ui.GA = kk;
			net.minecraft.client.gui.components.PlayerFaceRenderer.draw(g, sk.get(), bx + 8, by + 7, 16, Ui.al(0xFFFFFF, 1f));
			Ui.GA = 1f;
			Ui.tx(g, nm, bx + 31, by + 6, 0xFFFFFF, 0.95f * kk);
			Ui.ts(g, sub, bx + 31, by + 17, 8f, ias ? Vis.mix(0xFFFFFF, ac, 0.6f) : 0xFFFFFF, (ias ? 0.9f : 0.45f) * kk);
			if (ias) Ic.dr(g, "chev", bx + bw - 14, by + bh / 2, 10, Ui.al(0xFFFFFF, (0.5f + 0.5f * ah) * kk));
			ab = new int[]{bx, by, bw, bh};
		}
		int ix = width - 28;
		small(g, mx, my, ac, ix, height - 52, "lang", "lang");
		small(g, mx, my, ac, ix, height - 80, "shirt", "skin");
		small(g, mx, my, ac, ix, height - 108, "discord", "dc");
		String l = "Azure " + Upd.ver() + "   Minecraft 1.21.11";
		Ui.ts(g, l, 12, height - 18, 8f, 0xFFFFFF, 0.4f * kk);
		String r = "Not affiliated with Mojang AB";
		Ui.ts(g, r, width - Ui.w(r, 8f) - 12, height - 18, 8f, 0xFFFFFF, 0.3f * kk);
		if (minecraft.getOverlay() == null) Intro.draw(g);
	}

	boolean ias() {
		return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("ias");
	}

	void small(GuiGraphics g, int mx, int my, int ac, int x, int y, String ic, String key) {
		boolean ov = mx >= x && mx < x + 22 && my >= y && my < y + 22;
		float h = an(key, ov, 0.3f);
		if (h > 0.03f) Ui.glow(g, x, y, 22, 22, 11, ac, h);
		Ui.rr(g, x, y, 22, 22, 11, Ui.al(Vis.mix(0x0C1426, ac, 0.3f * h), 0.62f));
		Ui.ro(g, x, y, 22, 22, 11, Ui.al(h > 0.05f ? ac : 0xFFFFFF, 0.14f + 0.7f * h));
		Ic.draw(g, ic, x + 11, y + 11, Ui.al(0xFFFFFF, 0.55f + 0.4f * h));
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		if (Intro.busy()) {
			Intro.skip();
			return true;
		}
		double mx = e.x();
		double my = e.y();
		for (B b : bs) {
			if (in(mx, my, b.x, b.y, b.w, b.h)) {
				Snd.p("click");
				b.r.run();
				return true;
			}
		}
		if (ab != null && ias() && in(mx, my, ab[0], ab[1], ab[2], ab[3])) {
			for (String cn : new String[]{"ru.vidtu.ias.screen.AccountScreen", "ru.vidtu.ias.gui.AccountScreen", "ru.vidtu.ias.screen.AccountListScreen"}) {
				try {
					minecraft.setScreen((Screen) Class.forName(cn).getConstructor(Screen.class).newInstance(this));
					return true;
				} catch (Throwable x) {
				}
			}
			return true;
		}
		if (in(mx, my, width - 28, height - 108, 22, 22)) {
			Snd.p("click");
			Api.dc();
			return true;
		}
		if (in(mx, my, width - 28, height - 80, 22, 22)) {
			Snd.p("click");
			minecraft.setScreen(new AzSkin(this));
			return true;
		}
		if (in(mx, my, width - 28, height - 52, 22, 22)) {
			Snd.p("click");
			minecraft.setScreen(new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager()));
			return true;
		}
		if (skin != null && skn != null && in(mx, my, skin.getX(), skin.getY(), skin.getWidth(), skin.getHeight())) skn.poke();
		return super.mouseClicked(e, dbl);
	}

	public boolean keyPressed(net.minecraft.client.input.KeyEvent e) {
		if (Intro.busy()) {
			Intro.skip();
			return true;
		}
		if (Wc.key != null && Wc.key.matches(e)) {
			minecraft.setScreen(Gui.open(this));
			return true;
		}
		return super.keyPressed(e);
	}

	public boolean shouldCloseOnEsc() {
		return false;
	}
}
