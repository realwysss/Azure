package dev.wscl.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MnAcc {
	@Mutable
	@Accessor("user")
	void user(User u);

	@Mutable
	@Accessor("userApiService")
	void api(UserApiService a);

	@Mutable
	@Accessor("userPropertiesFuture")
	void props(CompletableFuture<UserApiService.UserProperties> f);

	@Mutable
	@Accessor("profileKeyPairManager")
	void pkm(ProfileKeyPairManager m);

	@Accessor("reportingContext")
	void rc(ReportingContext c);
}
