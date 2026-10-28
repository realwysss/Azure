package dev.wscl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

public class AzAcc extends Screen {
	final Screen par;
	EditBox tf;
	Button go;
	Button bk;
	String msg = "";
	boolean busy;
	Supplier<PlayerSkin> sk;
	final HashMap<UUID, Supplier<PlayerSkin>> fc = new HashMap<>();
	List<Object> al = List.of();
	boolean ias;
	int px;
	int py;
	int pw;
	int ph;
	int nr;
	int off;
	int ly;

	public AzAcc(Screen p) {
		super(Component.literal("Accounts"));
		par = p;
	}

	protected void init() {
		ias = Ias.on();
		al = ias ? Ias.accs() : List.of();
		nr = ias ? Math.max(1, Math.min(4, (height - 36 - 244) / 22)) : 0;
		ph = ias ? 210 + nr * 22 : 172;
		pw = Math.min(320, width - 30);
		px = (width - pw) / 2;
		py = Math.max(6, (height - ph - 26) / 2);
		off = Math.max(0, Math.min(off, al.size() - nr));
		sk = Acc.sk();
		int x = px + 16;
		int w = pw - 32;
		tf = new EditBox(font, x, py + 98, w, 20, Component.literal("Session ID"));
		tf.setMaxLength(4096);
		tf.setHint(Component.literal("Paste a session ID"));
		tf.addFormatter((s, i) -> FormattedCharSequence.forward("*".repeat(s.length()), net.minecraft.network.chat.Style.EMPTY));
		addRenderableWidget(tf);
		go = addRenderableWidget(Button.builder(Component.literal("Switch"), b -> sw()).bounds(x, py + 122, w / 2 - 2, 20).build());
		bk = addRenderableWidget(Button.builder(Component.literal("Back to my account"), b -> {
			Acc.back();
			sk = Acc.sk();
			msg = "Back on your own account.";
		}).bounds(x + w / 2 + 2, py + 122, w - w / 2 - 2, 20).build());
		ly = py + 186;
		if (ias) addRenderableWidget(Button.builder(Component.literal("+ Add account"), b -> Ias.add(this)).bounds(x, ly + nr * 22 + 4, w, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(px + pw / 2 - 50, py + ph + 6, 100, 20).build());
		setInitialFocus(tf);
	}

	void sw() {
		if (busy) return;
		busy = true;
		msg = "Checking...";
		Acc.go(tf.getValue(), r -> {
			msg = r;
			busy = false;
			if (r.startsWith("Now")) {
				tf.setValue("");
				sk = Acc.sk();
			}
		});
	}

	PlayerSkin face(UUID u) {
		if (u == null) return net.minecraft.client.resources.DefaultPlayerSkin.getDefaultSkin();
		return fc.computeIfAbsent(u, k -> {
			var l = minecraft.playerSkinRenderCache().createLookup(ResolvableProfile.createUnresolved(k));
			return () -> l.get().playerSkin();
		}).get();
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		super.renderBackground(g, mx, my, d);
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.glow(g, px, py, pw, ph, 14, ac, 0.25f);
		Ui.rr(g, px, py, pw, ph, 14, Ui.al(Ui.tn(0x0C0A1A), 0.96f));
		Ui.ro(g, px, py, pw, ph, 14, Ui.al(ac, 0.45f));
		Ui.ts(g, "Accounts", px + 16, py + 14, 12f, 0xFFFFFF, 1f);
		int cy = py + 34;
		Ui.rr(g, px + 12, cy, pw - 24, 38, 10, Ui.al(0xFFFFFF, 0.05f));
		PlayerFaceRenderer.draw(g, sk.get(), px + 20, cy + 7, 24, 0xFFFFFFFF);
		Ui.tx(g, minecraft.getUser().getName(), px + 52, cy + 9, 0xFFFFFF, 1f);
		boolean alt = Acc.alt();
		Ui.ts(g, alt ? "Session account" : "Signed in", px + 52, cy + 21, 8f, alt ? Vis.mix(ac, 0xFFFFFF, 0.4f) : 0xFFFFFF, alt ? 1f : 0.5f);
		Ui.rr(g, px + pw - 22, cy + 16, 6, 6, 3, Ui.al(alt ? ac : 0x5CE08A, 1f));
		sec(g, ac, "SESSION ID", py + 82);
		String m = msg.isEmpty() ? "Only use your own session ID. It is never saved." : msg;
		Ui.ts(g, m, px + pw / 2f - Ui.w(m, 7.5f) / 2f, py + 148, 7.5f, msg.isEmpty() ? 0xFFFFFF : Vis.mix(ac, 0xFFFFFF, 0.5f), msg.isEmpty() ? 0.4f : 1f);
		if (!ias) return;
		sec(g, ac, "SAVED ACCOUNTS", py + 166);
		if (al.size() > nr) Ui.ts(g, (off + 1) + "-" + Math.min(al.size(), off + nr) + " of " + al.size(), px + pw - 16 - Ui.w((off + 1) + "-" + Math.min(al.size(), off + nr) + " of " + al.size(), 7.5f), py + 169, 7.5f, 0xFFFFFF, 0.4f);
		int x = px + 12;
		int w = pw - 24;
		if (al.isEmpty()) {
			Ui.rr(g, x, ly, w, nr * 22 - 2, 8, Ui.al(0xFFFFFF, 0.03f));
			String t = "No saved accounts yet. Add a Microsoft or offline account.";
			Ui.ts(g, t, px + pw / 2f - Ui.w(t, 7.5f) / 2f, ly + nr * 11 - 5, 7.5f, 0xFFFFFF, 0.45f);
			return;
		}
		String me = minecraft.getUser().getName();
		for (int i = 0; i < nr && off + i < al.size(); i++) {
			Object a = al.get(off + i);
			int y = ly + i * 22;
			boolean ov = mx >= x && mx < x + w && my >= y && my < y + 20;
			boolean od = ov && mx >= x + w - 20;
			String n = Ias.nm(a);
			boolean cur = n.equalsIgnoreCase(me);
			Ui.rr(g, x, y, w, 20, 7, Ui.al(cur ? ac : 0xFFFFFF, cur ? 0.14f : ov && !od ? 0.09f : 0.04f));
			if (cur) Ui.rr(g, x + 1, y + 5, 2, 10, 1, Ui.al(ac, 1f));
			PlayerFaceRenderer.draw(g, face(Ias.skin(a)), x + 7, y + 4, 12, 0xFFFFFFFF);
			Ui.tx(g, n, x + 25, y + 6, 0xFFFFFF, 1f);
			String tp = cur ? "In use" : Ias.ms(a) ? "Microsoft" : "Offline";
			Ui.ts(g, tp, x + 30 + Ui.w(n), y + 7, 7.5f, cur ? Vis.mix(ac, 0xFFFFFF, 0.4f) : 0xFFFFFF, cur ? 1f : 0.4f);
			if (ov && !od && !cur) Ui.ts(g, "Click to log in", x + w - 26 - Ui.w("Click to log in", 7.5f), y + 7, 7.5f, 0xFFFFFF, 0.5f);
			Ic.dr(g, "x", x + w - 10, y + 10, 8, Ui.al(od ? 0xFF6060 : 0xFFFFFF, od ? 1f : 0.35f));
		}
	}

	void sec(GuiGraphics g, int ac, String t, int y) {
		Ui.rr(g, px + 16, y + 3, 3, 7, 1, Ui.al(ac, 1f));
		Ui.ts(g, t, px + 24, y + 3, 7.5f, 0xFFFFFF, 0.6f);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		if (ias && e.button() == 0) {
			int x = px + 12;
			int w = pw - 24;
			for (int i = 0; i < nr && off + i < al.size(); i++) {
				int y = ly + i * 22;
				if (e.x() < x || e.x() >= x + w || e.y() < y || e.y() >= y + 20) continue;
				Object a = al.get(off + i);
				if (e.x() >= x + w - 20) Ias.del(this, a);
				else Ias.login(this, a);
				return true;
			}
		}
		return super.mouseClicked(e, dbl);
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		if (ias && al.size() > nr) {
			off = Math.max(0, Math.min(al.size() - nr, off - (int) Math.signum(sy)));
			return true;
		}
		return super.mouseScrolled(mx, my, sx, sy);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		bk.active = Acc.alt();
		go.active = !busy;
		super.render(g, mx, my, d);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
