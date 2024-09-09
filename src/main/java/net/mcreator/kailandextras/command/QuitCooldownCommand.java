package net.mcreator.kailandextras.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.Commands;

import net.mcreator.kailandextras.procedures.QuitarCooldownProcedure;

@Mod.EventBusSubscriber
public class QuitCooldownCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("quitcooldown").requires(s -> s.hasPermission(3)).executes(arguments -> {
            ServerLevel world = arguments.getSource().getLevel();
            Entity entity = arguments.getSource().getEntity();
            
            //
            if (entity == null)
                entity = FakePlayerFactory.getMinecraft(world);

            //
            if (entity instanceof ServerPlayer player) {
                //
                QuitarCooldownProcedure.execute(player);
            }

            return 0;
        }));
    }
}
