package me.gamercoder215.starcosmetics.api.player;

import com.google.common.collect.ImmutableList;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.api.cosmetics.particle.ParticleReducer;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.PetCosmetics;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.PetPosition;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a setting available to players.
 * @param <T> Setting Type
 */
@SuppressWarnings("unchecked")
public final class PlayerSetting<T> {

    /**
     * Whether the player should receive chat notifications.
     */
    @SettingDescription("settings.notifications.desc")
    public static final PlayerSetting<Boolean> NOTIFICATIONS =
            ofBoolean("notifications", "settings.notifications", true);

    /**
     * Whether the player should receive sound notifications.
     */
    @SettingDescription("settings.notifications.sound.desc")
    public static final PlayerSetting<Boolean> SOUND_NOTIFICATIONS =
            ofBoolean("sound_notifications", "settings.notifications.sound", true);

    /**
     * How much the player should be launched when spawning a structure, to "avoid" suffocation.
     */
    @SettingDescription("settings.structure_velocity.desc")
    public static final PlayerSetting<VelocityPower> STRUCTURE_VELOCITY =
            ofEnum("structure_velocity", "settings.structure_velocity", VelocityPower.MEDIUM);

    /**
     * Where the player's pet should be placed.
     */
    @SettingDescription("settings.pet_position.desc")
    public static final PlayerSetting<PetPosition> PET_POSITION =
            ofEnum("pet_position", "settings.pet_position", PetPosition.BEHIND);

    /**
     * What cosmetics the player's pets should have.
     */
    @SettingDescription("settings.pet_cosmetics.desc")
    public static final PlayerSetting<PetCosmetics> PET_COSMETICS =
            ofEnum("pet_cosmetics", "settings.pet_cosmetics", PetCosmetics.OWNER_ONLY);

    /**
     * The color/bold format for the Hologram.
     */
    public static final PlayerSetting<HologramFormat> HOLOGRAM_FORMAT =
            ofEnum("hologram_format", "settings.hologram_format", HologramFormat.YELLOW);

    /**
     * How much to reduce particles by.
     */
    @SettingDescription("settings.particle_reduction.desc")
    public static final PlayerSetting<ParticleReducer> PARTICLE_REDUCTION =
            ofEnum("particle_reduction", "settings.particle_reduction", ParticleReducer.NORMAL);

    /**
     * The color of the armor when using emotes.
     */
    @SettingDescription("settings.emote_color.desc")
    public static final PlayerSetting<DyeColor> EMOTE_COLOR =
    		ofEnum("emote_color", "settings.emote_color", DyeColor.LIME);

    // Setting Enums

    /**
     * Enum used to determine the Velocity Power of {@link PlayerSetting#STRUCTURE_VELOCITY}.
     */
    public enum VelocityPower {
        /**
         * The player will not be launched.
         */
        NONE(0),
        /**
         * The player will be launched a little bit.
         */
        WEAK(0.75),
        /**
         * The player will be launched a medium amount.
         */
        MEDIUM(2.5),
        /**
         * The player will be launched a lot.
         */
        STRONG(4)
        ;

        private final double launchPower;

        VelocityPower(double launchPower) {
            this.launchPower = launchPower;
        }

        /**
         * Fetches the launch power for this VelocityPower.
         * @return Launch Power
         */
        public double getLaunchPower() {
            return launchPower;
        }
    }

    /**
     * Represents a Hologram Format.
     */
    public enum HologramFormat {

        DARK_RED(ChatColor.DARK_RED), RED(ChatColor.RED), GOLD(ChatColor.GOLD), YELLOW(ChatColor.YELLOW), DARK_GREEN(ChatColor.DARK_GREEN), GREEN(ChatColor.GREEN), AQUA(ChatColor.AQUA), DARK_AQUA(ChatColor.DARK_AQUA), DARK_BLUE(ChatColor.DARK_BLUE), BLUE(ChatColor.BLUE), LIGHT_PURPLE(ChatColor.LIGHT_PURPLE), DARK_PURPLE(ChatColor.DARK_PURPLE), WHITE(ChatColor.WHITE), GRAY(ChatColor.GRAY), DARK_GRAY(ChatColor.DARK_GRAY), BLACK(ChatColor.BLACK),

