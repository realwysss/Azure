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
	long fu;

	public void front() {
		fu = Util.getMillis() + 3200;
	}
	public static int pw;
	public static int ph;
	static final String[] EM = {"dab", "takel", "floss", "reanimated", "fresh", "worm", "wiggle", "jubilation", "electro"};
	static final int[] DU = {2200, 2600, 3000, 3200, 3200, 3600, 3000, 3400, 3400};
	static final int SP = 7;
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
	final float[] ex = new float[9];

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
			em = mode == 2 ? SP : (last + 1 + (int) (now % (EM.length - 1))) % EM.length;
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
			if (em != SP) yaw += df * Math.min(1f, dt * 3f);
			else yaw = tg;
		} else if (idle && mode == 1 && now < fu) {
			float dfr = ((0f - yaw) % 360f + 540f) % 360f - 180f;
			yaw += dfr * Math.min(1f, dt * 4f);
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
		float[] e = new float[15];
		e[0] = bra;
		e[1] = brz;
		e[2] = -bra;
		e[3] = -brz;
		e[4] = tgh;
		e[5] = tgp;
		if (em >= 0 && ev > 0.01f) pose(em, et, e);
		float k = Math.min(1f, dt * (em >= 0 ? 16f : 9f));
		for (int i = 0; i < 9; i++) ex[i] = sm(ex[i], e[6 + i], k);
		ra = sm(ra, e[0], k);
		rz = sm(rz, e[1], k);
		la = sm(la, e[2], k);
		lz = sm(lz, e[3], k);
		hy = sm(hy, e[4], Math.min(1f, dt * 7f));
		hx = sm(hx, e[5], Math.min(1f, dt * 7f));
		float rad = (float) Math.PI / 180f;
		PsMxn pm = (PsMxn) w;
		pm.ry(yaw + (em == SP ? 360f * p * p * (3f - 2f * Math.min(1f, p)) : 0f));
		pm.rx(pit + ex[8]);
		for (var md : new net.minecraft.client.model.player.PlayerModel[]{pm.wide(), pm.slim()}) {
			md.head.yRot = hy * rad;
			md.head.xRot = hx * rad;
			md.rightArm.xRot = ra;
			md.rightArm.zRot = rz;
			md.leftArm.xRot = la;
			md.leftArm.zRot = lz;
			md.rightLeg.xRot = ex[0];
			md.leftLeg.xRot = ex[1];
			md.body.yRot = ex[2];
			md.rightLeg.yRot = ex[2] * 0.8f;
			md.leftLeg.yRot = ex[2] * 0.8f;
			md.body.zRot = ex[3];
			md.body.xRot = ex[4];
			md.root().y = ex[5];
			md.rightLeg.zRot = ex[6];
			md.leftLeg.zRot = ex[7];
		}
		cur = w;
		pw = Math.round(w.getWidth() * 0.55f);
		ph = Math.round(w.getHeight() * 0.3f);
		w.render(g, mx, my, d);
		cur = null;
	}

	static float sn(float t, float f) {
		return (float) Math.sin(t * f);
	}

	static void pose(int em, float t, float[] e) {
		switch (em) {
			case 0:
				float dk = Math.min(1f, t * 6f);
				e[0] = -0.15f - 0.05f * dk;
				e[1] = 2.35f * dk;
				e[2] = -2.25f * dk;
				e[3] = 0.55f * dk;
				e[4] = -34f * dk;
				e[5] = 42f * dk;
				e[10] = 0.12f * dk;
				break;
			case 1:
				float sw = sn(t, 5f);
				e[0] = -2.55f;
				e[1] = 0.3f;
				e[2] = -2.2f;
				e[3] = -0.2f;
				e[4] = sw * 10f;
				e[5] = 8f;
				e[9] = sw * 0.14f;
				e[11] = sw * 0.12f;
				e[12] = 0.06f + sw * 0.08f;
				e[13] = -0.06f + sw * 0.08f;
				break;
			case 2:
				float fl = sn(t, 13f);
				e[0] = 0.25f;
				e[1] = fl * 1.15f;
				e[2] = 0.25f;
				e[3] = fl * 1.15f;
				e[4] = -fl * 10f;
				e[5] = 6f;
				e[8] = -fl * 0.55f;
				e[6] = 0.1f;
				e[7] = 0.1f;
				break;
			case 3:
				float st = (float) Math.floor(t * 7f);
				float j = st % 2 == 0 ? 1f : -1f;
				e[0] = -1.25f + j * 0.18f;
				e[1] = 0.15f;
				e[2] = -1.25f - j * 0.18f;
				e[3] = -0.15f;
				e[4] = j * 22f;
				e[5] = 14f + j * 6f;
				e[6] = j * 0.5f;
				e[7] = -j * 0.5f;
				e[10] = 0.22f;
				e[11] = 0.1f * j;
				break;
			case 4:
				float fr = sn(t, 9f);
				e[0] = -0.9f + fr * 0.75f;
				e[1] = 0.3f;
				e[2] = -0.9f - fr * 0.75f;
				e[3] = -0.3f;
				e[4] = fr * 8f;
				e[5] = 8f + Math.abs(fr) * 6f;
				e[6] = fr * 0.4f;
				e[7] = -fr * 0.4f;
				e[10] = 0.12f;
				e[11] = -Math.abs(fr) * 1.5f;
				break;
			case 5:
				float wm = Math.min(1f, t * 3f);
				float wv = sn(t, 10f);
				e[0] = -3.0f * wm;
				e[1] = 0.1f;
				e[2] = -3.0f * wm;
				e[3] = -0.1f;
				e[6] = wv * 0.55f;
				e[7] = wv * 0.55f;
				e[10] = sn(t, 10f) * 0.25f;
				e[14] = 80f * wm;
				break;
			case 6:
				float wg = sn(t, 9f);
				e[0] = -0.5f + wg * 0.25f;
				e[1] = 0.65f + wg * 0.25f;
				e[2] = -0.5f - wg * 0.25f;
				e[3] = -0.65f + wg * 0.25f;
				e[4] = wg * 14f;
				e[5] = 4f;
				e[8] = wg * 0.4f;
				e[9] = wg * 0.1f;
				e[12] = 0.1f + wg * 0.1f;
				e[13] = -0.1f + wg * 0.1f;
				break;
			case 7:
				float jb = Math.abs(sn(t, 6f));
				e[0] = -3.05f;
				e[1] = 0.45f;
				e[2] = -3.05f;
				e[3] = -0.45f;
				e[4] = 0f;
				e[5] = -10f;
				e[6] = jb * 0.5f;
				e[7] = -jb * 0.5f;
				e[11] = -jb * 5f;
				break;
			case 8:
				float el = sn(t, 12f);
				e[0] = -0.9f + el * 0.5f;
				e[1] = 0.35f + Math.max(0f, el) * 0.45f;
				e[2] = -0.9f - el * 0.5f;
				e[3] = -0.35f - Math.max(0f, -el) * 0.45f;
				e[4] = el * 10f;
				e[5] = 6f;
				e[6] = el * 0.45f;
				e[7] = -el * 0.45f;
				e[12] = 0.12f + Math.max(0f, el) * 0.35f;
				e[13] = -0.12f - Math.max(0f, -el) * 0.35f;
				e[11] = Math.abs(el) * -1.2f;
				e[10] = -0.08f;
				break;
			default:
				e[5] = -6f;
				break;
		}
	}
}
