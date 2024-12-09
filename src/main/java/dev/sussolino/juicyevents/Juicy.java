package dev.sussolino.juicyevents;

import dev.sussolino.juicyevents.commands.ICommand;
import dev.sussolino.juicyevents.commands.impl.EventCommand;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.impl.CrystalEvent;
import dev.sussolino.juicyevents.yml.EventsYml;
import dev.sussolino.juicyevents.yml.settings.SettingsYml;
import dev.sussolino.socket.netty.NettyClient;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class Juicy {

    private final JavaPlugin plugin;
    private final NettyClient client;

    private final EventsYml eventsYml;
    private final SettingsYml settingsYml;

    private final List<ICommand> commands;


    @SneakyThrows
    public Juicy(JavaPlugin plugin) {
        this.plugin = plugin;

        this.eventsYml = new EventsYml(plugin);
        this.settingsYml = new SettingsYml(plugin);

        this.client = new NettyClient("127.0.0.1", 4095);

        this.commands = List.of(new EventCommand(this));
    }

    @SneakyThrows
    public void register() {
        this.client.start();
        this.commands.forEach(command -> command.register(command
                .getClass()
                .getSimpleName()
                .toLowerCase()
                .replace("command", ""), true));
    }
}
