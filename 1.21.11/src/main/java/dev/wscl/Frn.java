package dev.wscl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class Frn {
	public static class F {
		public String n;
		public boolean on;
		public String sv;
		public long seen;
	}

	public static volatile List<F> fr = List.of();
	public static volatile List<String> in = List.of();
	public static volatile List<String> out = List.of();
	public static volatile java.util.Set<String> noaz = java.util.Set.of();
	static final java.util.Set<String> tried = new java.util.HashSet<>();
	public static volatile long fast;
	public static boolean dev;
	public static volatile boolean busy;
	public static volatile boolean ld;
	public static volatile String err = "";
	static volatile long last;
	static Set<String> knownIn;
	static String who = "";

	static String key() {
		return Minecraft.getInstance().getUser().getName().toLowerCase(java.util.Locale.ROOT);
	}

	public static String tok() {
		if (Cfg.c.ft == null) Cfg.c.ft = new java.util.HashMap<>();
		return Cfg.c.ft.getOrDefault(key(), "");
	}

	public static boolean premium() {
		String t = Minecraft.getInstance().getUser().getAccessToken();
		return dev || t != null && t.length() > 30;
	}

	public static boolean in() {
		return dev || !tok().isEmpty();
	}

	public static int onl() {
		int n = 0;
		for (F f : fr) if (f.on) n++;
		return n;
	}

	static String body(String extra) {
		return "{\"tok\":" + Api.q(tok()) + extra + "}";
	}

	public static void login(Runnable done) {
		if (busy) return;
		busy = true;
		err = "";
		Thread t = new Thread(() -> {
			try {
				Api.url();
				for (int i = 0; i < 40 && Api.base.isEmpty(); i++) Thread.sleep(250);
				var m = Minecraft.getInstance();
				var u = m.getUser();
				String sid = UUID.randomUUID().toString().replace("-", "");
				m.services().sessionService().joinServer(u.getProfileId(), u.getAccessToken(), sid);
				JsonObject o = JsonParser.parseString(Api.post("/f_login", "{\"name\":" + Api.q(u.getName()) + ",\"sid\":" + Api.q(sid) + "}")).getAsJsonObject();
				String tk = o.get("tok").getAsString();
				m.execute(() -> {
					Cfg.c.ft.put(key(), tk);
					Cfg.sv();
				});
				busy = false;
				last = 0;
				refresh(true);
			} catch (Throwable e) {
				err = "Could not verify this account. Try again in a moment.";
				busy = false;
			}
			if (done != null) Minecraft.getInstance().execute(done);
		}, "azure-friends-login");
		t.setDaemon(true);
		t.start();
	}

	public static void tick() {
		if (dev) return;
		if (!in()) {
			if (premium() && !busy && Cfg.c.onl && tried.add(key())) login(null);
			return;
		}
		if (!who.equals(key())) {
			who = key();
			fr = List.of();
			in = List.of();
			out = List.of();
			knownIn = null;
			last = 0;
		}
		long gap = System.currentTimeMillis() - fast < 3000L ? 5000L : 20000L;
		if (System.currentTimeMillis() - last > gap) refresh(false);
	}

	public static void refresh(boolean force) {
		long now = System.currentTimeMillis();
		if (dev || !in() || busy && !force) return;
		if (!force && now - last < 4000L) return;
		last = now;
		Thread t = new Thread(() -> {
			try {
				if (Api.base.isEmpty()) {
					Api.url();
					for (int i = 0; i < 40 && Api.base.isEmpty(); i++) Thread.sleep(250);
				}
				JsonObject o = JsonParser.parseString(Api.post("/f_list", body(""))).getAsJsonObject();
				List<F> nf = new ArrayList<>();
				for (JsonElement e : o.getAsJsonArray("friends")) {
					JsonObject x = e.getAsJsonObject();
					F f = new F();
					f.n = x.get("n").getAsString();
					f.on = x.get("on").getAsBoolean();
					f.sv = x.has("sv") ? x.get("sv").getAsString() : "";
					f.seen = x.get("seen").getAsLong();
					nf.add(f);
				}
				nf.sort((a, b) -> a.on != b.on ? (a.on ? -1 : 1) : a.n.compareToIgnoreCase(b.n));
				List<String> ni = new ArrayList<>();
				for (JsonElement e : o.getAsJsonArray("in")) ni.add(e.getAsString());
				List<String> no = new ArrayList<>();
				for (JsonElement e : o.getAsJsonArray("out")) no.add(e.getAsString());
				java.util.Set<String> na = new HashSet<>();
				if (o.has("outx")) for (JsonElement e : o.getAsJsonArray("outx")) {
					JsonObject x = e.getAsJsonObject();
					if (!x.get("az").getAsBoolean()) na.add(x.get("n").getAsString().toLowerCase(java.util.Locale.ROOT));
				}
				noaz = na;
				fr = nf;
				in = ni;
				out = no;
				ld = true;
				err = "";
				Set<String> now2 = new HashSet<>(ni);
				if (knownIn != null) {
					for (String s : ni) if (!knownIn.contains(s)) toast(s);
				} else if (!ni.isEmpty()) toastAll(ni.size());
				knownIn = now2;
			} catch (Throwable e) {
				String m = String.valueOf(e.getMessage());
				if (m.contains("401")) Minecraft.getInstance().execute(() -> {
					Cfg.c.ft.remove(key());
					Cfg.sv();
				});
				err = m.contains("401") ? "Please verify your account again." : "Friends are offline right now.";
			}
		}, "azure-friends");
		t.setDaemon(true);
		t.start();
	}

	static void toastAll(int n) {
		var m = Minecraft.getInstance();
		m.execute(() -> net.minecraft.client.gui.components.toasts.SystemToast.add(m.getToastManager(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId.PERIODIC_NOTIFICATION, net.minecraft.network.chat.Component.literal("Friend requests"), net.minecraft.network.chat.Component.literal(n == 1 ? "1 person wants to be your friend." : n + " people want to be your friend.")));
	}

	public static boolean exists(String n) {
		try {
			var r = Api.H.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create("https://api.mojang.com/users/profiles/minecraft/" + n)).timeout(java.time.Duration.ofSeconds(8)).GET().build(), java.net.http.HttpResponse.BodyHandlers.discarding());
			return r.statusCode() != 404 && r.statusCode() != 204;
		} catch (Throwable e) {
			return true;
		}
	}

	static void toast(String n) {
		var m = Minecraft.getInstance();
		m.execute(() -> net.minecraft.client.gui.components.toasts.SystemToast.add(m.getToastManager(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId.PERIODIC_NOTIFICATION, net.minecraft.network.chat.Component.literal("Friend request"), net.minecraft.network.chat.Component.literal(n + " wants to be your friend.")));
	}

	public static void act(String a, String n, Consumer<String> done) {
		Thread t = new Thread(() -> {
			String res;
			try {
				if (a.equals("f_add") && !exists(n)) throw new Exception("nouser");
				JsonObject o = JsonParser.parseString(Api.post("/" + a, body(",\"n\":" + Api.q(n)))).getAsJsonObject();
				res = o.has("st") ? o.get("st").getAsString() : "ok";
			} catch (Throwable e) {
				String m = String.valueOf(e.getMessage());
				res = m.contains("nouser") ? "nouser" : m.contains("400") ? "bad" : m.contains("401") ? "login" : m.contains("429") ? "slow" : "error";
			}
			final String r = res;
			last = 0;
			refresh(true);
			if (done != null) Minecraft.getInstance().execute(() -> done.accept(r));
		}, "azure-friends-act");
		t.setDaemon(true);
		t.start();
	}

	public static String seen(long s) {
		if (s < 0) return "Never seen on Azure";
		if (s < 3600) return "Last seen " + Math.max(1, s / 60) + " min ago";
		if (s < 86400) return "Last seen " + s / 3600 + " h ago";
		return "Last seen " + s / 86400 + " d ago";
	}
}
