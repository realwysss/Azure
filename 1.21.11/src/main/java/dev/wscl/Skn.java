package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import dev.wscl.mixin.PsMxn;
import net.minecraft.util.Util;

public class Skn {
	public static Object cur;
	public static int pb;
	public static net.minecraft.resources.Identifier cape;
	static net.minecraft.client.model.player.PlayerModel capeModel;
	public int mode;
	long twn;
	long fu;

	public void play(int i) {
		em = i;
		last = i;
		est = Util.getMillis();
	}

	public void front() {
		fu = Util.getMillis() + 3200;
	}
	public static int pw;
	public static int ph;
	static final String[] EM = {"takel", "floss", "dab", "reanimated", "fresh", "worm", "wiggle", "jubilation", "electro"};
	static final int[] DU = {3200, 3200, 2600, 3200, 3200, 3600, 3000, 3400, 3400};
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
	final float[] ex = new float[11];

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
		float el = em < 0 ? 0f : now - est;
		float env = em < 0 ? 0f : ss(el / 250f) * ss((DU[em] - el) / 300f);
		ev = em < 0 ? sm(ev, 0f, Math.min(1f, dt * 8f)) : env;
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
		float[] e = new float[17];
		e[0] = bra;
		e[1] = brz;
		e[2] = -bra;
		e[3] = -brz;
		e[4] = tgh;
		e[5] = tgp;
		if (em >= 0 && ev > 0.001f) {
			float[] q = new float[17];
			pose(em, et, q);
			for (int i = 0; i < 17; i++) e[i] += (q[i] - e[i]) * ev;
		}
		for (int i = 0; i < 11; i++) ex[i] = e[6 + i];
		ra = e[0];
		rz = e[1];
		la = e[2];
		lz = e[3];
		float hk = Math.min(1f, dt * (em >= 0 ? 20f : 7f));
		hy = sm(hy, e[4], hk);
		hx = sm(hx, e[5], hk);
		float rad = (float) Math.PI / 180f;
		PsMxn pm = (PsMxn) w;
		pm.ry(yaw + (em == SP ? 360f * p * p * (3f - 2f * Math.min(1f, p)) : 0f));
		pm.rx(pit + ex[8]);
		float tw = ex[2];
		float ln = ex[3];
		float bw = ex[4];
		org.joml.Vector3f hp = new org.joml.Vector3f(0f, 12f, 0f).rotate(new org.joml.Quaternionf().rotationZYX(ln, tw, bw));
		int pbb = Math.round(w.getHeight() * 0.3f);
		float yo = pbb * 16f / (0.97f * w.getHeight() / 2.125f);
		for (var md : new net.minecraft.client.model.player.PlayerModel[]{pm.wide(), pm.slim(), cm()}) {
			var rt = md.root();
			rt.x = -hp.x + ex[9];
			rt.y = 12f - hp.y + ex[5] - yo;
			rt.z = -hp.z;
			rt.xRot = bw;
			rt.yRot = tw;
			rt.zRot = ln;
			if (md == capeModel) continue;
			md.head.yRot = hy * rad;
			md.head.xRot = hx * rad;
			md.rightArm.xRot = ra;
			md.rightArm.zRot = rz;
			md.leftArm.xRot = la;
			md.leftArm.zRot = lz;
			md.body.xRot = 0f;
			md.body.yRot = 0f;
			md.body.zRot = 0f;
			float lf = 1f - ex[10];
			md.rightLeg.xRot = ex[0] - bw * lf;
			md.leftLeg.xRot = ex[1] - bw * lf;
			md.rightLeg.yRot = -tw * lf;
			md.leftLeg.yRot = -tw * lf;
			md.rightLeg.zRot = ex[6] - ln * lf;
			md.leftLeg.zRot = ex[7] - ln * lf;
		}
		cur = w;
		pw = Math.round(w.getWidth() * 0.75f);
		ph = Math.round(w.getHeight() * 0.4f);
		pb = pbb;
		w.render(g, mx, my, d);
		cur = null;
		pb = 0;
	}

	static float ss(float x) {
		x = Math.max(0f, Math.min(1f, x));
		return x * x * (3f - 2f * x);
	}

	static float sw(float t, float hz) {
		return (float) Math.sin(t * hz * 6.2832f);
	}

	static float cw(float t, float hz) {
		return (float) Math.cos(t * hz * 6.2832f);
	}

	static float pu(float x) {
		return Math.signum(x) * (float) Math.pow(Math.abs(x), 0.6);
	}

	static final float[] DAB = {-2.45f, -0.55f, -1.7f, 1.05f, -35f, 35f, 0f, 0f, 0.15f, -0.1f, 0.12f, 0.3f, 0.06f, -0.06f, 0f, 0f, 0f};

	static float[] mir(float[] a) {
		float[] b = a.clone();
		b[0] = a[2];
		b[1] = -a[3];
		b[2] = a[0];
		b[3] = -a[1];
		b[4] = -a[4];
		b[6] = a[7];
		b[7] = a[6];
		b[8] = -a[8];
		b[9] = -a[9];
		b[12] = -a[13];
		b[13] = -a[12];
		b[15] = -a[15];
		return b;
	}

	static final float[][] RE = {
			{-1.57f, 0.25f, -1.57f, -0.25f, 0f, 8f, 0f, 0f, 0f, 0.05f, 0.08f, 0.3f, 0f, 0f, 0f, 0f, 0f},
			{-2.9f, 0.15f, -0.2f, -0.25f, 20f, -10f, 0f, 0f, 0.25f, -0.06f, 0f, 0.3f, 0f, 0f, 0f, 0.6f, 0f},
			{-1.57f, 0.25f, -1.57f, -0.25f, 0f, 8f, 0f, 0f, 0f, 0.05f, 0.08f, 0.3f, 0f, 0f, 0f, 0f, 0f},
			{-0.2f, 0.25f, -2.9f, -0.15f, -20f, -10f, 0f, 0f, -0.25f, 0.06f, 0f, 0.3f, 0f, 0f, 0f, -0.6f, 0f}};

	static float ps(float x) {
		return Math.max(0f, x);
	}

	static void pose(int em, float t, float[] e) {
		float s;
		switch (em) {
			case 0:
				s = sw(t, 2f);
				e[0] = -2.7f;
				e[1] = 0.55f;
				e[2] = -0.25f + 0.15f * s;
				e[3] = -0.35f;
				e[4] = 10f * s;
				e[5] = -4f;
				e[12] = 0.55f * ps(s);
				e[13] = -0.55f * ps(-s);
				e[9] = -0.07f * s;
				e[11] = -0.5f * Math.abs(s);
				e[15] = -0.6f * s;
				break;
			case 1: {
				float p = t * 1.25f * 6.2832f;
				float z = 0.55f * (float) Math.sin(p);
				float x = 0.45f * (float) Math.cos(p);
				e[0] = x;
				e[1] = 0.12f + z;
				e[2] = x;
				e[3] = -0.12f + z;
				e[15] = -1.3f * (float) Math.sin(p);
				e[9] = 0.07f * (float) Math.sin(p);
				e[4] = -8f * (float) Math.sin(p);
				e[5] = 4f;
				e[11] = 0.25f * (1f - (float) Math.cos(2f * p)) / 2f;
				break;
			}
			case 2: {
				float a = ss(t / 0.25f) * (1f - ss((t - 1.05f) / 0.25f));
				float b = ss((t - 1.3f) / 0.25f) * (1f - ss((t - 2.3f) / 0.25f));
				float[] m = mir(DAB);
				for (int i = 0; i < 17; i++) e[i] = DAB[i] * a + m[i] * b;
				break;
			}
			case 3: {
				float bt = t / 0.4f;
				int ix = (int) Math.floor(bt);
				float k = ss((bt - ix) / 0.3f);
				float[] a = RE[(ix + 3) % 4];
				float[] b = RE[ix % 4];
				for (int i = 0; i < 17; i++) e[i] = a[i] + (b[i] - a[i]) * k;
				e[11] += 0.35f * (1f - (float) Math.cos(bt * 6.2832f)) / 2f;
				break;
			}
			case 4:
				s = sw(t, 1f);
				e[0] = -0.75f;
				e[1] = 0.35f * s + 0.05f;
				e[2] = -0.75f;
				e[3] = 0.35f * s - 0.05f;
				e[15] = -1f * s;
				e[9] = 0.06f * s;
				e[8] = -0.12f * s;
				e[4] = 12f * s;
				e[5] = 2f;
				e[12] = 0.18f * ps(-s);
				e[13] = -0.18f * ps(s);
				e[11] = 0.2f * Math.abs(sw(t, 2f));
				break;
			case 5: {
				float dn = ss(t / 0.5f) * (1f - ss((t - 3.1f) / 0.5f));
				s = sw(t, 1.2f);
				e[0] = -2.9f + 0.3f * s;
				e[1] = 0.15f;
				e[2] = -2.9f - 0.3f * s;
				e[3] = -0.15f;
				e[5] = -35f * dn;
				e[10] = (1.5708f - 0.22f * ps(s)) * dn;
				e[11] = (10.5f - 2.2f * ps(-s)) * dn;
				e[6] = 0.25f * ps(-s) * dn;
				e[7] = 0.25f * ps(-s) * dn;
				e[16] = dn;
				break;
			}
			case 6:
				s = sw(t, 2.2f);
				e[0] = -1.25f;
				e[1] = -0.25f;
				e[2] = -1.25f;
				e[3] = 0.25f;
				e[15] = 0.9f * s;
				e[9] = -0.1f * s;
				e[4] = -6f * s;
				e[5] = 6f;
				e[11] = 0.3f * Math.abs(s);
				break;
			case 7: {
				float h1 = (float) Math.sin(Math.PI * Math.min(1f, Math.max(0f, (t - 0.3f) / 0.6f)));
				float h2 = (float) Math.sin(Math.PI * Math.min(1f, Math.max(0f, (t - 1.7f) / 0.6f)));
				float hop = Math.max(h1, h2);
				float w = sw(t, 1.5f);
				e[0] = -2.85f - 0.2f * w;
				e[1] = 0.35f;
				e[2] = -2.85f + 0.2f * w;
				e[3] = -0.35f;
				e[5] = -15f;
				e[6] = -0.35f * hop;
				e[7] = 0.25f * hop;
				e[11] = -2.2f * hop;
				break;
			}
			case 8:
				s = sw(t, 2f);
				e[0] = -0.55f - 0.45f * s;
				e[1] = 0.15f;
				e[2] = -0.55f + 0.45f * s;
				e[3] = -0.15f;
				e[12] = 0.4f * ps(s);
				e[13] = -0.4f * ps(-s);
				e[6] = -0.2f * ps(-s);
				e[7] = -0.2f * ps(s);
				e[15] = 0.5f * s;
				e[9] = -0.05f * s;
				e[5] = 5f * Math.abs(s);
				e[11] = 0.3f * Math.abs(s);
				break;
			default:
				e[5] = -6f;
				break;
		}
	}
}
