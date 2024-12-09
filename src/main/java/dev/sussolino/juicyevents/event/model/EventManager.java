package dev.sussolino.juicyevents.event.model;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.sussolino.juicyevents.event.Event;
import org.bukkit.entity.Player;

public abstract class EventManager {

    protected final Event event;

    public EventManager(Event event) {
        this.event = event;
    }


    public abstract void starting();
    public abstract void started();
    public abstract void finished();

    public abstract void onReceive(final Player player, final User user, final PacketReceiveEvent packet);
    public abstract void onSend(final Player player, final User user, final PacketSendEvent packet);
}
