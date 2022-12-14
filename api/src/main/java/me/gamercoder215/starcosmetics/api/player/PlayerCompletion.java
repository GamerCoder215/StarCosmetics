package me.gamercoder215.starcosmetics.api.player;

import me.gamercoder215.starcosmetics.api.Completion;
import me.gamercoder215.starcosmetics.api.Rarity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom completion a player has to achieve to unlock GENERAL cosmetics, through any toggled event.
 */
public enum PlayerCompletion implements Completion {

    /**
     * Completion for being struck by lightning.
     */
    LIGHTNING(Rarity.RARE),

    /**
     * Completion for reaching the roof of the Nether.
     */
    NETHER_ROOF(Rarity.EPIC),

    /**
     * Completion for dying to a warden with its sonic boom attack.
     */
    SONIC_BOOM_DEATH(Rarity.LEGENDARY),
    ;

    private final Rarity rarity;

    PlayerCompletion(Rarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public @NotNull String getKey() {
        return name().toLowerCase();
    }

    @Override
    public @NotNull String getNamespace() {
        return "player";
    }

    @Override
    public @NotNull Rarity getRarity() {
        return rarity;
    }
}
