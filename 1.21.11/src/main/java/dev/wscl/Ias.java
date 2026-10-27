package dev.wscl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class Ias {
	public static boolean on() {
		return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("ias");
	}

	public static void hide() {
		if (!on()) return;
		try {
			Class<?> k = Class.forName("ru.vidtu.ias.config.IASConfig");
			for (String f : new String[]{"serversButton", "serversText", "titleButton", "titleText"}) {
				try {
					var fl = k.getDeclaredField(f);
					fl.setAccessible(true);
					fl.setBoolean(null, false);
				} catch (Throwable e) {
				}
			}
		} catch (Throwable e) {
		}
	}

	public static boolean open() {
		Minecraft m = Minecraft.getInstance();
		try {
			m.setScreen((Screen) Class.forName("ru.vidtu.ias.screen.AccountScreen").getConstructor(Screen.class).newInstance(m.screen));
			return true;
		} catch (Throwable x) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	static List<Object> raw() {
		try {
			return (List<Object>) Class.forName("ru.vidtu.ias.config.IASStorage").getField("ACCOUNTS").get(null);
		} catch (Throwable e) {
			return null;
		}
	}

	public static List<Object> accs() {
		List<Object> l = raw();
		return l == null ? new ArrayList<>() : new ArrayList<>(l);
	}

	static Object cl(Object a, String n) {
		try {
			Method m = Class.forName("ru.vidtu.ias.account.Account").getMethod(n);
			return m.invoke(a);
		} catch (Throwable e) {
			return null;
		}
	}

	public static String nm(Object a) {
		Object o = cl(a, "name");
		return o == null ? "?" : o.toString();
	}

	public static UUID skin(Object a) {
		Object o = cl(a, "skin");
		if (o instanceof UUID u) return u;
		o = cl(a, "uuid");
		return o instanceof UUID u ? u : null;
	}

	public static boolean ms(Object a) {
		return Boolean.TRUE.equals(cl(a, "canLogin"));
	}

	static Screen mk(String cn, Class<?>[] ts, Object... as) throws Exception {
		Constructor<?> c = Class.forName(cn).getDeclaredConstructor(ts);
		c.setAccessible(true);
		return (Screen) c.newInstance(as);
	}

	static void save() {
		try {
			Class<?> k = Class.forName("ru.vidtu.ias.IAS");
			k.getMethod("disclaimersStorage").invoke(null);
			k.getMethod("saveStorage").invoke(null);
		} catch (Throwable e) {
		}
	}

	public static void login(Screen par, Object a) {
		Minecraft m = Minecraft.getInstance();
		try {
			Screen p = mk("ru.vidtu.ias.screen.LoginPopupScreen", new Class<?>[]{Screen.class}, par);
			m.setScreen(p);
			Class<?> lh = Class.forName("ru.vidtu.ias.auth.handlers.LoginHandler");
			if (ms(a)) {
				Method lg = Class.forName("ru.vidtu.ias.account.Account").getMethod("login", lh, Runnable.class);
				var ex = (java.util.concurrent.Executor) Class.forName("ru.vidtu.ias.IAS").getMethod("executor").invoke(null);
				Runnable dn = () -> {
				};
				ex.execute(() -> {
					try {
						lg.invoke(a, p, dn);
					} catch (Throwable e) {
					}
				});
			} else {
				String n = nm(a);
				UUID u = (UUID) Class.forName("ru.vidtu.ias.account.OfflineAccount").getMethod("uuid", String.class).invoke(null, n);
				Class<?> ld = Class.forName("ru.vidtu.ias.auth.LoginData");
				Object d = ld.getConstructor(String.class, UUID.class, String.class, boolean.class).newInstance(n, u, "ias:offline", false);
				lh.getMethod("success", ld, boolean.class).invoke(p, d, false);
			}
		} catch (Throwable e) {
			m.setScreen(par);
		}
	}

	public static void add(Screen par) {
		try {
			java.util.function.Consumer<Object> cb = a -> {
				List<Object> l = raw();
				if (l != null) l.add(a);
				save();
			};
			Minecraft.getInstance().setScreen(mk("ru.vidtu.ias.screen.AddPopupScreen", new Class<?>[]{Screen.class, boolean.class, java.util.function.Consumer.class}, par, false, cb));
		} catch (Throwable e) {
		}
	}

	public static void del(Screen par, Object a) {
		try {
			Runnable cb = () -> {
				List<Object> l = raw();
				if (l != null) l.remove(a);
				save();
			};
			Minecraft.getInstance().setScreen(mk("ru.vidtu.ias.screen.DeletePopupScreen", new Class<?>[]{Screen.class, Class.forName("ru.vidtu.ias.account.Account"), Runnable.class}, par, a, cb));
		} catch (Throwable e) {
		}
	}
}
