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
		if (c.getString().indexOf('') >= 0) return c;
		if (nm != null && ST.contains(nm.toLowerCase()) && Cfg.gi("tagshow", 1) == 1) c = Component.empty().append(Component.literal("\u2605 ").withStyle(net.minecraft.ChatFormatting.GOLD)).append(c);
		if (!has(nm)) return c;
		MutableComponent t = Component.literal("").withStyle(S);
		return Component.empty().append(t).append(" ").append(c);
	}

	public static Component chat(Component c) {
		if (c.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc && tc.getKey().startsWith("chat.type.") && tc.getArgs().length > 0 && tc.getArgs()[0] instanceof Component n) {
			Object[] a = tc.getArgs().clone();
			String nm = n.getStyle().getInsertion() != null ? n.getStyle().getInsertion() : n.getString();
			a[0] = add(nm, n);
			MutableComponent r = Component.translatable(tc.getKey(), a);
			r.setStyle(c.getStyle());
			for (Component s : c.getSiblings()) r.append(s);
			return r;
		}
		String s = c.getString();
		if (s.indexOf('') >= 0 || Cfg.gi("tagshow", 1) != 1) return c;
		java.util.regex.Matcher m = NM.matcher(s.length() > 90 ? s.substring(0, 90) : s);
		while (m.find()) {
			if (has(m.group())) return Component.empty().append(Component.literal("").withStyle(S)).append(" ").append(c);
		}
		return c;
	}

	static final java.util.regex.Pattern NM = java.util.regex.Pattern.compile("[A-Za-z0-9_]{3,16}");
}
