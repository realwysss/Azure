package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public class Upd {
	static final String API = "https://api.github.com/repos/realwysss/Azure/releases?per_page=10";
	public static volatile String tag = "";
	public static volatile String page = "";
	public static volatile String name = "";
	static volatile String url = "";
	static volatile String dig = "";
	public static volatile String st = "";
	public static volatile int pr = -1;
	public static volatile boolean ready;
	static long last;
	static boolean busy;
	static boolean hook;
	static Path staged;
	static Path cur;
	static String curName;

	public static String ver() {
		return FabricLoader.getInstance().getModContainer("azure").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("0");
	}

	static int[] nums(String s) {
		Matcher m = Pattern.compile("\\d+").matcher(s);
		int[] o = new int[6];
		int i = 0;
		while (m.find() && i < 6) o[i++] = Integer.parseInt(m.group());
		return o;
	}

	static boolean newer(String a, String b) {
		int[] x = nums(a);
		int[] y = nums(b);
		for (int i = 0; i < 6; i++) {
			if (x[i] != y[i]) return x[i] > y[i];
		}
		return false;
	}

	public static boolean has() {
		return !tag.isEmpty() && newer(tag, ver());
	}

	public static void tick() {
		if (Cfg.gi("upm", 1) == 0 || busy) return;
		long n = System.currentTimeMillis();
		if (n - last < 6 * 3600000L && last != 0) return;
		last = n;
		busy = true;
		Thread t = new Thread(() -> {
			try {
				HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
				String b = h.send(HttpRequest.newBuilder(URI.create(API)).timeout(Duration.ofSeconds(10)).header("Accept", "application/vnd.github+json").GET().build(), HttpResponse.BodyHandlers.ofString()).body();
				JsonArray a = JsonParser.parseString(b).getAsJsonArray();
				for (JsonElement e : a) {
					JsonObject r = e.getAsJsonObject();
					if (r.get("draft").getAsBoolean()) continue;
					String tg = r.get("tag_name").getAsString();
					String u = "";
					String d = "";
					String nm = "";
					for (JsonElement x : r.getAsJsonArray("assets")) {
						JsonObject as = x.getAsJsonObject();
						String an = as.get("name").getAsString();
						if (an.endsWith(".jar") && !an.contains("sources") && an.contains("1.21.11")) {
							u = as.get("browser_download_url").getAsString();
							d = as.has("digest") && !as.get("digest").isJsonNull() ? as.get("digest").getAsString() : "";
							nm = an;
							break;
						}
					}
					if (u.isEmpty()) continue;
					if (!tg.equals(tag)) {
						tag = tg;
						page = r.get("html_url").getAsString();
						url = u;
						dig = d;
						name = nm;
						if (newer(tg, ver()) && !tg.equals(Cfg.c.useen)) toast();
					}
					break;
				}
				if (has() && Cfg.gi("upm", 1) == 2) install();
			} catch (Throwable e) {
			} finally {
				busy = false;
			}
		}, "azure-update");
		t.setDaemon(true);
		t.start();
	}

	static void toast() {
		var m = net.minecraft.client.Minecraft.getInstance();
		m.execute(() -> {
			try {
				net.minecraft.client.gui.components.toasts.SystemToast.add(m.getToastManager(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId.PERIODIC_NOTIFICATION, net.minecraft.network.chat.Component.literal("Azure " + tag), net.minecraft.network.chat.Component.literal("A new beta is out. Open the bell in the Azure menu."));
			} catch (Throwable e) {
			}
		});
	}

	public static void seen() {
		if (!tag.isEmpty() && !tag.equals(Cfg.c.useen)) {
			Cfg.c.useen = tag;
			Cfg.sv();
		}
	}

	public static boolean unseen() {
		return has() && !tag.equals(Cfg.c.useen);
	}

	public static void install() {
		if (pr >= 0 || ready || !has() || url.isEmpty()) return;
		if (FabricLoader.getInstance().isModLoaded("azureloader")) {
			st = "Updates when you start the game";
			return;
		}
		if (dig.isEmpty() || !dig.startsWith("sha256:")) {
			st = "No checksum for this release";
			return;
		}
		Path jar = FabricLoader.getInstance().getModContainer("azure").flatMap(c -> c.getOrigin().getPaths().stream().findFirst()).orElse(null);
		if (jar == null || !Files.isRegularFile(jar)) {
			st = "Not running from a jar";
			return;
		}
		cur = jar;
		pr = 0;
		st = "Downloading";
		final String u = url;
		final String d = dig.substring(7).toLowerCase();
		final String nm = name;
		Thread t = new Thread(() -> {
			try {
				Path dir = FabricLoader.getInstance().getConfigDir().resolve("azure").resolve("update");
				Files.createDirectories(dir);
				Path f = dir.resolve(nm + ".part");
				HttpClient h = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(10)).build();
				HttpResponse<InputStream> r = h.send(HttpRequest.newBuilder(URI.create(u)).GET().build(), HttpResponse.BodyHandlers.ofInputStream());
				long len = r.headers().firstValueAsLong("content-length").orElse(-1);
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				long got = 0;
				try (InputStream in = r.body(); var out = Files.newOutputStream(f)) {
					byte[] buf = new byte[65536];
					int k;
					while ((k = in.read(buf)) > 0) {
						md.update(buf, 0, k);
						out.write(buf, 0, k);
						got += k;
						if (len > 0) pr = (int) (got * 100 / len);
					}
				}
				StringBuilder sb = new StringBuilder();
				for (byte x : md.digest()) sb.append(String.format("%02x", x));
				if (!sb.toString().equals(d)) {
					Files.deleteIfExists(f);
					pr = -1;
					st = "Checksum did not match";
					return;
				}
				Path fin = dir.resolve(nm);
				Files.move(f, fin, StandardCopyOption.REPLACE_EXISTING);
				staged = fin;
				curName = nm;
				pr = 100;
				ready = true;
				st = "Ready, installs when you close the game";
				hook();
			} catch (Throwable e) {
				pr = -1;
				st = "Download failed";
			}
		}, "azure-install");
		t.setDaemon(true);
		t.start();
	}

	static void hook() {
		if (hook) return;
		hook = true;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				if (staged == null || cur == null || !Files.isRegularFile(staged)) return;
				Path dst = cur.getParent().resolve(curName);
				String s = "$o='" + cur.toString().replace("'", "''") + "';$n='" + staged.toString().replace("'", "''") + "';$d='" + dst.toString().replace("'", "''") + "';"
						+ "for($i=0;$i -lt 90;$i++){try{Remove-Item -LiteralPath $o -Force -ErrorAction Stop;break}catch{Start-Sleep 1}};"
						+ "Move-Item -LiteralPath $n -Destination $d -Force";
				new ProcessBuilder("powershell", "-NoProfile", "-WindowStyle", "Hidden", "-Command", s).start();
			} catch (Throwable e) {
			}
		}, "azure-update-apply"));
	}

	public static void backup() {
		try {
			String v = ver();
			if (Cfg.c.lv != null && !Cfg.c.lv.isEmpty() && !Cfg.c.lv.equals(v)) {
				Path cd = FabricLoader.getInstance().getConfigDir();
				Path dir = cd.resolve("azure").resolve("backups").resolve(Cfg.c.lv + "_" + new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date()));
				Files.createDirectories(dir);
				String[] fl = {"azure.json", "hitcolor.json", "hitglow.json", "crystalglow.json", "xpglow.json", "anchorglow.json", "fireglow.json", "blurreborn.json", "ogfog.json", "oldpotions.json", "itemsopacity.json", "noeb.json", "klfx.json", "tuffbright.json", "totemparticleglow.json", "nobounce.json"};
				for (String f : fl) {
					Path p = cd.resolve(f);
					if (Files.isRegularFile(p)) Files.copy(p, dir.resolve(f), StandardCopyOption.REPLACE_EXISTING);
				}
				try (var st2 = Files.list(dir.getParent())) {
					var l = st2.sorted().toList();
					for (int i = 0; i < l.size() - 6; i++) {
						try (var w = Files.walk(l.get(i))) {
							w.sorted(java.util.Comparator.reverseOrder()).forEach(q -> q.toFile().delete());
						}
					}
				}
			}
			Cfg.c.lv = v;
		} catch (Throwable e) {
		}
	}
}
