package me.gamercoder215.starcosmetics.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gamercoder215.starcosmetics.StarCosmetics;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StarPlaceholders extends PlaceholderExpansion {

    private final StarCosmetics plugin;

    public StarPlaceholders(StarCosmetics plugin) {
        this.plugin = plugin;
        register();
    }

    private static final Map<String, Function<OfflinePlayer, String>> OFFLINE_PH = new HashMap<String, Function<OfflinePlayer, String>>() {{
        put("name", OfflinePlayer::getName);
    }};

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return "team-inceptus";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    // Impl

    @Override
    public List<String> getPlaceholders() {
        return new ArrayList<>(OFFLINE_PH.keySet());
    }

    @Override
    public String onRequest(OfflinePlayer p, String arg) {
        if (OFFLINE_PH.containsKey(arg)) return OFFLINE_PH.get(arg).apply(p);
        return null;
    }

}
