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
import java.util.ArrayList;
import java.util.List;

public class News {
	public static class It {
		public int id;
		public String t = "";
		public String b = "";
		public String d = "";
		public String u = "";
	}

	public static volatile List<It> items = new ArrayList<>();

	public static void refresh() {
		last = 0;
	}
	static long last;
	static boolean busy;
	static boolean first = true;

	public static void tick() {
		if (busy || Cfg.gi("news", 1) == 0) return;
		long n = System.currentTimeMillis();
		if (last != 0 && n - last < 600000L) return;
		last = n;
		busy = true;
		Thread t = new Thread(() -> {
			try {
				HttpClient h = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
				String b = Api.url().isEmpty() ? h.send(HttpRequest.newBuilder(URI.create("https://raw.githubusercontent.com/realwysss/Azure/main/news.json")).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString()).body() : Api.get("/news");
				JsonArray a = JsonParser.parseString(b).getAsJsonObject().getAsJsonArray("items");
				List<It> l = new ArrayList<>();
				for (JsonElement e : a) {
					JsonObject o = e.getAsJsonObject();
					It i = new It();
					i.id = o.get("id").getAsInt();
					i.t = o.has("t") ? o.get("t").getAsString() : "";
					i.b = o.has("b") ? o.get("b").getAsString() : "";
					i.d = o.has("d") ? o.get("d").getAsString() : "";
					i.u = o.has("u") ? o.get("u").getAsString() : "";
					l.add(i);
				}
				l.sort((x, y) -> y.id - x.id);
				items = l;
				if (unseen() > 0) toast();
			} catch (Throwable e) {
			} finally {
				busy = false;
			}
		}, "azure-news");
		t.setDaemon(true);
		t.start();
	}

	public static int unseen() {
		int s = Cfg.gi("nseen", 0);
		int n = 0;
		for (It i : items) if (i.id > s) n++;
		return n;
	}

	public static void seen() {
		int m = Cfg.gi("nseen", 0);
		for (It i : items) m = Math.max(m, i.id);
		if (m != Cfg.gi("nseen", 0)) Cfg.si("nseen", m);
	}

	static void toast() {
		var m = net.minecraft.client.Minecraft.getInstance();
		int s = Cfg.gi("nseen", 0);
		It top = null;
		for (It i : items) if (i.id > s && (top == null || i.id > top.id)) top = i;
		final It tp = top;
		if (tp == null) return;
		m.execute(() -> {
			try {
				net.minecraft.client.gui.components.toasts.SystemToast.add(m.getToastManager(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId.PERIODIC_NOTIFICATION, net.minecraft.network.chat.Component.literal(tp.t), net.minecraft.network.chat.Component.literal("New message from Azure. Open the bell in the Azure menu."));
			} catch (Throwable e) {
			}
		});
	}
}
