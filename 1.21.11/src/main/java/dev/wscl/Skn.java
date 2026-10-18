package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import dev.wscl.mixin.PsMxn;
import net.minecraft.util.Util;

public class Skn {
	public static Object cur;
	public static net.minecraft.resources.Identifier cape;
	static net.minecraft.client.model.player.PlayerModel capeModel;
	public int mode;
	long twn;
	public static int pw;
	public static int ph;
	static final String[] EM = {"wave", "hello", "flex", "clap", "salute", "dab", "shrug", "think", "dance", "cheer", "point", "yes", "no", "tpose", "zombie", "boxing", "hug", "facepalm", "run", "peace", "heart", "stretch", "headbang", "lookaround", "spin", "inspect", "lookarm", "hip"};
	static final int[] DU = {3000, 3000, 2600, 2600, 2400, 2200, 2200, 2800, 4200, 2600, 2400, 2000, 2000, 2400, 2600, 3200, 2400, 2400, 3000, 2400, 2400, 2800, 3000, 3600, 2200, 3000, 3000, 3000};
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
	float ev;
	int em = -1;
	int last = -1;
	long est;
	long enx;
	float ra;
	float rz;
	float la;
	float lz;

	public static net.minecraft.client.model.player.PlayerModel cm() {
		if (capeModel == null) capeModel = new net.minecraft.client.model.player.PlayerCapeModel(Minecraft.getInstance().getEntityModels().bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER_CAPE));
		return capeModel;
	}

	public Skn(int sw, int sh, int x, int y) {
		this(sw, sh, x, y, null);
	}

	public Skn(int sw, int sh, int x, int y, java.util.function.Supplier<net.minecraft.world.entity.player.PlayerSkin> sup) {
		Minecraft m = Minecraft.getInstance();
		w = new PlayerSkinWidget(sw, sh, m.getEntityModels(), sup != null ? sup : m.getSkinManager().createLookup(m.getGameProfile(), false));
		w.setPosition(x, y);
		t0 = Util.getMillis();
		lmv = t0;
		lft = t0;
		enx = t0 + 3500;
	}

	public void poke() {
		em = (last + 1 + (int) (Util.getMillis() % (EM.length - 1))) % EM.length;
		last = em;
		est = Util.getMillis();
		enx = est + DU[em] + 5000;
	}

	static float sm(float a, float b, float k) {
		return a + (b - a) * k;
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
		boolean idle = mode != 0 || now - lmv > 450;
		float cx = w.getX() + w.getWidth() / 2f;
		float cy = w.getY() + w.getHeight() * 0.35f;
		float warm = Math.max(0f, Math.min(1f, (now - t0 - 350) / 900f));
		warm = warm * warm * (3f - 2f * warm);
		if (idle && warm < 1f) warm = 1f;
		if (em < 0 && now > enx && now - t0 > (mode != 0 ? 800 : 3000)) {
			em = mode == 2 ? 24 : (last + 1 + (int) (now % (EM.length - 1))) % EM.length;
			last = em;
			est = now;
		}
		float p = em < 0 ? 0f : (now - est) / (float) DU[em];
		if (em >= 0 && p >= 1f) {
			em = -1;
			enx = now + (mode == 2 ? 3500 + (now % 2000) : mode == 1 ? 700 + (now % 1200) : 6000 + (now % 5000));
			p = 0f;
		}
		float env = em < 0 ? 0f : (float) Math.sin(Math.min(1f, p) * Math.PI);
		env = Math.min(1f, env * 2.2f);
		ev = sm(ev, env, Math.min(1f, dt * 8f));
		float fw = idle && mode == 0 ? ev : 0f;
		if (mode == 2) {
			float tg = 180f + 14f * (float) Math.sin((now - t0) / 900f);
			float df = ((tg - yaw) % 360f + 540f) % 360f - 180f;
			if (em != 24) yaw += df * Math.min(1f, dt * 3f);
			else yaw = tg;
		} else if (idle) {
			yaw += dt * (mode == 1 ? 26f : 38f) * (1f - fw);
		} else {
			float tg = Math.max(-75f, Math.min(75f, (mx - cx) * 0.4f)) * warm;
			float df = ((tg - yaw) % 360f + 540f) % 360f - 180f;
			yaw += df * Math.min(1f, dt * (2.2f + 2.8f * warm));
		}
		if (fw > 0.02f) {
			float df2 = ((0f - yaw) % 360f + 540f) % 360f - 180f;
			yaw += df2 * Math.min(1f, dt * 5f) * fw;
		}
		float pt = Math.max(-22f, Math.min(22f, idle ? 0f : (my - cy) * 0.12f * warm));
		pit += (pt - pit) * Math.min(1f, dt * 4f);
		float tt = (now - t0) / 1000f;
		float et = em < 0 ? 0f : (now - est) / 1000f;
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
		float br = (float) Math.sin(tt * 1.7f);
		float bra = br * 0.04f;
		float brz = 0.07f + 0.02f * br;
		float[] e = {bra, brz, -bra, -brz, tgh, tgp};
		if (em >= 0 && ev > 0.01f) pose(em, et, e);
		float k = Math.min(1f, dt * 9f);
		ra = sm(ra, e[0], k);
		rz = sm(rz, e[1], k);
		la = sm(la, e[2], k);
		lz = sm(lz, e[3], k);
		hy = sm(hy, e[4], Math.min(1f, dt * 7f));
		hx = sm(hx, e[5], Math.min(1f, dt * 7f));
		float rad = (float) Math.PI / 180f;
		PsMxn pm = (PsMxn) w;
		pm.ry(yaw + (em == 24 ? 360f * p * p * (3f - 2f * Math.min(1f, p)) : 0f));
		pm.rx(pit);
		for (var md : new net.minecraft.client.model.player.PlayerModel[]{pm.wide(), pm.slim()}) {
			md.head.yRot = hy * rad;
			md.head.xRot = hx * rad;
			md.rightArm.xRot = ra;
			md.rightArm.zRot = rz;
			md.leftArm.xRot = la;
			md.leftArm.zRot = lz;
		}
		cur = w;
		pw = Math.round(w.getWidth() * 0.55f);
		ph = Math.round(w.getHeight() * 0.3f);
		w.render(g, mx, my, d);
		cur = null;
	}

	static void pose(int em, float t, float[] e) {
		switch (em) {
			case 0:
				e[0] = -0.4f;
				e[1] = 2.55f + (float) Math.sin(t * 9f) * 0.32f;
				e[4] = (float) Math.sin(t * 2.2f) * 6f;
				e[5] = -4f;
				break;
			case 1:
				e[0] = -0.4f;
				e[1] = 2.55f + (float) Math.sin(t * 9f) * 0.32f;
				e[2] = -0.4f;
				e[3] = -2.55f - (float) Math.sin(t * 9f + 1.5f) * 0.32f;
				e[5] = -6f;
				break;
			case 2:
				float fl = 1.55f + (float) Math.sin(t * 13f) * 0.05f;
				e[0] = -0.1f;
				e[1] = fl;
				e[2] = -0.1f;
				e[3] = -fl;
				e[5] = 6f;
				break;
			case 3:
				float cl = (float) (Math.sin(t * 17f) + 1f) / 2f;
				e[0] = -1.4f;
				e[1] = -0.05f - cl * 0.32f;
				e[2] = -1.4f;
				e[3] = 0.05f + cl * 0.32f;
				e[5] = 8f;
				break;
			case 4:
				e[0] = -2.05f;
				e[1] = -0.35f;
				e[4] = 0f;
				e[5] = -8f;
				break;
			case 5:
				e[0] = -0.3f;
				e[1] = 2.35f;
				e[2] = -1.55f;
				e[3] = -1.0f;
				e[4] = -30f;
				e[5] = 32f;
				break;
			case 6:
				float sh = (float) Math.sin(t * 6f) * 0.12f;
				e[0] = -0.35f;
				e[1] = 0.95f + sh;
				e[2] = -0.35f;
				e[3] = -0.95f - sh;
				e[5] = -6f;
				e[4] = (float) Math.sin(t * 3f) * 8f;
				break;
			case 7:
				e[0] = -1.95f;
				e[1] = -0.7f;
				e[4] = -14f;
				e[5] = 12f;
				break;
			case 8:
				float dn = (float) Math.sin(t * 7f);
				e[0] = -0.2f;
				e[1] = 1.3f + dn * 1.0f;
				e[2] = -0.2f;
				e[3] = -(1.3f - dn * 1.0f);
				e[4] = dn * 14f;
				e[5] = (float) Math.sin(t * 14f) * 9f;
				break;
			case 9:
				e[0] = -0.2f;
				e[1] = 2.85f + (float) Math.sin(t * 11f) * 0.2f;
				e[2] = -0.2f;
				e[3] = -2.85f - (float) Math.sin(t * 11f + 1f) * 0.2f;
				e[5] = -16f;
				break;
			case 10:
				e[0] = -1.57f;
				e[1] = 0.12f;
				e[4] = 18f;
				e[5] = 2f;
				break;
			case 11:
				e[5] = 8f + (float) Math.sin(t * 8f) * 14f;
				e[4] = 0f;
				break;
			case 12:
				e[4] = (float) Math.sin(t * 8f) * 24f;
				e[5] = 0f;
				break;
			case 13:
				e[0] = -0.05f;
				e[1] = 1.57f;
				e[2] = -0.05f;
				e[3] = -1.57f;
				e[4] = 0f;
				e[5] = 0f;
				break;
			case 14:
				e[0] = -1.57f;
				e[1] = 0.05f;
				e[2] = -1.57f;
				e[3] = -0.05f;
				e[5] = 6f;
				break;
			case 15:
				float pu = (float) Math.sin(t * 9f);
				e[0] = -1.1f - pu * 0.55f;
				e[1] = -0.25f;
				e[2] = -1.1f + pu * 0.55f;
				e[3] = 0.25f;
				e[4] = pu * 10f;
				break;
			case 16:
				e[0] = -1.25f;
				e[1] = -0.75f;
				e[2] = -1.25f;
				e[3] = 0.75f;
				e[5] = 10f;
				break;
			case 17:
				e[0] = -2.35f;
				e[1] = -0.45f;
				e[5] = 24f;
				e[4] = -8f;
				break;
			case 18:
				float rn = (float) Math.sin(t * 13f);
				e[0] = rn * 1.1f;
				e[1] = 0.1f;
				e[2] = -rn * 1.1f;
				e[3] = -0.1f;
				e[5] = 5f;
				break;
			case 19:
				e[0] = -0.3f;
				e[1] = 2.1f;
				e[4] = 14f;
				e[5] = -4f;
				break;
			case 20:
				e[0] = -2.7f;
				e[1] = -0.35f;
				e[2] = -2.7f;
				e[3] = 0.35f;
				e[5] = -8f;
				break;
			case 21:
				float sr = (float) Math.sin(t * 3f) * 0.12f;
				e[0] = -0.25f;
				e[1] = 2.9f + sr;
				e[2] = -0.25f;
				e[3] = -2.9f - sr;
				e[5] = -22f;
				break;
			case 22:
				float hb = (float) Math.sin(t * 16f);
				e[0] = -0.4f;
				e[1] = 2.0f + hb * 0.12f;
				e[2] = -0.4f;
				e[3] = -2.0f - hb * 0.12f;
				e[5] = 8f + hb * 26f;
				break;
			case 23:
				e[4] = (float) Math.sin(t * 2.4f) * 55f;
				e[5] = (float) Math.sin(t * 1.2f) * 6f;
				break;
			case 25:
				e[0] = -1.0f;
				e[1] = -0.1f;
				e[2] = -1.0f;
				e[3] = 0.1f;
				e[4] = (float) Math.sin(t * 2.2f) * 10f;
				e[5] = 32f;
				break;
			case 26:
				e[0] = -1.35f;
				e[1] = -0.25f;
				e[4] = -26f;
				e[5] = 8f + (float) Math.sin(t * 3f) * 4f;
				break;
			case 27:
				e[0] = -0.05f;
				e[1] = 0.75f;
				e[2] = -0.05f;
				e[3] = -0.75f;
				e[4] = (float) Math.sin(t * 1.6f) * 18f;
				e[5] = -4f;
				break;
			default:
				e[5] = -6f;
				break;
		}
	}
}
