package dev.wscl;

public class Zx {
	static final int[] k = {91, 17, 203, 66, 148, 37};

	public static String d(String h) {
		char[] c = new char[h.length() / 4];
		for (int i = 0; i < c.length; i++) c[i] = (char) (Integer.parseInt(h.substring(i * 4, i * 4 + 4), 16) ^ k[i % k.length]);
		return new String(c);
	}
}
