package ru.baronessdev.personal.aidisabler;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public final class AIDisabler extends JavaPlugin {
    public static AIDisabler inst;

    public AIDisabler() {
        inst = this;
    }

    @Override
    public void onEnable() {
        Config.init();

        List<EntityType> blocked = Config.getList("disabled").stream()
                .map(EntityType::valueOf)
                .collect(Collectors.toList());

        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getWorlds().forEach(world -> world.getLivingEntities().stream()
                .filter(ent -> blocked.contains(ent.getType())).forEach(ent -> ent.setAI(false))), 0L, 20L);

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onEntitySpawn(EntitySpawnEvent event) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    entity.setAI(!blocked.contains(entity.getType()));
                }
            }

        }, this);
    }

    @Override
    public void onDisable() {
    }
}
