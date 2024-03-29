package me.gamercoder215.starcosmetics.wrapper.commands;

import com.google.common.collect.ImmutableMap;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.api.cosmetics.*;
import me.gamercoder215.starcosmetics.api.cosmetics.capes.Cape;
import me.gamercoder215.starcosmetics.api.cosmetics.emote.Emote;
import me.gamercoder215.starcosmetics.api.cosmetics.gadget.Gadget;
import me.gamercoder215.starcosmetics.api.cosmetics.hat.Hat;
import me.gamercoder215.starcosmetics.api.cosmetics.particle.ParticleShape;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.PetType;
import me.gamercoder215.starcosmetics.api.cosmetics.structure.StructureInfo;
import me.gamercoder215.starcosmetics.api.cosmetics.trail.Trail;
import me.gamercoder215.starcosmetics.api.cosmetics.trail.TrailType;
import me.gamercoder215.starcosmetics.api.player.PlayerSetting;
import me.gamercoder215.starcosmetics.api.player.SoundEventSelection;
import me.gamercoder215.starcosmetics.api.player.StarPlayer;
import me.gamercoder215.starcosmetics.api.player.StarPlayerUtil;
import me.gamercoder215.starcosmetics.util.*;
import me.gamercoder215.starcosmetics.util.inventory.InventorySelector;
import me.gamercoder215.starcosmetics.util.inventory.ItemBuilder;
import me.gamercoder215.starcosmetics.util.inventory.StarInventory;
import me.gamercoder215.starcosmetics.util.inventory.StarInventoryUtil;
import me.gamercoder215.starcosmetics.wrapper.Wrapper;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.gamercoder215.starcosmetics.api.cosmetics.emote.Emote.EMOTE_TAG;
import static me.gamercoder215.starcosmetics.api.player.StarPlayerUtil.getCached;
import static me.gamercoder215.starcosmetics.util.Generator.cw;
import static me.gamercoder215.starcosmetics.util.Generator.genGUI;
import static me.gamercoder215.starcosmetics.util.inventory.StarInventoryUtil.itemBuilder;
import static me.gamercoder215.starcosmetics.wrapper.Wrapper.*;
import static me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper.builder;

public interface CommandWrapper {

    Map<String, List<String>> COMMANDS = ImmutableMap.<String, List<String>>builder()
            .put("starsettings", Arrays.asList("ssettings", "settings", "ss"))
            .put("starreload", Arrays.asList("sreload", "sr"))
            .put("starcosmetics", Arrays.asList("scosmetics", "sc", "cosmetics", "cs"))
            .put("starabout", Arrays.asList("sabout", "sa", "stara"))
            .put("starstructures", Arrays.asList("sstructures", "sstr"))
            .put("starpets", Arrays.asList("starp", "sp", "spets", "pets"))
            .put("starhelp", Collections.singletonList("shelp"))
            .put("starcosmeticinfo", Arrays.asList("cosmeticinfo", "scinfo", "scosmeticinfo", "cinfo"))
            .put("starhologram", Arrays.asList("starh", "hologram"))
            .put("starcapes", Arrays.asList("scapes", "starcape", "capes", "cape"))
            .put("staremote", Arrays.asList("semote", "emote", "e", "se"))
            .build();

    Map<String, String> COMMAND_PERMISSION = ImmutableMap.<String, String>builder()
            .put("starsettings", "starcosmetics.user.settings")
            .put("starreload", "starcosmetics.admin.reloadconfig")
            .put("starcosmetics", "starcosmetics.user.cosmetics")
            .put("starstructures", "starcosmetics.user.cosmetics")
            .put("starpets", "starcosmetics.user.cosmetics")
            .put("starcosmeticinfo", "starcosmetics.user.cosmetics")
            .put("starhologram", "starcosmetics.user.cosmetics")
            .put("starcapes", "starcosmetics.user.cosmetics")
            .put("staremote", "starcosmetics.user.cosmetics")
            .build();

