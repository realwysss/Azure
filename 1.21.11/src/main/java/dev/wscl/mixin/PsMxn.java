package dev.wscl.mixin;

import net.minecraft.client.gui.components.PlayerSkinWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerSkinWidget.class)
public interface PsMxn {
	@Accessor("rotationX")
	void rx(float v);

	@Accessor("rotationY")
	void ry(float v);

	@Accessor("wideModel")
	net.minecraft.client.model.player.PlayerModel wide();

	@Accessor("slimModel")
	net.minecraft.client.model.player.PlayerModel slim();
}
