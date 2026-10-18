package dev.wscl;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class Fl {
	public static KeyMapping key;
	public static boolean on;
	public static float yaw;
	public static float pitch;
	static CameraType prev;
	static boolean was;

	public static void reg(KeyMapping.Category cat) {
		key = net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(new KeyMapping("key.azure.freelook", InputConstants.UNKNOWN.getValue(), cat));
	}

	static void set(Minecraft m, boolean v) {
		if (v == on || m.player == null) return;
		on = v;
		if (v) {
			yaw = m.player.getYRot();
			pitch = m.player.getXRot();
			if (Cfg.gi("freelook.tp", 1) == 1) {
				prev = m.options.getCameraType();
				m.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
		} else if (prev != null) {
			m.options.setCameraType(prev);
			prev = null;
		}
	}

	public static void tick(Minecraft m) {
		if (!Mod.live("freelook")) {
			if (on) set(m, false);
			return;
		}
		if (m.screen != null) return;
		boolean hold = Cfg.gi("freelook.mode", 0) == 1;
		if (hold) set(m, key.isDown());
		else while (key.consumeClick()) set(m, !on);
	}
}
