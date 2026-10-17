package dev.wscl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

public class Onl {
	public static volatile int n = -1;
	static Thread th;

	public static void tick() {
		if (th != null && th.isAlive()) return;
		String api = Cfg.c.api == null ? "" : Cfg.c.api.trim();
		if (api.isEmpty()) return;
		if (Cfg.c.uid == null || Cfg.c.uid.isEmpty()) Cfg.c.uid = UUID.randomUUID().toString();
		th = new Thread(() -> {
			HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
			while (true) {
				try {
					String a = Cfg.c.api.trim();
					HttpRequest r = HttpRequest.newBuilder(URI.create(a + "?id=" + Cfg.c.uid)).timeout(Duration.ofSeconds(8)).POST(HttpRequest.BodyPublishers.noBody()).build();
					String b = h.send(r, HttpResponse.BodyHandlers.ofString()).body();
					int i = b.indexOf("\"n\":");
					if (i >= 0) n = Integer.parseInt(b.substring(i + 4).replaceAll("[^0-9].*", "").trim());
				} catch (Exception e) {
				}
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					return;
				}
			}
		}, "azure-online");
		th.setDaemon(true);
		th.start();
	}
}
