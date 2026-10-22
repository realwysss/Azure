package dev.wscl;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class Tag {
	static final Style S = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("azure", "tag")));

	public static boolean has(String nm) {
		if (nm == null || Cfg.gi("tagshow", 1) != 1) return false;
		String me = net.minecraft.client.Minecraft.getInstance().getUser().getName();
		if (nm.equalsIgnoreCase(me)) return Cfg.gi("tagshare", 1) == 1;
		return Onl.users.contains(nm.toLowerCase());
	}

	static final java.util.Set<String> ST = java.util.Set.of("dlehf", "hitsonly");

	public static Component add(String nm, Component c) {
		if (nm != null && ST.contains(nm.toLowerCase()) && Cfg.gi("tagshow", 1) == 1) c = Component.empty().append(Component.literal("\u2605 ").withStyle(net.minecraft.ChatFormatting.GOLD)).append(c);
		if (!has(nm)) return c;
		MutableComponent t = Component.literal("").withStyle(S);
		return Component.empty().append(t).append(" ").append(c);
	}
}
