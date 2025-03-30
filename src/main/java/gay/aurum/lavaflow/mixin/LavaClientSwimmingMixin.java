package gay.aurum.lavaflow.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ClientPlayerEntity.class)
public class LavaClientSwimmingMixin {

	@Redirect(method = "tickMovement", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 1),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 2)))
	private boolean lavaflow$lavaSwimSprint(ClientPlayerEntity instance) {

		return instance.isTouchingWater() || instance.isInLava();
	}
}
