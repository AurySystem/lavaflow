package gay.aurum.lavaflow;

import gay.aurum.lavaflow.blocks.LavaBubbleStream;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LavaFlow implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Lava Swimming");

	public static Identifier ID(String id){ return new Identifier("lavaflow", id);}

	public static final Block LAVA_BUBBLES = Registry.register(Registry.BLOCK, ID("lava_bubble_column"),new LavaBubbleStream(AbstractBlock.Settings.of(Material.LAVA).noCollision().luminance(state -> 15).dropsNothing()));

	public static final DefaultParticleType LAVABUBL = FabricParticleTypes.simple();
	public static final DefaultParticleType LAVABUBLDOWN = FabricParticleTypes.simple();

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Hello Quilt world from {}!", mod.metadata().name());
		Registry.register(Registry.PARTICLE_TYPE, ID( "lava_bubble_up"), LAVABUBL);
		Registry.register(Registry.PARTICLE_TYPE, ID( "lava_bubble_down"), LAVABUBLDOWN);
	}



}
