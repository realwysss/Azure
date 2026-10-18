package dev.azldr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class Ldr implements PreLaunchEntrypoint {
	static final String API = "https://api.github.com/repos/realwysss/Azure/releases?per_page=100";

	public void onPreLaunch() {
		FabricLoader fl = FabricLoader.getInstance();
		if (fl.getEnvironmentType() != EnvType.CLIENT) return;
		String par = System.getProperty("azure.par");
		if (par != null) {
			try {
				ProcessHandle.of(Long.parseLong(par)).ifPresent(h -> h.onExit().thenRun(() -> Runtime.getRuntime().halt(0)));
			} catch (Throwable e) {
			}
			return;
		}
		if (fl.isDevelopmentEnvironment() || fl.isModLoaded("azure")) return;
		try {
			Path core = prep(fl);
			if (core != null) run(fl, core);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	static int[] nums(String s) {
		Matcher m = Pattern.compile("\\d+").matcher(s);
		int[] o = new int[8];
		int i = 0;
		while (m.find() && i < 8) o[i++] = Integer.parseInt(m.group());
		return o;
	}

	static int cmp(String a, String b) {
		int[] x = nums(a);
		int[] y = nums(b);
		for (int i = 0; i < 8; i++) if (x[i] != y[i]) return Integer.compare(x[i], y[i]);
		return 0;
	}

	static boolean off(FabricLoader fl) {
		try {
			Path p = fl.getConfigDir().resolve("azure.json");
			if (!Files.isRegularFile(p)) return false;
			JsonObject o = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
			JsonObject v = o.has("v") ? o.getAsJsonObject("v") : null;
			return v != null && v.has("upm") && v.get("upm").getAsInt() == 0;
		} catch (Throwable e) {
			return false;
		}
	}

	static List<Path> cached(Path dir, String pre) throws Exception {
		List<Path> l = new ArrayList<>();
		if (!Files.isDirectory(dir)) return l;
		try (var s = Files.list(dir)) {
			s.filter(p -> p.getFileName().toString().startsWith(pre) && p.getFileName().toString().endsWith(".jar")).forEach(l::add);
		}
		l.sort((a, b) -> cmp(b.getFileName().toString().substring(pre.length()), a.getFileName().toString().substring(pre.length())));
		return l;
	}

	static String sha(Path p) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		try (InputStream in = Files.newInputStream(p)) {
			byte[] b = new byte[65536];
			int k;
			while ((k = in.read(b)) > 0) md.update(b, 0, k);
		}
		StringBuilder sb = new StringBuilder();
		for (byte x : md.digest()) sb.append(String.format("%02x", x));
		return sb.toString();
	}

	static Path prep(FabricLoader fl) throws Exception {
		String mc = fl.getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();
		String pre = "azure-mc" + mc + "-";
		Path dir = fl.getConfigDir().resolve("azure").resolve("core");
		List<Path> have = cached(dir, pre);
		Path best = have.isEmpty() ? null : have.get(0);
		if (off(fl)) return best;
		try {
			HttpClient h = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(5)).build();
			String b = h.send(HttpRequest.newBuilder(URI.create(API)).timeout(Duration.ofSeconds(8)).header("Accept", "application/vnd.github+json").GET().build(), HttpResponse.BodyHandlers.ofString()).body();
			JsonObject top = null;
			String tn = null;
			String td = null;
			for (JsonElement e : JsonParser.parseString(b).getAsJsonArray()) {
				JsonObject r = e.getAsJsonObject();
				if (r.get("draft").getAsBoolean()) continue;
				for (JsonElement x : r.getAsJsonArray("assets")) {
					JsonObject a = x.getAsJsonObject();
					String n = a.get("name").getAsString();
					if (!n.startsWith(pre) || !n.endsWith(".jar") || !a.has("digest") || a.get("digest").isJsonNull()) continue;
					String d = a.get("digest").getAsString();
					if (!d.startsWith("sha256:")) continue;
					if (tn == null || cmp(n.substring(pre.length()), tn.substring(pre.length())) > 0) {
						top = a;
						tn = n;
						td = d.substring(7).toLowerCase();
					}
				}
			}
			if (top != null) {
				Path f = dir.resolve(tn);
				if (Files.isRegularFile(f) && sha(f).equals(td)) return trim(dir, pre, f);
				Files.createDirectories(dir);
				Path t = dir.resolve(tn + ".part");
				HttpResponse<InputStream> rs = h.send(HttpRequest.newBuilder(URI.create(top.get("browser_download_url").getAsString())).GET().build(), HttpResponse.BodyHandlers.ofInputStream());
				try (InputStream in = rs.body()) {
					Files.copy(in, t, StandardCopyOption.REPLACE_EXISTING);
				}
				if (!sha(t).equals(td)) {
					Files.deleteIfExists(t);
					return best;
				}
				Files.move(t, f, StandardCopyOption.REPLACE_EXISTING);
				return trim(dir, pre, f);
			}
		} catch (Throwable e) {
		}
		return best;
	}

	static Path trim(Path dir, String pre, Path keep) throws Exception {
		int k = 0;
		for (Path p : cached(dir, pre)) {
			if (p.equals(keep)) continue;
			if (++k > 1) {
				try {
					Files.deleteIfExists(p);
				} catch (Throwable e) {
				}
			}
		}
		return keep;
	}

	static final String[] FW = {"modrinth.", "minecraft.", "org.lwjgl.", "io.netty.", "jna."};

	static void run(FabricLoader fl, Path core) throws Exception {
		List<String> c = new ArrayList<>();
		c.add(ProcessHandle.current().info().command().orElse(Path.of(System.getProperty("java.home"), "bin", "java").toString()));
		String am = null;
		Set<String> seen = new HashSet<>();
		for (String a : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			if (a.startsWith("-D")) {
				int i = a.indexOf('=');
				seen.add(a.substring(2, i < 0 ? a.length() : i));
			}
			if (a.startsWith("-Dfabric.addMods=")) am = a.substring(17);
			else if (!a.startsWith("-Dmodrinth.internal.ipc")) c.add(a);
		}
		for (String k : System.getProperties().stringPropertyNames()) {
			if (seen.contains(k) || k.startsWith("modrinth.internal.ipc") || k.equals("modrinth.process.args")) continue;
			for (String f : FW) {
				if (k.startsWith(f)) {
					c.add("-D" + k + "=" + System.getProperty(k));
					break;
				}
			}
		}
		c.add("-Dfabric.addMods=" + (am == null || am.isEmpty() ? "" : am + File.pathSeparator) + core.toAbsolutePath());
		c.add("-Dazure.par=" + ProcessHandle.current().pid());
		c.add("-cp");
		c.add(System.getProperty("java.class.path"));
		c.add("net.fabricmc.loader.impl.launch.knot.KnotClient");
		Collections.addAll(c, fl.getLaunchArguments(false));
		Process p = new ProcessBuilder(c).inheritIO().start();
		Runtime.getRuntime().addShutdownHook(new Thread(p::destroy));
		int rc = p.waitFor();
		Runtime.getRuntime().halt(rc);
	}
}
