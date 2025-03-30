package gay.aurum.lavaflow;

import gay.aurum.lavaflow.particles.LavaBubbleParticleDown;
import gay.aurum.lavaflow.particles.LavaBubbleParticleUp;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class ClientInit implements ClientModInitializer {


	@Override
	public void onInitializeClient(ModContainer mod) {
		ParticleFactoryRegistry.getInstance().register(LavaFlow.LAVABUBL, LavaBubbleParticleUp.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LavaFlow.LAVABUBLDOWN, LavaBubbleParticleDown.Factory::new);
	}

}
