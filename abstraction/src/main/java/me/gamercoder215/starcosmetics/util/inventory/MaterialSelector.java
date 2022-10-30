package me.gamercoder215.starcosmetics.util.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.api.cosmetics.registry.CosmeticLocation;
import me.gamercoder215.starcosmetics.api.player.cosmetics.SoundEventSelection;
import me.gamercoder215.starcosmetics.util.Constants;
import me.gamercoder215.starcosmetics.util.StarMaterial;
import me.gamercoder215.starcosmetics.wrapper.Wrapper;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static me.gamercoder215.starcosmetics.wrapper.Wrapper.get;

public final class MaterialSelector {

    private static final Wrapper w = Constants.w;
    
    private MaterialSelector() { throw new UnsupportedOperationException(); }

    @NotNull
    public static Material toMaterial(@NotNull Class<? extends Event> eventClass) {
        if (Modifier.isAbstract(eventClass.getModifiers())) throw new IllegalArgumentException("Using Abstract Class");

        String n = eventClass.getSimpleName().toLowerCase();
        Material chosen = null;

        if (n.contains("player")) chosen = Material.IRON_SWORD;
        if (chosen == null && n.contains("block")) chosen = Material.DIRT;
        if (chosen == null && n.contains("server")) chosen = StarMaterial.OBSERVER.find();
        if (chosen == null && n.contains("inventory")) chosen = StarMaterial.CRAFTING_TABLE.find();
        if (chosen == null && n.contains("weather")) chosen = Material.BUCKET;
        if (chosen == null && n.contains("vehicle")) chosen = Material.MINECART;

        switch (n) {
            case "asyncplayerchatevent": return StarMaterial.COMMAND_BLOCK.find();
            case "playerjoinevent": return StarMaterial.GRASS_BLOCK.find();
            case "playerrespawnevent": return Material.BEACON;
        }

        if (chosen == null) chosen = Material.REDSTONE;
        return chosen;
    }

    @NotNull
    public static Material toMaterial(@NotNull Sound s) {
        String n = s.name();
        Material chosen = null;

        for (Material m : Arrays.stream(Material.values()).filter(w::isItem).collect(Collectors.toList())) {
            if (n.equals(m.name())) return m; // Takes Absolute Priority
            
            if (n.contains(m.name())) {
                chosen = m;
                break;
            }
        }
        
        if (chosen == null && n.startsWith("ENTITY")) chosen = StarMaterial.POPPY.find();   
        if (chosen == null && n.contains("GENERIC")) chosen = Material.LEATHER_CHESTPLATE;
        if (chosen == null && n.contains("VILLAGER")) chosen = Material.EMERALD;

        if (n.startsWith("MUSIC")) if (n.contains("OVERWORLD")) chosen = StarMaterial.GRASS_BLOCK.find();
        else if (n.contains("NETHER")) chosen = Material.NETHERRACK;
        else chosen = Material.NOTE_BLOCK;

        if (chosen == null && n.startsWith("RECORD")) chosen = Material.JUKEBOX;
        if (chosen == null && n.startsWith("UI")) chosen = Material.REDSTONE_BLOCK;
        if (chosen == null && n.startsWith("WEATHER")) chosen = Material.BUCKET;

        return chosen == null ? Material.STONE : chosen;
    }

    @NotNull
    public static String getFriendlyName(@Nullable Sound s) {
        if (s == null) return "";

        String n = WordUtils.capitalizeFully(s.name().replace("_", " "));
        String prefix = n.split(" ")[0].toLowerCase();
        String suffix = n.substring(prefix.length());

        switch (prefix) {
            case "entity": return get("constants.friendly.entity") + " - " + suffix;
            case "music": return get("constants.friendly.music") + " - " + suffix;
            case "record": return get("constants.friendly.record") + " - " + suffix;
            case "ui": return get("constants.friendly.ui") + " - " + suffix;
            case "weather": return get("constants.friendly.weather") + " - " + suffix;
        }

        return n;
    }

