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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;

public class SkyL {
	static final String U = "https://azureclient.dev/skies/";
	public static volatile List<String> idx = List.of();
	static ZipFile th;
	public static volatile int st;

	static Path dir() {
		return Minecraft.getInstance().gameDirectory.toPath().resolve("config/azure/skies");
	}

	static Path dl(String nm) throws Exception {
		Path p = dir().resolve(nm);
		if (Files.exists(p)) return p;
		Files.createDirectories(p.getParent());
		HttpClient hc = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
		HttpResponse<InputStream> r = hc.send(HttpRequest.newBuilder(URI.create(U + nm)).build(), HttpResponse.BodyHandlers.ofInputStream());
		if (r.statusCode() != 200) throw new Exception("http " + r.statusCode());
		Path tp = p.resolveSibling(nm + ".part");
		try (InputStream in = r.body()) {
			Files.copy(in, tp, StandardCopyOption.REPLACE_EXISTING);
		}
		Files.move(tp, p, StandardCopyOption.REPLACE_EXISTING);
		return p;
	}

	public static void ld() {
		if (st != 0) return;
		st = 1;
		Thread t = new Thread(SkyL::sync, "azure-skies");
		t.setDaemon(true);
		t.start();
	}

	@SuppressWarnings("unchecked")
	static synchronized void sync() {
		if (th != null) return;
		try {
			ZipFile z = new ZipFile(dl("thumbs.zip").toFile());
			try (InputStream in = z.getInputStream(z.getEntry("index.json"))) {
				idx = new com.google.gson.Gson().fromJson(new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8), List.class);
			}
			th = z;
			st = 2;
		} catch (Throwable e) {
			st = 3;
		}
	}

	public static String nm(int i) {
		return i >= 0 && i < idx.size() ? idx.get(i) : "Sky " + i;
	}

	public static byte[] thumb(int i) {
		if (th == null) sync();
		if (th == null) return null;
		try {
			ZipEntry e = th.getEntry(i + ".png");
			if (e == null) return null;
			try (InputStream in = th.getInputStream(e)) {
				return in.readAllBytes();
			}
		} catch (Throwable e) {
			return null;
		}
	}

	public static Path sky(int i) {
		try {
			return dl("sky-" + i + ".zip");
		} catch (Throwable e) {
			return null;
		}
	}
}
