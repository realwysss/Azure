package dev.wscl;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class Prv {
	static Skn skn;
	static int pick = -1;

	public static boolean ent(GuiGraphics g, String kind, int n, float x, float y, float w, float h, float yaw, float age) {
		try {
			Minecraft m = Minecraft.getInstance();
			EntityRenderState st;
			float bh;
			float sc;
			float off;
			if (kind.equals("crystal")) {
				EndCrystalRenderState s = new EndCrystalRenderState();
				s.showsBottom = true;
				s.entityType = EntityType.END_CRYSTAL;
				s.boundingBoxWidth = 2f;
				s.boundingBoxHeight = 2f;
				st = s;
				bh = 2f;
				sc = h * 0.3f;
				off = 0.15f;
			} else if (kind.equals("xp")) {
				ExperienceOrbRenderState s = new ExperienceOrbRenderState();
				s.icon = n >= 2477 ? 10 : n >= 1237 ? 9 : n >= 617 ? 8 : n >= 307 ? 7 : n >= 149 ? 6 : n >= 73 ? 5 : n >= 37 ? 4 : n >= 17 ? 3 : n >= 7 ? 2 : n >= 3 ? 1 : 0;
				s.entityType = EntityType.EXPERIENCE_ORB;
				s.boundingBoxWidth = 0.5f;
				s.boundingBoxHeight = 0.5f;
				st = s;
				bh = 0.5f;
				sc = h * 4.2f;
				off = 0f;
			} else if (kind.equals("axolotl") || kind.equals("parrot")) {
				LivingEntityRenderState s;
				if (kind.equals("axolotl")) {
					AxolotlRenderState a = new AxolotlRenderState();
					a.variant = Axolotl.Variant.values()[Math.floorMod(n, Axolotl.Variant.values().length)];
					a.inWaterFactor = 1f;
					a.onGroundFactor = 0f;
					a.entityType = EntityType.AXOLOTL;
					a.boundingBoxWidth = 0.75f;
					a.boundingBoxHeight = 0.42f;
					s = a;
					bh = 0.42f;
					sc = h * 1.0f;
				} else {
					ParrotRenderState p = new ParrotRenderState();
					p.variant = Parrot.Variant.values()[Math.floorMod(n, Parrot.Variant.values().length)];
					p.pose = net.minecraft.client.model.animal.parrot.ParrotModel.Pose.STANDING;
					p.entityType = EntityType.PARROT;
					p.boundingBoxWidth = 0.5f;
					p.boundingBoxHeight = 0.9f;
					s = p;
					bh = 0.9f;
					sc = h * 0.62f;
				}
				s.scale = 1f;
				s.ageScale = 1f;
				s.pose = net.minecraft.world.entity.Pose.STANDING;
				s.bodyRot = 0f;
				s.yRot = 0f;
				s.xRot = 0f;
				st = s;
				off = 0f;
			} else return false;
			st.lightCoords = 0xF000F0;
			st.shadowPieces.clear();
			st.outlineColor = 0;
			st.ageInTicks = age;
			EntityRenderer<?, ?> r = m.getEntityRenderDispatcher().getRenderer(st);
			if (r == null) return false;
			org.joml.Quaternionf q = new org.joml.Quaternionf().rotateZ((float) Math.PI).rotateY(yaw);
			boolean mobk = kind.equals("axolotl") || kind.equals("parrot");
			org.joml.Quaternionf cam = new org.joml.Quaternionf().rotateX(mobk ? 0.12f : 0.4f);
			q.mul(cam);
			g.submitEntityRenderState(st, sc, new org.joml.Vector3f(0f, bh / 2f + off, 0f), q, cam, (int) x, (int) y, (int) (x + w), (int) (y + h));
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static int mobPick() {
		if (pick < 0) pick = new Random().nextInt(2000);
		return pick;
	}

	public static void mobNew() {
		pick = new Random().nextInt(2000);
	}

	public static boolean player(GuiGraphics g, float x, float y, float w, float h) {
		try {
			if (skn == null || skn.w.getWidth() != (int) w || skn.w.getHeight() != (int) h) skn = new Skn((int) w, (int) h, (int) x, (int) y);
			skn.w.setPosition((int) x, (int) y);
			skn.render(g, 0, 0, 0f);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}
