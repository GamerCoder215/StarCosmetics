package me.gamercoder215.starcosmetics.util.selection;

import me.gamercoder215.starcosmetics.api.cosmetics.BaseGadget;
import me.gamercoder215.starcosmetics.api.cosmetics.CompletionCriteria;
import me.gamercoder215.starcosmetics.api.cosmetics.Cosmetic;
import me.gamercoder215.starcosmetics.api.cosmetics.CosmeticRarity;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public final class GadgetSelection extends CosmeticSelection implements Consumer<PlayerInteractEvent> {

    private final String name;
    private final ItemStack item;
    private final Consumer<PlayerInteractEvent> clickAction;

    private GadgetSelection(String name, CompletionCriteria criteria, CosmeticRarity rarity, ItemStack item, Consumer<PlayerInteractEvent> action) {
        super(item, criteria, rarity);
        this.name = name;
        this.item = item;
        this.clickAction = action;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void accept(PlayerInteractEvent playerInteractEvent) {
        clickAction.accept(playerInteractEvent);
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public Cosmetic getParent() {
        return BaseGadget.CLICK_GADGET;
    }

    public ItemStack getItem() {
        return item;
    }

    public static final class Builder {

        ItemStack item;
        String id;
        Consumer<PlayerInteractEvent> action;
        CosmeticRarity rarity;
        CompletionCriteria criteria;

        private Builder() {}

        public Builder info(String id, CompletionCriteria criteria, CosmeticRarity rarity) {
            this.id = id;
            this.rarity = rarity;
            this.criteria = criteria;
            return this;
        }

        public Builder item(ItemStack item) {
            this.item = item;
            return this;
        }

        public Builder item(Material m) {
            return item(new ItemStack(m));
        }

        public Builder action(Consumer<PlayerInteractEvent> action) {
            this.action = action;
            return this;
        }

        public GadgetSelection build() {
            return new GadgetSelection(id, criteria, rarity, item, action);
        }

    }

}
