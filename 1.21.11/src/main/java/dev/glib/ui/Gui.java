package dev.glib.ui;

import dev.glib.Gm;
import dev.wysl.Colors;
import dev.wysl.ui.Canvas;
import dev.wysl.ui.Kit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class Gui extends Kit {
    private static final String[] AN = {"Static", "Pulse", "Flicker", "Rainbow", "Shimmer"};
    private static final String[] BL = {"Additive", "Screen"};

    private int col = 0xa24bff;
    private int rad = 55;
    private int str = 40;
    private int fal = 100;
    private int an = 1;
    private int spd = 100;
    private int bl = 0;
    private int sel = -1;
    private int tab;
    private float tabK = 1f;
    private boolean start;

    private void init0() {
        if (start) return;
        start = true;
        for (Gm m : Gm.ALL) {
            if (!m.has()) continue;
            col = m.get("col", col);
            rad = m.get("rad", rad);
            str = m.get("str", str);
            fal = m.get("fal", fal);
            an = m.get("anim", an);
            spd = m.get("spd", spd);
            bl = m.get("blend", bl);
            break;
        }
    }

    private void all(String f, int v) {
        for (Gm m : Gm.ALL) if (m.has()) m.put(f, v);
    }

    private void sync() {
        long t = System.nanoTime();
        for (Gm m : Gm.ALL) if (m.has()) {
            m.put("anim", 3);
            m.put("spd", spd);
            m.clock(t);
        }
        an = 3;
    }

    private void copy(Gm s) {
        col = s.get("col", col);
        rad = s.get("rad", rad);
        str = s.get("str", str);
        fal = s.get("fal", fal);
        an = s.get("anim", an);
        spd = s.get("spd", spd);
        bl = s.get("blend", bl);
        all("col", col);
        all("rad", rad);
        all("str", str);
        all("fal", fal);
        all("anim", an);
        all("spd", spd);
        all("blend", bl);
        if (an == 3) sync();
    }

    @Override
    public void draw(Canvas g, int width, int height, double mouseX, double mouseY) {
        init0();
        begin(mouseX, mouseY);
        float cw = Math.min(width - 24, 440);
        float ch = Math.min(height - 12, 262);
        float x = (width - cw) / 2f;
        float y = (height - ch) / 2f + (1f - open) * 16f;
        dim(g, width, height);
        glass(g, x, y, cw, ch);
        g.text("Glow Library", x + 16, y + 12, fd(TX));
        String sb = "one place for every glow mod";
        g.text(sb, x + cw - 16 - g.width(sb), y + 12, fd(MU));
        g.fill(x + 16, y + 32, cw - 32, 1, 0, fd(0x16FFFFFF));
        float top = y + 42;
        float bot = y + ch - 36;
        float lw = 186;
        float lx = x + 14;
        well(g, lx, top, lw, bot - top, 10);
        float rh = 27;
        for (int i = 0; i < Gm.ALL.length; i++) {
            Gm m = Gm.ALL[i];
            float ry = top + 5 + i * rh;
            boolean has = m.has();
            boolean over = hov(lx + 4, ry, lw - 8, rh - 3);
            float k = an("g" + i, over || sel == i ? 1f : 0f, 14f);
            g.fill(lx + 4, ry, lw - 8, rh - 3, 8, fd(Colors.alpha(0x20FFFFFF, 0.3f + k * 0.7f)), fd(Colors.alpha(0x0CFFFFFF, 0.3f + k * 0.7f)));
            int c = has ? m.get("col", 0xffffff) : 0x555566;
            g.fill(lx + 10, ry + 7, 8, 8, 4, fd(0xFF000000 | c));
            g.shadow(lx + 10, ry + 7, 8, 8, 4, 5, fd(Colors.alpha(0xFF000000 | c, has && m.on() ? 0.6f : 0f)));
            String nm = m.nm.equals("Totem Particle Glow") ? "Totem Particles" : m.nm;
            g.text(nm, lx + 24, ry + 7, fd(has ? TX : MU));
            if (!has) {
                String ni = "not installed";
                g.text(ni, lx + lw - 10 - g.width(ni), ry + 7, fd(MU));
                continue;
            }
            int idx = i;
            add("sel" + i, lx + 4, ry, lw - 82, rh - 3, () -> sel = sel == idx ? -1 : idx);
            btn(g, "op" + i, "Open", lx + lw - 76, ry + 3, 40, false, () -> {
                m.save();
                Minecraft mc = Minecraft.getInstance();
                try {
                    Screen p = mc.screen;
                    mc.setScreen((Screen) Class.forName(m.cls).getConstructor(Screen.class).newInstance(p));
                } catch (Throwable t) {
                }
            });
            tgl(g, "on" + i, lx + lw - 30, ry + 6, m.on(), () -> {
                m.setOn(!m.on());
                m.save();
            });
        }
        float rx = lx + lw + 12;
        float rw = x + cw - 14 - rx;
        seg(g, "tab", rx, top, rw, 18, new String[]{"Look", "Effect"}, tab, v -> {
            if (tab != v) tabK = 0f;
            tab = v;
        });
        tabK = ease(tabK, 1f, 11f);
        cf = tabK;
        float so = (1f - tabK) * 18f;
        float ty = top + 26;
        float cx = rx + so;
        if (tab == 0) {
            pick(g, "col", () -> col, v -> {
                col = v & 0xFFFFFF;
                all("col", col);
            }, cx, ty, rw, 38);
            sld(g, "rad", "Size", cx, ty + 46, rw, rad, 10, 500, 5, v -> {
                rad = (int) v;
                all("rad", rad);
            }, rad + "%");
            sld(g, "str", "Strength", cx, ty + 70, rw, str, 0, 500, 5, v -> {
                str = (int) v;
                all("str", str);
            }, str + "%");
            sld(g, "fal", "Softness", cx, ty + 94, rw, fal, 25, 300, 5, v -> {
                fal = (int) v;
                all("fal", fal);
            }, fal + "%");
        } else {
            g.text("Effect", cx, ty, fd(MU));
            seg(g, "an", cx, ty + 11, rw, 16, AN, an, v -> {
                an = v;
                all("anim", an);
                if (an == 3) sync();
            });
            g.text("Blend", cx, ty + 36, fd(MU));
            seg(g, "bl", cx + rw - 118, ty + 33, 118, 16, BL, bl, v -> {
                bl = v;
                all("blend", bl);
            });
            sld(g, "spd", "Speed", cx, ty + 60, rw, spd, 10, 500, 5, v -> {
                spd = (int) v;
                all("spd", spd);
                if (an == 3) sync();
            }, spd + "%");
            btn(g, "sync", "Sync rainbow", cx, ty + 90, 90, false, this::sync);
        }
        cf = 1f;
        float by = y + ch - 28;
        if (sel >= 0 && Gm.ALL[sel].has()) btn(g, "cp", "Copy " + Gm.ALL[sel].nm.split(" ")[0] + " to all", x + 14, by, 110, false, () -> copy(Gm.ALL[sel]));
        else g.text("click a mod to copy its settings to all", x + 16, by + 5, fd(MU));
        btn(g, "done", "Done", x + cw - 14 - 60, by, 60, true, () -> {
            save();
            close.run();
        });
        tipDraw(g, width);
    }

    @Override
    public void save() {
        super.save();
        for (Gm m : Gm.ALL) if (m.has()) m.save();
    }
}
