package net.mcreator.kailandextras.item;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;

import net.mcreator.kailandextras.init.KailandextrasModTabs;
import net.mcreator.kailandextras.procedures.OrdenesProcedure;

import java.util.List;

public class VaritaDelDictadoItem extends Item {
    public VaritaDelDictadoItem() {
        super(new Item.Properties().tab(KailandextrasModTabs.TAB_KAILAND_EXTRAS).durability(500).fireResistant().rarity(Rarity.EPIC));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getEnchantmentValue() {
        return 19;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(Component.literal("\u00A75Al hacer click derecho da ordenes a los jugadores en un radio de 8x8. Si fallan la orden, \u00A74pierden vida."));
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        ItemStack itemstack = event.getItemStack();
        InteractionHand hand = event.getHand();

        if (world.isClientSide()) return;

        if (itemstack.getItem() instanceof VaritaDelDictadoItem) {
            if (hand == InteractionHand.MAIN_HAND || hand == InteractionHand.OFF_HAND) {
                Vec3 playerPos = player.position();
                if (player instanceof ServerPlayer) {
                    boolean aplicarCooldown = OrdenesProcedure.execute(world, playerPos.x, playerPos.y, playerPos.z, (ServerPlayer) player);
                    if (aplicarCooldown) {
                        player.getCooldowns().addCooldown(this, 140);
                    }
                }

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
