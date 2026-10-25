package dev.wscl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class Rv {
	public static class R {
		public String n = "";
		public String t = "";
		public String d = "";
		public int r;
	}

	public static volatile List<R> list = List.of();
	public static volatile double avg;
	public static volatile int cnt;
	public static volatile boolean ok;
	static long last;
	static volatile boolean busy;
	static final Map<String, Identifier> HD = new ConcurrentHashMap<>();
	static final Set<String> RQ = ConcurrentHashMap.newKeySet();

	static String dec(String s) {
		return s.replace("&#039;", "'").replace("&#39;", "'").replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
	}

	public static void refresh() {
		long n = System.currentTimeMillis();
		if (busy || n - last < 30000L) return;
		busy = true;
		last = n;
		Api.url();
		Thread t = new Thread(() -> {
			try {
				if (Api.base.isEmpty()) {
					last = 0;
					return;
				}
				JsonObject o = JsonParser.parseString(Api.get("/reviews")).getAsJsonObject();
				List<R> l = new ArrayList<>();
				for (JsonElement e : o.getAsJsonArray("items")) {
					JsonObject x = e.getAsJsonObject();
					R r = new R();
					r.n = dec(x.get("n").getAsString());
					r.t = dec(x.get("t").getAsString());
					r.d = x.get("d").getAsString();
					r.r = x.get("r").getAsInt();
					l.add(r);
				}
				avg = o.get("avg").getAsDouble();
				cnt = o.get("n").getAsInt();
				list = l;
				ok = true;
			} catch (Throwable e) {
			} finally {
				busy = false;
			}
		}, "azure-reviews");
		t.setDaemon(true);
		t.start();
	}

	public static Identifier head(String name) {
		String k = name.toLowerCase();
		Identifier id = HD.get(k);
		if (id != null) return id;
		if (RQ.add(k)) {
			Thread t = new Thread(() -> {
				try {
					HttpResponse<byte[]> r = Api.H.send(HttpRequest.newBuilder(URI.create("https://minotar.net/helm/" + java.net.URLEncoder.encode(name, "UTF-8") + "/64.png")).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
					if (r.statusCode() / 100 != 2) return;
					NativeImage ni = NativeImage.read(new java.io.ByteArrayInputStream(r.body()));
					Minecraft m = Minecraft.getInstance();
					Identifier nid = Identifier.fromNamespaceAndPath("azure", "rvh_" + k.replaceAll("[^a-z0-9_]", "_"));
					m.execute(() -> {
						m.getTextureManager().register(nid, new DynamicTexture(() -> "azure_rvh", ni));
						HD.put(k, nid);
					});
				} catch (Throwable e) {
				}
			}, "azure-rvhead");
			t.setDaemon(true);
			t.start();
		}
		return null;
	}
}
