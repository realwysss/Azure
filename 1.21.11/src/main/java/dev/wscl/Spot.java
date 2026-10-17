package dev.wscl;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.NativeImage;

public class Spot {
	public static volatile boolean active;
	public static volatile boolean playing;
	public static volatile String song = "";
	public static volatile String art = "";
	public static volatile long pos;
	public static volatile long dur;
	public static volatile long stamp;
	public static volatile float raw = -1f;
	public static float lvl;
	public static boolean test;
	static Thread th;
	static Process pr;
	static volatile int[] pend;
	static int cn;
	public static Identifier tex;
	static DynamicTexture dt;
	static volatile long seen;

	public static boolean on() {
		return active && !song.isEmpty();
	}

	public static void tick() {
		seen = System.currentTimeMillis();
		if (th != null && th.isAlive()) return;
		if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;
		th = new Thread(Spot::ctl, "azure-spotify");
		th.setDaemon(true);
		th.start();
	}

	static boolean running() {
		return ProcessHandle.allProcesses().anyMatch(p -> p.info().command().map(c -> c.toLowerCase().endsWith("spotify.exe")).orElse(false));
	}

	static void ctl() {
		int miss = 0;
		while (true) {
			try {
				if (test) {
					Thread.sleep(2000);
					continue;
				}
				boolean idle = System.currentTimeMillis() - seen > 15000L;
				boolean sp = !idle && running();
				if (sp) {
					miss = 0;
					if (pr == null || !pr.isAlive()) start();
				} else {
					miss++;
					if ((miss > 4 || idle) && pr != null) {
						pr.destroy();
						pr = null;
						active = false;
						song = "";
						raw = -1f;
					}
				}
				Thread.sleep(idle ? 8000 : 4000);
			} catch (InterruptedException e) {
				return;
			} catch (Throwable e) {
			}
		}
	}

	static void start() {
		try {
			Path dir = FabricLoader.getInstance().getConfigDir().resolve("azure");
			Files.createDirectories(dir);
			Path ps = dir.resolve("spot.ps1");
			try (InputStream in = Spot.class.getResourceAsStream("/assets/azure/spot.ps1")) {
				Files.write(ps, in.readAllBytes());
			}
			Path cov = dir.resolve("cover.bin");
			pr = new ProcessBuilder("powershell", "-NoLogo", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden", "-ExecutionPolicy", "Bypass", "-File", ps.toString(), "-cover", cov.toString(), "-ppid", String.valueOf(ProcessHandle.current().pid())).redirectErrorStream(true).start();
			Process p = pr;
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					p.destroyForcibly();
				} catch (Throwable e) {
				}
			}));
			Thread rd = new Thread(() -> {
				try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
					String ln;
					while ((ln = r.readLine()) != null) line(ln, cov);
				} catch (Throwable e) {
				}
				active = false;
			}, "azure-spotify-rd");
			rd.setDaemon(true);
			rd.start();
		} catch (Throwable e) {
			pr = null;
		}
	}

	static void line(String ln, Path cov) {
		String[] a = ln.split("\t", -1);
		if (a.length == 0) return;
		switch (a[0]) {
			case "I":
				if (a.length >= 6) {
					playing = a[1].equals("1");
					pos = num(a[2]);
					dur = num(a[3]);
					stamp = System.currentTimeMillis();
					song = a[4];
					art = a[5];
					active = true;
				}
				break;
			case "N":
				active = false;
				song = "";
				art = "";
				playing = false;
				break;
			case "L":
				if (a.length >= 2) try {
					raw = Float.parseFloat(a[1].replace(',', '.'));
				} catch (Exception e) {
					raw = -1f;
				}
				break;
			case "C":
				try {
					BufferedImage im = ImageIO.read(cov.toFile());
					if (im == null) break;
					int n = 64;
					BufferedImage sm = new BufferedImage(n, n, BufferedImage.TYPE_INT_ARGB);
					var g2 = sm.createGraphics();
					g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					g2.drawImage(im, 0, 0, n, n, null);
					g2.dispose();
					int[] px = new int[n * n];
					sm.getRGB(0, 0, n, n, px, 0, n);
					pend = px;
				} catch (Throwable e) {
				}
				break;
			default:
		}
	}

	static long num(String s) {
		try {
			return Long.parseLong(s.trim());
		} catch (Exception e) {
			return 0;
		}
	}

	public static long now() {
		long p = pos + (playing ? System.currentTimeMillis() - stamp : 0);
		return dur > 0 ? Math.max(0, Math.min(dur, p)) : p;
	}

	public static void upd() {
		int[] px = pend;
		if (px != null) {
			pend = null;
			try {
				Minecraft m = Minecraft.getInstance();
				NativeImage ni = new NativeImage(64, 64, false);
				for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) ni.setPixel(x, y, px[y * 64 + x] | 0xFF000000);
				Identifier id = Identifier.fromNamespaceAndPath("azure", "cover" + (cn++));
				DynamicTexture d = new DynamicTexture(() -> "azure_cover", ni);
				m.getTextureManager().register(id, d);
				if (tex != null) m.getTextureManager().release(tex);
				tex = id;
				dt = d;
			} catch (Throwable e) {
			}
		}
		float tg;
		if (raw >= 0f) tg = playing ? Math.min(1f, raw * 1.5f) : 0f;
		else tg = playing ? 0.5f + 0.3f * (float) Math.sin(System.currentTimeMillis() / 160.0) : 0f;
		lvl += (tg - lvl) * (tg > lvl ? 0.55f : 0.16f);
	}

	public static String tm(long ms) {
		long s = Math.max(0, ms / 1000);
		return (s / 60) + ":" + String.format("%02d", s % 60);
	}
}
