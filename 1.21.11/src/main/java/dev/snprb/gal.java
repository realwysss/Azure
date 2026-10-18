package dev.snprb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class gal extends Screen {
	static final ExecutorService ex = Executors.newSingleThreadExecutor(r -> {
		Thread t = new Thread(r, "snprb-ld");
		t.setDaemon(true);
		return t;
	});

	public static class it {
		public File f;
		public img t;
		public boolean ld;
		public int x, y, w, h;
	}

	final Screen p;
	public List<it> l = new ArrayList<>();
	int sr, gen;

	public gal(Screen p) {
		super(Component.literal("Gallery"));
		this.p = p;
	}

	@Override
	protected void init() {
		gen++;
		addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose()).bounds(5, 5, 50, 20).build());
		fr();
		File[] fs = new File(minecraft.gameDirectory, "screenshots").listFiles((d, n) -> n.endsWith(".png"));
		if (fs == null) return;
		Arrays.sort(fs, Comparator.comparingLong(File::lastModified).reversed());
		for (File f : fs) {
			it i = new it();
			i.f = f;
			l.add(i);
		}
		sr = Math.min(sr, mx());
	}

	void fr() {
		for (it i : l) if (i.t != null) i.t.fr();
		l.clear();
	}

	int sc() {
		return (int) minecraft.getWindow().getGuiScale();
	}

	int cols() {
		return Math.max(1, (width - 30) / 110);
	}

	public int x0() {
		return (width - cols() * 110 + 10) / 2;
	}

	int mx() {
		return Math.max(0, 40 + (l.size() + cols() - 1) / cols() * 70 - height);
	}

	public void lay() {
		int cs = cols(), x0 = x0();
		for (int k = 0; k < l.size(); k++) {
			it i = l.get(k);
			int cx = x0 + k % cs * 110, cy = 40 + k / cs * 70 - sr;
			if (i.t == null) {
				i.w = 100;
				i.h = 60;
			} else {
				i.w = i.t.gw;
				i.h = i.t.gh;
			}
			i.x = cx + (100 - i.w) / 2;
			i.y = cy + (60 - i.h) / 2;
		}
	}

	public int at(double mx, double my) {
		for (int k = 0; k < l.size(); k++) {
			it i = l.get(k);
			if (mx >= i.x && mx < i.x + i.w && my >= i.y && my < i.y + i.h) return k;
		}
		return -1;
	}

	void ld(it i) {
		i.ld = true;
		int g = gen, s = sc();
		Minecraft m = minecraft;
		ex.execute(() -> {
			try {
				img.pre r = img.rd(i.f, 100, 60, s, cfg.c.dwn);
				m.execute(() -> {
					if (g != gen) r.n().close();
					else i.t = new img(r);
				});
			} catch (Exception e) {
			}
		});
	}

	@Override
	public void render(GuiGraphics c, int mx, int my, float d) {
		super.render(c, mx, my, d);
		int ac = dev.wscl.Hud.ac() & 0xFFFFFF;
		dev.wscl.Ui.ts(c, "Gallery", width / 2f - dev.wscl.Ui.w("Gallery", 14f) / 2f, 9, 14f, 0xFFFFFF, 1f);
		String n = l.size() + (l.size() == 1 ? " screenshot" : " screenshots");
		dev.wscl.Ui.tx(c, n, width - dev.wscl.Ui.w(n) - 10, 12, 0xFFFFFF, 0.45f);
		lay();
		int h = at(mx, my);
		for (int k = 0; k < l.size(); k++) {
			it i = l.get(k);
			if (i.y + i.h < 0 || i.y > height) continue;
			if (k == h) dev.wscl.Ui.glow(c, i.x - 2, i.y - 2, i.w + 4, i.h + 4, 6, ac, 0.5f);
			dev.wscl.Ui.rr(c, i.x - 2, i.y - 2, i.w + 4, i.h + 4, 6, dev.wscl.Ui.al(k == h ? ac : 0xFFFFFF, k == h ? 0.3f : 0.07f));
			if (i.t == null) {
				if (!i.ld) ld(i);
				c.fill(i.x, i.y, i.x + i.w, i.y + i.h, 0x20FFFFFF);
			} else c.blit(i.t.id, i.x, i.y, i.x + i.w, i.y + i.h, 0f, 1f, 0f, 1f);
			if (k == h) {
				dev.wscl.Ui.ro(c, i.x - 2, i.y - 2, i.w + 4, i.h + 4, 6, dev.wscl.Ui.al(ac, 0.95f));
				dev.wscl.Ui.cx(c, i.f.getName(), width / 2, 30, 0xFFFFFF, 0.6f);
			}
		}
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double hx, double vy) {
		sr = Math.max(0, Math.min(mx(), (int) (sr - vy * 20)));
		return true;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent e, boolean b) {
		if (super.mouseClicked(e, b)) return true;
		lay();
		int i = at(e.x(), e.y());
		if (i < 0) return false;
		minecraft.setScreen(new viw(this, l.get(i).f));
		return true;
	}

	@Override
	public void removed() {
		gen++;
		fr();
	}

	@Override
	public void onClose() {
		minecraft.setScreen(p);
	}
}
