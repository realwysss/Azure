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

	public static Component add(String nm, Component c) {
		if (c.getString().indexOf('') >= 0) return c;
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
		java.util.regex.Matcher m = NM.matcher(s);
		int e = Math.min(s.length(), 90);
		boolean sep = false;
		for (int i = 0; i < e; i++) {
			char ch = s.charAt(i);
			if (ch == ':' || ch == '»' || ch == '>') {
				e = i;
				sep = true;
				break;
			}
		}
		while (m.find() && m.start() < e) {
			if (has(m.group())) return ins(c, m.start());
			if (!sep) break;
		}
		return c;
	}

	static Component ins(Component c, int at) {
		MutableComponent r = Component.empty();
		int[] p = {0};
		boolean[] d = {false};
		c.visit((st, t) -> {
			if (!d[0] && at < p[0] + t.length()) {
				int k = at - p[0];
				if (k > 0) r.append(Component.literal(t.substring(0, k)).withStyle(st));
				r.append(Component.literal("").withStyle(S)).append(" ");
				r.append(Component.literal(t.substring(k)).withStyle(st));
				d[0] = true;
			} else r.append(Component.literal(t).withStyle(st));
			p[0] += t.length();
			return java.util.Optional.empty();
		}, Style.EMPTY);
		return d[0] ? r : c;
	}

	static final java.util.regex.Pattern NM = java.util.regex.Pattern.compile("[A-Za-z0-9_]{3,16}");
}
