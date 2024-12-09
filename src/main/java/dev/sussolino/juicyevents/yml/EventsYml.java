package dev.sussolino.juicyevents.yml;

import dev.sussolino.juicyapi.file.BukkitFile;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class EventsYml extends BukkitFile {

    public EventsYml(JavaPlugin plugin) {
        super("events", plugin);
    }

    public Location getSpawn(final String event) {
        return getConfig().getLocation(event + ".spawn");
    }
    public Location getLobby(final String event) {
        return getConfig().getLocation(event + ".lobby");
    }

    public boolean exist(final String eventName) {
        return getEvents().contains(eventName);
    }

    public void removeEvent(final String event) {
        final List<String> events = new ArrayList<>(getEvents());
        events.remove(event);
        getConfig().set("events", events);
        reload();
    }

    public void addEvent(final String event) {
        final List<String> events = new ArrayList<>(getEvents());
        if (!events.contains(event)) {
            events.add(event);
            getConfig().set("events", events);
            reload();
        }
    }

    public List<String> getEvents() {
        return getConfig().getStringList("events");
    }

}
