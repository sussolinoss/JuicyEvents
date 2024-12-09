package dev.sussolino.juicyevents.event.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorder;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.model.EventManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CrystalEventManager extends EventManager {

    private final int start_range;
    private final int bedrock_range;

    private final double centerX, centerZ;

    public CrystalEventManager(Event event) {
        super(event);
        this.start_range = 150;
        this.bedrock_range = 25;

        this.centerX = event.getPlayerProcessor().getSpawn().x();
        this.centerZ = event.getPlayerProcessor().getSpawn().z();

        final WrapperPlayServerWorldBorder wrap = new WrapperPlayServerWorldBorder(
                centerX, centerZ,
                start_range, bedrock_range,
                0,
                0,
                0,
                0);

        this.event.getPlayerProcessor().sendPacketAll(wrap);
    }

    @Override
    public void starting() {

    }

    @Override
    public void started() {
        new BukkitRunnable() {
            int seconds = start_range;
            @Override
            public void run() {
                if (seconds-- == bedrock_range) {
                    event.getTimeProcessor().sendTitle("&cBEDROCK!", "MIU MIUUU");
                    //TODO break blocks for bedrock state
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(event.getPlugin(), 20L, 20L);

        final WrapperPlayServerWorldBorder wrap = new WrapperPlayServerWorldBorder(
                centerX, centerZ,
                start_range, bedrock_range,
                1000 * 3,
                1,
                10,
                10);

        this.event.getPlayerProcessor().sendPacketAll(wrap);
    }

    @Override
    public void finished() {

    }

    @Override
    public void onReceive(Player player, User user, PacketReceiveEvent packet) {

    }

    @Override
    public void onSend(Player player, User user, PacketSendEvent packet) {

    }
}
