/*
 * Copyright 2025 FrozenBlock
 * This file is part of Trailier Tales.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.trailiertales.mixin.client.boat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.trailiertales.impl.client.AbstractBoatRendererInterface;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RaftRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(RaftRenderer.class)
public class RaftRendererMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	public void trailierTales$init(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation, CallbackInfo info) {
		if (AbstractBoatRenderer.class.cast(this) instanceof AbstractBoatRendererInterface abstractBoatRendererInterface) {
			abstractBoatRendererInterface.trailierTales$setBannerBaseTexture(
				modelLayerLocation.model().withPath((string) -> {
					string = string.substring(Math.max(0, string.indexOf("/")));
					return "textures/entity/boat/banner_base/" + string + ".png";
				})
			);
			abstractBoatRendererInterface.trailierTales$setRaft(true);
		}
	}
}
