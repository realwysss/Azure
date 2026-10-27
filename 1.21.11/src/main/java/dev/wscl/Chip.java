package dev.wscl;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;

public class Chip extends AbstractWidget {
	final Supplier<PlayerSkin> sk;
	float h;

	public Chip(int x, int y) {
		super(x, y, 120, 26, Component.literal("Account"));
		Minecraft m = Minecraft.getInstance();
		sk = m.getSkinManager().createLookup(m.getGameProfile(), false);
		width = Math.max(110, Ui.w(m.getUser().getName()) + (Ias.on() ? 58 : 46));
	}

	protected void renderWidget(GuiGraphics g, int mx, int my, float d) {
		Minecraft m = Minecraft.getInstance();
		int ac = Hud.ac() & 0xFFFFFF;
		boolean ias = Ias.on();
		boolean ov = ias && isHovered();
		h += ((ov ? 1f : 0f) - h) * 0.3f;
		if (h > 0.03f) Ui.glow(g, getX(), getY(), width, height, 9, ac, h);
		Ui.rr(g, getX(), getY(), width, height, 9, Ui.al(Vis.mix(0x0B191E, ac, 0.28f * h), 0.8f));
		Ui.ro(g, getX(), getY(), width, height, 9, Ui.al(h > 0.05f ? ac : 0xFFFFFF, 0.16f + 0.7f * h));
		PlayerFaceRenderer.draw(g, sk.get(), getX() + 5, getY() + 5, 16, Ui.al(0xFFFFFF, 1f));
		String nm = m.getUser().getName();
		Ui.tx(g, nm, getX() + 27, getY() + (ias ? 4 : 9), 0xFFFFFF, 0.95f);
		if (ias) Ui.ts(g, "Switch account", getX() + 27, getY() + 14, 7.5f, Vis.mix(0xFFFFFF, ac, 0.6f), 0.9f);
	}

	public void onClick(MouseButtonEvent e, boolean dbl) {
		if (Ias.on()) Ias.open();
	}

	public void playDownSound(net.minecraft.client.sounds.SoundManager s) {
	}

	protected void updateWidgetNarration(NarrationElementOutput n) {
	}
}
