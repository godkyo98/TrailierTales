package net.lunade.onetwenty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NonFallingBrushableBlock extends BrushableBlock {

	public NonFallingBrushableBlock(Block block, Properties properties, SoundEvent soundEvent, SoundEvent soundEvent2) {
		super(block, properties, soundEvent, soundEvent2);
	}

	@Override
	public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
		BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
		if (blockEntity instanceof BrushableBlockEntity brushableBlockEntity) {
			brushableBlockEntity.checkReset();
		}
	}

	@Override
	public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {

	}

}
