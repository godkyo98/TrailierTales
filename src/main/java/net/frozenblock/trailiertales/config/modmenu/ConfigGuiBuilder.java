package net.frozenblock.trailiertales.config.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.trailiertales.TTConstants;
import net.frozenblock.trailiertales.config.TTBlockConfig;
import net.frozenblock.trailiertales.config.TTEntityConfig;
import net.frozenblock.trailiertales.config.TTItemConfig;
import net.frozenblock.trailiertales.config.TTMiscConfig;
import net.frozenblock.trailiertales.config.TTWorldgenConfig;
import net.frozenblock.trailiertales.config.gui.TTBlockConfigGui;
import net.frozenblock.trailiertales.config.gui.TTEntityConfigGui;
import net.frozenblock.trailiertales.config.gui.TTItemConfigGui;
import net.frozenblock.trailiertales.config.gui.TTMiscConfigGui;
import net.frozenblock.trailiertales.config.gui.TTWorldgenConfigGui;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ConfigGuiBuilder {

	public static Screen buildScreen(@NotNull Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(TTConstants.text("component.title"));

		configBuilder.setSavingRunnable(() -> {
			TTWorldgenConfig.INSTANCE.save();
			TTBlockConfig.INSTANCE.save();
			TTEntityConfig.INSTANCE.save();
			TTItemConfig.INSTANCE.save();
			TTMiscConfig.INSTANCE.save();
		});

		ConfigEntryBuilder entryBuilder = configBuilder.getEntryBuilder();

		var block = configBuilder.getOrCreateCategory(TTConstants.text("block"));
		TTBlockConfigGui.setupEntries(block, entryBuilder);

		var item = configBuilder.getOrCreateCategory(TTConstants.text("item"));
		TTItemConfigGui.setupEntries(item, entryBuilder);

		var entity = configBuilder.getOrCreateCategory(TTConstants.text("entity"));
		TTEntityConfigGui.setupEntries(entity, entryBuilder);

		var worldgen = configBuilder.getOrCreateCategory(TTConstants.text("worldgen"));
		TTWorldgenConfigGui.setupEntries(worldgen, entryBuilder);

		var misc = configBuilder.getOrCreateCategory(TTConstants.text("misc"));
		TTMiscConfigGui.setupEntries(misc, entryBuilder);

		return configBuilder.build();
	}
}
