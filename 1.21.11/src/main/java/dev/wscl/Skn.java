package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import dev.wscl.mixin.PsMxn;
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
	float hy;
	float hx;
	float wv;
	long wst;
	long wnx;
	long gst;
	long gnx;
	int gar;
	float ge;

	public Skn(int sw, int sh, int x, int y) {
		Minecraft m = Minecraft.getInstance();
		w = new PlayerSkinWidget(sw, sh, m.getEntityModels(), m.getSkinManager().createLookup(m.getGameProfile(), false));
		w.setPosition(x, y);
		t0 = Util.getMillis();
		lmv = t0;
		lft = t0;
		gnx = t0 + 4000;
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
		float tt = (now - t0) / 1000f;
		boolean lng = now - lmv > 9000 && now - t0 > 9000;
		if (lng && now > wnx && wst == 0) {
			wst = now;
			wnx = now + 22000;
		}
		boolean waving = wst != 0 && now - wst < 3000;
		if (wst != 0 && !waving) wst = 0;
		if (!idle) wst = 0;
		wv += ((waving && lng ? 1f : 0f) - wv) * Math.min(1f, dt * 6f);
		if (wv > 0.02f) {
			float df2 = ((0f - yaw) % 360f + 540f) % 360f - 180f;
			yaw += df2 * Math.min(1f, dt * 5f) * wv;
			yaw -= dt * 38f * wv;
		}
		if (now > gnx && !idle) {
			gst = now;
			gar = (int) (now % 2);
			gnx = now + 6000 + (now % 5000);
		}
		float gp = gst == 0 ? 0f : Math.max(0f, Math.min(1f, (now - gst) / 1800f));
		float gk = gst == 0 || gp >= 1f ? 0f : (float) Math.sin(gp * Math.PI);
		ge += (gk - ge) * Math.min(1f, dt * 8f);
		float tgh;
		float tgp;
		if (idle) {
			tgh = (float) Math.sin(tt * 0.9f) * 22f;
			tgp = (float) Math.sin(tt * 0.6f + 1f) * 5f;
		} else {
			float tg2 = Math.max(-75f, Math.min(75f, (mx - cx) * 0.4f)) * warm;
			float df3 = ((tg2 - yaw) % 360f + 540f) % 360f - 180f;
			tgh = Math.max(-40f, Math.min(40f, df3 * 0.8f + (mx - cx) * 0.05f));
			tgp = Math.max(-22f, Math.min(22f, (my - cy) * 0.1f)) * warm;
		}
		if (wv > 0.05f) {
			tgh = (float) Math.sin(tt * 2.2f) * 6f;
			tgp = -4f;
		}
		hy += (tgh - hy) * Math.min(1f, dt * 6f);
		hx += (tgp - hx) * Math.min(1f, dt * 6f);
		float rad = (float) Math.PI / 180f;
		float br = (float) Math.sin(tt * 1.7f);
		float ra = br * 0.04f;
		float rz = 0.07f + 0.02f * br;
		float la = -br * 0.04f;
		float lz = -rz;
		float wr = 0f;
		float rx2 = ra;
		float rz2 = rz;
		if (wv > 0.01f) {
			float up = wv;
			rx2 = ra * (1f - up) + -0.4f * up;
			rz2 = rz * (1f - up) + (2.55f + (float) Math.sin(tt * 9f) * 0.32f) * up;
		}
		float lx2 = la;
		float lz2 = lz;
		if (ge > 0.01f && wv < 0.3f) {
			if (gar == 0) {
				lx2 = la * (1f - ge) + -1.1f * ge;
				lz2 = lz * (1f - ge) + -0.15f * ge;
			} else {
				rx2 = ra * (1f - ge) + -1.1f * ge;
				rz2 = rz * (1f - ge) + 0.15f * ge;
			}
		}
		PsMxn pm = (PsMxn) w;
		pm.ry(yaw);
		pm.rx(pit);
		for (var md : new net.minecraft.client.model.player.PlayerModel[]{pm.wide(), pm.slim()}) {
			md.head.yRot = hy * rad;
			md.head.xRot = hx * rad;
			md.hat.yRot = md.head.yRot;
			md.hat.xRot = md.head.xRot;
			md.rightArm.xRot = rx2;
			md.rightArm.zRot = rz2;
			md.leftArm.xRot = lx2;
			md.leftArm.zRot = lz2;
			md.rightSleeve.xRot = rx2;
			md.rightSleeve.zRot = rz2;
			md.leftSleeve.xRot = lx2;
			md.leftSleeve.zRot = lz2;
		}
		w.render(g, mx, my, d);
	}
}
