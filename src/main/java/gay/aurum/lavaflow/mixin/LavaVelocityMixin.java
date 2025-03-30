package gay.aurum.lavaflow.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LavaVelocityMixin {
	@Shadow
	protected
	boolean jumping;

	@Inject(method = "travel", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER ),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tag/TagKey;)D")))
	private void lavaflow$lavaSwimSpeed(CallbackInfo ci) {

		double multiH = ((LivingEntity)(Object)this).isSprinting() ? 1.7D : 1.3D;

		double multiV = this.jumping || ((LivingEntity)(Object)this).isSneaking() ? 1.6D : 1.3D;

		((LivingEntity)(Object)this).setVelocity(((LivingEntity)(Object)this).getVelocity().multiply(multiH,multiV,multiH).add(0,0,0));
	}

	@Redirect(method = "travel", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasNoGravity()Z"),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z")))
	private boolean lavaflow$lavaSwimGravity(LivingEntity instance) {

		return instance.hasNoGravity() || instance.isSwimming() && !instance.isSneaking();
	}

}
