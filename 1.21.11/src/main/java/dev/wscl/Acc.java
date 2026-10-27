package dev.wscl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

public class Acc {
	static User orig;

	public static boolean alt() {
		return orig != null;
	}

	public static String tok(String s) {
		String t = s == null ? "" : s.trim();
		if (t.regionMatches(true, 0, "bearer ", 0, 7)) t = t.substring(7).trim();
		int i = t.lastIndexOf(':');
		if (i >= 0) t = t.substring(i + 1).trim();
		return t;
	}

	public static void go(String raw, java.util.function.Consumer<String> done) {
		String t = tok(raw);
		if (t.length() < 20) {
			done.accept("That does not look like a session token.");
			return;
		}
		Thread th = new Thread(() -> {
			String r;
			try {
				HttpResponse<String> h = Api.H.send(HttpRequest.newBuilder(URI.create("https://api.minecraftservices.com/minecraft/profile")).timeout(Duration.ofSeconds(10)).header("Authorization", "Bearer " + t).GET().build(), HttpResponse.BodyHandlers.ofString());
				if (h.statusCode() / 100 != 2) {
					r = h.statusCode() == 401 ? "Token expired or wrong." : h.statusCode() == 404 ? "That account has no Minecraft profile." : "Mojang said " + h.statusCode() + ".";
				} else {
					JsonObject o = JsonParser.parseString(h.body()).getAsJsonObject();
					String id = o.get("id").getAsString();
					String nm = o.get("name").getAsString();
					UUID u = UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20));
					Minecraft.getInstance().execute(() -> {
						sw(new User(nm, u, t, Optional.empty(), Optional.empty()));
						done.accept("Now playing as " + nm + ".");
					});
					return;
				}
			} catch (Throwable e) {
				r = "Could not reach Mojang.";
			}
			String rr = r;
			Minecraft.getInstance().execute(() -> done.accept(rr));
		}, "azure-acc");
		th.setDaemon(true);
		th.start();
	}

	public static void back() {
		if (orig != null) {
			User u = orig;
			orig = null;
			sw(u);
		}
	}

	static void sw(User u) {
		Minecraft m = Minecraft.getInstance();
		if (orig == null && m.getUser() != null && !m.getUser().getProfileId().equals(u.getProfileId())) orig = m.getUser();
		dev.wscl.mixin.MnAcc a = (dev.wscl.mixin.MnAcc) m;
		a.user(u);
		try {
			var api = new com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService(java.net.Proxy.NO_PROXY).createUserApiService(u.getAccessToken());
			a.api(api);
			a.props(CompletableFuture.completedFuture(com.mojang.authlib.minecraft.UserApiService.OFFLINE_PROPERTIES));
			a.pkm(net.minecraft.client.multiplayer.ProfileKeyPairManager.create(api, u, net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir()));
			a.rc(net.minecraft.client.multiplayer.chat.report.ReportingContext.create(net.minecraft.client.multiplayer.chat.report.ReportEnvironment.local(), api));
		} catch (Throwable e) {
			Sv.lg("account swap services failed: " + e);
		}
	}
}
