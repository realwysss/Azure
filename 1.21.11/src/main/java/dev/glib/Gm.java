package dev.glib;

import net.fabricmc.loader.api.FabricLoader;

public final class Gm {
    public final String id;
    public final String nm;
    public final String pk;
    public final String cls;

    public Gm(String id, String nm, String pk) {
        this.id = id;
        this.nm = nm;
        this.pk = pk;
        this.cls = pk + ".ui.Panel";
    }

    public static final Gm[] ALL = {
        new Gm("hitglow", "Hit Glow", "dev.htgl"),
        new Gm("xpglow", "XP Glow", "dev.xpgl"),
        new Gm("crystalglow", "Crystal Glow", "dev.crgl"),
        new Gm("anchorglow", "Anchor Glow", "dev.angl"),
        new Gm("totemparticleglow", "Totem Particle Glow", "dev.tpgl")
    };

    public boolean has() {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    Object cfg() {
        try {
            return Class.forName(pk + ".Cfg").getField("c").get(null);
        } catch (Throwable t) {
            return null;
        }
    }

    public int get(String f, int d) {
        try {
            Object c = cfg();
            return c == null ? d : c.getClass().getField(f).getInt(c);
        } catch (Throwable t) {
            return d;
        }
    }

    public boolean on() {
        try {
            Object c = cfg();
            return c != null && c.getClass().getField("on").getBoolean(c);
        } catch (Throwable t) {
            return false;
        }
    }

    public void setOn(boolean v) {
        try {
            Object c = cfg();
            c.getClass().getField("on").setBoolean(c, v);
        } catch (Throwable t) {
        }
    }

    public void put(String f, int v) {
        try {
            Object c = cfg();
            if (c == null) return;
            c.getClass().getField(f).setInt(c, v);
            try {
                Object core = c.getClass().getField("core").get(c);
                if (core != null) core.getClass().getField(f).setInt(core, v);
            } catch (Throwable t) {
            }
        } catch (Throwable t) {
        }
    }

    public void save() {
        try {
            Class.forName(pk + ".Cfg").getMethod("sv").invoke(null);
        } catch (Throwable t) {
        }
    }

    public void clock(long v) {
        try {
            Class.forName(pk + ".Cfg").getField("T0").setLong(null, v);
        } catch (Throwable t) {
        }
    }
}
