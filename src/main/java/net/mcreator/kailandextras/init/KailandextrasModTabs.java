
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.kailandextras.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

public class KailandextrasModTabs {
	public static CreativeModeTab TAB_KAILAND_EXTRAS;

	public static void load() {
		TAB_KAILAND_EXTRAS = new CreativeModeTab("tabkailand_extras") {
			@Override
			public ItemStack makeIcon() {
				return new ItemStack(KailandextrasModItems.ICON.get());
			}

			@Override
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
}
