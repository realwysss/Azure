package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class Api {
	static volatile String base = "";
	static long last;
	static boolean busy;
	static final HttpClient H = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

	public static String url() {
		long n = System.currentTimeMillis();
		if (!busy && (last == 0 || n - last > 600000L)) {
			busy = true;
			last = n;
			Thread t = new Thread(() -> {
				try {
					String b = H.send(HttpRequest.newBuilder(URI.create("https://raw.githubusercontent.com/realwysss/Azure/main/api.json")).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString()).body();
					String u = JsonParser.parseString(b).getAsJsonObject().get("url").getAsString();
					base = u.endsWith("/") ? u.substring(0, u.length() - 1) : u;
				} catch (Throwable e) {
				} finally {
					busy = false;
				}
			}, "azure-api");
			t.setDaemon(true);
			t.start();
		}
		return base;
	}

	public static String post(String path, String json) throws Exception {
		HttpResponse<String> r = H.send(HttpRequest.newBuilder(URI.create(base + path)).timeout(Duration.ofSeconds(12)).header("content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
		if (r.statusCode() / 100 != 2) throw new Exception("http " + r.statusCode());
		return r.body();
	}

	public static String get(String path) throws Exception {
		HttpResponse<String> r = H.send(HttpRequest.newBuilder(URI.create(base + path)).timeout(Duration.ofSeconds(12)).GET().build(), HttpResponse.BodyHandlers.ofString());
		if (r.statusCode() / 100 != 2) throw new Exception("http " + r.statusCode());
		return r.body();
	}

	public static String q(String s) {
		return new com.google.gson.JsonPrimitive(s).toString();
	}

	public static void hello() throws Exception {
		var m = net.minecraft.client.Minecraft.getInstance();
		String nm = m.getUser().getName();
		String js = "{\"id\":" + q(Cfg.c.uid) + ",\"name\":" + q(nm) + ",\"v\":" + q(Upd.ver()) + ",\"share\":" + (Cfg.gi("tagshare", 1) == 1 ? "true" : "false") + "}";
		JsonObject o = JsonParser.parseString(post("/hello", js)).getAsJsonObject();
		Onl.n = o.get("online").getAsInt();
		Set<String> s = new HashSet<>();
		JsonArray a = o.getAsJsonArray("names");
		for (var e : a) s.add(e.getAsString());
		Onl.users = s;
	}
}
