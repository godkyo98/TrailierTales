/*
 * Copyright 2025 FrozenBlock
 * This file is part of Trailier Tales.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.trailiertales.mixin.common.ectoplasm_block;

import net.frozenblock.trailiertales.impl.InEctoplasmBlockInterface;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements InEctoplasmBlockInterface {

	@Unique
	private boolean trailierTales$clipInEctoplasm;

	@Unique
	@Override
	public void trailierTales$setClipInEctoplasm(boolean clipInEctoplasm) {
		this.trailierTales$clipInEctoplasm = clipInEctoplasm;
	}

	@Unique
	@Override
	public boolean trailierTales$wasClipInEctoplasm() {
		return this.trailierTales$clipInEctoplasm;
	}
}
