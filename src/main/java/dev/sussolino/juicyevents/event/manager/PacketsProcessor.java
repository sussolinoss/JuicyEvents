package dev.sussolino.juicyevents.event.manager;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.model.EventManager;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketsProcessor implements PacketListener {

    private final Event event;

    public PacketsProcessor(Event event) {
        this.event = event;
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    private List<EventManager> getManagers() {
        return this.event.getProcessors();
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        this.getManagers().forEach(manager -> manager.onReceive(player, event.getUser(), event));
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        this.getManagers().forEach(manager -> manager.onSend(player, event.getUser(), event));
    }
}
