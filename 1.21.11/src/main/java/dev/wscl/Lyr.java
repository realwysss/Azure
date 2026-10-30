package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Lyr {
	static class Ln {
		String t;
		String[] cs;
		float[] cw;
		int[] ws;
		int from, to;
		double x, y, z;
		float yaw;
		int side;
		long st, en, born, dead;
		double up;
		boolean ttl, note;
	}

	static volatile String key = "";
	static volatile long[] ts = new long[0];
	static volatile String[] tx = new String[0];
	static volatile int stt;
	static volatile boolean rough;
	static int li = -1;
	static final List<Ln> air = new ArrayList<>();
	static long lt;
	static String ttlk = "";
	static int sn;
	static long nat;
	static long nxn;
	static final Matrix4f M1 = new Matrix4f();
	static final Matrix4f M2 = new Matrix4f();
	static final java.util.Random RN = new java.util.Random();
	static Identifier gtx, rtx;
	static final List<Object[]> GQ = new ArrayList<>();

	static int gi(String k, int d) {
		return Cfg.gi("lyrics." + k, d);
	}

	static long now() {
		return Spot.now() + gi("off", 0);
	}

	static float eo(float p) {
		float q = 1 - p;
		return 1 - q * q * q * q;
	}

	public static void tick() {
		Spot.tick();
		Spot.upd();
		String k = Spot.on() ? Spot.song + "\u0001" + Spot.art : "";
		if (!k.equals(key)) {
			key = k;
			ts = new long[0];
			tx = new String[0];
			li = -1;
			stt = k.isEmpty() ? 0 : 1;
			if (!k.isEmpty()) {
				String s0 = Spot.song, a0 = Spot.art;
				long du = Spot.dur;
				Thread t = new Thread(() -> load(k, s0, a0, du), "azure-lyrics");
				t.setDaemon(true);
				t.start();
			}
		}
		Minecraft m = Minecraft.getInstance();
		if (m.player == null || stt < 2) return;
		long n = now();
		long r = System.currentTimeMillis();
		if (gi("tt", 1) == 1 && !ttlk.equals(key) && (stt == 3 || n < 15000)) {
			ttlk = key;
			spawn(m, Spot.song + (Spot.art.isEmpty() ? "" : "  •  " + Spot.art), n, n + 2600, true, false, 0);
		}
		if (stt >= 3) {
			if (Spot.playing && r > nxn) {
				nxn = r + 2200 + RN.nextInt(1600);
				spawn(m, RN.nextBoolean() ? "♪" : "♫", n, n + 1200, false, true, 0);
			}
			return;
		}
		long[] t0 = ts;
		int i = java.util.Arrays.binarySearch(t0, n);
		if (i < 0) i = -i - 2;
		if (i != li) {
			li = i;
			if (i >= 0 && !tx[i].isBlank()) spawn(m, tx[i], t0[i], i + 1 < t0.length ? t0[i + 1] : t0[i] + 4000, false, false, 0);
			else for (Ln l : air) if (l.dead == 0) l.dead = r;
		}
		if (gi("fl", 1) == 1) follow(m, n, r);
	}

	static void follow(Minecraft m, long n, long r) {
		Ln l = null;
		for (Ln o : air) if (o.dead == 0 && !o.ttl && !o.note) l = o;
		if (l == null) return;
		Vec3 e = m.player.getEyePosition();
		double dx = l.x - e.x, dz = l.z - e.z;
		double dist = Math.sqrt(dx * dx + dz * dz);
		double ang = Math.toDegrees(Math.atan2(-dx, dz));
		double df = Math.abs(((m.player.getYRot() - ang) % 360 + 540) % 360 - 180);
		if (df < 62 && dist < gi("ds", 6) * 2 + 4) return;
		int k = from(l, n);
		if (k <= l.from || k >= l.to) return;
		if (l.t.charAt(k - 1) != ' ') return;
		int cut = k;
		while (cut > l.from && l.t.charAt(cut - 1) == ' ') cut--;
		l.to = cut;
		l.dead = r;
		Ln nl = mk(l.t, l.st, l.en, false, false);
		nl.from = k;
		place(m, nl, l.side);
		lift();
		air.add(nl);
	}

	static int from(Ln l, long n) {
		int k = 0;
		for (int i = 0; i < l.cs.length; i++) if (rt(l, i) <= n) k = i + 1;
		return k;
	}

	static Ln mk(String t, long st, long en, boolean ttl, boolean note) {
		Ln l = new Ln();
		l.t = t;
		int n = t.length();
		l.cs = new String[n];
		l.cw = new float[n];
		l.ws = new int[n];
		Font f = Minecraft.getInstance().font;
		int w0 = 0;
		for (int i = 0; i < n; i++) {
			l.cs[i] = String.valueOf(t.charAt(i));
			l.cw[i] = f.width(l.cs[i]);
			if (i > 0 && t.charAt(i - 1) == ' ' && t.charAt(i) != ' ') w0 = i;
			l.ws[i] = w0;
		}
		l.to = n;
		l.st = st;
		l.en = Math.max(st + 600, en);
		l.ttl = ttl;
		l.note = note;
		l.born = System.currentTimeMillis();
		return l;
	}

	static void place(Minecraft m, Ln l, int side) {
		Vec3 e = m.player.getEyePosition();
		int pl = gi("pl", 0);
		if (side == 0) side = pl == 0 ? (sn++ % 2 == 0 ? 1 : -1) : pl == 2 ? 1 : pl == 3 ? -1 : 0;
		if (l.note) side = RN.nextBoolean() ? 1 : -1;
		l.side = side;
		float yaw = m.player.getYRot() + side * (l.note ? 20 + RN.nextInt(20) : 26);
		double ds = gi("ds", 6);
		double rd = Math.toRadians(yaw);
		l.x = e.x - Math.sin(rd) * ds;
		l.z = e.z + Math.cos(rd) * ds;
		l.y = e.y + gi("ht", 1) * 0.5 + (l.note ? RN.nextDouble() * 0.8 - 0.4 : 0);
		l.yaw = yaw;
	}

	static void lift() {
		long r = System.currentTimeMillis();
		for (Ln o : air) if (o.dead == 0) o.dead = r;
	}

	static void spawn(Minecraft m, String t, long st, long en, boolean ttl, boolean note, int side) {
		if (!note) lift();
		Ln l = mk(t, st, en, ttl, note);
		place(m, l, side);
		if (note) l.dead = l.born + 1400;
		air.add(l);
	}

	static Path dir() {
		return FabricLoader.getInstance().getConfigDir().resolve("azure/lyrics2");
	}

	static void load(String k, String s0, String a0, long du) {
		try {
			Files.createDirectories(dir());
			Path f = dir().resolve(Integer.toHexString(k.toLowerCase().hashCode()) + ".lrc");
			String body = null;
			if (Files.exists(f)) {
				body = Files.readString(f);
				if (body.startsWith("#none") && System.currentTimeMillis() - Files.getLastModifiedTime(f).toMillis() > 12 * 3600000L) body = null;
			}
			if (body == null) {
				body = fetch(s0, a0, du);
				Files.writeString(f, body == null ? "#none" : body);
			}
			if (!k.equals(key)) return;
			if (body == null || body.startsWith("#none")) {
				miss();
				return;
			}
			parse(body, du);
			if (ts.length == 0) miss();
			else stt = 2;
		} catch (Throwable e) {
			if (k.equals(key)) {
				stt = 4;
				nat = System.currentTimeMillis();
			}
		}
	}

	static void miss() {
		stt = 3;
		nat = System.currentTimeMillis();
	}

	static String q(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	static JsonElement get(HttpClient hc, String u) {
		try {
			HttpResponse<String> r = hc.send(HttpRequest.newBuilder(URI.create(u)).header("User-Agent", "Azure Client (azureclient.dev)").timeout(java.time.Duration.ofSeconds(10)).build(), HttpResponse.BodyHandlers.ofString());
			if (r.statusCode() != 200) return null;
			return JsonParser.parseString(r.body());
		} catch (Throwable e) {
			return null;
		}
	}

	static String pick(JsonObject o) {
		if (o == null) return null;
		if (o.has("instrumental") && o.get("instrumental").getAsBoolean()) return null;
		if (o.has("syncedLyrics") && !o.get("syncedLyrics").isJsonNull()) return o.get("syncedLyrics").getAsString();
		if (o.has("plainLyrics") && !o.get("plainLyrics").isJsonNull()) return "#plain\n" + o.get("plainLyrics").getAsString();
		return null;
	}

	static String ct(String s) {
		s = s.replaceAll("(?i)\\s*[(\\[][^)\\]]*(feat|ft\\.|with |remaster|version|live|edit|explicit|mono|stereo|bonus|deluxe|official|audio|video|lyric|sped|slowed|reverb)[^)\\]]*[)\\]]", "");
		s = s.replaceAll("(?i)\\s+-\\s+[^-]*(remaster|version|live|edit|mono|stereo|bonus|single|radio|mix|sped|slowed).*$", "");
		return s.trim();
	}

	static String ca(String a) {
		return a.split("(?i)\\s*(,|&| feat\\.? | ft\\.? | x | and )\\s*")[0].trim();
	}

	static String fetch(String s0, String a0, long du) {
		HttpClient hc = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
		String t1 = ct(s0);
		String a1 = ca(a0);
		String pl = null;
		for (String[] v : new String[][]{{s0, a1}, {t1, a1}}) {
			JsonElement e = get(hc, "https://lrclib.net/api/get?track_name=" + q(v[0]) + "&artist_name=" + q(v[1]) + (du > 0 ? "&duration=" + du / 1000 : ""));
			String s = e != null && e.isJsonObject() ? pick(e.getAsJsonObject()) : null;
			if (s != null && !s.startsWith("#plain")) return s;
			if (pl == null) pl = s;
		}
		for (String u : new String[]{"track_name=" + q(t1) + "&artist_name=" + q(a1), "q=" + q(t1 + " " + a1), "q=" + q(t1)}) {
			JsonElement r = get(hc, "https://lrclib.net/api/search?" + u);
			if (r == null || !r.isJsonArray()) continue;
			JsonArray ja = r.getAsJsonArray();
			String best = null;
			double bd = 1e9;
			for (JsonElement x : ja) {
				JsonObject o = x.getAsJsonObject();
				String p = pick(o);
				if (p == null) continue;
				double d = du > 0 && o.has("duration") && !o.get("duration").isJsonNull() ? Math.abs(o.get("duration").getAsDouble() - du / 1000.0) : 0;
				if (u.startsWith("q=" + q(t1)) && u.length() == ("q=" + q(t1)).length() && du > 0 && d > 4) continue;
				if (d > 12) continue;
				if (!p.startsWith("#plain") && d < bd) {
					best = p;
					bd = d;
				}
				if (pl == null) pl = p;
			}
			if (best != null) return best;
		}
		if (pl != null) return pl;
		JsonElement o = get(hc, "https://api.lyrics.ovh/v1/" + q(a1).replace("+", "%20") + "/" + q(t1).replace("+", "%20"));
		if (o != null && o.isJsonObject() && o.getAsJsonObject().has("lyrics")) {
			String s = o.getAsJsonObject().get("lyrics").getAsString().trim();
			if (!s.isEmpty()) return "#plain\n" + s;
		}
		return null;
	}

	static void parse(String b, long du) {
		List<Long> t = new ArrayList<>();
		List<String> x = new ArrayList<>();
		if (b.startsWith("#plain")) {
			List<String> ok = new ArrayList<>();
			for (String l : b.substring(6).trim().split("\n")) if (!l.isBlank() && !l.trim().startsWith("Paroles de")) ok.add(l.trim());
			long d0 = du > 0 ? du : 180000;
			for (int i = 0; i < ok.size(); i++) {
				t.add((long) (d0 * (0.08 + 0.87 * i / Math.max(1, ok.size()))));
				x.add(ok.get(i));
			}
			rough = true;
		} else {
			rough = false;
			for (String l : b.split("\n")) {
				l = l.trim();
				List<Long> tl = new ArrayList<>();
				while (l.startsWith("[")) {
					int c = l.indexOf(']');
					if (c < 0) break;
					String tg = l.substring(1, c);
					l = l.substring(c + 1);
					try {
						String[] p = tg.split(":");
						tl.add((long) ((Integer.parseInt(p[0]) * 60 + Double.parseDouble(p[1])) * 1000));
					} catch (Exception e) {
					}
				}
				for (long v : tl) {
					t.add(v);
					x.add(l.trim());
				}
			}
		}
		Integer[] o = new Integer[t.size()];
		for (int i = 0; i < o.length; i++) o[i] = i;
		java.util.Arrays.sort(o, (a, c) -> Long.compare(t.get(a), t.get(c)));
		long[] ts2 = new long[o.length];
		String[] tx2 = new String[o.length];
		for (int i = 0; i < o.length; i++) {
			ts2[i] = t.get(o[i]);
			tx2[i] = x.get(o[i]);
		}
		tx = tx2;
		ts = ts2;
	}

	static long rt(Ln l, int i) {
		long span = (long) (Math.min(l.en - l.st, 7000) * 0.8);
		int len = Math.max(1, l.cs.length);
		return l.st + span * l.ws[i] / len + (i - l.ws[i]) * 28L;
	}

	static int base(int i) {
		int cm = gi("cm", 0);
		if (cm == 1) return Rw.cv(gi("tc", 0xFFFFFF));
		if (cm == 2) return java.awt.Color.HSBtoRGB((System.currentTimeMillis() / 3000f + i * 0.045f) % 1f, 0.6f, 1f) & 0xFFFFFF;
		return Hud.ac() & 0xFFFFFF;
	}

	interface Dr {
		void ch(String c, float cw, float x, float y, float s, int col);
	}

	static float pw(Ln l) {
		float w = 0;
		for (int i = l.from; i < l.to; i++) w += l.cw[i];
		return w;
	}

	static void lay(Ln l, long n, Dr d) {
		int an = l.ttl ? 3 : gi("an", 0);
		boolean hl = gi("hl", 1) == 1;
		int hc = Rw.cv(gi("hc", 0xFFFFFF));
		float la = Math.min(1f, Math.max(0f, (n - l.st) / 300f));
		long r = System.currentTimeMillis();
		float x = -pw(l) / 2f;
		for (int i = l.from; i < l.to; i++) {
			float cw = l.cw[i];
			long t0 = rt(l, i);
			float p = l.note ? Math.min(1f, (r - l.born) / 320f) : l.dead > 0 ? 1f : Math.max(0f, Math.min(1f, (n - t0) / 320f));
			float e = eo(p);
			float a = e, s = 1f, yo = 0f;
			switch (an) {
				case 0:
					s = 0.85f + 0.15f * e;
					yo = (1 - e) * 6f;
					break;
				case 1: {
					float c = 1.70158f, q = p - 1;
					s = 0.85f + 0.15f * (1 + (c + 1) * q * q * q + c * q * q);
					break;
				}
				case 2:
					a = p > 0 ? 1f : 0f;
					break;
				case 3:
					a = eo(la);
					break;
				case 4:
					yo = (float) Math.sin(r / 260.0 + i * 0.45) * 1.6f;
					break;
				case 5:
					yo = -(1 - e) * 10f;
					break;
			}
			int b = base(i);
			int col = b;
			if (l.ttl || l.note) col = hc;
			else if (hl && l.dead == 0) {
				if (an == 3 || an == 4) {
					col = n >= t0 ? hc : Vis.mix(b, 0x000000, 0.35f);
				} else col = Vis.mix(hc, b, Math.max(0f, Math.min(1f, (n - t0 - 320) / 900f)));
			}
			if (a > 0.02f && l.t.charAt(i) != ' ') d.ch(l.cs[i], cw, x + cw / 2f, yo, s, Math.round(a * 255) << 24 | col);
			x += cw;
		}
	}

	static void tex() {
		if (gtx != null) return;
		var tm = Minecraft.getInstance().getTextureManager();
		NativeImage g = new NativeImage(64, 64, false);
		for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) {
			double dx = (x - 31.5) / 32, dy = (y - 31.5) / 32;
			double d = dx * dx + dy * dy;
			int v = (int) Math.round(255 * Math.max(0, Math.exp(-d * 4.2) - 0.015));
			g.setPixel(x, y, v << 24 | 0xFFFFFF);
		}
		gtx = Identifier.fromNamespaceAndPath("azure", "lyr_glow");
		tm.register(gtx, new DynamicTexture(() -> "azure_lyr_glow", g));
		NativeImage ry = new NativeImage(128, 128, false);
		java.util.Random rr = new java.util.Random(7);
		int nr = 14;
		double[] ra = new double[nr], rl = new double[nr], rw2 = new double[nr];
		for (int i = 0; i < nr; i++) {
			ra[i] = i * Math.PI * 2 / nr + rr.nextDouble() * 0.3;
			rl[i] = 0.55 + rr.nextDouble() * 0.45;
			rw2[i] = 60 + rr.nextDouble() * 90;
		}
		for (int y = 0; y < 128; y++) for (int x = 0; x < 128; x++) {
			double dx = (x - 63.5) / 64, dy = (y - 63.5) / 64;
			double d = Math.sqrt(dx * dx + dy * dy);
			double an = Math.atan2(dy, dx);
			double s = 0;
			for (int i = 0; i < nr; i++) {
				double c = Math.cos(an - ra[i]);
				if (c <= 0) continue;
				s += Math.pow(c, rw2[i]) * Math.max(0, 1 - d / rl[i]);
			}
			s = Math.min(1, s * 1.2 + Math.exp(-d * d * 30) * 0.8);
			int v = (int) Math.round(255 * s);
			ry.setPixel(x, y, v << 24 | 0xFFFFFF);
		}
		rtx = Identifier.fromNamespaceAndPath("azure", "lyr_rays");
		tm.register(rtx, new DynamicTexture(() -> "azure_lyr_rays", ry));
	}

	static void quad(VertexConsumer v, Matrix4f m, float cx, float cy, float hw, float hh, float rot, int c) {
		float cs = (float) Math.cos(rot), sn2 = (float) Math.sin(rot);
		float[][] p = {{-hw, -hh, 0, 0}, {-hw, hh, 0, 1}, {hw, hh, 1, 1}, {hw, -hh, 1, 0}};
		for (int k = 0; k < 2; k++) {
			for (int j = 0; j < 4; j++) {
				float[] q = p[k == 0 ? j : 3 - j];
				float x = cx + q[0] * cs - q[1] * sn2, y = cy + q[0] * sn2 + q[1] * cs;
				v.addVertex(m, x, y, 0.5f).setColor(c).setUv(q[2], q[3]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(0, 0, 1);
			}
		}
	}

	public static void draw(MultiBufferSource.BufferSource bs, PoseStack ps, Vec3 cam) {
		if (gi("wh", 0) == 1 || air.isEmpty()) return;
		Minecraft m = Minecraft.getInstance();
		Font f = m.font;
		long r = System.currentTimeMillis();
		float dt = lt == 0 ? 0.016f : Math.min(0.1f, (r - lt) / 1000f);
		lt = r;
		long n = now();
		long fo = gi("fo", 4) * 1000L;
		air.removeIf(l -> l.dead > 0 && r - l.dead > (l.note ? 1600 : fo));
		float sz = gi("sz", 100) / 100f * 0.03f;
		boolean face = gi("fc", 0) == 1;
		boolean sw = gi("sw", 0) == 1;
		boolean sh = gi("sh", 1) == 1;
		boolean ol = gi("ol", 0) == 1;
		boolean gl = gi("gl", 1) == 1;
		boolean ry = gi("ry", 1) == 1;
		int gc = Rw.cv(gi("gc", Rw.AZ));
		float gs = gi("gs", 60) / 100f;
		float gz = gi("gz", 100) / 100f;
		float rlen = gi("rl", 100) / 100f;
		float pulse = gi("pm", 1) == 1 ? 0.7f + 0.6f * Spot.lvl : 1f;
		Font.DisplayMode dm = sw ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
		Quaternionf cr = m.gameRenderer.getMainCamera().rotation();
		Vec3 lk = Vec3.directionFromRotation(m.gameRenderer.getMainCamera().xRot(), m.gameRenderer.getMainCamera().yRot());
		if (gl) tex();
		RenderType gt = gl ? RenderTypes.entityTranslucentEmissive(gtx) : null;
		RenderType rtp = gl && ry ? RenderTypes.entityTranslucentEmissive(rtx) : null;
		int[] rk = new int[3];
		for (int j = air.size() - 1; j >= 0; j--) {
			Ln l = air.get(j);
			if (l.note) continue;
			int si = l.side + 1;
			double tgt = rk[si]++ * (l.ttl ? 0.32 : 0.38) * gi("sz", 100) / 100.0;
			if (l.dead > 0 && gi("fu", 1) == 1) tgt += eo(Math.min(1f, (r - l.dead) / (float) fo)) * 0.35;
			l.up += (tgt - l.up) * (1 - Math.exp(-dt * 7));
		}
		for (Ln l : air) {
			if (l.note) l.up = eo(Math.min(1f, (r - l.born) / 1600f)) * 0.6;
			float fa;
			if (l.dead > 0) {
				float q = Math.min(1f, (r - l.dead) / (float) (l.note ? 1600 : fo));
				fa = 1 - q * q;
			} else fa = 1f;
			if (fa <= 0.02f) continue;
			double rx = l.x - cam.x, ry2 = l.y + l.up - cam.y, rz = l.z - cam.z;
			if (rx * lk.x + ry2 * lk.y + rz * lk.z < -1) continue;
			M1.set(ps.last().pose()).translate((float) rx, (float) ry2, (float) rz);
			if (face) M1.rotate(cr);
			else M1.rotate(new Quaternionf().rotationY((float) (Math.PI - Math.toRadians(l.yaw))));
			float s2 = l.ttl ? sz * 0.75f : l.note ? sz * 1.6f : sz;
			M1.scale(s2, -s2, s2);
			if (gl) {
				float w = pw(l);
				float ga = gs * fa * pulse * (l.dead > 0 ? 0.6f : 1f);
				GQ.add(new Object[]{new Matrix4f(M1), w, ga, l.note});
			}
			lay(l, n, (c, cw, x, y, s, col) -> {
				int a2 = Math.round(((col >>> 24) & 255) * fa);
				if (a2 < 5) return;
				int cc = a2 << 24 | col & 0xFFFFFF;
				M2.set(M1).translate(x, y + 4, 0).scale(s, s, s);
				if (ol) f.drawInBatch8xOutline(FormattedCharSequence.forward(c, Style.EMPTY), -cw / 2f, -4, cc, a2 << 24, M2, bs, 0xF000F0);
				else f.drawInBatch(c, -cw / 2f, -4, cc, sh, M2, bs, dm, 0, 0xF000F0);
			});
		}
		var fr = ((dev.wscl.mixin.GaAcc) m.gameRenderer).azfg();
		var old = com.mojang.blaze3d.systems.RenderSystem.getShaderFog();
		com.mojang.blaze3d.systems.RenderSystem.setShaderFog(fr.getBuffer(FogRenderer.FogMode.NONE));
		bs.endBatch();
		for (Object[] q : GQ) {
			Matrix4f mt = (Matrix4f) q[0];
			float w = (Float) q[1], ga = (Float) q[2];
			quad(bs.getBuffer(gt), mt, 0, 0.5f, w / 2f + 14 * gz, 9 * gz + 4, 0, glc(gc, ga * 0.55f));
			if (rtp != null && !(Boolean) q[3]) {
				float rr = (9 + 10 * gz) * 1.4f * rlen * (0.85f + 0.15f * pulse);
				float rot = r / 2600f;
				int rc = glc(gc, ga * 0.8f);
				quad(bs.getBuffer(rtp), mt, -w / 2f - 2, 0.5f, rr, rr, rot, rc);
				quad(bs.getBuffer(rtp), mt, w / 2f + 2, 0.5f, rr, rr, -rot, rc);
			}
		}
		GQ.clear();
		if (gt != null) bs.endBatch(gt);
		if (rtp != null) bs.endBatch(rtp);
		com.mojang.blaze3d.systems.RenderSystem.setShaderFog(old);
	}

	static int glc(int rgb, float a) {
		a = Math.max(0f, Math.min(1f, a));
		return Math.round(a * 255) << 24 | rgb & 0xFFFFFF;
	}

	public static void hud(GuiGraphics g, Minecraft m, int w, int h) {
		long r = System.currentTimeMillis();
		if (nat > 0 && r - nat < 4500 && Spot.on()) {
			float k = r - nat < 250 ? eo((r - nat) / 250f) : r - nat > 3900 ? 1 - (r - nat - 3900) / 600f : 1f;
			String s = stt == 4 ? "Could not load the lyrics for this song" : "Lyrics for this song are not available";
			int tw = Hud.tw(s);
			int y = h - 72 + Math.round((1 - k) * 4);
			Ui.rr(g, w / 2 - tw / 2 - 8, y - 4, tw + 16, 16, 6, Ui.al(0x0A0714, 0.55f * k));
			Hud.dt(g, s, w / 2 - tw / 2, y, Ui.al(0xFFFFFF, 0.85f * k));
		}
		int wh = gi("wh", 0);
		if (wh == 0 && !Hud.ed) return;
		float s = Hud.es(Hud.LYR);
		int bw = 280;
		int bh = 46;
		int[] p = Hud.place(Hud.LYR, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		if (gi("pnl", 0) == 1) Hud.pn(g, p[0], p[1], bw, bh);
		Font f = m.font;
		long n = now();
		int cx = p[0] + bw / 2;
		String msg = null;
		if (!Spot.on()) msg = Hud.ed ? "Lyrics show up here" : null;
		else if (stt == 1) msg = "Looking for lyrics...";
		else if (stt == 3) msg = "No lyrics for this song";
		else if (stt == 4) msg = "Could not load lyrics";
		if (msg != null || stt != 2) {
			if (msg != null) Hud.dt(g, msg, cx - Hud.tw(msg) / 2, p[1] + bh / 2 - 4, Ui.al(0xFFFFFF, 0.6f));
			ps.popMatrix();
			return;
		}
		long[] t0 = ts;
		String[] x0 = tx;
		int i = li;
		if (gi("ctx", 1) == 1) {
			if (i > 0 && i - 1 < x0.length && !x0[i - 1].isBlank()) ctxl(g, f, x0[i - 1], cx, p[1] + 2);
			if (i + 1 < x0.length && !x0[i + 1].isBlank()) ctxl(g, f, x0[i + 1], cx, p[1] + bh - 10);
		}
		if (i >= 0 && i < x0.length && !x0[i].isBlank()) {
			if (hl == null || !hl.t.equals(x0[i]) || hl.st != t0[i]) hl = mk(x0[i], t0[i], i + 1 < t0.length ? t0[i + 1] : t0[i] + 4000, false, false);
			int y = p[1] + bh / 2;
			boolean shd = gi("sh", 1) == 1;
			lay(hl, n, (c, cw, xx, yy, sc, col) -> {
				ps.pushMatrix();
				ps.translate(cx + xx * 1.25f, y + yy);
				ps.scale(sc * 1.25f, sc * 1.25f);
				g.drawString(f, c, Math.round(-cw / 2f), -4, col, shd);
				ps.popMatrix();
			});
		} else {
			String d = "♪";
			g.drawString(f, d, cx - f.width(d) / 2, p[1] + bh / 2 - 4, Ui.al(Hud.ac() & 0xFFFFFF, 0.5f + 0.5f * (float) Math.sin(r / 300.0)), false);
		}
		if (rough) Hud.dt(g, "rough timing", p[0] + bw - Hud.tw("rough timing") - 2, p[1] + bh - 9, Ui.al(0xFFFFFF, 0.3f));
		ps.popMatrix();
	}

	static Ln hl;

	static void ctxl(GuiGraphics g, Font f, String t, int cx, int y) {
		String s = Hud.fit(t, 260);
		g.drawString(f, s, cx - f.width(s) / 2, y, Ui.al(0xFFFFFF, 0.4f), false);
	}
}
