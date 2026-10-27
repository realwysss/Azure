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
	public static volatile String dcl = "";
	static long last;
	static long spl;
	static volatile boolean spb;
	static boolean busy;
	public static class Sp {
		public String nm;
		public String ip;
		public String ic;
		public int col;

		Sp(String nm, String ip, int col, String ic) {
			this.nm = nm;
			this.ip = ip;
			this.col = col;
			this.ic = ic;
		}
	}

	public static volatile java.util.List<Sp> sps = java.util.List.of(new Sp("ShardPvP", "shardpvp.xyz", 0xF5C542, ""));
	public static final HttpClient H = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

	public static void spr() {
		long n = System.currentTimeMillis();
		if (base.isEmpty() || spb || n - spl < 20000L) return;
		spb = true;
		spl = n;
		Thread t = new Thread(() -> {
			try {
				JsonObject so = JsonParser.parseString(get("/sponsors")).getAsJsonObject();
				if (so.has("set") && so.get("set").getAsBoolean()) sps = psp(so.getAsJsonArray("items"));
			} catch (Throwable e) {
			} finally {
				spb = false;
			}
		}, "azure-sponsors");
		t.setDaemon(true);
		t.start();
	}

	public static String url() {
		long n = System.currentTimeMillis();
		if (!busy && (last == 0 || n - last > 600000L)) {
			busy = true;
			last = n;
			Thread t = new Thread(() -> {
				try {
					String b = H.send(HttpRequest.newBuilder(URI.create("https://raw.githubusercontent.com/realwysss/Azure/main/api.json")).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString()).body();
					JsonObject jo = JsonParser.parseString(b).getAsJsonObject();
					String u = jo.get("url").getAsString();
					if (jo.has("dc")) dcl = jo.get("dc").getAsString();
					if (jo.has("sponsors")) {
						java.util.List<Sp> nl = psp(jo.getAsJsonArray("sponsors"));
						if (!nl.isEmpty()) sps = nl;
					}
					base = u.endsWith("/") ? u.substring(0, u.length() - 1) : u;
					try {
						JsonObject so = JsonParser.parseString(get("/sponsors")).getAsJsonObject();
						if (so.has("set") && so.get("set").getAsBoolean()) sps = psp(so.getAsJsonArray("items"));
					} catch (Throwable x) {
					}
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

	static java.util.List<Sp> psp(com.google.gson.JsonArray ar) {
		java.util.List<Sp> nl = new java.util.ArrayList<>();
		for (var e : ar) {
			JsonObject o = e.getAsJsonObject();
			int cl = 0xF5C542;
			try {
				cl = Integer.parseInt(o.get("color").getAsString().replace("#", ""), 16) & 0xFFFFFF;
			} catch (Throwable x) {
			}
			String nm = o.get("name").getAsString().replace("&amp;", "&").replace("&#039;", "'").replace("&quot;", "\"");
			nl.add(new Sp(nm, o.get("ip").getAsString(), cl, o.has("icon") ? o.get("icon").getAsString() : ""));
		}
		return nl;
	}

	public static final String DC = "https://discord.gg/JhRYhbCC9s";

	public static void dc() {
		url();
		var m = net.minecraft.client.Minecraft.getInstance();
		m.setScreen(new AzDc(m.screen));
	}

	public static String post(String path, String json) throws Exception {
		HttpResponse<String> r = H.send(HttpRequest.newBuilder(URI.create(base + "?a=" + path.substring(1))).timeout(Duration.ofSeconds(12)).header("content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
		if (r.statusCode() / 100 != 2) throw new Exception("http " + r.statusCode());
		return r.body();
	}

	public static String get(String path) throws Exception {
		HttpResponse<String> r = H.send(HttpRequest.newBuilder(URI.create(base + "?a=" + path.substring(1))).timeout(Duration.ofSeconds(12)).GET().build(), HttpResponse.BodyHandlers.ofString());
		if (r.statusCode() / 100 != 2) throw new Exception("http " + r.statusCode());
		return r.body();
	}

	public static String q(String s) {
		return new com.google.gson.JsonPrimitive(s).toString();
	}

	static String ukey(net.minecraft.client.Minecraft m) {
		try {
			var d = java.security.MessageDigest.getInstance("SHA-256").digest(("azure-u1:" + m.getUser().getProfileId()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < 16; i++) b.append(String.format("%02x", d[i]));
			return b.toString();
		} catch (Throwable e) {
			return "";
		}
	}

	public static void hello() throws Exception {
		var m = net.minecraft.client.Minecraft.getInstance();
		String nm = m.getUser().getName();
		String js = "{\"id\":" + q(Acc.alt() ? Cfg.c.uid + "-" + ukey(m).substring(0, 8) : Cfg.c.uid) + ",\"u\":" + q(ukey(m)) + ",\"name\":" + q(nm) + ",\"v\":" + q(Upd.ver()) + ",\"share\":" + (Cfg.gi("tagshare", 1) == 1 ? "true" : "false") + "}";
		JsonObject o = JsonParser.parseString(post("/hello", js)).getAsJsonObject();
		Onl.n = o.get("online").getAsInt();
		Set<String> s = new HashSet<>();
		JsonArray a = o.getAsJsonArray("names");
		for (var e : a) s.add(e.getAsString());
		Onl.users = s;
	}
}
