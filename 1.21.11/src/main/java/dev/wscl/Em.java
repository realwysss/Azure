package dev.wscl;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class Em {
	static final String[] N = {"heart", "skull", "fire", "star", "crown", "check", "x", "smile", "sad", "cool", "bolt", "gem", "sword", "shield"};
	static final Style F = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("azure", "emoji")));

	public static String[] names() {
		return N;
	}

	static int idx(String s) {
		for (int i = 0; i < N.length; i++) if (N[i].equals(s)) return i;
		return -1;
	}

	public static Component cv(Component c) {
		if (!Mod.live("emoji") || c.getString().indexOf(':') < 0) return c;
		MutableComponent o = Component.empty();
		boolean[] hit = {false};
		c.visit((st, t) -> {
			int p = 0;
			while (p < t.length()) {
				int a = t.indexOf(':', p);
				int b = a < 0 ? -1 : t.indexOf(':', a + 1);
				int i = b < 0 ? -1 : idx(t.substring(a + 1, b));
				if (i < 0) {
					int q = a < 0 ? t.length() : a + 1;
					o.append(Component.literal(t.substring(p, q)).withStyle(st));
					p = q;
					continue;
				}
				if (a > p) o.append(Component.literal(t.substring(p, a)).withStyle(st));
				o.append(Component.literal(String.valueOf((char) (0xE000 + i))).withStyle(st.applyTo(F)));
				hit[0] = true;
				p = b + 1;
			}
			return Optional.empty();
		}, Style.EMPTY);
		return hit[0] ? o : c;
	}
}
