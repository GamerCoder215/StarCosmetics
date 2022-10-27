package me.gamercoder215.starcosmetics.api.cosmetics.trail;

import me.gamercoder215.starcosmetics.api.cosmetics.Cosmetic;
import me.gamercoder215.starcosmetics.api.cosmetics.registry.CosmeticLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a StarCosmetics Trail
 * @param <T> Trail Object Type (Item Trails, Entity Trails, etc.)
 */
public interface Trail<T> extends Cosmetic {

    /**
     * Fetches the type of the object used in the trail.
     * @return Trail Object Type ({@link Particle}, {@link Material}, etc)
     */
    @NotNull
    Class<T> getObjectType();

    /**
     * Fetches the type of this trail.
     * @return Trail Type
     */
    @NotNull
    TrailType getType();

    /**
     * Runs a CosmeticLocation based on this parent.
     * @param en Entity to use
     * @param loc CosmeticLocation to use
     */
    void run(@NotNull Entity en, CosmeticLocation<?> loc);

    /**
     * @deprecated use {@link #run(Entity, CosmeticLocation)}
     */
    @Override
    @Deprecated
    default void run(Location l, CosmeticLocation<?> cloc) {
        throw new UnsupportedOperationException();
    }

}
