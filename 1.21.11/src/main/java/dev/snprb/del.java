package dev.snprb;

import java.io.File;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class del extends Screen {
	final Screen p;
	final File f;

	public del(Screen p, File f) {
		super(Component.literal("Delete this screenshot?"));
		this.p = p;
		this.f = f;
	}

	@Override
	protected void init() {
		int y = height / 2, x = width / 2;
		addRenderableWidget(Button.builder(Component.literal("Yes"), b -> {
			f.delete();
			minecraft.setScreen(p instanceof viw v ? v.p : p);
		}).bounds(x - 105, y, 100, 20).build());
		addRenderableWidget(Button.builder(Component.literal("No"), b -> onClose()).bounds(x + 5, y, 100, 20).build());
	}

	@Override
	public void renderBackground(GuiGraphics c, int mx, int my, float d) {
		super.renderBackground(c, mx, my, d);
		int ac = dev.wscl.Hud.ac() & 0xFFFFFF;
		int cw = 260;
		int cx = (width - cw) / 2;
		int cy = height / 2 - 62;
		dev.wscl.Ui.glow(c, cx, cy, cw, 112, 12, ac, 0.35f);
		dev.wscl.Ui.rr(c, cx, cy, cw, 112, 12, dev.wscl.Ui.al(dev.wscl.Ui.tn(0x090714), 0.96f));
		dev.wscl.Ui.ro(c, cx, cy, cw, 112, 12, dev.wscl.Ui.al(0xFFFFFF, 0.14f));
	}

	@Override
	public void render(GuiGraphics c, int mx, int my, float d) {
		super.render(c, mx, my, d);
		dev.wscl.Ui.cx(c, title.getString(), width / 2, height / 2 - 48, 0xFFFFFF, 1f);
		dev.wscl.Ui.cx(c, f.getName(), width / 2, height / 2 - 32, 0xFFFFFF, 0.45f);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(p);
	}
}
