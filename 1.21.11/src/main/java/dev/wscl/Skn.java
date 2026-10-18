package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.util.Util;

public class Skn {
	public PlayerSkinWidget w;
	float yaw;
	float pit;
	int lmx = -1;
	int lmy = -1;
	long lmv;
	long lft;
	long t0;

	public Skn(int sw, int sh, int x, int y) {
		Minecraft m = Minecraft.getInstance();
		w = new PlayerSkinWidget(sw, sh, m.getEntityModels(), m.getSkinManager().createLookup(m.getGameProfile(), false));
		w.setPosition(x, y);
		t0 = Util.getMillis();
		lmv = t0;
		lft = t0;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		long now = Util.getMillis();
		if (mx != lmx || my != lmy) {
			if (lmx >= 0 && (Math.abs(mx - lmx) + Math.abs(my - lmy)) > 1) lmv = now;
			lmx = mx;
			lmy = my;
		}
		float dt = Math.min(0.1f, (now - lft) / 1000f);
		lft = now;
		boolean idle = now - lmv > 450;
		float cx = w.getX() + w.getWidth() / 2f;
		float cy = w.getY() + w.getHeight() * 0.35f;
		float warm = Math.max(0f, Math.min(1f, (now - t0 - 350) / 900f));
		warm = warm * warm * (3f - 2f * warm);
		if (idle) {
			yaw += dt * 38f;
			if (now - lmv > 450 && warm < 1f) warm = 1f;
		} else {
			float tg = Math.max(-75f, Math.min(75f, (mx - cx) * 0.4f)) * warm;
			float df = ((tg - yaw) % 360f + 540f) % 360f - 180f;
			yaw += df * Math.min(1f, dt * (2.2f + 2.8f * warm));
		}
		float pt = Math.max(-22f, Math.min(22f, idle ? 0f : (my - cy) * 0.12f * warm));
		pit += (pt - pit) * Math.min(1f, dt * 4f);
		((dev.wscl.mixin.PsMxn) w).ry(yaw);
		((dev.wscl.mixin.PsMxn) w).rx(pit);
		w.render(g, mx, my, d);
	}
}
