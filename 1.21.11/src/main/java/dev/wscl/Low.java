package dev.wscl;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.level.ParticleStatus;

public class Low {
	static final String[] K = {"rd", "sd", "pt", "cl", "ao", "bb", "ed", "sh", "mm"};

	public static void apply(boolean on, int preset) {
		Minecraft m = Minecraft.getInstance();
		Options o = m.options;
		if (on) {
			if (Cfg.gi("low.saved", 0) == 0) {
				Cfg.si("low.rd", o.renderDistance().get());
				Cfg.si("low.sd", o.simulationDistance().get());
				Cfg.si("low.pt", o.particles().get().ordinal());
				Cfg.si("low.cl", o.cloudStatus().get().ordinal());
				Cfg.si("low.ao", o.ambientOcclusion().get() ? 1 : 0);
				Cfg.si("low.bb", o.biomeBlendRadius().get());
				Cfg.si("low.ed", (int) Math.round(o.entityDistanceScaling().get() * 100));
				Cfg.si("low.sh", o.entityShadows().get() ? 1 : 0);
				Cfg.si("low.mm", o.mipmapLevels().get());
				Cfg.si("low.saved", 1);
			}
			boolean u = preset == 1;
			o.renderDistance().set(u ? 5 : 8);
			o.simulationDistance().set(u ? 5 : 6);
			o.particles().set(u ? ParticleStatus.MINIMAL : ParticleStatus.DECREASED);
			o.cloudStatus().set(CloudStatus.OFF);
			o.ambientOcclusion().set(false);
			o.biomeBlendRadius().set(0);
			o.entityDistanceScaling().set(u ? 0.5 : 0.75);
			o.entityShadows().set(false);
			o.mipmapLevels().set(0);
		} else if (Cfg.gi("low.saved", 0) == 1) {
			o.renderDistance().set(Cfg.gi("low.rd", 12));
			o.simulationDistance().set(Cfg.gi("low.sd", 12));
			o.particles().set(ParticleStatus.values()[Math.max(0, Math.min(2, Cfg.gi("low.pt", 0)))]);
			o.cloudStatus().set(CloudStatus.values()[Math.max(0, Math.min(CloudStatus.values().length - 1, Cfg.gi("low.cl", 2)))]);
			o.ambientOcclusion().set(Cfg.gi("low.ao", 1) == 1);
			o.biomeBlendRadius().set(Cfg.gi("low.bb", 2));
			o.entityDistanceScaling().set(Cfg.gi("low.ed", 100) / 100.0);
			o.entityShadows().set(Cfg.gi("low.sh", 1) == 1);
			o.mipmapLevels().set(Cfg.gi("low.mm", 4));
			Cfg.si("low.saved", 0);
		}
		o.save();
	}
}
