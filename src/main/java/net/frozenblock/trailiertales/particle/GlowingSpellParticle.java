package net.frozenblock.trailiertales.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class GlowingSpellParticle extends SpellParticle {

	public GlowingSpellParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
	}

	@Override
	protected int getLightColor(float tint) {
		return 240;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;

		public Provider(SpriteSet spriteProvider) {
			this.sprite = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel world, double d, double e, double f, double g, double h, double i) {
			return new GlowingSpellParticle(world, d, e, f, g, h, i, this.sprite);
		}
	}

	public static class MobEffectProvider implements ParticleProvider<ColorParticleOption> {
		private final SpriteSet sprite;

		public MobEffectProvider(SpriteSet spriteSet) {
			this.sprite = spriteSet;
		}

		public Particle createParticle(@NotNull ColorParticleOption colorParticleOption, ClientLevel world, double d, double e, double f, double g, double h, double i) {
			GlowingSpellParticle particle = new GlowingSpellParticle(world, d, e, f, g, h, i, this.sprite);
			particle.setColor(colorParticleOption.getRed(), colorParticleOption.getGreen(), colorParticleOption.getBlue());
			particle.setAlpha(colorParticleOption.getAlpha());
			return particle;
		}
	}
}
