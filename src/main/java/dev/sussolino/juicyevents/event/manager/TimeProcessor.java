package dev.sussolino.juicyevents.event.manager;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.sussolino.juicyapi.color.ColorUtils;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.model.EventManager;
import dev.sussolino.juicyevents.event.model.EventState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class TimeProcessor extends EventManager {

    private final PlayerProcessor playerProcessor;

    public TimeProcessor(Event event) {
        super(event);
        this.playerProcessor = event.getPlayerProcessor();
    }

    private List<Player> getPlayers() {
        return this.playerProcessor.getPlayers();
    }

    final AtomicInteger seconds = new AtomicInteger(11);

    @Override
    public void starting() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (seconds.getAndDecrement() == 0) {

                    event.setState(EventState.STARTED);

                    sendTitle("&aGO!", "");
                    sendSound(Sound.ENTITY_ENDERMAN_SCREAM);

                    cancel();
                    return;
                }

                sendSound(Sound.BLOCK_AMETHYST_BLOCK_HIT);
                sendTitle("&eStarting...", "&7%s&fs".formatted(seconds.get()));
            }
        }.runTaskTimer(event.getPlugin(), 20, 20);
    }

    /**
     *
     *    Utils
     *
     */

    public void sendTitle(final String title, final String subtitle) {
        getPlayers().forEach(player -> player.showTitle(
                Title.title(
                        Component.text(ColorUtils.color(title)), Component.text(ColorUtils.color(subtitle)))));
    }
    public void sendSound(final Sound sound) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1, 1));
    }

    public String getTime() {
        final int minutes = seconds.get() / 60;
        final int hours = minutes / 60;
        return "%02d:%02d:%02d".formatted(hours, minutes, seconds.get());
    }

    /**
     *
     *    States
     *
     */

    @Override
    public void started() {
        new BukkitRunnable() {

            @Override
            public void run() {
                seconds.getAndIncrement();
            }
        }.runTaskTimer(event.getPlugin(), 20, 20);
    }

    @Override
    public void finished() {

    }





    @Override
    public void onReceive(Player player, User user, PacketReceiveEvent packet) {}

    @Override
    public void onSend(Player player, User user, PacketSendEvent packet) {}
}

