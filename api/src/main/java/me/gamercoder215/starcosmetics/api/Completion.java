package me.gamercoder215.starcosmetics.api;

import org.jetbrains.annotations.NotNull;

/**
 * Represents any Completion a player can achieve.
 */
public interface Completion {

    /**
     * Fetches the criteria for this Completion.
     * @return Completion Criteria
     */
    @NotNull
    default CompletionCriteria getCriteria() {
        return CompletionCriteria.fromCompletion(this);
    }

    /**
     * Fetches the key used in the config to fetch whether this Completion is done.
     * @return Completion Configuration Key
     */
    @NotNull
    String getKey();

    /**
     * Fetches the namespace of this Completion.
     * @return Completion Namespace
     */
    @NotNull
    String getNamespace();

    /**
     * Fetches the Rarity of this Completion.
     * @return Completion Rarity
     */
    @NotNull
    Rarity getRarity();

    /**
     * Constructs the Completion's full key.
     * @return Full Key
     */
    @NotNull
    default String getFullKey() {
        return getNamespace() + ":" + getKey();
    }

}
