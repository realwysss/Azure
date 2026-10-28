package dev.wscl;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;

public class Lib {
	static final String NM = "styles-lib-2.zip";
	static final String URL = "https://azureclient.dev/" + NM;
	public static volatile Map<String, List<String>> idx = Map.of();
	static ZipFile z;
	public static volatile int st;

	static Path f() {
		return Minecraft.getInstance().gameDirectory.toPath().resolve("config/azure/" + NM);
	}

	public static void ld() {
		if (st != 0) return;
		st = 1;
		Thread th = new Thread(Lib::sync, "azure-styles-lib");
		th.setDaemon(true);
		th.start();
	}

	@SuppressWarnings("unchecked")
	static synchronized void sync() {
		if (z != null) return;
		st = 1;
		try {
			Path p = f();
			if (!Files.exists(p)) {
				Files.createDirectories(p.getParent());
				HttpClient hc = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
				HttpResponse<InputStream> r = hc.send(HttpRequest.newBuilder(URI.create(URL)).build(), HttpResponse.BodyHandlers.ofInputStream());
				if (r.statusCode() != 200) throw new Exception("http " + r.statusCode());
				Path tp = p.resolveSibling(NM + ".part");
				try (InputStream in = r.body()) {
					Files.copy(in, tp, StandardCopyOption.REPLACE_EXISTING);
				}
				Files.move(tp, p, StandardCopyOption.REPLACE_EXISTING);
			}
			ZipFile zz = new ZipFile(p.toFile());
			try (InputStream in = zz.getInputStream(zz.getEntry("index.json"))) {
				idx = new com.google.gson.Gson().fromJson(new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8), Map.class);
			}
			z = zz;
			st = 2;
		} catch (Throwable e) {
			st = 3;
		}
	}

	public static List<String> of(String c) {
		return idx.getOrDefault(c, List.of());
	}

	public static byte[] get(String c, String n, String path) {
		if (z == null) sync();
		if (z == null) return null;
		try {
			ZipEntry e = z.getEntry(c + "/" + n + "/" + path);
			if (e == null) return null;
			try (InputStream in = z.getInputStream(e)) {
				return in.readAllBytes();
			}
		} catch (Throwable e) {
			return null;
		}
	}
}