    public static void chooseSound(@NotNull Player p, Consumer<Sound> clickAction) {
        StarInventory inv = w.createInventory("choose:sound_inv", 54, get("gui.choose.sound"));
        inv.setCancelled();
        inv.setAttribute("chosen_action", clickAction);

        List<ItemStack> items = new ArrayList<>();
        for (Sound s : Sound.values()) {
            Material m = toMaterial(s);
            ItemStack item = new ItemStack(m);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + getFriendlyName(s));

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.addAll(Arrays.stream(ChatPaginator.wordWrap(
                    get("gui.choose.sound_desc." + s.name().toLowerCase(), "null"), 30
            )).map(str -> ChatColor.GRAY + str).collect(Collectors.toList()));
            lore.add(" ");
            if (!lore.get(1).equalsIgnoreCase("null")) meta.setLore(lore);

            item.setItemMeta(meta);

            NBTWrapper nbt = NBTWrapper.of(item);
            nbt.set("sound", s.name());
            item = nbt.getItem();

            items.add(item);
        }

        Map<Integer, List<ItemStack>> rows = generateRows(items);
        inv.setAttribute("rows", rows);
        setRows(inv, rows);
        setScrolls(inv);

        p.openInventory(inv);
    }

    public static void chooseEvent(@NotNull Player p, Consumer<Class<? extends Event>> clickAction) {
        StarInventory inv = w.createInventory("choose:event_inv", 54, get("gui.choose.event"));
        inv.setCancelled();
        inv.setAttribute("chosen_action", clickAction);

        List<ItemStack> items = new ArrayList<>();
        for (Class<? extends Event> clazz : SoundEventSelection.AVAILABLE_EVENTS) {
            Material m = toMaterial(clazz);
            ItemStack item = new ItemStack(m);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + clazz.getSimpleName());

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.addAll(Arrays.stream(ChatPaginator.wordWrap(
                get("gui.choose.event_desc." + clazz.getSimpleName(), "null"), 30
            )).map(str -> ChatColor.GRAY + str).collect(Collectors.toList()));
            lore.add(" ");
            if (!lore.get(1).equalsIgnoreCase("null")) meta.setLore(lore);

            item.setItemMeta(meta);

            NBTWrapper nbt = NBTWrapper.of(item);
            nbt.set("event", clazz);
            item = nbt.getItem();

            items.add(item);
        }

        Map<Integer, List<ItemStack>> rows = generateRows(items);
        inv.setAttribute("rows", rows);
        setRows(inv, rows);
        setScrolls(inv);

        p.openInventory(inv);
    }

    public static void setScrolls(Inventory inv) {
        int size = inv.getSize();

        ItemStack up = getHead("arrow_up");
        ItemMeta uMeta = up.getItemMeta();
        uMeta.setDisplayName(ChatColor.GREEN + get("constants.up"));
        up.setItemMeta(uMeta);
        NBTWrapper uNBT = NBTWrapper.of(up);
        uNBT.setID("scroll_up");
        uNBT.set("row", 0);
        up = uNBT.getItem();
        inv.setItem(26 - (54 - size), up);

        ItemStack down = getHead("arrow_down");
        ItemMeta dMeta = down.getItemMeta();
        dMeta.setDisplayName(ChatColor.GREEN + get("constants.down"));
        down.setItemMeta(dMeta);
        NBTWrapper dNBT = NBTWrapper.of(down);
        dNBT.setID("scroll_down");
        dNBT.set("up_item", 26 - (54 - size));
        down = dNBT.getItem();
        inv.setItem(35 - (54 - size), down);
    }

    public static ItemStack getHead(String key) {
        try {
            Properties p = new Properties();
            p.load(MaterialSelector.class.getResourceAsStream("/util/heads.properties"));

            String value = p.getProperty(key);
            if (value == null) return null;

            ItemStack head = w.isLegacy() ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1, (short) 3) : new ItemStack(Material.matchMaterial("PLAYER_HEAD"));
            SkullMeta hMeta = (SkullMeta) head.getItemMeta();

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));
            Method mtd = hMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(hMeta, profile);

            head.setItemMeta(hMeta);
            return head;
        } catch (IOException | ReflectiveOperationException e) {
            StarConfig.print(e);
        }

        return null;
    } 

    @NotNull
    public static Map<Integer, List<ItemStack>> generateRows(ItemStack... items) {
        return generateRows(Arrays.asList(items));
    }

    public static void setRows(Inventory inv, Map<Integer, List<ItemStack>> rows) {
        setRows(inv, rows, 0);
    }

    public static void setRows(Inventory inv, Map<Integer, List<ItemStack>> rows, int start) {
        int limit = (inv.getSize() - 18) / 9;
        if (limit < 1) return;

        int startRow = Math.min(start, 0);

        for (int i = startRow; i < Math.min(rows.size(), limit + startRow); i++) {
            List<ItemStack> row = rows.get(i);
            if (row.isEmpty() || row.size() > 7) throw new IllegalArgumentException("Unexpected row size: " + row.size() + " (" + i + ")");
            
            for (int j = 0; j < row.size(); j++) {
                int slot = ((i + 1) * 9) + j + 1;
                inv.setItem(slot, row.get(j));
            }
        }
    }

    @NotNull
    public static ItemStack toItemStack(@NotNull CosmeticLocation<?> loc) {
        final Material type;
        final String name;
        Object input = loc.getInput();

        if (input instanceof Material) {
            Material m = (Material) input;
            type = m;
            name = WordUtils.capitalizeFully(m.name().replace("_", " "));
        }
        else if (input instanceof Particle) {
            Particle p = (Particle) input;
            type = toMaterial(p);
            name = WordUtils.capitalizeFully(p.name().replace("_", " "));
        }
        else if (input instanceof EntityType) {
            EntityType e = (EntityType) input;
            type = toMaterial(e);
            name = WordUtils.capitalizeFully(e.name().replace("_", " "));
        }
        else if (input instanceof Sound) {
            Sound s = (Sound) input;
            type = toMaterial(s);
            name = getFriendlyName(s);
        }
        else throw new IllegalArgumentException("Unexpected input type: " + input + " (" + input.getClass().getName() + ")");

        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + loc.getParent().getDisplayName() + " | " + name);
        List<String> lore = new ArrayList<>();
        lore.add(loc.getRarity().toString());
        if (!loc.getRarity().isSecret()) {
            lore.add(" ");
            lore.addAll(Arrays.stream(
                    ChatPaginator.wordWrap(loc.getCompletionCriteria().getDisplayMessage(), 30)
            ).map(s -> ChatColor.GRAY + s).collect(Collectors.toList()));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        NBTWrapper nbt = NBTWrapper.of(item);
        nbt.setID("choose:cosmetic");
        nbt.set("location", loc);
        item = nbt.getItem();

        return item;
    }

    @NotNull
    public static Material toMaterial(@NotNull EntityType type) {
        Material m = null;

        for (Material mat : Material.values()) {
            if (mat.name().equalsIgnoreCase(type.name())) {
                m = mat;
                break;
            }

            if (mat.name().equalsIgnoreCase(type.name() + "_SPAWN_EGG")) {
                m = mat;
                break;
            }
        }

        if (m == null) m = Material.APPLE;

        return m;
    }

    @NotNull
    public static Material toMaterial(@NotNull Particle particle) {
        for (Material m : Material.values()) if (particle.name().equalsIgnoreCase(m.name())) return m;

        switch (particle.name().toLowerCase()) {
            case "white_ash":
            case "ash": return Material.FLINT_AND_STEEL;
            case "block_crack":
            case "block_dust":
            case "block_marker": return Material.STONE;
            case "bubble_column_up":
            case "bubble_pop": return Material.matchMaterial("SEAGRASS");
            case "campire_cosy_smoke":
            case "campfire_signal_smoke": return Material.matchMaterial("CAMPFIRE");
            case "cloud": return StarMaterial.WHITE_WOOL.find();
            case "composter": return Material.matchMaterial("COMPOSTER");
            case "crimson_spore": return Material.matchMaterial("CRIMSON_NYLIUM");
            case "crit": return Material.DIAMOND_SWORD;
            case "crit_magic": return Material.ENCHANTED_BOOK;
            case "current_down": return Material.matchMaterial("TRIDENT");
            case "damage_indicator": return Material.IRON_AXE;
            case "dolphin": return Material.matchMaterial("DOLPHIN_SPAWN_EGG");
            case "dragon_breath": return Material.DRAGON_EGG;
            case "dripping_dripstone_lava":
            case "falling_dripstone_lava":
            case "falling_lava":
            case "landing_lava":
            case "lava":
            case "drip_lava": return Material.LAVA_BUCKET;
            case "dripping_dripstone_water":
            case "falling_dripstone_water":
            case "falling_water":
            case "drip_water": return Material.WATER_BUCKET;
            case "falling_honey":
            case "landing_honey":
            case "dripping_honey": return Material.matchMaterial("HONEY_BLOCK");
            case "falling_obsidian_tear":
            case "landing_obsidian_tear":
            case "dripping_obsidian_tear": return Material.matchMaterial("CRYING_OBSIDIAN");
            case "falling_nectar":
            case "wax_on":
            case "wax_off": return Material.matchMaterial("HONEYCOMB");
            case "dust_color_transition": return Material.FLINT;
            case "electric_spark": return Material.END_ROD;
            case "explosion_huge":
            case "explosion_large":
            case "explosion_normal": return Material.TNT;
            case "falling_dust": return Material.GRAVEL;
            case "spore_blossom_air":
            case "falling_spore_blossom": return Material.matchMaterial("SPORE_BLOSSOM");
            case "fireworks_spawrk": return Material.FIREWORK;
            case "small_flame":
            case "flame": return Material.TORCH;
            case "flash": return Material.matchMaterial("LIGHTNING_ROD");
            case "glow": return Material.SEA_LANTERN;
            case "glow_squid_ink": return Material.matchMaterial("GLOW_INK_SAC");
            case "heart": return Material.GOLDEN_APPLE;
            case "item_crack": return Material.IRON_PICKAXE;
            case "mob_appearence": return Material.ROTTEN_FLESH;
            case "nautilus": return Material.matchMaterial("NAUTILUS_SHELL");
            case "note": return Material.NOTE_BLOCK;
            case "reverse_portal":
            case "portal": return Material.OBSIDIAN;
            case "scrape": return Material.IRON_BLOCK;
            case "sculk_charge":
            case "sculk_charge_pop":
            case "vibration":
            case "sculk_soul": return Material.matchMaterial("SCULK_SENSOR");
            case "shriek": return Material.matchMaterial("SCULK_SHRIEKER");
            case "slime": return Material.SLIME_BLOCK;
            case "smoke_large":
            case "smoke_normal": return Material.FURNACE;
            case "sneeze": return Material.PAPER;
            case "snow_shovel": return Material.IRON_HOE;
            case "snowflake":
            case "snowball": return Material.SNOW_BALL;
            case "sonic_boom": return Material.matchMaterial("SCULK");
            case "soul": return Material.SOUL_SAND;
            case "soul_fire_flame": return Material.matchMaterial("SOUL_TORCH");
            case "spell_mob":
            case "spell_witch":
            case "spell_mob_ambient":
            case "spell": return Material.ENCHANTMENT_TABLE;
            case "spit": return StarMaterial.LEAD.find();
            case "squid_ink": return Material.INK_SACK;
            case "suspended_depth":
            case "suspended": return Material.STRING;
            case "sweep_attack": return Material.GOLD_SWORD;
            case "totem": return StarMaterial.TOTEM_OF_UNDYING.find();
            case "villager_angry":
            case "villager_happy":
            case "town_aura": return Material.EMERALD;
            case "warped_spore": return Material.matchMaterial("WARPED_NYLIUM");
            case "water_drop":
            case "water_wake":
            case "water_bubble": return StarMaterial.LILY_PAD.find();
        }

        return Material.CLAY_BALL;
    }

    @NotNull
    public static Map<Integer, List<ItemStack>> generateRows(Iterable<ItemStack> it) {
        Map<Integer, List<ItemStack>> map = new HashMap<>();
        List<ItemStack> list = ImmutableList.copyOf(it);
        if (list.size() == 0) return map;

        int size = list.size();

        if (size < 7) {
            map.put(0, list);
            return map;
        } else {
            int rows = size / 7;
            int remainder = size % 7;

            for (int i = 0; i < rows; i++) map.put(i, list.subList(i * 7, (i + 1) * 7));

            if (remainder != 0) map.put(rows, list.subList(rows * 7, rows * 7 + remainder));
        }

        return map;
    }

}
