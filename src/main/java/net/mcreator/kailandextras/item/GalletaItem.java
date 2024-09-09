package net.mcreator.kailandextras.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.mcreator.kailandextras.init.KailandextrasModTabs;

public class GalletaItem extends Item {
    public GalletaItem() {
        super(new Item.Properties()
            .tab(KailandextrasModTabs.TAB_KAILAND_EXTRAS)
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                .nutrition(3)
                .saturationMod(0.5f)
                .alwaysEat()
                .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            // Postres
            player.connection.disconnect(Component.literal("Has sido desconectado por comer una galleta troll."));
        }
        return result;
    }
}