    Map<String, String> COMMAND_DESCRIPTION = ImmutableMap.<String, String>builder()
            .put("starsettings", "Opens the StarCosmetics settings menu.")
            .put("starreload", "Reloads the StarCosmetics configuration.")
            .put("starcosmetics", "Opens the StarCosmetics menu.")
            .put("starabout", "Displays information about StarCosmetics.")
            .put("starstructures", "Opens the StarCosmetics structures menu.")
            .put("starpets", "Opens the StarCosmetics pets menu.")
            .put("starhelp", "Displays help for StarCosmetics.")
            .put("starcosmeticinfo", "Displays information about the cosmetic the player has currently equipped.")
            .put("starhologram", "Opens the StarCosmetics hologram menu.")
            .put("starcapes", "Opens the StarCosmetics capes menu.")
            .put("staremote", "Performs an emote or opens up the StarCosmetics emote menu.")
            .build();

    Map<String, String> COMMAND_USAGE = ImmutableMap.<String, String>builder()
            .put("starsettings", "/starsettings")
            .put("starreload", "/starreload")
            .put("starcosmetics", "/starcosmetics")
            .put("starabout", "/starabout")
            .put("starstructures", "/starstructures [structure]")
            .put("starpets", "/starpets [remove|spawn]")
            .put("starhelp", "/starhelp")
            .put("starcosmeticinfo", "/starcosmeticinfo <cosmetic>")
            .put("starhologram", "/starhologram")
            .put("staremote", "/staremote [emote]")
            .build();

    // Command Methods

