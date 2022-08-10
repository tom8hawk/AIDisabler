package ru.baronessdev.personal.aidisabler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public final class AIDisabler extends JavaPlugin {
    public static AIDisabler inst;
    private static final Map<UUID, List<LivingEntity>> previous = new HashMap<>();

    public AIDisabler() {
        inst = this;
    }

    @Override
    public void onEnable() {
        Config.init();

        List<EntityType> blocked = Config.getList("disabled").stream()
                .map(EntityType::valueOf)
                .collect(Collectors.toList());

        Bukkit.getWorlds().forEach(world -> world.getLivingEntities().stream()
                        .filter(ent -> blocked.contains(ent.getType())).forEach(ent -> ent.setAI(false)));

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onEntitySpawn(EntitySpawnEvent event) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    entity.setAI(!blocked.contains(entity.getType()));
                }
            }

            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                Location toLoc = event.getTo();

                if (toLoc != null) {
                    Block toBlock = toLoc.getBlock();
                    Block fromBlock = event.getFrom().getBlock();

                    if (!toBlock.equals(fromBlock)) {
                        UUID uuid = event.getPlayer().getUniqueId();

                        if (previous.containsKey(uuid))
                            previous.get(uuid).forEach(living -> living.setAI(false));

                        List<LivingEntity> now = new ArrayList<>();
                        toBlock.getWorld().getNearbyEntities(toBlock.getLocation(), 15, 15, 15, ent -> blocked.contains(ent.getType())).forEach(entity -> {
                            if (entity instanceof LivingEntity) {
                                LivingEntity living = (LivingEntity) entity;
                                living.setAI(true);

                                now.add(living);
                            }
                        });

                        previous.put(uuid, now);
                    }
                }
            }
        }, this);
    }

    @Override
    public void onDisable() {
    }
}
