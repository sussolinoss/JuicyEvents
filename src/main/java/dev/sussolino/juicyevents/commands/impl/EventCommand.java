package dev.sussolino.juicyevents.commands.impl;

import dev.sussolino.juicyevents.Juicy;
import dev.sussolino.juicyevents.commands.ICommand;
import dev.sussolino.juicyevents.event.Event;
import dev.sussolino.juicyevents.event.model.EventState;
import dev.sussolino.juicyevents.yml.settings.Settings;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventCommand extends ICommand {

    private final Juicy instance;
    private final Map<String, Event> events = new HashMap<>();

    public EventCommand(Juicy instance) {
        super(instance.getPlugin());
        this.instance = instance;
    }

    @Override
    public void command(Player p, String[] args) {
        if (args.length == 0) {
            //TODO GUI
            return;
        }

        if (args.length == 1) return;

        String sub = args[0];
        String eventName = args[1];

        if (!this.instance.getEventsYml().exist(eventName) && !sub.equals("create") && !sub.equals("settings")) {
            p.sendMessage(Settings.EVENT_NOT_EXIST.getAsString("%event%", eventName));
            return;
        }

        /*          [Basic]
         *  event create/delete eventName
         *  event start eventName
         *  event stop eventName
         *  event enable eventName
         */

        if (args.length == 2) {
            switch (sub) {
                case "join" -> {
                    final Event event = events.get(eventName);

                    if (event == null) {
                        p.sendMessage(Settings.EVENT_NOT_ENABLED.getAsString("%event%", eventName));
                        return;
                    }

                    if (event.getState().equals(EventState.FINISHED)) {
                        p.sendMessage(Settings.EVENT_ALREADY_FINISHED.getAsString("%event%", eventName));
                        return;
                    }

                    p.sendMessage(Settings.EVENT_JOIN.getAsString("%event%", eventName));

                    event.getPlayerProcessor().addPlayer(p);
                }
                case "create" -> {
                    final boolean alreadyExist = this.instance.getEventsYml().exist(eventName);

                    if (alreadyExist) {
                        p.sendMessage(Settings.EVENT_ALREADY_EXIST.getAsString("%event%", eventName));
                        return;
                    }

                    this.instance.getEventsYml().addEvent(eventName);
                }
                case "delete" -> {
                    final Event event = events.get(eventName);

                    if (!event.getState().equals(EventState.FINISHED)) event.getPlayerProcessor().finished();

                    this.instance.getEventsYml().removeEvent(eventName);

                    p.sendMessage(Settings.EVENT_DELETE.getAsString("%event%", eventName));
                }
                case "enable" -> {
                    Event event = this.events.get(eventName);

                    if (event != null && !event.getState().equals(EventState.FINISHED)) {
                        p.sendMessage(Settings.EVENT_ALREADY_ENABLED.getAsString("%event%", eventName));
                        return;
                    }

                    event = new Event(this.instance, eventName);
                    p.teleport(event.getPlayerProcessor().getSpawn());

                    this.events.put(eventName, event);
                    this.instance.getClient().send("event " + Settings.ANNOUNCE.getAsString("%event%", eventName));

                    p.sendMessage(Settings.EVENT_ENABLE.getAsString("%event%", eventName));
                }
                case "start" -> {
                    final Event event = this.events.get(eventName);

                    if (event == null) {
                        p.sendMessage(Settings.EVENT_NOT_ENABLED.getAsString("%event%", eventName));
                        return;
                    }

                    final EventState state = event.getState();
                    final boolean enabled = state.equals(EventState.ENABLED);

                    if (!enabled) {
                        p.sendMessage(
                                state.equals(EventState.STARTED) ?
                                        Settings.EVENT_ALREADY_STARTED.getAsString("%event%", eventName) :
                                        Settings.EVENT_ALREADY_FINISHED.getAsString("%event%", eventName));
                        return;
                    }

                    event.setState(EventState.STARTING);
                }
                case "stop" -> {
                    final Event event = this.events.get(eventName);

                    if (event == null) {
                        p.sendMessage(Settings.EVENT_NOT_ENABLED.getAsString("%event%", eventName));
                        return;
                    }
                    final EventState state = event.getState();

                    if (state.equals(EventState.FINISHED)) {
                        p.sendMessage(Settings.EVENT_ALREADY_FINISHED.getAsString("%event%", eventName));
                        return;
                    }

                    event.setState(EventState.FINISHED);
                }
            }
        }
        /**
         *              [Settings]
         *  event settings set-kit [eventName]
         *  event settings set-position [eventName]
         *  event settings set-lobby [eventName]
         */
        else if (args.length == 3) {
            String group = args[0];
            sub = args[1];
            eventName = args[2];

            if (!group.equals("settings")) return;

            FileConfiguration config = this.instance.getEventsYml().getConfig();
            String path = eventName + ".";

            switch (sub) {
                case "set-kit" -> {
                    final PlayerInventory inv = p.getInventory();
                    final ItemStack[] contents = inv.getContents();
                    final ItemStack[] armor = inv.getArmorContents();

                    final ItemStack off = inv.getItemInOffHand();

                    path += "kit.";

                    config.set(path + ".contents", contents);
                    config.set(path + ".armor", armor);
                    config.set(path + ".off", off);

                    this.instance.getEventsYml().reload();

                    p.sendMessage(Settings.EVENT_SET_KIT.getAsString("%event%", eventName));
                }
                case "set-position" -> {
                    final Location location = p.getLocation();

                    config.set(path + ".spawn", location);

                    this.instance.getEventsYml().reload();

                    p.sendMessage(Settings.EVENT_SET_POSITION.getAsString("%event%", eventName));
                }
                case "set-lobby" -> {
                    final Location location = p.getLocation();

                    config.set(path + ".lobby", location);

                    this.instance.getEventsYml().reload();

                    p.sendMessage(Settings.EVENT_SET_LOBBY.getAsString("%event%", eventName));
                }
            }
        }
    }

    @Override
    public List<String> tab(Player p, String[] args) {
        return switch (args.length) {
            case 1 -> List.of("join", "create", "delete", "enable", "start", "stop", "settings");
            case 2 -> args[0].equals("settings") ? List.of("set-kit", "set-position", "set-lobby") : !args[0].equals("create") ? this.instance.getEventsYml().getEvents() : List.of();
            case 3 -> args[0].equals("settings") ? this.instance.getEventsYml().getEvents() : List.of();
            default -> List.of();
        };
    }
}
