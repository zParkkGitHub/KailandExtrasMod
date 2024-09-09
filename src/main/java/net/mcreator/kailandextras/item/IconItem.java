
package net.mcreator.kailandextras.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class IconItem extends Item {
	public IconItem() {
		super(new Item.Properties().tab(null).stacksTo(64).rarity(Rarity.RARE));
	}
}
