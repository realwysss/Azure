package dev.wscl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.KeyMapping;

public class Ss {
	static Object cfg() {
		try {
			Class<?> k = Class.forName("dev.sprswp.Cfg");
			Field f = k.getDeclaredField("c");
			f.setAccessible(true);
			return f.get(null);
		} catch (Throwable e) {
			return null;
		}
	}

	public static boolean here() {
		return cfg() != null;
	}

	public static int gi(String n, int d) {
		try {
			Object c = cfg();
			Field f = c.getClass().getDeclaredField(n);
			f.setAccessible(true);
			return f.get(c) instanceof Boolean b ? (b ? 1 : 0) : f.getInt(c);
		} catch (Throwable e) {
			return d;
		}
	}

	public static void si(String n, int v) {
		try {
			Object c = cfg();
			Field f = c.getClass().getDeclaredField(n);
			f.setAccessible(true);
			if (f.getType() == boolean.class) f.setBoolean(c, v == 1);
			else f.setInt(c, v);
			Method m = c.getClass().getDeclaredMethod("sav");
			m.setAccessible(true);
			m.invoke(null);
		} catch (Throwable e) {
		}
	}

	public static KeyMapping key() {
		try {
			Field f = Class.forName("dev.sprswp.SprSwp").getDeclaredField("ky");
			f.setAccessible(true);
			return (KeyMapping) f.get(null);
		} catch (Throwable e) {
			return null;
		}
	}
}