        BOLD_DARK_RED(ChatColor.DARK_RED, ChatColor.BOLD), BOLD_RED(ChatColor.RED, ChatColor.BOLD), BOLD_GOLD(ChatColor.GOLD, ChatColor.BOLD), BOLD_YELLOW(ChatColor.YELLOW, ChatColor.BOLD), BOLD_DARK_GREEN(ChatColor.DARK_GREEN, ChatColor.BOLD), BOLD_GREEN(ChatColor.GREEN, ChatColor.BOLD), BOLD_AQUA(ChatColor.AQUA, ChatColor.BOLD), BOLD_DARK_AQUA(ChatColor.DARK_AQUA, ChatColor.BOLD), BOLD_DARK_BLUE(ChatColor.DARK_BLUE, ChatColor.BOLD), BOLD_BLUE(ChatColor.BLUE, ChatColor.BOLD), BOLD_LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, ChatColor.BOLD), BOLD_DARK_PURPLE(ChatColor.DARK_PURPLE, ChatColor.BOLD), BOLD_WHITE(ChatColor.WHITE, ChatColor.BOLD), BOLD_GRAY(ChatColor.GRAY, ChatColor.BOLD), BOLD_DARK_GRAY(ChatColor.DARK_GRAY, ChatColor.BOLD), BOLD_BLACK(ChatColor.BLACK, ChatColor.BOLD),
        ;

        private final String toString;

        HologramFormat(ChatColor... format) {
            this.toString = Arrays.stream(format).map(ChatColor::toString).collect(Collectors.joining());
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    private final T def;
    private final Class<T> type;
    private final String displayKey;

    private final String id;
    private final List<T> values = new ArrayList<>();

    private PlayerSetting(String id, String displayKey, Class<T> type, T def) {
        this.displayKey = displayKey;
        this.def = def;
        this.type = type;
        this.id = id;
    }

    @SafeVarargs
    private PlayerSetting(String id, String displayKey, Class<T> type, T def, T... values) {
        this.displayKey = displayKey;
        this.def = def;
        this.type = type;
        this.id = id;
        this.values.addAll(Arrays.asList(values));
    }

    // Default Value Impl

    private static PlayerSetting<Boolean> ofBoolean(String id, String displayKey, boolean def) {
        return new PlayerSetting<>(id, displayKey, Boolean.class, def, true, false);
    }

    private static <T extends Enum<T>> PlayerSetting<T> ofEnum(String id, String displayKey, T def) {
        return new PlayerSetting<>(id, displayKey, (Class<T>) def.getClass(), def, (T[]) def.getClass().getEnumConstants());
    }

    /**
     * Fetches the default value of this setting.
     * @return Default Value
     */
    @NotNull
    public T getDefaultValue() {
        return def;
    }

    /**
     * Fetches the Setting's ID.
     * @return Setting ID
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Fetches the Setting Type.
     * @return Setting Type
     */
    @NotNull
    public Class<T> getType() {
        return type;
    }

    /**
     * Fetches all of the possible values for this setting.
     * @return List of Possible Values
     */
    @NotNull
    public List<T> getPossibleValues() {
        return ImmutableList.copyOf(values);
    }

    /**
     * Fetches the setting value off of a Player.
     * @param p Player to fetch from
     * @param setting Setting to fetch
     * @param <T> Setting Type
     * @return Setting Value
     * @throws IllegalArgumentException if player or setting is null
     */
    public static <T> T getSettingValue(@NotNull Player p, @NotNull PlayerSetting<T> setting) throws IllegalArgumentException {
        if (p == null) throw new IllegalArgumentException("Player cannot be null!");
        if (setting == null) throw new IllegalArgumentException("Setting cannot be null!");

        StarPlayer sp = new StarPlayer(p);
        return sp.getSetting(setting);
    }

    /**
     * Fetches the setting's display name.
     * @return Setting Display Name
     */
    @NotNull
    public String getDisplayName() {
        return StarConfig.getConfig().get(displayKey);
    }

    /**
     * Fetches the setting's description.
     * @return Setting Description
     */
    @NotNull
    public String getDescription() {
        try {
            Field f = PlayerSetting.class.getDeclaredField(id.toUpperCase());
            f.setAccessible(true);

            SettingDescription desc = f.getDeclaredAnnotation(SettingDescription.class);
            if (desc == null) return "";

            return desc.value();
        } catch (NoSuchFieldException e) {
            StarConfig.print(e);
        }

        return null;
    }

    /**
     * Creates an array of all of the Settings.
     * @return Setting Values Array
     */
    @NotNull
    public static PlayerSetting<?>[] values() {
        List<PlayerSetting<?>> values = new ArrayList<>();

        try {
            for (Field f : PlayerSetting.class.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) continue;

                if (PlayerSetting.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    values.add((PlayerSetting<?>) f.get(null));
                }
            }
        } catch (ReflectiveOperationException e) {
            StarConfig.print(e);
        }

        values.sort(Comparator.comparing(PlayerSetting::getId));

        return values.toArray(new PlayerSetting[0]);
    }

    /**
     * Fetches a Setting by its ID.
     * @param id Setting ID
     * @return Setting
     */
    @Nullable
    public static PlayerSetting<?> byId(@Nullable String id) {
        if (id == null) return null;

        return Arrays.stream(values())
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj == null) return false;
    	if (!(obj instanceof PlayerSetting)) return false;
    	PlayerSetting<?> setting = (PlayerSetting<?>) obj;
    	return setting.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
