package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Lyr {
	static class Ln {
		String t;
		double x, y, z;
		float yaw;
		long st, en;
		long dead;
		double up, lift;
		boolean ttl;
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

	static int gi(String k, int d) {
		return Cfg.gi("lyrics." + k, d);
	}

	static long now() {
		return Spot.now() + gi("off", 0);
	}

	public static void tick() {
		Spot.tick();
		String k = Spot.on() ? Spot.song + "\u0001" + Spot.art : "";
		if (!k.equals(key)) {
			key = k;
			ts = new long[0];
			tx = new String[0];
			li = -1;
			stt = k.isEmpty() ? 0 : 1;
			if (!k.isEmpty()) {
				String sn = Spot.song, ar = Spot.art;
				long du = Spot.dur;
				Thread t = new Thread(() -> load(k, sn, ar, du), "azure-lyrics");
				t.setDaemon(true);
				t.start();
			}
		}
		Minecraft m = Minecraft.getInstance();
		if (m.player == null || stt != 2) return;
		long n = now();
		long[] t0 = ts;
		int i = -1;
		for (int j = 0; j < t0.length; j++) if (t0[j] <= n) i = j;
		if (gi("tt", 1) == 1 && !ttlk.equals(key) && i < 0 && n < 15000) {
			ttlk = key;
			spawn(m, Spot.song + (Spot.art.isEmpty() ? "" : "  -  " + Spot.art), n, n + 2600, true);
		}
		if (i != li) {
			li = i;
			if (i >= 0 && !tx[i].isBlank()) spawn(m, tx[i], t0[i], i + 1 < t0.length ? t0[i + 1] : t0[i] + 4000, false);
			else for (Ln l : air) if (l.dead == 0) l.dead = System.currentTimeMillis();
		}
	}

	static void spawn(Minecraft m, String t, long st, long en, boolean ttl) {
		long r = System.currentTimeMillis();
		for (Ln l : air) {
			if (l.dead == 0) l.dead = r;
			l.lift += 0.42 * gi("sz", 100) / 100.0;
		}
		Ln l = new Ln();
		l.t = t;
		l.st = st;
		l.en = Math.max(st + 600, en);
		l.ttl = ttl;
		Vec3 e = m.player.getEyePosition();
		float yaw = m.player.getYRot();
		double ds = gi("ds", 6);
		double rd = Math.toRadians(yaw);
		l.x = e.x - Math.sin(rd) * ds;
		l.z = e.z + Math.cos(rd) * ds;
		l.y = e.y + gi("ht", 1) * 0.5;
		l.yaw = yaw;
		air.add(l);
		int kp = gi("kp", 3);
		int alive = 0;
		for (int j = air.size() - 1; j >= 0; j--) {
			Ln o = air.get(j);
			if (++alive > kp && o.dead > 0 && r - o.dead < gi("fo", 4) * 1000L - 300) o.dead = r - gi("fo", 4) * 1000L + 300;
		}
	}

	static void load(String k, String sn, String ar, long du) {
		try {
			Path d = FabricLoader.getInstance().getConfigDir().resolve("azure/lyrics");
			Files.createDirectories(d);
			Path f = d.resolve(Integer.toHexString(k.toLowerCase().hashCode()) + ".lrc");
			String body;
			if (Files.exists(f)) body = Files.readString(f);
			else {
				body = fetch(sn, ar, du);
				Files.writeString(f, body == null ? "#none" : body);
			}
			if (!k.equals(key)) return;
			if (body == null || body.startsWith("#none")) {
				stt = 3;
				return;
			}
			parse(body, du);
			stt = ts.length == 0 ? 3 : 2;
		} catch (Throwable e) {
			if (k.equals(key)) stt = 4;
		}
	}

	static String q(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	static JsonElement get(HttpClient hc, String u) throws Exception {
		HttpResponse<String> r = hc.send(HttpRequest.newBuilder(URI.create(u)).header("User-Agent", "Azure Client (azureclient.dev)").timeout(java.time.Duration.ofSeconds(12)).build(), HttpResponse.BodyHandlers.ofString());
		if (r.statusCode() != 200) return null;
		return JsonParser.parseString(r.body());
	}

	static String pick(JsonObject o) {
		if (o == null) return null;
		if (o.has("syncedLyrics") && !o.get("syncedLyrics").isJsonNull()) return o.get("syncedLyrics").getAsString();
		if (o.has("plainLyrics") && !o.get("plainLyrics").isJsonNull()) return "#plain\n" + o.get("plainLyrics").getAsString();
		return null;
	}

	static String fetch(String sn, String ar, long du) throws Exception {
		HttpClient hc = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
		String a1 = ar.split(",")[0].trim();
		JsonElement e = get(hc, "https://lrclib.net/api/get?track_name=" + q(sn) + "&artist_name=" + q(a1) + (du > 0 ? "&duration=" + du / 1000 : ""));
		String s = e != null && e.isJsonObject() ? pick(e.getAsJsonObject()) : null;
		if (s != null && !s.startsWith("#plain")) return s;
		JsonElement r = get(hc, "https://lrclib.net/api/search?track_name=" + q(sn) + "&artist_name=" + q(a1));
		if (r == null || !r.isJsonArray() || r.getAsJsonArray().isEmpty()) r = get(hc, "https://lrclib.net/api/search?q=" + q(sn + " " + a1));
		if (r != null && r.isJsonArray()) {
			JsonArray ja = r.getAsJsonArray();
			String pl = null;
			for (JsonElement x : ja) {
				String p = pick(x.getAsJsonObject());
				if (p == null) continue;
				if (!p.startsWith("#plain")) return p;
				if (pl == null) pl = p;
			}
			if (s == null) s = pl;
		}
		return s;
	}

	static void parse(String b, long du) {
		List<Long> t = new ArrayList<>();
		List<String> x = new ArrayList<>();
		if (b.startsWith("#plain")) {
			String[] ls = b.substring(6).trim().split("\n");
			List<String> ok = new ArrayList<>();
			for (String l : ls) if (!l.isBlank()) ok.add(l.trim());
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

	static int base(int i, int n) {
		int cm = gi("cm", 0);
		if (cm == 1) return gi("tc", 0xFFFFFF) & 0xFFFFFF;
		if (cm == 2) return java.awt.Color.HSBtoRGB((System.currentTimeMillis() / 3000f + i * 0.045f) % 1f, 0.65f, 1f) & 0xFFFFFF;
		return Hud.ac() & 0xFFFFFF;
	}

	static float ob(float p) {
		float c = 1.70158f;
		float q = p - 1;
		return 1 + (c + 1) * q * q * q + c * q * q;
	}

	interface Dr {
		void ch(String c, int i, float x, float y, float s, int col);
	}

	static void lay(Ln l, long n, long r, Font f, Dr d) {
		String t = l.t;
		int len = t.length();
		int an = l.ttl ? 2 : gi("an", 0);
		long span = (long) (Math.min(l.en - l.st, 7000) * 0.82);
		float la = 1f;
		if (an == 2) la = Math.min(1f, Math.max(0f, (n - l.st) / 400f));
		boolean hl = gi("hl", 1) == 1;
		int hc = gi("hc", 0xFFFFFF) & 0xFFFFFF;
		float w = f.width(t);
		float x = -w / 2f;
		for (int i = 0; i < len; i++) {
			String c = String.valueOf(t.charAt(i));
			float cw = f.width(c);
			long rt = l.st + span * i / Math.max(1, len);
			float p = l.dead > 0 ? 1f : Math.max(0f, Math.min(1f, (n - rt) / 260f));
			float a = 1f, s = 1f, yo = 0f;
			switch (an) {
				case 0:
					a = p;
					s = 0.2f + 0.8f * ob(p);
					break;
				case 1:
					a = p > 0 ? 1f : 0f;
					break;
				case 2:
					a = la;
					break;
				case 3:
					a = an == 3 && l.dead > 0 ? 1f : p;
					yo = (float) Math.sin(r / 260.0 + i * 0.45) * 1.6f;
					break;
				case 4:
					a = p;
					yo = -(1 - p) * (1 - p) * 14f;
					break;
			}
			int b = base(i, len);
			int col = b;
			if (hl && l.dead == 0) {
				if (an == 2 || an == 3) col = n >= rt ? hc : Vis.mix(b, 0x000000, 0.35f);
				else col = Vis.mix(hc, b, Math.max(0f, Math.min(1f, (n - rt - 260) / 700f)));
			}
			if (l.ttl) col = hc;
			if (a > 0.02f && !c.isBlank()) d.ch(c, i, x + cw / 2f, yo, s, Math.round(a * 255) << 24 | col);
			x += cw;
		}
	}

	public static void draw(MultiBufferSource.BufferSource bs, PoseStack ps, Vec3 cam) {
		int wh = gi("wh", 0);
		if (wh == 1 || air.isEmpty()) return;
		Minecraft m = Minecraft.getInstance();
		Font f = m.font;
		long r = System.currentTimeMillis();
		float dt = lt == 0 ? 0.016f : Math.min(0.1f, (r - lt) / 1000f);
		lt = r;
		long n = now();
		long fo = gi("fo", 4) * 1000L;
		air.removeIf(l -> l.dead > 0 && r - l.dead > fo);
		float sz = gi("sz", 100) / 100f * 0.03f;
		boolean face = gi("fc", 0) == 1;
		boolean sw = gi("sw", 0) == 1;
		boolean sh = gi("sh", 1) == 1;
		boolean ol = gi("ol", 0) == 1;
		Font.DisplayMode dm = sw ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
		Quaternionf cr = m.gameRenderer.getMainCamera().rotation();
		for (Ln l : air) {
			l.up += (l.lift - l.up) * Math.min(1f, dt * 8f);
			if (gi("fu", 1) == 1 && l.dead > 0) l.lift += dt * 0.18;
			float fa = l.dead > 0 ? 1f - (r - l.dead) / (float) fo : 1f;
			if (fa <= 0.02f) continue;
			Matrix4f mt = new Matrix4f(ps.last().pose()).translate((float) (l.x - cam.x), (float) (l.y + l.up - cam.y), (float) (l.z - cam.z));
			if (face) mt.rotate(cr);
			else mt.rotate(new Quaternionf().rotationY((float) (Math.PI - Math.toRadians(l.yaw))));
			float s2 = l.ttl ? sz * 0.75f : sz;
			mt.scale(s2, -s2, s2);
			lay(l, n, r, f, (c, i, x, y, s, col) -> {
				int a2 = Math.round(((col >>> 24) & 255) * fa);
				if (a2 < 5) return;
				int cc = a2 << 24 | col & 0xFFFFFF;
				Matrix4f cm = new Matrix4f(mt).translate(x, y + 4, 0).scale(s, s, s);
				float cx = -f.width(c) / 2f;
				if (ol) f.drawInBatch8xOutline(FormattedCharSequence.forward(c, Style.EMPTY), cx, -4, cc, a2 << 24, cm, bs, 0xF000F0);
				else f.drawInBatch(c, cx, -4, cc, sh, cm, bs, dm, 0, 0xF000F0);
			});
		}
		bs.endBatch();
	}

	public static void hud(GuiGraphics g, Minecraft m, int w, int h) {
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
		long r = System.currentTimeMillis();
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
			if (i > 0 && !x0[i - 1].isBlank()) ctxl(g, f, x0[i - 1], cx, p[1] + 2);
			if (i + 1 < x0.length && !x0[i + 1].isBlank()) ctxl(g, f, x0[i + 1], cx, p[1] + bh - 10);
		}
		if (i >= 0 && !x0[i].isBlank()) {
			Ln l = new Ln();
			l.t = x0[i];
			l.st = t0[i];
			l.en = i + 1 < t0.length ? t0[i + 1] : t0[i] + 4000;
			int y = p[1] + bh / 2;
			lay(l, n, r, f, (c, j, xx, yy, sc, col) -> {
				ps.pushMatrix();
				ps.translate(cx + xx, y + yy);
				ps.scale(sc * 1.25f, sc * 1.25f);
				g.drawString(f, c, Math.round(-f.width(c) / 2f), -4, col, gi("sh", 1) == 1);
				ps.popMatrix();
			});
		} else {
			String d = "♪";
			g.drawString(f, d, cx - f.width(d) / 2, p[1] + bh / 2 - 4, Ui.al(Hud.ac() & 0xFFFFFF, 0.5f + 0.5f * (float) Math.sin(r / 300.0)), false);
		}
		if (rough) Hud.dt(g, "rough timing", p[0] + bw - Hud.tw("rough timing") - 2, p[1] + bh - 9, Ui.al(0xFFFFFF, 0.3f));
		ps.popMatrix();
	}

	static void ctxl(GuiGraphics g, Font f, String t, int cx, int y) {
		String s = Hud.fit(t, 260);
		g.drawString(f, s, cx - f.width(s) / 2, y, Ui.al(0xFFFFFF, 0.4f), false);
	}
}
