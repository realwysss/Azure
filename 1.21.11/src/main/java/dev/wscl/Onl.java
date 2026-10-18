package dev.wscl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Onl {
	public static volatile int n = -1;
	public static volatile Set<String> users = new HashSet<>();
	static Thread th;
	static final String U = "https://ntfy.sh/azure-client-online-k7q2m9x4";

	public static void tick() {
		if (!Cfg.c.onl) {
			n = -1;
			return;
		}
		if (th != null && th.isAlive()) return;
		if (Cfg.c.uid == null || Cfg.c.uid.isEmpty()) Cfg.c.uid = UUID.randomUUID().toString();
		th = new Thread(() -> {
			HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
			long last = 0;
			while (Cfg.c.onl) {
				try {
					if (System.currentTimeMillis() - last > 300000L) {
						h.send(HttpRequest.newBuilder(URI.create(U)).timeout(Duration.ofSeconds(8)).POST(HttpRequest.BodyPublishers.ofString("azure:" + Cfg.c.uid + (Cfg.gi("tagshare", 1) == 1 ? "|" + net.minecraft.client.Minecraft.getInstance().getUser().getName() : ""))).build(), HttpResponse.BodyHandlers.discarding());
						last = System.currentTimeMillis();
					}
					String b = h.send(HttpRequest.newBuilder(URI.create(U + "/json?poll=1&since=8m")).timeout(Duration.ofSeconds(8)).GET().build(), HttpResponse.BodyHandlers.ofString()).body();
					Set<String> s = new HashSet<>();
					Set<String> nm = new HashSet<>();
					for (String ln : b.split("\n")) {
						int i = ln.indexOf("\"message\":\"azure:");
						if (i < 0) continue;
						s.add(ln.substring(i + 18, Math.min(ln.length(), i + 54)));
						int j = i + 54;
						if (j < ln.length() && ln.charAt(j) == '|') {
							int e = ln.indexOf('"', j);
							if (e > j) nm.add(ln.substring(j + 1, e).toLowerCase());
						}
					}
					if (!s.isEmpty()) n = s.size();
					users = nm;
				} catch (Exception e) {
				}
				try {
					Thread.sleep(90000);
				} catch (Exception e) {
					return;
				}
			}
		}, "azure-online");
		th.setDaemon(true);
		th.start();
	}
}
