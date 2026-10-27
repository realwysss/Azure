package dev.snprb;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class viw extends Screen {
	public final Screen p;
	public final File f;
	public img t;

	public viw(Screen p, File f) {
		super(Component.literal(f.getName()));
		this.p = p;
		this.f = f;
	}

	@Override
	protected void init() {
		fr();
		try {
			t = new img(img.rd(f, width - 40, height - 60, (int) minecraft.getWindow().getGuiScale(), true));
		} catch (Exception e) {
		}
		int y = height - 30, x = width / 2 - 205;
		addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose()).bounds(x, y, 100, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Copy"), b -> CompletableFuture.runAsync(() -> {
			String er = null;
			try {
				snprb.cp(f);
			} catch (Throwable e) {
				er = e.toString();
			}
			String fer = er;
			minecraft.execute(() -> {
				b.setMessage(Component.literal(fer == null ? "Copied!" : "Error!"));
				if (fer != null && minecraft.player != null)
					minecraft.player.displayClientMessage(Component.literal("Copy failed: " + fer).withStyle(ChatFormatting.RED), false);
			});
		})).bounds(x + 105, y, 100, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Open"), b -> Util.getPlatform().openFile(f.getParentFile())).bounds(x + 210, y, 100, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Delete"), b -> minecraft.setScreen(new del(this, f))).bounds(x + 315, y, 100, 20).build());
	}

	void fr() {
		if (t != null) t.fr();
		t = null;
	}

	@Override
	public void render(GuiGraphics c, int mx, int my, float d) {
		super.render(c, mx, my, d);
		if (t == null) return;
		int x = (width - t.gw) / 2, y = 10 + (height - 60 - t.gh) / 2;
		int ac = dev.wscl.Hud.ac() & 0xFFFFFF;
		dev.wscl.Ui.glow(c, x - 3, y - 3, t.gw + 6, t.gh + 6, 8, ac, 0.4f);
		dev.wscl.Ui.rr(c, x - 3, y - 3, t.gw + 6, t.gh + 6, 8, dev.wscl.Ui.al(dev.wscl.Ui.tn(0x0A0714), 0.95f));
		c.blit(t.id, x, y, x + t.gw, y + t.gh, 0f, 1f, 0f, 1f);
		dev.wscl.Ui.ro(c, x - 3, y - 3, t.gw + 6, t.gh + 6, 8, dev.wscl.Ui.al(ac, 0.7f));
	}

	@Override
	public void removed() {
		fr();
	}

	@Override
	public void onClose() {
		minecraft.setScreen(p);
	}
}
