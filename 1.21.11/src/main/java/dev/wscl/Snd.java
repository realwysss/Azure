package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class Snd {
	public static final String[] ST = {"Off", "Soft", "Crisp", "Retro", "Deep", "Glass", "Bubble", "Wood", "Chime", "Switch", "Laser", "Pixel", "Drip", "Pluck", "Synth", "Tick", "Bell", "Meow"};

	public static String[] opts() {
		return Cfg.gi("meow", 0) == 1 ? ST : java.util.Arrays.copyOf(ST, ST.length - 1);
	}

	public static void p(String ev) {
		Cfg c = Cfg.c;
		if (c.sfx <= 0 || c.sfx >= ST.length) return;
		Minecraft m = Minecraft.getInstance();
		Object sc = m.screen;
		if (!(sc instanceof Mnu || sc instanceof AzTitle || sc instanceof HudEd)) return;
		m.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("azure", "s" + c.sfx + "." + ev)), 1f, c.sfv / 100f));
	}
}
