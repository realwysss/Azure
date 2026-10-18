package dev.wscl;

import com.mojang.blaze3d.platform.InputConstants;
import dev.wysl.Thm;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Wc implements ClientModInitializer {
	public static KeyMapping key;
	static int wtk;
	public static KeyMapping zkey;
	public static float zk;
	static long zt;

	public static float zoom() {
		long n = System.nanoTime();
		float dt = zt == 0 ? 0.016f : Math.min(0.1f, (n - zt) / 1e9f);
		zt = n;
		net.minecraft.client.Minecraft m = net.minecraft.client.Minecraft.getInstance();
		boolean on = Mod.live("zoom") && zkey != null && zkey.isDown() && m.screen == null;
		float to = on ? 1f : 0f;
		if (Cfg.gi("zoom.mode", 0) == 1) zk = to;
		else zk += (to - zk) * (1f - (float) Math.exp(-dt * (on ? 11f : 8f)));
		if (Math.abs(to - zk) < 0.002f) zk = to;
		return zk * zk * (3f - 2f * zk);
	}
	static boolean rd;
	public static final KeyMapping[] mk = new KeyMapping[4];
	static boolean hs;
	static boolean sp;
	static boolean spr;
	static boolean hbl;
	static boolean wh;
	static net.minecraft.world.entity.LivingEntity tg;
	static long tgt;

	public void onInitializeClient() {
		Cfg.ld();
		Upd.backup();
		if (!Cfg.first) Cfg.c.fs = true;
		Cfg.sv();
		Mods.init();
		Thm.ld();
		if (Cfg.first) {
			Thm.set(0x8B5CF6);
			Cfg.sv();
		}
		if (Cfg.c.ver < 3) {
			Thm.set(0x8B5CF6);
			Cfg.c.ver = 3;
			Cfg.sv();
		}
		Tx.reg();
		KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("azure", "main"));
		KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.azure.open", InputConstants.KEY_RSHIFT, cat));
		Fl.reg(cat);
		dev.snprb.snprb.init(cat);
		for (int i = 0; i < 4; i++) mk[i] = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.azure.msg" + (i + 1), InputConstants.UNKNOWN.getValue(), cat));
		KeyMapping z = zkey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.azure.zoom", InputConstants.UNKNOWN.getValue(), cat));
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(m -> Sx.stop());
		Runtime.getRuntime().addShutdownHook(new Thread(Sx::stop));
		ClientTickEvents.START_CLIENT_TICK.register(m -> {
			boolean sw = Mod.live("sprint") && m.player != null;
			if (sw) m.options.keySprint.setDown(true);
			else if (spr) m.options.keySprint.setDown(false);
			spr = sw;
		});
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			if (!rd && m.getOverlay() == null) {
				rd = true;
				Snd.p("done");
			}
			if (m.getWindow() != null) Ico.set(m.getWindow().handle());
			Dog.tick();
			Sx.tick(m);
			Upd.tick();
			if (!Cfg.c.fs && !sp && m.screen instanceof AzTitle) {
				sp = true;
				m.setScreen(new AzSetup(m.screen, true));
			}
			Rpc.tick(m);
			Onl.tick();
			Sky.tick();
			Fl.tick(m);
			Tc.tick(m);
			Fps.tick(false);
			if (Mod.live("spotify")) Spot.tick();
			if (m.getWindow() != null && (wtk++ % 40) == 0) org.lwjgl.glfw.GLFW.glfwSetWindowTitle(m.getWindow().handle(), "Azure Client");
			boolean hs2 = m.screen != null;
			if (hs2 != hs && rd && m.player != null) Snd.p(hs2 ? "open" : "close");
			hs = hs2;
			while (k.consumeClick()) if (m.screen == null) m.setScreen(Gui.open(null));
			boolean hb = Mod.live("hitbox");
			if (hb != hbl && m.levelRenderer != null) {
				hbl = hb;
				m.levelRenderer.debugRenderer.refreshRendererList();
			}
			Cfg c = Cfg.c;
			for (int i = 0; i < 4; i++) while (mk[i].consumeClick()) {
				String t = new String[]{c.m1, c.m2, c.m3, c.m4}[i];
				if (Mod.live("cmsg") && m.player != null && m.screen == null && !t.isEmpty()) {
					if (t.startsWith("/")) m.player.connection.sendCommand(t.substring(1));
					else m.player.connection.sendChat(t);
				}
			}
			if (m.player != null) {
				if (m.player.hurtTime > 0 && !wh) Hud.combo = 0;
				wh = m.player.hurtTime > 0;
			}
			Crs.xk *= 0.78f;
			if (Crs.xk < 0.1f) Crs.xk = 0f;
			if (tg != null) {
				if (tg.isDeadOrDying() || tg.isRemoved()) {
					Snd.p("kill");
					if (tg instanceof net.minecraft.world.entity.player.Player && Mod.live("atext") && m.player != null) m.player.connection.sendChat(new String[]{"gg", "ez", "good fight", "nice one", "wp"}[Math.max(0, Math.min(4, Cfg.gi("atext.m", 0)))]);
					tg = null;
				} else if (System.currentTimeMillis() - tgt > 4000) tg = null;
			}
		});
		net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((p, wl, h, en, hr) -> {
			if (wl.isClientSide()) {
				Snd.p("hit");
				Crs.xk = 5f;
				Hud.reach = p.distanceTo(en);
				Hud.combo++;
				if (en instanceof net.minecraft.world.entity.LivingEntity le) {
					tg = le;
					tgt = System.currentTimeMillis();
				}
			}
			return net.minecraft.world.InteractionResult.PASS;
		});
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.register((hd, s, m) -> {
			Snd.p("join");
			Pops.clear();
		});
		HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("azure", "hud"), Hud::draw);
		ScreenEvents.AFTER_INIT.register((mc, s, w, h) -> {
			if (Cfg.c.tag && s instanceof TitleScreen) Screens.getButtons(s).add(Button.builder(Component.literal("Azure"), b -> mc.setScreen(Gui.open(s))).bounds(6, 6, 92, 20).build());
		});
	}
}
