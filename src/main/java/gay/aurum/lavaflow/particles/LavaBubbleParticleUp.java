package gay.aurum.lavaflow.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class LavaBubbleParticleUp extends SpriteBillboardParticle {

	protected LavaBubbleParticleUp(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
		super(clientWorld, x, y, z, vx, vy, vz);
		this.gravityStrength = -0.125F;
		this.velocityMultiplier = 0.85F;
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.scale *= this.random.nextFloat() * 0.6F + 0.2F;
		this.velocityX = vx * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityY = vy * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityZ = vz * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.maxAge = (int)(40.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.dead && !this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.LAVA)) {
			this.markDead();
		}
	}

	@ClientOnly
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
			LavaBubbleParticleUp particle = new LavaBubbleParticleUp(clientWorld, x, y, z, vx, vy, vz);
			particle.setSprite(spriteProvider);
			return particle;
		}
	}
}
