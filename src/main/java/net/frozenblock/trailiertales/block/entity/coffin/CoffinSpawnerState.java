package net.frozenblock.trailiertales.block.entity.coffin;

import java.util.Optional;
import net.frozenblock.trailiertales.TTConstants;
import net.frozenblock.trailiertales.block.CoffinBlock;
import net.frozenblock.trailiertales.registry.TTSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum CoffinSpawnerState implements StringRepresentable {
	INACTIVE("inactive", 0, false, false),
	ACTIVE("active", 3, true, false),
	IRRITATED("irritated", 5, true, false),
	AGGRESSIVE("aggressive", 7, true, true),
	OMINOUS("ominous", 7, true, true);
	private final String name;
	private final int lightLevel;
	private final boolean isCapableOfSpawning;
	private final boolean finalWave;
	private final ResourceLocation headTexture;
	private final ResourceLocation footTexture;

	CoffinSpawnerState(final String name, int lightLevel, final boolean isCapableOfSpawning, final boolean finalWave) {
		this.name = name;
		this.lightLevel = lightLevel;
		this.isCapableOfSpawning = isCapableOfSpawning;
		this.finalWave = finalWave;
		this.headTexture = getTexture(name, false);
		this.footTexture = getTexture(name, true);
	}

	CoffinSpawnerState tickAndGetNext(BlockPos pos, @NotNull CoffinSpawner spawner, BlockState state, ServerLevel level) {
		return switch (this) {
			case INACTIVE -> getInactiveState(pos, spawner, state, level);
			case ACTIVE -> activeTickAndGetNext(this, pos, spawner, state, level);
			case IRRITATED -> activeTickAndGetNext(this, pos, spawner, state, level);
			case AGGRESSIVE -> activeTickAndGetNext(this, pos, spawner, state, level);
			case OMINOUS -> activeTickAndGetNext(this, pos, spawner, state, level);
		};
	}

	static CoffinSpawnerState getStateForPower(ServerLevel level, @NotNull CoffinSpawner coffinSpawner) {
		CoffinSpawnerData coffinSpawnerData = coffinSpawner.getData();
		if (coffinSpawnerData.detectedAnyPlayers()) {
			if (!coffinSpawnerData.isPowerCooldownFinished(level)) {
				if (coffinSpawner.getIrritatedConfig().powerForNextLevel() <= coffinSpawnerData.power) {
					return AGGRESSIVE;
				} else if (coffinSpawner.getNormalConfig().powerForNextLevel() <= coffinSpawnerData.power) {
					return IRRITATED;
				}
			}
			return ACTIVE;
		}
		return INACTIVE;
	}

	private static CoffinSpawnerState getInactiveState(
		BlockPos pos,
		@NotNull CoffinSpawner spawner,
		BlockState state,
		@NotNull ServerLevel level
	) {
		CoffinSpawnerData coffinSpawnerData = spawner.getData();
		if (!coffinSpawnerData.hasMobToSpawn(level, level.random, pos)) {
			return INACTIVE;
		} else {
			Direction direction = CoffinBlock.getConnectedDirection(state);
			coffinSpawnerData.tryDetectPlayers(level, pos, direction, spawner);
			if (spawner.canSpawnApparition(level, pos)) {
				spawner.spawnApparition(level, pos);
			}

			return getStateForPower(level, spawner);
		}
	}

	private static CoffinSpawnerState activeTickAndGetNext(
		CoffinSpawnerState coffinSpawnerState,
		BlockPos pos,
		@NotNull CoffinSpawner spawner,
		BlockState state,
		@NotNull ServerLevel level
	) {
		CoffinSpawnerData coffinSpawnerData = spawner.getData();
		CoffinSpawnerConfig coffinSpawnerConfig = spawner.getConfig();
		if (!coffinSpawnerData.hasMobToSpawn(level, level.random, pos)) {
			return INACTIVE;
		} else {
			Direction direction = CoffinBlock.getConnectedDirection(state);
			coffinSpawnerData.tryDetectPlayers(level, pos, direction, spawner);
			if (!coffinSpawnerData.detectedAnyPlayers()) {
				return INACTIVE;
			}
			int additionalPlayers = coffinSpawnerData.countAdditionalPlayers();

			if (spawner.canSpawnApparition(level, pos)) {
				spawner.spawnApparition(level, pos);
			}

			if (!coffinSpawnerData.isPowerCooldownFinished(level) && coffinSpawnerData.power >= coffinSpawnerConfig.powerForNextLevel()) {
				coffinSpawnerData.powerCooldownEndsAt = level.getGameTime() + (long)spawner.getPowerCooldownLength();
				coffinSpawnerData.power = coffinSpawnerState == AGGRESSIVE ? spawner.getAggressiveConfig().powerForNextLevel() : coffinSpawnerData.power;
				level.playSound(null, pos, TTSounds.COFFIN_INCREASE_POWER, SoundSource.BLOCKS, 0.5F, 0.9F + (level.random.nextFloat() * 0.2F));
				return coffinSpawnerState.getNextPowerState();
			}

			if (coffinSpawnerData.hasFinishedSpawningAllMobs(coffinSpawnerConfig, additionalPlayers)) {
				if (coffinSpawnerData.haveAllCurrentMobsDied()) {
					coffinSpawnerData.totalMobsSpawned = 0;
					if (coffinSpawnerState.finalWave) {
						coffinSpawnerData.nextMobSpawnsAt = 0L;
						coffinSpawnerData.power = 0;
						coffinSpawnerData.powerCooldownEndsAt = 0L;
						long cooldownTime = 36000L;
						coffinSpawnerData.nextApparitionSpawnsAt = level.getGameTime() + cooldownTime;
						if (coffinSpawnerData.haveAllCurrentApparitionsDied()) {
							return INACTIVE;
						}
					}
				}
			} else if (coffinSpawnerData.isReadyToSpawnNextMob(level, coffinSpawnerConfig, additionalPlayers)) {
				spawner.spawnMob(level, pos).ifPresent(uuid -> {
					coffinSpawnerData.currentMobs.add(uuid);
					++coffinSpawnerData.totalMobsSpawned;
					coffinSpawnerData.nextMobSpawnsAt = level.getGameTime() + (long)coffinSpawnerConfig.ticksBetweenSpawn();
					coffinSpawnerData.spawnPotentials().getRandom(level.getRandom()).ifPresent(spawnData -> {
						coffinSpawnerData.nextSpawnData = Optional.of(spawnData.data());
						spawner.markUpdated();
					});
				});
			}
		}
		return coffinSpawnerState;
	}

	public int getLightLevel() {
		return lightLevel;
	}

	public boolean isCapableOfSpawning() {
		return this.isCapableOfSpawning;
	}

	public ResourceLocation getHeadTexture() {
		return this.headTexture;
	}

	public ResourceLocation getFootTexture() {
		return this.footTexture;
	}

	public CoffinSpawnerState getNextPowerState() {
		return switch (this) {
			case INACTIVE -> CoffinSpawnerState.ACTIVE;
			case ACTIVE -> CoffinSpawnerState.IRRITATED;
			case IRRITATED -> CoffinSpawnerState.AGGRESSIVE;
			case AGGRESSIVE -> CoffinSpawnerState.AGGRESSIVE;
			case OMINOUS -> CoffinSpawnerState.OMINOUS;
		};
	}

	@Contract("_, _ -> new")
	private static @NotNull ResourceLocation getTexture(String stateName, boolean foot) {
		return TTConstants.id("textures/entity/coffin/coffin_" + (foot ? "foot_" : "head_") + stateName + ".png");
	}

	@Override
	public @NotNull String getSerializedName() {
		return this.name;
	}
}
