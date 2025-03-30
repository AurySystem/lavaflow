package gay.aurum.lavaflow.mixin;

import gay.aurum.lavaflow.LavaFlow;
import gay.aurum.lavaflow.blocks.LavaBubbleStream;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(TransparentBlock.class)
public class BlueIceMixin extends Block {
	public BlueIceMixin(Settings settings) {
		super(settings);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if(this == (Blocks.BLUE_ICE)){
			LavaBubbleStream.update(world, pos.down(), state);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if(this == (Blocks.BLUE_ICE)){
			LavaFlow.LOGGER.info("ahhh");
			if (  direction == Direction.DOWN && neighborState.isOf(Blocks.LAVA)) {
				world.scheduleBlockTick(pos, this, 20);
			}
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if(this == (Blocks.BLUE_ICE)){
			world.scheduleBlockTick(pos, this, 20);
		}
	}
}
