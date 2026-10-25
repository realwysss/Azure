package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Rel {
	public static class R {
		public String tag = "";
		public String name = "";
		public String date = "";
		public String body = "";
		public String url = "";
		public boolean pre;
		public long t;
	}

	public static volatile List<R> list = new ArrayList<>();
	static long last;
	static boolean busy;

	public static void tick() {
		if (busy) return;
		long n = System.currentTimeMillis();
		if (last != 0 && n - last < 1800000L) return;
		last = n;
		busy = true;
		Thread t = new Thread(() -> {
			try {
				HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
				String b = h.send(HttpRequest.newBuilder(URI.create("https://api.github.com/repos/realwysss/Azure/releases?per_page=100")).timeout(Duration.ofSeconds(12)).header("Accept", "application/vnd.github+json").GET().build(), HttpResponse.BodyHandlers.ofString()).body();
				JsonArray a = JsonParser.parseString(b).getAsJsonArray();
				List<R> l = new ArrayList<>();
				for (JsonElement e : a) {
					JsonObject o = e.getAsJsonObject();
					if (o.get("draft").getAsBoolean()) continue;
					R r = new R();
					r.tag = o.get("tag_name").getAsString();
					r.name = o.has("name") && !o.get("name").isJsonNull() && !o.get("name").getAsString().isBlank() ? o.get("name").getAsString() : "Azure " + r.tag;
					r.body = o.has("body") && !o.get("body").isJsonNull() ? o.get("body").getAsString().replace("\r", "") : "";
					r.url = "https://azureclient.dev";
					r.pre = o.get("prerelease").getAsBoolean();
					String pa = o.has("published_at") && !o.get("published_at").isJsonNull() ? o.get("published_at").getAsString() : "";
					r.t = pa.isEmpty() ? 0 : Instant.parse(pa).getEpochSecond();
					r.date = pa.length() >= 10 ? pa.substring(0, 10) : "";
					l.add(r);
				}
				l.sort((x, y) -> Long.compare(y.t, x.t));
				list = l;
				if (Cfg.gi("rseen", -1) == -1 && !l.isEmpty()) Cfg.si("rseen", (int) l.get(0).t);
			} catch (Throwable e) {
			} finally {
				busy = false;
			}
		}, "azure-releases");
		t.setDaemon(true);
		t.start();
	}

	public static int unseen() {
		int s = Cfg.gi("rseen", -1);
		if (s < 0) return 0;
		int n = 0;
		for (R r : list) if (r.t > s) n++;
		return n;
	}

	public static void seen() {
		int m = Cfg.gi("rseen", 0);
		for (R r : list) m = Math.max(m, (int) r.t);
		if (m != Cfg.gi("rseen", 0)) Cfg.si("rseen", m);
	}
}
