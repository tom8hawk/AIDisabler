package ru.baronessdev.personal.aidisabler;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.baronessdev.personal.aidisabler.AIDisabler.inst;

public final class Config {
    private static final YamlConfiguration configuration = new YamlConfiguration();

    public static void init() {
        String path = inst.getDataFolder() + File.separator + "config.yml";
        File file = new File(path);

        if (!file.exists())
            inst.saveResource("config.yml", true);

        try {
            configuration.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getList(String path) {
        return configuration.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }
}
