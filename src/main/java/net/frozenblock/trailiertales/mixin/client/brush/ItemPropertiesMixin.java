package net.frozenblock.trailiertales.mixin.client.brush;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.trailiertales.config.ItemConfig;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemProperties.class)
public class ItemPropertiesMixin {

	@ModifyExpressionValue(
		method = "method_49351(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/LivingEntity;I)F",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;getUseItem()Lnet/minecraft/world/item/ItemStack;"
		)
	)
	private static ItemStack trailierTales$useSmoothBrushingAnim(ItemStack original) {
		if (ItemConfig.SMOOTH_BRUSH_ANIMATION) {
			return ItemStack.EMPTY;
		}
		return original;
	}
}
