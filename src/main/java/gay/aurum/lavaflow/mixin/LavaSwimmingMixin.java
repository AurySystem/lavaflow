package gay.aurum.lavaflow.mixin;

import gay.aurum.lavaflow.LavaSubmerged;
import net.minecraft.entity.Entity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class LavaSwimmingMixin implements LavaSubmerged {
	@Shadow
	private BlockPos blockPos;
	private boolean lavaflow$lavasubmerged = false;

	@Inject(method = "updateSwimming", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/Entity;setSwimming(Z)V", shift = At.Shift.AFTER, ordinal = 0))
	private void lavaflow$lavaSwimUpdate1(CallbackInfo ci) {
		((Entity)(Object)this).setSwimming(((Entity)(Object)this).isSwimming() ||
				(((Entity)(Object)this).isSprinting()
				&& ((Entity)(Object)this).isInLava()
				&& !((Entity)(Object)this).hasVehicle())
		);

	}

	@Inject(method = "updateSwimming", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/Entity;setSwimming(Z)V", shift = At.Shift.AFTER, ordinal = 1))
	private void lavaflow$lavaSwimUpdate2(CallbackInfo ci) {
		((Entity)(Object)this).setSwimming(((Entity)(Object)this).isSwimming() ||
				(((Entity)(Object)this).isSprinting()
				&& this.lavaflow$lavasubmerged
				&& !((Entity)(Object)this).hasVehicle()
				&& ((Entity)(Object)this).world.getFluidState(this.blockPos).isIn(FluidTags.LAVA))
		);
	}

	@Inject(method = "updateSubmergedInWaterState", at = @At("HEAD"))
	private void lavaflow$submergedInLava(CallbackInfo ci) {
		lavaflow$lavasubmerged = ((Entity)(Object)this).isSubmergedIn(FluidTags.LAVA);
	}
	@Inject(method = "shouldLeaveSwimmingPose", at = @At("RETURN"),cancellable = true)
	private void lavaflow$PoseacceptsLava(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(cir.getReturnValue() && !((Entity)(Object)this).isInLava());
	}

	@Override
	public boolean getSubmergedInLava() {
		return lavaflow$lavasubmerged;
	}
}
