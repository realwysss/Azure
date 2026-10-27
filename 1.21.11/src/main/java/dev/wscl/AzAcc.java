package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class AzAcc extends Screen {
	final Screen par;
	EditBox tf;
	Button go;
	Button bk;
	String msg = "";
	boolean busy;

	public AzAcc(Screen p) {
		super(Component.literal("Accounts"));
		par = p;
	}

	protected void init() {
		int w = Math.min(300, width - 40);
		int x = (width - w) / 2;
		int y = height / 2 - 40;
		tf = new EditBox(font, x, y + 22, w, 20, Component.literal("Session token"));
		tf.setMaxLength(4096);
		tf.setHint(Component.literal("Paste the session token"));
		tf.addFormatter((s, i) -> FormattedCharSequence.forward("*".repeat(s.length()), net.minecraft.network.chat.Style.EMPTY));
		addRenderableWidget(tf);
		go = addRenderableWidget(Button.builder(Component.literal("Switch"), b -> sw()).bounds(x, y + 48, w / 2 - 2, 20).build());
		bk = addRenderableWidget(Button.builder(Component.literal("Back to my account"), b -> {
			Acc.back();
			msg = "Back on your own account.";
		}).bounds(x + w / 2 + 2, y + 48, w / 2 - 2, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose()).bounds(x, y + 74, w, 20).build());
		setInitialFocus(tf);
	}

	void sw() {
		if (busy) return;
		busy = true;
		msg = "Checking...";
		String t = tf.getValue();
		Acc.go(t, r -> {
			msg = r;
			busy = false;
			if (r.startsWith("Now")) tf.setValue("");
		});
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		super.render(g, mx, my, d);
		int cx = width / 2;
		int y = height / 2 - 40;
		g.drawCenteredString(font, "Playing as " + minecraft.getUser().getName(), cx, y - 22, 0xFFFFFFFF);
		g.drawCenteredString(font, "Only use a token of an account you own. Never share a token.", cx, y - 8, 0xFFAAAAAA);
		g.drawCenteredString(font, msg, cx, y + 102, 0xFFFFFFFF);
		bk.active = Acc.alt();
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
