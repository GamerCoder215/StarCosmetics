package me.gamercoder215.starcosmetics.wrapper.cosmetics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.gamercoder215.starcosmetics.api.CompletionCriteria;
import me.gamercoder215.starcosmetics.api.Rarity;
import me.gamercoder215.starcosmetics.api.cosmetics.*;
import me.gamercoder215.starcosmetics.util.selection.CosmeticSelection;
import me.gamercoder215.starcosmetics.util.selection.TrailSelection;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

import static me.gamercoder215.starcosmetics.wrapper.cosmetics.CosmeticSelections.getForVersion;
import static me.gamercoder215.starcosmetics.wrapper.cosmetics.CosmeticSelections.join;

final class CosmeticSelections1_18 implements CosmeticSelections {

    // Cosmetic Maps

    // Trails
    private static final List<CosmeticSelection<?>> PROJECTILE_TRAILS = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new TrailSelection("otherside", BaseTrail.PROJECTILE_TRAIL, Material.MUSIC_DISC_OTHERSIDE,
                    CompletionCriteria.fromMined(2500, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE), Rarity.RARE))
            .build();

    // Selections

    private static final Map<Cosmetic, List<CosmeticSelection<?>>> SELECTIONS = ImmutableMap.<Cosmetic, List<CosmeticSelection<?>>>builder()
            .put(BaseTrail.PROJECTILE_TRAIL, join(PROJECTILE_TRAILS, BaseTrail.PROJECTILE_TRAIL, "1_17"))
            .put(BaseTrail.SOUND_TRAIL, getForVersion(BaseTrail.SOUND_TRAIL, "1_17"))
            .put(BaseTrail.GROUND_TRAIL, getForVersion(BaseTrail.GROUND_TRAIL, "1_17"))

            .put(BaseShape.ALL, getForVersion(BaseShape.ALL, "1_17"))

            .put(BaseHat.NORMAL, getForVersion(BaseHat.NORMAL, "1_17"))
            .put(BaseHat.ANIMATED, getForVersion(BaseHat.ANIMATED, "1_17"))

            .put(BaseGadget.INSTANCE, getForVersion(BaseGadget.INSTANCE, "1_17"))

            .put(BaseCape.NORMAL, getForVersion(BaseCape.NORMAL, "1_17"))
            .put(BaseCape.ANIMATED, getForVersion(BaseCape.ANIMATED, "1_17"))
            .build();

    @Override
    public Map<Cosmetic, List<CosmeticSelection<?>>> getAllSelections() {
        return SELECTIONS;
    }

    @Override
    public void loadPets() {
        CosmeticSelections.loadExternalPets("1_17");
    }
}