    default void settings(Player p) {
        p.openInventory(Generator.createSettingsInventory(p));
        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    default void reloadConfig(CommandSender sender) {
        if (!sender.hasPermission("starcosmetics.admin.reloadconfig")) {
            sendError(sender, "error.permission");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + get("command.reload.reloading"));
        Plugin plugin = StarConfig.getPlugin();

        plugin.reloadConfig();
        StarConfig.updateCache();

        Bukkit.getOnlinePlayers().forEach(StarPlayer::new);

        sender.sendMessage(ChatColor.YELLOW + get("command.reload.reloaded"));
        if (sender instanceof Player) StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess((Player) sender);
    }

    default void about(Player p) {
        StarInventory inv = Generator.genGUI("about", 27, get("menu.about"));
        inv.setCancelled();

        ItemStack head = itemBuilder(Generator.generateHead(p), meta -> meta.setDisplayName(ChatColor.AQUA + get("menu.about.head")));
        inv.setItem(4, head);

        ItemStack cosmetics = itemBuilder(StarMaterial.RED_CONCRETE.findStack(),
                meta -> {
                    meta.setDisplayName(ChatColor.GOLD + get("menu.about.cosmetics"));
                    meta.setLore(
                            Arrays.asList(
                                    ChatColor.YELLOW + getWithArgs("menu.about.projectile_trail_count", comma(getCosmeticCount(TrailType.PROJECTILE))),
                                    ChatColor.DARK_GREEN + getWithArgs("menu.about.ground_trail_count", comma(getCosmeticCount(TrailType.GROUND))),
                                    ChatColor.AQUA + getWithArgs("menu.about.sound_trail_count", comma(getCosmeticCount(TrailType.PROJECTILE_SOUND))),
                                    " ",
                                    ChatColor.DARK_PURPLE + getWithArgs("menu.about.particle_shape_count", comma(getCosmeticCount(ParticleShape.class))),
                                    ChatColor.DARK_AQUA + getWithArgs("menu.about.structure_count", comma(StarConfig.getRegistry().getAvailableStructures().size())),
                                    ChatColor.LIGHT_PURPLE + getWithArgs("menu.about.pet_count", comma(PetType.values().length)),
                                    ChatColor.GREEN + getWithArgs("menu.about.hat_count", comma(getCosmeticCount(Hat.class))),
                                    ChatColor.BLUE + getWithArgs("menu.about.gadget_count", comma(getCosmeticCount(Gadget.class))),
                                    ChatColor.DARK_GREEN + getWithArgs("menu.about.cape_count", comma(getCosmeticCount(Cape.class))),
                                    ChatColor.GOLD + getWithArgs("menu.about.emote_count", comma(Emote.values().length)),
                                    " ",
                                    ChatColor.RED + getWithArgs("menu.about.total_cosmetic_count", comma(getCosmeticCount()))
                            )
                    );
                }
        );
        inv.setItem(10, cosmetics);

        inv.setItem(12,
                ItemBuilder.ofHead("github")
                        .name(ChatColor.DARK_GRAY + "GitHub")
                        .id("about_link")
                        .nbt(nbt -> {
                            nbt.set("link", "https://github.com/GamerCoder215/StarCosmetics");
                            nbt.set("message_id", "menu.about.message.github");
                            nbt.set("color", "555555");
                        })
                        .build());

        inv.setItem(13,
                ItemBuilder.ofHead("discord")
                        .name(StarChat.hexMessage("5865F2", "Discord"))
                        .id("about_link")
                        .nbt(nbt -> {
                            nbt.set("link", "https://discord.gg/WVFNWEvuqX");
                            nbt.set("message_id", "menu.about.message.discord");
                            nbt.set("color", "5865F2");
                        })
                        .build());

        inv.setItem(14,
                ItemBuilder.ofHead("youtube")
                        .name(ChatColor.RED + "YouTube")
                        .id("about_link")
                        .nbt(nbt -> {
                            nbt.set("link", "https://www.youtube.com/@GamerCoder215");
                            nbt.set("message_id", "menu.about.message.youtube");
                            nbt.set("color", "FF5555");
                        })
                        .build());

        inv.setItem(15,
                ItemBuilder.ofHead("twitter")
                        .name(StarChat.hexMessage("1DA1F2", "Twitter"))
                        .id("about_link")
                        .nbt(nbt -> {
                            nbt.set("link", "https://twitter.com/gamercoder215");
                            nbt.set("message_id", "menu.about.message.twitter");
                            nbt.set("color", "1DA1F2");
                        })
                        .build());

        inv.setItem(16,
                ItemBuilder.ofHead("instagram")
                        .name(ChatColor.LIGHT_PURPLE + "Instagram")
                        .id("about_link")
                        .nbt(nbt -> {
                            nbt.set("link", "https://www.instagram.com/teaminceptus");
                            nbt.set("message_id", "menu.about.message.instagram");
                            nbt.set("color", "FF55FF");
                        })
                        .build());

        p.openInventory(inv);
    }

    default void cosmetics(Player p) {
        StarInventory inv = Generator.genGUI("cosmetics_parent_menu", 54, get("menu.cosmetics"));
        inv.setCancelled();

        // Parents

        for (CosmeticParent parent : CosmeticParent.values())
            inv.setItem(parent.getPlace(), builder(parent.getIcon(),
                    meta -> {
                        meta.setDisplayName(ChatColor.YELLOW + get(parent.getDisplayKey()));
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                    },
                    nbt -> {
                        nbt.setID("cosmetic:selection:parent");
                        nbt.set("parent", parent.name());
                    })
            );

        // Particle Shapes

        List<CosmeticLocation<?>> sel = Wrapper.allFor(BaseShape.class);
        inv.setAttribute("collections:custom:particle", sel);
        inv.setAttribute("items_display:particle", "menu.cosmetics.shape");

        inv.setItem(24, builder(StarMaterial.FIREWORK_STAR.findStack(),
                meta -> {
                    FireworkEffectMeta pMeta = (FireworkEffectMeta) meta;
                    pMeta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.shape"));
                    pMeta.setEffect(FireworkEffect.builder()
                            .withColor(Color.fromRGB(r.nextInt(16777216)))
                            .build());

                    pMeta.addItemFlags(ItemFlag.values());
                },
                nbt -> {
                    nbt.setID("cosmetic:selection:custom");
                    nbt.set("type", "particle");
                    nbt.set("custom_id", "particle");
                }
        ));

        // Structures

        try {
            StarInventory structureInv = Generator.createStructureInventory(p);
            inv.setItem(29, builder(Material.STRUCTURE_BLOCK,
                    meta -> meta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.choose.structure")),
                    nbt -> {
                        nbt.setID("cosmetic:selection:custom_inventory");
                        nbt.set("inventory_key", "structures");
                        nbt.set("cooldown", "structures");
                    }
            ));
            inv.setAttribute("structures", structureInv);
        } catch (IllegalArgumentException ignored) {
            inv.setItem(29, ItemBuilder.LOCKED);
        }

        // Sound Events
        inv.setItem(30, builder(Material.NOTE_BLOCK,
                meta -> meta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.choose.sound")),
                nbt -> {
                    nbt.setID("cosmetic:selection:custom_inventory");
                    nbt.set("inventory_key", "sound_events");
                })
        );
        inv.setAttribute("sound_events", Generator.createSelectionInventory(p));

        // Gadgets

        inv.setItem(31, builder(StarMaterial.FIREWORK_ROCKET.findStack(),
                meta -> meta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.choose.gadget")),
                nbt -> {
                    nbt.setID("cosmetic:selection");
                    nbt.set("cosmetic", BaseGadget.INSTANCE.getNamespace());
                    nbt.set("display", BaseGadget.INSTANCE.getDisplayName());
                })
        );

        // Pets

        inv.setItem(33, builder(StarInventoryUtil.getHead("rabbit_pet"),
                meta -> meta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.choose.pet")),
                nbt -> {
                    nbt.setID("cosmetic:selection:custom_inventory");
                    nbt.set("inventory_key", "pets");
                }
        ));
        inv.setAttribute("pets", Generator.createPetInventory(p));

        // Emotes

        inv.setItem(40, builder(Material.ARMOR_STAND,
                meta -> meta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.choose.emote")),
                nbt -> {
                    nbt.setID("cosmetic:selection:custom_inventory");
                    nbt.set("inventory_key", "emotes");
                }));
        inv.setAttribute("emotes", Generator.createEmotesInventory(p));

        // Settings

        ItemStack settings = ItemBuilder.of(Material.NETHER_STAR)
                .id("player_settings")
                .name(ChatColor.YELLOW + get("menu.cosmetics.settings"))
                .nbt(nbt -> nbt.set("back", "cosmetics"))
                .build();

        inv.setItem(52, settings);

        p.openInventory(inv);
    }

    default void help(CommandSender sender) {
        List<String> info = new ArrayList<>();
        for (String name : COMMANDS.keySet()) {
            PluginCommand cmd = Bukkit.getPluginCommand(name);

            if (!sender.isOp() && COMMAND_PERMISSION.get(name) != null && !(sender.hasPermission(COMMAND_PERMISSION.get(name)))) continue;

            if (sender.isOp())
                info.add(ChatColor.AQUA + "/" + cmd.getName() + ChatColor.WHITE + " - " + ChatColor.BLUE + COMMAND_DESCRIPTION.get(name) + ChatColor.WHITE + " | " + ChatColor.GOLD + (COMMAND_PERMISSION.get(name) == null ? "No Permissions" : COMMAND_PERMISSION.get(name)));
            else
                info.add(ChatColor.AQUA + "/" + cmd.getName() + ChatColor.WHITE + " - " + ChatColor.BLUE + COMMAND_DESCRIPTION.get(name));
        }

        String msg = ChatColor.DARK_AQUA + String.valueOf(ChatColor.UNDERLINE) + get("constants.commands") + "\n\n" + String.join("\n", info.toArray(new String[]{}));
        sender.sendMessage(msg);
    }

    default void trails(Player p) {
        StarInventory parentInv = Generator.createParentInventory(CosmeticParent.TRAILS);
        p.openInventory(parentInv);
        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    default void structures(Player p, @Nullable String structure) {
        if (structure == null) {
            try {
                p.openInventory(Generator.createStructureInventory(p));
                StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
            } catch (IllegalArgumentException e) {
                sendError(p, "error.cosmetics.no_structures");
                StarSound.BLOCK_NOTE_BLOCK_PLING.playFailure(p);
            }
            return;
        }

        if (StarCooldowns.checkCooldown("structure", p)) return;

        StarPlayer sp = new StarPlayer(p);

        StructureInfo info = StarConfig.getRegistry().getAvailableStructures()
                .stream()
                .filter(inf -> inf.getLocalizedName().equalsIgnoreCase(structure))
                .findFirst().orElse(null);

        if (info == null) {
            sendError(p, "error.cosmetics.structure_not_found");
            return;
        }

        if (sp.getSetting(PlayerSetting.STRUCTURE_VELOCITY) != PlayerSetting.VelocityPower.NONE) {
            PlayerSetting.VelocityPower power = sp.getSetting(PlayerSetting.STRUCTURE_VELOCITY);

            p.setMetadata("immune_fall", new FixedMetadataValue(StarConfig.getPlugin(), true));
            p.setVelocity(p.getLocation().getDirection().multiply(-power.getLaunchPower()));
        }

        info.getStructure().placeAndRemove(p.getLocation().add(p.getLocation().getDirection()), 200);
        StarCooldowns.set("structure", p, 100);

        StarRunnable.syncLater(() -> {
            if (p.getVelocity().getY() < 0.1) p.removeMetadata("immune_fall", StarConfig.getPlugin());
        }, 5);
        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    default void pets(Player p, String[] args) {
        if (args == null || args.length < 1) {
            p.openInventory(Generator.createPetInventory(p));
            StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
            return;
        }

        StarPlayer sp = new StarPlayer(p);
        switch (args[0].toLowerCase()) {
            case "remove": {
                if (sp.getSpawnedPet() == null) {
                    sendError(p, "error.cosmetics.no_pet");
                    return;
                }

                StarPlayerUtil.removePet(p);
                sp.sendNotification(ChatColor.GREEN + get("success.cosmetics.pet_removed"));
                break;
            }
            default: {
                sendError(p, "error.argument");
                break;
            }
        }
    }

    default void shapes(Player p) {
        List<CosmeticLocation<?>> sel = Wrapper.allFor(BaseShape.class);
        List<StarInventory> invs = Generator.createSelectionInventory(p, sel, get("menu.cosmetics.shape"));
        invs.forEach(inv -> StarInventoryUtil.setBack(inv, cw::cosmetics));

        ItemStack cancel = builder(Material.BARRIER,
                meta -> meta.setDisplayName(ChatColor.RED + get("menu.cosmetics.particle.reset")),
                nbt -> nbt.setID("cancel:particle")
        );
        invs.forEach(i -> i.setItem(18, cancel));

        p.openInventory(invs.get(0));
        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    default void soundSelection(Player p, String... args) {
        if (args == null || args.length < 1) {
            p.openInventory(Generator.createSelectionInventory(p));
            StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
            return;
        }

        StarPlayer sp = new StarPlayer(p);

        switch (args[0].toLowerCase()) {
            case "add": {
                if (sp.getSelectionLimit() <= sp.getSoundSelections().size()) {
                    sendError(p, "error.cosmetics.selection_limit");
                    StarSound.BLOCK_NOTE_BLOCK_PLING.playFailure(p);
                    return;
                }

                InventorySelector.createSelection(p);
                break;
            }
            default: {
                sendError(p, "error.argument");
                break;
            }
        }

        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    default void hats(Player p) {
        StarInventory parentInv = Generator.createParentInventory(CosmeticParent.HATS);
        p.openInventory(parentInv);
        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    Map<String, Object> PARENTS_INFO = ImmutableMap.<String, Object>builder()
            .put("projectile_trail", TrailType.PROJECTILE)
            .put("sound_trail", TrailType.PROJECTILE_SOUND)
            .put("ground_trail", TrailType.GROUND)

            .put("hat", Hat.class)
            .put("particle_shape", ParticleShape.class)
            .put("gadget", Gadget.class)
            .build();

    default void cosmeticInfo(Player p, TrailType type) {
        StarPlayer sp = new StarPlayer(p);
        CosmeticLocation<?> loc = sp.getSelectedTrail(type);

        if (loc == null) {
            sendError(p, "error.cosmetics.unequipped.trail");
            return;
        }

        String[] s = new String[] {
                ChatColor.DARK_GREEN + getWithArgs("command.cosmetic_info.selected_cosmetic_parent", ChatColor.AQUA + loc.getParent().getDisplayName()),
                ChatColor.DARK_GREEN + getWithArgs("command.cosmetic_info.selected_trail", ChatColor.AQUA + loc.getDisplayName() + " (" + ChatColor.DARK_AQUA + loc.getFullKey() + ChatColor.AQUA + ")\n"),
                ChatColor.DARK_GREEN + getWithArgs("command.cosmetic_info.selected_trail_type", ChatColor.AQUA + type.name())
        };
        p.sendMessage(s);
    }

    default void cosmeticInfo(Player p, Class<? extends Cosmetic> cosmetic) {
        StarPlayer sp = new StarPlayer(p);
        CosmeticLocation<?> loc = sp.getSelectedCosmetic(cosmetic);

        if (loc == null) {
            sendError(p, "error.cosmetics.unequipped");
            return;
        }

        String[] s = new String[] {
                ChatColor.DARK_GREEN + getWithArgs("command.cosmetic_info.selected_cosmetic_parent", ChatColor.AQUA + loc.getParent().getDisplayName()),
                ChatColor.DARK_GREEN + getWithArgs("command.cosmetic_info.selected_cosmetic", ChatColor.AQUA + loc.getDisplayName() + " (" + ChatColor.DARK_AQUA + loc.getFullKey() + ChatColor.AQUA + ")\n"),
        };
        p.sendMessage(s);
    }

    default void hologramInfo(Player p) {
        StarPlayer sp = new StarPlayer(p);
        StarInventory inv = genGUI(27, get("menu.cosmetics.hologram"));
        inv.setCancelled();

        ItemStack message = StarMaterial.OAK_SIGN.findStack();
        ItemMeta mMeta = message.getItemMeta();
        if (!sp.getHologramMessage().isEmpty())
            mMeta.setDisplayName(ChatColor.YELLOW + "\"" + sp.getHologramMessage() + "\"");
        else
            mMeta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.hologram.none"));
        message.setItemMeta(mMeta);
        inv.setItem(12, message);

        ItemStack setMessage = builder(Material.PAPER,
                meta -> meta.setDisplayName(ChatColor.YELLOW + get("menu.cosmetics.hologram.set")),
                nbt -> nbt.setID("cosmetic:hologram:set")
        );
        inv.setItem(14, setMessage);

        ItemStack resetMessage = builder(StarMaterial.RED_WOOL.findStack(),
                meta -> meta.setDisplayName(ChatColor.RED + get("menu.cosmetics.hologram.reset")),
                nbt -> nbt.setID("cosmetic:hologram:reset")
        );
        inv.setItem(22, resetMessage);

        while (inv.firstEmpty() != -1)
            inv.setItem(inv.firstEmpty(), ItemBuilder.GUI_BACKGROUND);

        p.openInventory(inv);
        StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
    }

    default void capes(@NotNull Player p, String arg) {
        if (arg == null) {
            p.openInventory(Generator.createParentInventory(CosmeticParent.CAPES));
            StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
            return;
        }

        StarPlayer sp = new StarPlayer(p);
        switch (arg.toLowerCase()) {
            case "remove": {
                if (sp.getSpawnedPet() == null) {
                    sendError(p, "error.cosmetics.no_cape");
                    return;
                }

                StarPlayerUtil.removeCape(p);
                sp.sendNotification(ChatColor.GREEN + get("success.cosmetics.cape_removed"));
                break;
            }
            default: {
                sendError(p, "error.argument");
                break;
            }
        }
    }

    default void emote(@NotNull Player p, @Nullable String emote) {
        if (emote == null) {
            p.openInventory(Generator.createEmotesInventory(p));
            StarSound.ENTITY_ARROW_HIT_PLAYER.playSuccess(p);
            return;
        }

        if (getCached(p).isEmoting()) {
            sendError(p, "error.cosmetics.emoting");
            return;
        }

        if (StarConfig.getConfig().isInPvP(p) && !StarConfig.getConfig().getCanEmoteInPvE()) {
            p.sendMessage(get("plugin.prefix") + ChatColor.RED + get("error.emote.pvp"));
            return;
        }

        if (StarCooldowns.checkCooldown(EMOTE_TAG, p)) return;

        StarPlayer sp = new StarPlayer(p);
        try {
            Emote e = Emote.valueOf(emote.toUpperCase());
            if (!e.getCriteria().isUnlocked(p)) {
                p.sendMessage(get("plugin.prefix") + ChatColor.RED + e.getCriteria().getDisplayMessage());
                return;
            }

            sp.emote(e);

            StarCooldowns.set(EMOTE_TAG, p, 100);
        } catch (IllegalArgumentException e) {
            sendError(p, "error.argument.emote");
        }
    }

    default void disableCosmetic(CommandSender sender, String loc) {
        if (!sender.hasPermission("starcosmetics.admin.disable_cosmetics")) {
            sendError(sender, "error.permission");
            return;
        }

        boolean somethingHappened = false;

        if (StarConfig.getRegistry().getByFullKey(loc) != null) {
            CosmeticLocation<?> c = StarConfig.getRegistry().getByFullKey(loc);
            StarConfig.getConfig().disableCosmetic(c);
            somethingHappened = true;
        }

        if (StarConfig.getRegistry().getByNamespace(loc) != null) {
            Cosmetic c = StarConfig.getRegistry().getByNamespace(loc);
            StarConfig.getConfig().disableCosmetic(c);
            somethingHappened = true;
        }

        if (!somethingHappened) {
            sendError(sender, "error.argument.cosmetic");
            return;
        }

        sender.sendMessage(prefix() + ChatColor.GREEN + get("success.cosmetics.disabled"));
    }

    default void enableCosmetic(CommandSender sender, String loc) {
        if (!sender.hasPermission("starcosmetics.admin.enable_cosmetics")) {
            sendError(sender, "error.permission");
            return;
        }

        boolean somethingHappened = false;
        if (StarConfig.getRegistry().getByFullKey(loc) != null) {
            CosmeticLocation<?> c = StarConfig.getRegistry().getByFullKey(loc);
            StarConfig.getConfig().enableCosmetic(c);
            somethingHappened = true;
        }

        if (StarConfig.getRegistry().getByNamespace(loc) != null) {
            Cosmetic c = StarConfig.getRegistry().getByNamespace(loc);
            StarConfig.getConfig().enableCosmetic(c);
            somethingHappened = true;
        }

        if (!somethingHappened) {
            sendError(sender, "error.argument.cosmetic");
            return;
        }

        sender.sendMessage(prefix() + ChatColor.GREEN + get("success.cosmetics.enabled"));
    }

    // Utilities

    static long getCosmeticCount(TrailType t) {
        return Wrapper.getCosmeticSelections().getAllSelections()
                .entrySet()
                .stream()
                .filter(e -> e.getKey() instanceof Trail)
                .filter(e -> ((Trail<?>) e.getKey()).getType() == t)
                .mapToLong(e -> e.getValue().size())
                .sum();
    }

    static long getCosmeticCount(Class<? extends Cosmetic> c) {
        return Wrapper.getCosmeticSelections().getAllSelections()
                .entrySet()
                .stream()
                .filter(e -> c.isInstance(e.getKey()))
                .mapToLong(e -> e.getValue().size())
                .sum();
    }

    static long getCosmeticCount() {
        long count = 0;
        count += Wrapper.getCosmeticSelections().getAllSelections()
                .values()
                .stream()
                .mapToLong(List::size)
                .sum();

        count += SoundEventSelection.AVAILABLE_EVENTS.size();
        count += PetType.values().length;
        count += StarConfig.getRegistry().getAvailableStructures().size();
        count += Emote.values().length;

        return count;
    }

    static String comma(long l) {
        return String.format("%,d", l);
    }

}
