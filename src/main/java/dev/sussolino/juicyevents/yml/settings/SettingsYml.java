package dev.sussolino.juicyevents.yml.settings;

import dev.sussolino.juicyapi.file.BukkitFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SettingsYml extends BukkitFile {

    public SettingsYml(JavaPlugin plugin) {
        super("settings", plugin);
    }

    public List<String> getFallBacks() {
        return getConfig().getStringList("fallback");
    }
}
