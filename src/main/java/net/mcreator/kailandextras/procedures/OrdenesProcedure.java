package net.mcreator.kailandextras.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

public class OrdenesProcedure {

    private static final Map<ServerPlayer, Map<String, Boolean>> playerActions = new HashMap<>();
    private static final Map<ServerPlayer, String> currentOrder = new HashMap<>();
    private static final Map<ServerPlayer, Integer> orderTimers = new HashMap<>();
    private static final List<String> availableOrders = new ArrayList<>();
    private static final Random random = new Random();

    private static final int RADIUS = 8;
    private static final int DAMAGE = 10;
    private static final int TIME_LIMIT = 100;

    static {
        availableOrders.add("\u00A7bAgacharte");
        availableOrders.add("\u00A7aMoverte");
        availableOrders.add("\u00A79Pegar al aire (clic izquierdo)");
        availableOrders.add("\u00A7eSaltar");
        availableOrders.add("\u00A76Lanzar un ítem");
        availableOrders.add("\u00A7dAbrir el inventario");
        availableOrders.add("\u00A7cEscribir un mensaje en el chat");
    }

    public OrdenesProcedure() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean execute(Level world, double x, double y, double z, ServerPlayer commander) {
        if (world.isClientSide() || !(world instanceof ServerLevel)) return false;
        ServerLevel serverWorld = (ServerLevel) world;

        List<ServerPlayer> playersInRange = serverWorld.players().stream()
            .filter(player -> player != commander)
            .filter(player -> player.distanceToSqr(commander) <= Math.pow(RADIUS, 2))
            .collect(Collectors.toList());

        if (playersInRange.isEmpty()) {
            return false;
        }

        String order = generateRandomOrder();
        for (ServerPlayer player : playersInRange) {
            sendOrderTitle(player, order);
            player.playSound(SoundEvents.NOTE_BLOCK_PLING, 1.0F, 1.0F);
            currentOrder.put(player, order);
            Map<String, Boolean> actions = new HashMap<>();
            availableOrders.forEach(action -> actions.put(action, false));
            playerActions.put(player, actions);
            orderTimers.put(player, 0);
            sendConfusionParticles(player);
        }

        return true;
    }

    private static void sendConfusionParticles(ServerPlayer player) {
        Vec3 pos = player.position();
        AABB aabb = player.getBoundingBox();
        ((ServerLevel) player.level).sendParticles(
            ParticleTypes.CLOUD,
            pos.x, pos.y + aabb.getYsize() / 2, pos.z,
            20, aabb.getXsize() / 2, aabb.getYsize() / 2, aabb.getZsize() / 2, 0.2
        );
    }

    private static String generateRandomOrder() {
        List<String> remainingOrders = new ArrayList<>(availableOrders);
        
        currentOrder.values().forEach(remainingOrders::remove);

        if (remainingOrders.isEmpty()) {
            remainingOrders = new ArrayList<>(availableOrders);
        }

        return remainingOrders.get(random.nextInt(remainingOrders.size()));
    }

    private static void sendOrderTitle(ServerPlayer player, String order) {
        player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(order)));
        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 100, 10));
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        ServerPlayer player = (ServerPlayer) event.player;

        if (orderTimers.containsKey(player)) {
            int currentTime = orderTimers.get(player);
            currentTime++;

            if (currentTime % 20 == 0) {
                player.displayClientMessage(Component.literal("\u00A7cTiempo restante: " + ((TIME_LIMIT - currentTime) / 20) + " segundos"), true);
            }

            if (currentTime >= TIME_LIMIT) {
                Map<String, Boolean> actions = playerActions.get(player);
                String order = currentOrder.get(player);
                boolean fulfilled = actions.getOrDefault(order, false);
                
                if (!fulfilled) {
                    player.hurt(DamageSource.MAGIC, DAMAGE);
                    player.sendSystemMessage(Component.literal("Fallaste la orden y recibiste 5 corazones de daño."));
                } else {
                    player.sendSystemMessage(Component.literal("Cumpliste la orden correctamente."));
                }

                currentOrder.remove(player);
                playerActions.remove(player);
                orderTimers.remove(player);
            } else {
                orderTimers.put(player, currentTime);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerSneak(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            Map<String, Boolean> actions = playerActions.get(player);
            if (actions != null && !actions.get("agacharte")) {
                if (player.isShiftKeyDown()) {
                    actions.put("agacharte", true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerTickEvent event) {
        ServerPlayer player = (ServerPlayer) event.player;
        Map<String, Boolean> actions = playerActions.get(player);
        if (actions != null && !actions.get("moverte")) {
            if (player.distanceToSqr(player.xOld, player.yOld, player.zOld) > 0.01) {
                actions.put("moverte", true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            Map<String, Boolean> actions = playerActions.get(player);
            if (actions != null && !actions.get("saltar")) {
                actions.put("saltar", true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        Map<String, Boolean> actions = playerActions.get(player);
        if (actions != null && !actions.get("pegar al aire (clic izquierdo)")) {
            actions.put("pegar al aire (clic izquierdo)", true);
        }
    }

    @SubscribeEvent
    public void onPlayerThrowItem(PlayerInteractEvent.RightClickItem event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        Map<String, Boolean> actions = playerActions.get(player);
        if (actions != null && !actions.get("lanzar un ítem")) {
            actions.put("lanzar un ítem", true);
        }
    }
}
