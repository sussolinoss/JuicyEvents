package dev.sussolino.juicyevents.event.manager.impl;

import dev.sussolino.juicyapi.item.ItemUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Getter
public final class Kit {

    private final @NotNull ItemStack[] armor;
    private final @NotNull ItemStack[] contents;

    private final ItemStack off;

    public Kit(final FileConfiguration config, final String event) {
        final String path = event + ".kit.";

        this.off = config.getItemStack(path + "off");
        this.armor = ItemUtils.getItemStackList(path + "armor", config);
        this.contents = ItemUtils.getItemStackList(path + "contents", config);
    }

    public void give(final @NotNull Player player) {
        final PlayerInventory inv = player.getInventory();

        inv.setContents(contents);
        inv.setItemInOffHand(off);
        inv.setArmorContents(armor);

        player.updateInventory();
    }
}
