package gay.aurum.lavaflow.blocks;

import gay.aurum.lavaflow.LavaFlow;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Optional;

public class LavaBubbleStream extends Block implements FluidDrainable {
	public static final BooleanProperty DRAG = Properties.DRAG;
	private static final int SCHEDULED_TICK_DELAY = 5;

	public LavaBubbleStream(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(DRAG, Boolean.TRUE));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockState blockState = world.getBlockState(pos.up());
		if (blockState.isAir()) {
			entity.onBubbleColumnSurfaceCollision(state.get(DRAG));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for(int i = 0; i < 2; ++i) {
					serverWorld.spawnParticles(
							ParticleTypes.LAVA_SPLASH,
							(double)pos.getX() + world.random.nextDouble(),
							(double)(pos.getY() + 1),
							(double)pos.getZ() + world.random.nextDouble(),
							1,
							0.0,
							0.0,
							0.0,
							1.0
					);
					serverWorld.spawnParticles(
							LavaFlow.LAVABUBL,
							(double)pos.getX() + world.random.nextDouble(),
							(double)(pos.getY() + 1),
							(double)pos.getZ() + world.random.nextDouble(),
							1,
							0.0,
							0.01,
							0.0,
							0.2
					);
				}
			}
		} else {
			entity.onBubbleColumnCollision(state.get(DRAG));
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		update(world, pos, state, world.getBlockState(pos.down()));
		update(world, pos, state, world.getBlockState(pos.up()));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.LAVA.getStill(false);
	}

	public static void update(WorldAccess world, BlockPos pos, BlockState state) {
		update(world, pos, world.getBlockState(pos), state);
	}

	public static void update(WorldAccess world, BlockPos pos, BlockState lava, BlockState bubbleSource) {
		if (canBeBubbleColumn(lava)) {
			BlockState bubbleState = getBubbleState(bubbleSource);
			world.setBlockState(pos, bubbleState, Block.NOTIFY_LISTENERS);

			boolean direct = false;
			if(bubbleState.isOf(LavaFlow.LAVA_BUBBLES)){
				direct = bubbleState.get(DRAG);
			}else if (lava.isOf(LavaFlow.LAVA_BUBBLES)) {
				direct = lava.get(DRAG);
			}
			Direction direction = direct ? Direction.DOWN : Direction.UP;
			BlockPos.Mutable mutable = pos.mutableCopy().move(direction);

			while(canBeBubbleColumn(world.getBlockState(mutable))) {
				if (!world.setBlockState(mutable, bubbleState, Block.NOTIFY_LISTENERS)) {
					return;
				}

				mutable.move(direction);
			}
		}
	}

	private static boolean canBeBubbleColumn(BlockState state) {
		return state.isOf(LavaFlow.LAVA_BUBBLES) || state.isOf(Blocks.LAVA) && state.getFluidState().getLevel() >= 8 && state.getFluidState().isSource();
	}

	private static BlockState getBubbleState(BlockState state) {
		if (state.isOf(LavaFlow.LAVA_BUBBLES)) {
			return state;
		} else if (state.isOf(Blocks.SOUL_SAND)) {
			return LavaFlow.LAVA_BUBBLES.getDefaultState().with(DRAG, Boolean.FALSE);
		} else {
			return state.isOf(Blocks.BLUE_ICE) ? LavaFlow.LAVA_BUBBLES.getDefaultState().with(DRAG, Boolean.TRUE) : Blocks.LAVA.getDefaultState();
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if (state.get(DRAG)) {
			world.addImportantParticle(LavaFlow.LAVABUBLDOWN, x + 0.5, y + 0.8, z, 0.0, 0.0, 0.0);
			if (random.nextInt(200) == 0) {
				world.playSound(
						x, y, z,
						SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
						SoundCategory.BLOCKS,
						0.2F + random.nextFloat() * 0.2F,
						0.9F + random.nextFloat() * 0.15F,
						false
				);
			}
		} else {
			world.addImportantParticle(LavaFlow.LAVABUBL, x + 0.5, y, z + 0.5, 0.0, 0.04, 0.0);
			world.addImportantParticle(
					LavaFlow.LAVABUBL, x + (double)random.nextFloat(), y + (double)random.nextFloat(), z + (double)random.nextFloat(), 0.0, 0.04, 0.0
			);
			if (random.nextInt(200) == 0) {
				world.playSound(
						x ,y, z,
						SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,
						SoundCategory.BLOCKS,
						0.2F + random.nextFloat() * 0.2F,
						0.9F + random.nextFloat() * 0.15F,
						false
				);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		world.scheduleFluidTick(pos, Fluids.LAVA, Fluids.LAVA.getTickRate(world));
		if (!state.canPlaceAt(world, pos)
				|| direction == Direction.DOWN
				|| direction == Direction.UP && !neighborState.isOf(LavaFlow.LAVA_BUBBLES) && canBeBubbleColumn(neighborState)) {
			world.scheduleBlockTick(pos, this, 5);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		return blockState.isOf(LavaFlow.LAVA_BUBBLES) || blockState.isOf(Blocks.MAGMA_BLOCK) || blockState.isOf(Blocks.SOUL_SAND);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(DRAG);
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
		return new ItemStack(Items.LAVA_BUCKET);
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Fluids.LAVA.getBucketFillSound();
	}
}
