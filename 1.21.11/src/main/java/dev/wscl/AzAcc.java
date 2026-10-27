package dev.wscl;

import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerSkin;

public class AzAcc extends Screen {
	final Screen par;
	EditBox tf;
	Button go;
	Button bk;
	String msg = "";
	boolean busy;
	Supplier<PlayerSkin> sk;
	int px;
	int py;
	int pw;
	int ph = 220;

	public AzAcc(Screen p) {
		super(Component.literal("Accounts"));
		par = p;
	}

	protected void init() {
		pw = Math.min(320, width - 30);
		px = (width - pw) / 2;
		py = Math.max(6, (height - ph) / 2);
		sk = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), false);
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
			sk = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), false);
			msg = "Back on your own account.";
		}).bounds(x + w / 2 + 2, py + 122, w - w / 2 - 2, 20).build());
		if (Ias.on()) addRenderableWidget(Button.builder(Component.literal("Open account manager"), b -> Ias.open()).bounds(x, py + 178, w, 20).build());
		else addRenderableWidget(Button.builder(Component.literal("Get In-Game Account Switcher"), b -> net.minecraft.util.Util.getPlatform().openUri("https://modrinth.com/mod/in-game-account-switcher")).bounds(x, py + 178, w, 20).build());
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
				sk = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), false);
			}
		});
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
		Ui.ts(g, alt ? "Session account" : "Your account", px + 52, cy + 21, 8f, alt ? Vis.mix(ac, 0xFFFFFF, 0.4f) : 0xFFFFFF, alt ? 1f : 0.5f);
		Ui.rr(g, px + pw - 22, cy + 16, 6, 6, 3, Ui.al(alt ? ac : 0x5CE08A, 1f));
		sec(g, ac, "SESSION ID", py + 82);
		sec(g, ac, "SAVED ACCOUNTS", py + 162);
		String hint = Ias.on() ? "Microsoft and offline accounts, saved by In-Game Account Switcher." : "Install In-Game Account Switcher to save more accounts.";
		Ui.ts(g, hint, px + 16, py + 204, 7.5f, 0xFFFFFF, 0.45f);
		String m = msg.isEmpty() ? "Only use a session ID of an account you own. It is never saved." : msg;
		Ui.ts(g, m, px + pw / 2f - Ui.w(m, 7.5f) / 2f, py + 148, 7.5f, msg.isEmpty() ? 0xFFFFFF : Vis.mix(ac, 0xFFFFFF, 0.5f), msg.isEmpty() ? 0.4f : 1f);
	}

	void sec(GuiGraphics g, int ac, String t, int y) {
		Ui.rr(g, px + 16, y + 3, 3, 7, 1, Ui.al(ac, 1f));
		Ui.ts(g, t, px + 24, y + 3, 7.5f, 0xFFFFFF, 0.6f);
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
