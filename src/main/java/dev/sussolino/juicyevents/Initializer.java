package dev.sussolino.juicyevents;

import com.github.retrooper.packetevents.PacketEvents;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.impl.CrystalEvent;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Initializer extends JavaPlugin {

    public static Juicy instance;
    private final List<Event> events = new ArrayList<>();

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        (instance = new Juicy(this)).register();

        this.events.add(new CrystalEvent(instance));
        this.events.forEach(Event::register);
        
        PacketEvents.getAPI().init();
    }
    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        HandlerList.unregisterAll(this);
    }
}
