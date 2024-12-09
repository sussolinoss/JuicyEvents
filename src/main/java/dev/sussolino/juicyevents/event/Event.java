package dev.sussolino.juicyevents.event;

import com.github.retrooper.packetevents.event.PacketListener;
import dev.sussolino.juicyapi.world.WorldUtils;
import dev.sussolino.juicyevents.Juicy;
import dev.sussolino.juicyevents.event.manager.PacketsProcessor;
import dev.sussolino.juicyevents.event.manager.PlayerProcessor;
import dev.sussolino.juicyevents.event.manager.TimeProcessor;
import dev.sussolino.juicyevents.event.model.EventManager;
import dev.sussolino.juicyevents.event.model.EventState;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Event implements PacketListener {

    private EventState state = EventState.ENABLED;

    private final String name;
    private final Juicy instance;
    private final JavaPlugin plugin;


    private final PlayerProcessor playerProcessor;
    private final TimeProcessor timeProcessor;

    private final PacketsProcessor eventManager;

    private final List<EventManager> processors = new ArrayList<>();


    public Event(final Juicy instance, final String name) {
        this.name = name;
        this.instance = instance;
        this.plugin = instance.getPlugin();

        this.processors.addAll(List.of(
                this.playerProcessor = new PlayerProcessor(this),
                this.timeProcessor = new TimeProcessor(this)));

        this.eventManager = new PacketsProcessor(this);
    }

    public World getWorld() {
        return WorldUtils.copyWorld(instance.getEventsYml().getSpawn(name).getWorld(), name + "-ily");
    }

    public void setState(final EventState state) {
        if (this.state == state) return;
        if (this.state == EventState.STARTED && state == EventState.STARTING) return;

        switch (state) {
            case STARTED -> this.processors.forEach(EventManager::started);
            case STARTING -> this.processors.forEach(EventManager::starting);
            case FINISHED -> {
                this.processors.forEach(EventManager::finished);
                WorldUtils.unloadWorld(this.getWorld());
            }
        }

        this.state = state;
    }

    public void register() {
        this.instance.getEventsYml().addEvent(name);
    }

    public void addProcessors(final EventManager... processors) {
        this.processors.addAll(Arrays.stream(processors).toList());
    }
}
