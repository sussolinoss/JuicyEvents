package dev.sussolino.juicyevents.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ICommand implements CommandExecutor, TabCompleter {

    protected final JavaPlugin plugin;

    public ICommand(final JavaPlugin manager) {
        this.plugin = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) command(p, args);

        return false;
    }

    /**
     *
     *    Void
     *
     */

    public abstract void command(final Player p, final String[] args);
    public abstract List<String> tab(final Player p, final String[] args);

    /**
     *
     *    Tab Complete
     *
     */

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return List.of();
        return tab(p, args);
    }

    /**
     *
     *    Register
     *
     */

    public void register(final String commandName, boolean tabComplete) {
        this.plugin.getLogger().info("[?] Registering command: " + commandName);

        final PluginCommand command = this.plugin.getCommand(commandName);

        assert command != null;

        command.setExecutor(this);
        if (tabComplete) command.setTabCompleter(this);
    }
}
