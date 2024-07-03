package net.frozenblock.trailiertales.entity;

import com.mojang.serialization.Dynamic;
import net.frozenblock.trailiertales.entity.ai.GhostAi;
import net.frozenblock.trailiertales.registry.RegisterEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// a fucking ghost
public class Ghost extends Monster {

	public Ghost(EntityType<? extends Ghost> entityType, Level world) {
		super(entityType, world);
		this.moveControl = new FlyingMoveControl(this, 20, true);
	}

	@NotNull
	public static AttributeSupplier.Builder createGhostAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 10.0)
			.add(Attributes.FLYING_SPEED, 0.5F)
			.add(Attributes.MOVEMENT_SPEED, 0.5F)
			.add(Attributes.ATTACK_DAMAGE, 3.0);
	}

	@Override
	@NotNull
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level);
		flyingPathNavigation.setCanFloat(true);
		flyingPathNavigation.setCanOpenDoors(false);
		flyingPathNavigation.setCanPassDoors(true);
		return flyingPathNavigation;
	}

	@Override
	public void tick() {
		this.noPhysics = true;
		super.tick();
		this.noPhysics = false;
		this.setNoGravity(true);
	}

	@Override
	@NotNull
	protected Brain.Provider<Ghost> brainProvider() {
		return Brain.provider(GhostAi.MEMORY_TYPES, GhostAi.SENSOR_TYPES);
	}

	@Override
	@NotNull
	protected Brain<Ghost> makeBrain(Dynamic<?> dynamic) {
		return GhostAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
	}

	@SuppressWarnings("unchecked")
	@Override
	@NotNull
	public Brain<Ghost> getBrain() {
		return (Brain<Ghost>) super.getBrain();
	}

	@Override
	protected void customServerAiStep() {
		this.level().getProfiler().push("ghostBrain");
		this.getBrain().tick((ServerLevel) this.level(), this);
		this.level().getProfiler().pop();
		this.level().getProfiler().push("ghostActivityUpdate");
		GhostAi.updateActivity(this);
		this.level().getProfiler().pop();
		super.customServerAiStep();
	}

	@Contract("null->false")
	public boolean canTargetEntity(@Nullable Entity entity) {
		return entity instanceof LivingEntity livingEntity
			&& this.level() == livingEntity.level()
			&& !this.level().getDifficulty().equals(Difficulty.PEACEFUL)
			&& EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)
			&& !this.isAlliedTo(livingEntity)
			&& livingEntity.getType() != EntityType.ARMOR_STAND
			&& livingEntity.getType() != RegisterEntities.GHOST
			&& !livingEntity.isInvulnerable()
			&& !livingEntity.isDeadOrDying()
			&& !livingEntity.isRemoved()
			&& this.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
	}
}
