package dev.wscl;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class Em {
	static final String[][] E = {{"heart", "♥"}, {"skull", "☠"}, {"fire", "♨"}, {"star", "★"}, {"crown", "♛"}, {"check", "✔"}, {"x", "✘"}, {"smile", "☺"}, {"sad", "☹"}, {"cool", "☻"}, {"bolt", "⚡"}, {"gem", "◆"}, {"sword", "⚔"}, {"sun", "☀"}, {"moon", "☾"}, {"note", "♪"}, {"flower", "✿"}, {"snow", "❄"}, {"anchor", "⚓"}, {"peace", "☮"}, {"warn", "⚠"}, {"coffee", "☕"}, {"phone", "☎"}, {"mail", "✉"}, {"plane", "✈"}, {"umbrella", "☂"}, {"radio", "☢"}};
	static final String[][] EM = {{"<3", "♥"}, {":)", "☺"}, {":(", "☹"}, {"B)", "☻"}};
	static final int ART = 13;
	static final Style F = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("azure", "emoji")));

	public static String names() {
		StringBuilder b = new StringBuilder();
		for (String[] e : E) b.append(':').append(e[0]).append(": ");
		return b.toString().trim();
	}

	static int idx(String s) {
		for (int i = 0; i < E.length; i++) if (E[i][0].equals(s)) return i;
		return -1;
	}

	static int art(char c) {
		for (int i = 0; i < ART; i++) if (E[i][1].charAt(0) == c) return i;
		return -1;
	}

	public static String out(String s) {
		if (!Mod.live("emoji")) return s;
		StringBuilder b = new StringBuilder();
		int p = 0;
		while (p < s.length()) {
			char c = s.charAt(p);
			if (c == ':') {
				int q = s.indexOf(':', p + 1);
				int i = q < 0 ? -1 : idx(s.substring(p + 1, q));
				if (i >= 0) {
					b.append(E[i][1]);
					p = q + 1;
					continue;
				}
			}
			boolean hit = false;
			if (p == 0 || s.charAt(p - 1) == ' ')
				for (String[] e : EM)
					if (s.startsWith(e[0], p) && (p + e[0].length() == s.length() || s.charAt(p + e[0].length()) == ' ')) {
						b.append(e[1]);
						p += e[0].length();
						hit = true;
						break;
					}
			if (hit) continue;
			b.append(c);
			p++;
		}
		return b.toString();
	}

	public static Component cv(Component c) {
		if (!Mod.live("emoji")) return c;
		String all = c.getString();
		boolean any = false;
		for (int i = 0; i < all.length() && !any; i++) if (all.charAt(i) == ':' || art(all.charAt(i)) >= 0) any = true;
		if (!any) return c;
		MutableComponent o = Component.empty();
		boolean[] hit = {false};
		c.visit((st, t) -> {
			StringBuilder run = new StringBuilder();
			int p = 0;
			while (p < t.length()) {
				char ch = t.charAt(p);
				int i = -1;
				int len = 1;
				if (ch == ':') {
					int q = t.indexOf(':', p + 1);
					int k = q < 0 ? -1 : idx(t.substring(p + 1, q));
					if (k >= 0 && k < ART) {
						i = k;
						len = q - p + 1;
					}
				} else i = art(ch);
				if (i < 0) {
					run.append(ch);
					p++;
					continue;
				}
				if (run.length() > 0) o.append(Component.literal(run.toString()).withStyle(st));
				run.setLength(0);
				o.append(Component.literal(String.valueOf((char) (0xE000 + i))).withStyle(st.applyTo(F)));
				hit[0] = true;
				p += len;
			}
			if (run.length() > 0) o.append(Component.literal(run.toString()).withStyle(st));
			return Optional.empty();
		}, Style.EMPTY);
		return hit[0] ? o : c;
	}
}
