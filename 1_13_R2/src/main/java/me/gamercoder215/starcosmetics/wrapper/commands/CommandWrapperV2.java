package me.gamercoder215.starcosmetics.wrapper.commands;

import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.api.cosmetics.CosmeticRegistry;
import me.gamercoder215.starcosmetics.api.cosmetics.structure.StructureInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import revxrsal.commands.annotation.*;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.stream.Collectors;

public class CommandWrapperV2 implements CommandWrapper {

    private static BukkitCommandHandler handler;

    public <T extends Plugin & CosmeticRegistry> CommandWrapperV2(final T plugin) {
        if (handler != null) return;
        handler = BukkitCommandHandler.create(plugin);

        handler.getAutoCompleter()
                .registerSuggestion("structures", SuggestionProvider.of(() -> plugin.getAvailableStructures()
                    .stream()
                    .map(StructureInfo::getLocalizedName)
                    .collect(Collectors.toList())));

        handler.register(
                this,
                new CosmeticCommands(this)
        );

        handler.registerBrigadier();
        handler.setLocale(StarConfig.getConfig().getLocale());

        plugin.getLogger().info("Loaded Command Wrapper V2 (1.13+)");
    }

    @Override
    @Command({"starsettings", "ssettings", "settings", "ss"})
    @Description("Opens the StarCosmetics settings menu.")
    @Usage("/starsettings")
    @CommandPermission("starcosmetics.user.settings")
    public void settings(Player p) {
        CommandWrapper.super.settings(p);
    }

    @Override
    @Command({"starreload", "sreload", "sr"})
    @Description("Reloads the StarCosmetics configuration.")
    @Usage("/starreload")
    @CommandPermission("starcosmetics.admin.reloadconfig")
    public void reloadConfig(CommandSender sender) {
        CommandWrapper.super.reloadConfig(sender);
    }

    @Override
    @Command({"starabout", "sabout", "sa", "stara"})
    @Description("Displays information about StarCosmetics.")
    @Usage("/starabout")
    public void about(Player p) { CommandWrapper.super.about(p); }

    // Cosmetic Commands

    @Command({"starcosmetics", "scosmetics", "sc", "cosmetics", "cs"})
    @Description("Opens the StarCosmetics Cosmetics menu.")
    @Usage("/starcosmetics")
    @CommandPermission("starcosmetics.user.cosmetics")
    public static final class CosmeticCommands {

        private final CommandWrapperV2 wrapper;

        CosmeticCommands(CommandWrapperV2 wrapper) {
            this.wrapper = wrapper;
        }

        @Default
        public void cosmetics(Player p) {
            wrapper.cosmetics(p);
        }

        @Subcommand({"pets", "pet"})
        @AutoComplete("remove")
        public void pets(Player p, @Default("") String args) {
            wrapper.pets(p, args);
        }

        @Subcommand({"structures", "structure"})
        @AutoComplete("@structures *")
        public void structures(Player p, @Default("") String structure) {
            wrapper.structures(p, structure);
        }

        @Subcommand({"customsounds", "sounds"})
        @AutoComplete("add")
        public void soundSelection(Player p, @Default("") String args) {
            wrapper.soundSelection(p, args.split("\\s"));
        }

    }

    @Override
    @Command({"starstructures", "sstructures", "sstr"})
    @Description("Opens the StarCosmetics Structures menu.")
    @Usage("/starstructures")
    @CommandPermission("starcosmetics.user.cosmetics")
    @AutoComplete("@structures *")
    public void structures(Player p, @Optional String structure) {
        CommandWrapper.super.structures(p, structure);
    }

    @Command({"starpets", "starp", "sp", "spets", "pets"})
    @Description("Opens the StarCosmetics Pets menu.")
    @Usage("/starpets")
    @CommandPermission("starcosmetics.user.cosmetics")
    @AutoComplete("remove")
    public void pets(Player p, @Optional String args) { CommandWrapper.super.pets(p, args == null ? null : args.split("\\s")); }

}
