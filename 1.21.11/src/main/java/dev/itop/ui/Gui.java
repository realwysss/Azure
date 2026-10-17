package dev.itop.ui;

import dev.itop.Cfg;
import dev.itop.Itop;
import dev.itop.Op;
import dev.wysl.Colors;
import dev.wysl.Wl;
import dev.wysl.ui.Canvas;
import dev.wysl.ui.Kit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class Gui extends Kit {
	static List<Item> all;

	private final List<Item> fl = new ArrayList<>();
	private String q = "";
	private boolean qf;
	private int bo;
	private int row;
	private int mrow;
	private Item sel;
	private boolean dirty;

	public static Screen open(Screen p) {
		return Wl.screen(p, new Gui(), "Items Opacity", Itop.key);
	}

	public Gui() {
		if (all == null) {
			all = new ArrayList<>();
			for (Item i : BuiltInRegistries.ITEM) if (i != Items.AIR) all.add(i);
			all.sort(Comparator.comparing(Op::key));
		}
		refilter();
		sel = fl.isEmpty() ? null : fl.get(0);
		for (Item i : fl) if (Op.key(i).equals("minecraft:stone")) sel = i;
	}

	private void refilter() {
		fl.clear();
		String s = q.toLowerCase(Locale.ROOT).trim();
		for (Item i : all) {
			if (bo == 1 && !(i instanceof BlockItem)) continue;
			if (!s.isEmpty() && !Op.key(i).contains(s)) continue;
			fl.add(i);
		}
		row = 0;
	}

	private void flush() {
		if (dirty) {
			dirty = false;
			Op.flush();
		}
	}

	@Override
	public void draw(Canvas g, int width, int height, double mouseX, double mouseY) {
		begin(mouseX, mouseY);
		float cw = Math.min(width - 24, 440);
		float ch = Math.min(height - 12, 252);
		float x = (width - cw) / 2f;
		float y = (height - ch) / 2f + (1f - open) * 16f;
		dim(g, width, height);
		glass(g, x, y, cw, ch);
		g.text("Items Opacity", x + 16, y + 12, fd(TX));
		Cfg c = Cfg.c;
		String st = c.on ? "On" : "Off";
		float tx = x + cw - 38;
		g.text(st, tx - 6 - g.width(st), y + 12, fd(c.on ? TX : MU));
		Runnable fm = () -> {
			c.on = !c.on;
			dirty = true;
			Op.rb();
			flush();
		};
		tgl(g, "master", tx, y + 10, c.on, fm);
		g.fill(x + 16, y + 32, cw - 32, 1, 0, fd(0x16FFFFFF));
		float top = y + 42;
		float bot = y + ch - 36;
		float lx = x + 14;
		float lw = cw - 28 - 164;
		float fy = top;
		boolean over = hov(lx, fy, lw - 112, 18);
		float k = an("sf", qf ? 1f : over ? 0.4f : 0f, 14f);
		g.fill(lx, fy, lw - 112, 18, 9, fd(WL), fd(0x30000000));
		g.ring(lx, fy, lw - 112, 18, 9, 1, fd(Colors.mix(ED, AC, k)), fd(Colors.mix(0x0AFFFFFF, AC, k * 0.5f)));
		String sh = q.isEmpty() && !qf ? "search" : q;
		g.text(sh, lx + 8, fy + 5, fd(q.isEmpty() ? MU : TX));
		if (qf && (clock % 1f) < 0.55f) g.fill(lx + 8 + g.width(q) + 1, fy + 4, 1, 10, 0, fd(TX));
		add("sfield", lx, fy, lw - 112, 18, () -> qf = true);
		seg(g, "bo", lx + lw - 106, fy, 106, 18, new String[]{"All", "Blocks"}, bo, i -> {
			bo = i;
			refilter();
		});
		float gy = top + 26;
		float cs = 22;
		int cols = Math.max(1, (int) (lw / cs));
		int rows = Math.max(1, (int) ((bot - gy) / cs));
		mrow = Math.max(0, (fl.size() + cols - 1) / cols - rows);
		row = Math.max(0, Math.min(row, mrow));
		float gx = lx + (lw - cols * cs) / 2f;
		g.fill(lx, gy - 2, lw, rows * cs + 4, 10, fd(0x30100620), fd(0x50080214));
		for (int r = 0; r < rows; r++) {
			for (int cc = 0; cc < cols; cc++) {
				int idx = (row + r) * cols + cc;
				if (idx >= fl.size()) break;
				Item it = fl.get(idx);
				float cx = gx + cc * cs;
				float cy = gy + r * cs;
				boolean s = it == sel;
				boolean h = hov(cx, cy, cs, cs);
				float kk = an("c" + r + "_" + cc, s ? 1f : h ? 0.5f : 0f, 16f);
				if (kk > 0.01f) {
					g.fill(cx + 1, cy + 1, cs - 2, cs - 2, 6, fd(Colors.alpha(0x30FFFFFF, kk)), fd(Colors.alpha(0x14FFFFFF, kk)));
					g.ring(cx + 1, cy + 1, cs - 2, cs - 2, 6, 1, fd(Colors.alpha(AC, kk)), fd(Colors.alpha(AC, kk * 0.3f)));
				}
				g.itm(Op.key(it), cx + 3, cy + 3, 16, false);
				int o = Op.get(Op.key(it));
				if (o < 100) g.fill(cx + cs - 8, cy + 2, 5, 5, 2.5f, fd(o == 0 ? 0xFFFF6A6A : 0xFFFFC857));
				if (h) tip = Op.key(it);
				add("cell" + r + "_" + cc, cx, cy, cs, cs, () -> {
					sel = it;
					qf = false;
				});
			}
		}
		if (fl.isEmpty()) g.text("nothing found", lx + 10, gy + 10, fd(MU));
		float rx = lx + lw + 12;
		float rw = x + cw - 14 - rx;
		well(g, rx, top, rw, bot - top, 10);
		if (sel != null) {
			String key = Op.key(sel);
			int o = Op.get(key);
			g.itm(key, rx + 10, top + 10, 32, false);
			String nm = new ItemStack(sel).getHoverName().getString();
			while (g.width(nm) > rw - 56 && nm.length() > 3) nm = nm.substring(0, nm.length() - 2);
			g.text(nm, rx + 50, top + 12, fd(TX));
			String kp = key.replace("minecraft:", "");
			while (g.width(kp) > rw - 56 && kp.length() > 3) kp = kp.substring(0, kp.length() - 2);
			g.text(kp, rx + 50, top + 24, fd(MU));
			sld(g, "op", "Opacity", rx + 10, top + 52, rw - 20, o, 0, 100, 1, v -> {
				Op.set(key, (int) v);
				dirty = true;
			}, o + "%");
			if (o == 0) {
				g.text("Warning: 0% is invisible.", rx + 10, top + 82, fd(0xFFFFB066));
				g.text("It will not be drawn at all.", rx + 10, top + 93, fd(0xFFFFB066));
				g.text("You can still hit it blindly.", rx + 10, top + 104, fd(0xFFFFB066));
			}
			float ry = bot - 24;
			btn(g, "rst", "Reset item", rx + 10, ry, rw - 20, false, () -> {
				Op.set(key, 100);
				dirty = true;
				flush();
			});
			row(g, "inv", "Hand and inventory", rx + 10, ry - 24, rw - 20, c.inv, () -> {
				c.inv = !c.inv;
				dirty = true;
				flush();
			});
		}
		float by = y + ch - 28;
		btn(g, "all", "Reset all", x + 14, by, 70, false, () -> {
			Cfg.c.m.clear();
			dirty = true;
			Op.rb();
			flush();
		});
		btn(g, "done", "Done", x + cw - 14 - 60, by, 60, true, () -> {
			save();
			close.run();
		});
		g.text(c.m.size() + " items changed", x + 92, by + 5, fd(MU));
		tipDraw(g, width);
	}

	@Override
	protected boolean wheel(double a) {
		row = Math.max(0, Math.min(mrow, row - (int) Math.signum(a)));
		return true;
	}

	@Override
	public boolean release(double x, double y, int b) {
		boolean r = super.release(x, y, b);
		flush();
		return r;
	}

	@Override
	public boolean type(int cp) {
		if (qf) {
			if (cp >= 32 && q.length() < 24) {
				q += (char) cp;
				refilter();
			}
			return true;
		}
		return super.type(cp);
	}

	@Override
	public boolean key(int a) {
		if (qf) {
			if (a == BACK && !q.isEmpty()) {
				q = q.substring(0, q.length() - 1);
				refilter();
			} else if (a == ENTER || a == ESCAPE) qf = false;
			return true;
		}
		return super.key(a);
	}

	@Override
	public boolean typing() {
		return qf || super.typing();
	}

	@Override
	public void save() {
		super.save();
		dirty = true;
		flush();
	}
}
