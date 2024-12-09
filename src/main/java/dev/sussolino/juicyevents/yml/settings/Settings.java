package dev.sussolino.juicyevents.yml.settings;

import dev.sussolino.juicyapi.color.ColorUtils;
import dev.sussolino.juicyevents.Initializer;
import org.bukkit.configuration.file.FileConfiguration;

public enum Settings {

    /**
     *    PREFIX
     */

    PREFIX,
    ANNOUNCE,
    FALLBACK,

    /**
     *    ERRORS
     */
    EVENT_ALREADY_EXIST,
    EVENT_ALREADY_ENABLED,
    EVENT_ALREADY_STARTED,
    EVENT_ALREADY_FINISHED,
    EVENT_NOT_ENABLED,
    EVENT_NOT_EXIST,

    /**
     *    Event Command
     */

    EVENT_CREATE,
    EVENT_DELETE,
    EVENT_START,
    EVENT_ENABLE,
    EVENT_STOP,
    EVENT_JOIN,
    EVENT_SET_DELAY,
    EVENT_SET_KIT,
    EVENT_SET_POSITION,
    EVENT_SET_LOBBY;

    private final String path;
    private final FileConfiguration config;

    Settings() {
        this.path = name()
                .toLowerCase()
                .replace("__", "-")
                .replace("_", ".");
        this.config = Initializer.instance.getSettingsYml().getConfig();
    }

    public String getAsString() {
        final String prefix = config.getString("prefix");
        final String string = config.getString(this.path);

        if (prefix == null) throw new NullPointerException("[!] [settings.yml] -> PREFIX");
        if (string == null) throw new NullPointerException("[!] [settings.yml] -> " + this.path);

        return ColorUtils.color(string.replace("%prefix%", prefix));
    }
    public String getAsString(final String replace, String replacer) {
        return ColorUtils.color(getAsString().replace(replace, replacer));
    }
}
