package gay.aurum.lavaflow.mixin;

import gay.aurum.lavaflow.blocks.LavaBubbleStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoulSandBlock.class)
public class SoulSandMixin {

	@Inject(method = "scheduledTick", at = @At("TAIL"))
	private void lavaflow$scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random, CallbackInfo ci) {
		LavaBubbleStream.update(world, pos.up(), state);
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("TAIL"))
	private void lavaflow$getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {

		if (direction == Direction.UP && neighborState.isOf(Blocks.LAVA)) {
			world.scheduleBlockTick(pos, ((SoulSandBlock)(Object)this), 20);
		}
	}
}
