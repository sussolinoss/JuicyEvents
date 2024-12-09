package dev.sussolino.juicyevents.event.manager;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.manager.impl.Kit;
import dev.sussolino.juicyevents.event.model.EventManager;
import dev.sussolino.juicyevents.event.model.EventState;
import dev.sussolino.juicyevents.yml.EventsYml;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public final class PlayerProcessor extends EventManager implements PacketListener {

    private final Kit kit;
    private final Location spawn;
    private final Location lobby;
    private final List<Player> deaths;
    private final List<Player> players;

    private final List<String> fallbacks;
    private final Random yech = new Random();

    private final int max;

    public PlayerProcessor(Event event) {
        super(event);
        this.deaths = new ArrayList<>();
        this.players = new ArrayList<>();

        final EventsYml yml = event.getInstance().getEventsYml();
        final Location spawn = yml.getSpawn(event.getName());
        final Location lobby = yml.getLobby(event.getName());

        this.spawn = new Location(event.getWorld(), spawn.x(), spawn.y(), spawn.z());
        this.lobby = new Location(event.getWorld(), lobby.x(), lobby.y(), lobby.z());

        this.kit = new Kit(yml.getConfig(), event.getName());

        this.fallbacks = event.getInstance().getSettingsYml().getFallBacks();
        this.max = (this.fallbacks.size() - 1) == 0 ? -1 : this.fallbacks.size() - 1;
    }

    /**
     *
     *   Actions Denied || Event State
     *
     */

    public void onSend(final Player p, final User user, final PacketSendEvent e) {}
    public void onReceive(final Player p, final User user, final PacketReceiveEvent e) {}

    /**
     *
     *   Event State Manager
     *
     */

    @Override
    public void starting() {}

    @Override
    public void started() {
        this.players.forEach(player -> player.setGameMode(GameMode.SURVIVAL));
    }

    @Override
    public void finished() {
        this.players.forEach(this::sendFallback);
    }

    /**
     *
     *   Player State Manager
     *
     */

    public void addPlayer(final Player player) {
        final EventState state = this.event.getState();
        final boolean rejoin = this.players.contains(player) && state.equals(EventState.STARTED) && this.deaths.contains(player);

        if (rejoin || event.getState().equals(EventState.STARTED)) {
            this.players.add(player);
            player.teleport(this.spawn);
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        this.kit.give(player);
        this.players.add(player);
        player.teleport(this.spawn);
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void sendPacket(final Player player, final PacketWrapper<?> packet) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
    public void sendPacketAll(final PacketWrapper<?> packet) {
        this.players.forEach(player -> sendPacket(player, packet));
    }

    public void sendFallback(Player player) {
        if (this.fallbacks.isEmpty()) {
            player.teleport(this.lobby);
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(this.fallbacks.get(this.yech.nextInt(max + 1)));

        player.sendPluginMessage(this.event.getPlugin(), "BungeeCord", out.toByteArray());
    }
}

