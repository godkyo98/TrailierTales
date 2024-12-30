package net.frozenblock.trailiertales.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.trailiertales.block.CoffinBlock;
import net.frozenblock.trailiertales.block.entity.coffin.CoffinBlockEntity;
import net.frozenblock.trailiertales.block.entity.coffin.CoffinSpawnerState;
import net.frozenblock.trailiertales.block.impl.CoffinPart;
import net.frozenblock.trailiertales.client.TTModelLayers;
import net.frozenblock.trailiertales.client.model.CoffinModel;
import net.frozenblock.trailiertales.registry.TTBlockEntityTypes;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CoffinRenderer implements BlockEntityRenderer<CoffinBlockEntity> {
	private final CoffinModel headModel;
	private final CoffinModel footModel;

	public CoffinRenderer(@NotNull Context context) {
		this(context.getModelSet());
	}

	public CoffinRenderer(@NotNull EntityModelSet entityModelSet) {
		this.headModel = new CoffinModel(entityModelSet.bakeLayer(TTModelLayers.COFFIN_HEAD));
		this.footModel = new CoffinModel(entityModelSet.bakeLayer(TTModelLayers.COFFIN_FOOT));
	}

	@NotNull
	public static ResourceLocation getCoffinTexture(@NotNull CoffinPart part, CoffinSpawnerState state, boolean ominous) {
		return part == CoffinPart.HEAD ? state.getHeadTexture() : state.getFootTexture();
	}

	@Override
	public void render(
		@NotNull CoffinBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
	) {
		Level level = blockEntity.getLevel();
		if (level != null) {
			float openProg = blockEntity.getOpenProgress(partialTick);
			openProg = 1F - openProg;
			openProg = 1F - openProg * openProg * openProg;

			BlockState blockState = blockEntity.getBlockState();
			DoubleBlockCombiner.NeighborCombineResult<? extends CoffinBlockEntity> neighborCombineResult = DoubleBlockCombiner.combineWithNeigbour(
				TTBlockEntityTypes.COFFIN,
				CoffinBlock::getBlockType,
				CoffinBlock::getConnectedDirection,
				CoffinBlock.FACING,
				blockState,
				level,
				blockEntity.getBlockPos(),
				(world, pos) -> false
			);
			int i = neighborCombineResult.apply(new BrightnessCombiner<>()).get(packedLight);
			CoffinPart part = blockState.getValue(CoffinBlock.PART);
			CoffinSpawnerState coffinSpawnerState = blockState.getValue(CoffinBlock.STATE);
			float f = blockState.getValue(CoffinBlock.FACING).toYRot();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			poseStack.mulPose(Axis.YP.rotationDegrees(-f));
			poseStack.translate(-0.5D, -0.5D, -0.5D);
			this.renderPiece(
				poseStack,
				buffer,
				part == CoffinPart.HEAD ? this.headModel : this.footModel,
				getCoffinTexture(part, coffinSpawnerState, false),
				null,
				openProg,
				i,
				packedOverlay,
				false
			);
		}
	}

	public void renderInHand(
		@NotNull PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, ResourceLocation headTexture, ResourceLocation footTexture, float openness
	) {
		this.headModel.setupAnim(openness);
		this.footModel.setupAnim(openness);
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(-Direction.SOUTH.toYRot()));
		poseStack.translate(-0.5D, -0.5D, -0.5D);
		this.renderPiece(poseStack, buffer, this.headModel, headTexture, null, 0F, packedLight, packedOverlay, false);
		this.renderPiece(poseStack, buffer, this.footModel, footTexture, null, 0F, packedLight, packedOverlay, true);
	}

	private void renderPiece(
		@NotNull PoseStack poseStack,
		MultiBufferSource bufferSource,
		@NotNull CoffinModel model,
		@NotNull ResourceLocation texture,
		@Nullable ResourceLocation glowingTexture,
		float openProgress,
		int packedLight,
		int packedOverlay,
		boolean foot
	) {
		if (foot) {
			poseStack.pushPose();
			poseStack.translate(0F, 0F, -1F);
		}
		model.setupAnim(openProgress * 1.5707964F);
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
		model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
		if (glowingTexture != null) {
			VertexConsumer glowingConsumer = bufferSource.getBuffer(RenderType.eyes(glowingTexture));
			model.renderToBuffer(poseStack, glowingConsumer, packedLight, packedOverlay);
		}
		if (foot) {
			poseStack.popPose();
		}
	}
}
