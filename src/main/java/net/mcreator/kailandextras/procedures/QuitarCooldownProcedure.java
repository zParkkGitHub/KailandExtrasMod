package net.mcreator.kailandextras.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class QuitarCooldownProcedure {

    public static void execute(ServerPlayer player) {
        if (player != null) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                Item item = itemStack.getItem();
                
                if (player.getCooldowns().isOnCooldown(item)) {
                    player.getCooldowns().removeCooldown(item);
                }
            }

            player.sendSystemMessage(Component.literal("Cooldowns eliminados de todos los ítems en tu inventario."));
        }
    }
}
