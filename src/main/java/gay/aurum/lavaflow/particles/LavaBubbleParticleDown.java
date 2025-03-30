package gay.aurum.lavaflow.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class LavaBubbleParticleDown extends SpriteBillboardParticle {
	private float accelerationAngle;

	protected LavaBubbleParticleDown(ClientWorld clientWorld, double x, double y, double z) {
		super(clientWorld, x, y, z);
		this.maxAge = (int)(Math.random() * 60.0) + 30;
		this.collidesWithWorld = false;
		this.velocityX = 0.0;
		this.velocityY = -0.05;
		this.velocityZ = 0.0;
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.scale *= this.random.nextFloat() * 0.6F + 0.2F;
		this.gravityStrength = 0.002F;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			float f = 0.6F;
			this.velocityX += (double)(0.6F * MathHelper.cos(this.accelerationAngle));
			this.velocityZ += (double)(0.6F * MathHelper.sin(this.accelerationAngle));
			this.velocityX *= 0.07;
			this.velocityZ *= 0.07;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.LAVA) || this.onGround) {
				this.markDead();
			}

			this.accelerationAngle += 0.08F;
		}
	}

	@ClientOnly
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
			LavaBubbleParticleDown particle = new LavaBubbleParticleDown(clientWorld,x, y, z);
			particle.setSprite(spriteProvider);
			return particle;
		}
	}
}
