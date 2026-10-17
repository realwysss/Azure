package dev.oldp;

import java.util.HashMap;
import net.fabricmc.api.ClientModInitializer;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public class OldP implements ClientModInitializer {
	public static KeyMapping key;

	static final HashMap<String, Integer> OLD = new HashMap<>();
	static final HashMap<MobEffect, Integer> CACHE = new HashMap<>();

	static {
		OLD.put("speed", 8171462);
		OLD.put("slowness", 5926017);
		OLD.put("haste", 14270531);
		OLD.put("mining_fatigue", 4866583);
		OLD.put("strength", 9643043);
		OLD.put("instant_health", 16262179);
		OLD.put("instant_damage", 4393481);
		OLD.put("jump_boost", 2293580);
		OLD.put("nausea", 5578058);
		OLD.put("regeneration", 13458603);
		OLD.put("resistance", 10044730);
		OLD.put("fire_resistance", 14981690);
		OLD.put("water_breathing", 3035801);
		OLD.put("invisibility", 8356754);
		OLD.put("blindness", 2039587);
		OLD.put("night_vision", 0x1f1fa1);
		OLD.put("hunger", 5797459);
		OLD.put("weakness", 0x484d48);
		OLD.put("poison", 5149489);
		OLD.put("wither", 3484199);
		OLD.put("health_boost", 16284963);
		OLD.put("absorption", 0x2552a5);
		OLD.put("saturation", 16262179);
		OLD.put("glowing", 9740385);
		OLD.put("levitation", 0xceffff);
		OLD.put("luck", 0x339900);
		OLD.put("unluck", 12624973);
		OLD.put("slow_falling", 16773073);
		OLD.put("conduit_power", 1950417);
		OLD.put("dolphins_grace", 8954814);
		OLD.put("bad_omen", 745784);
		OLD.put("hero_of_the_village", 0x44ff44);
	}

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping kk = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.oldpotions.open", InputConstants.KEY_SEMICOLON, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("oldpotions", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (kk.consumeClick()) if (m.screen == null) {
				if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(Gui.open(null));
				else if (m.player != null) m.player.displayClientMessage(Component.literal("Old Potions needs Wysss Library to open its menu"), true);
			}
		});
	}

	public static int old(MobEffect e, int now) {
		Integer v = CACHE.get(e);
		if (v == null) {
			var k = BuiltInRegistries.MOB_EFFECT.getKey(e);
			v = k == null ? null : OLD.get(k.getPath());
			if (v == null) v = -1;
			CACHE.put(e, v);
		}
		return v < 0 ? now : v;
	}
}
