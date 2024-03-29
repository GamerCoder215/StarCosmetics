package me.gamercoder215.starcosmetics.wrapper.cosmetics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.gamercoder215.starcosmetics.api.Rarity;
import me.gamercoder215.starcosmetics.api.cosmetics.*;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.PetInfo;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.PetType;
import me.gamercoder215.starcosmetics.util.StarMaterial;
import me.gamercoder215.starcosmetics.util.selection.*;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static me.gamercoder215.starcosmetics.api.CompletionCriteria.*;
import static me.gamercoder215.starcosmetics.api.Rarity.*;
import static me.gamercoder215.starcosmetics.api.cosmetics.BaseShape.circle;
import static me.gamercoder215.starcosmetics.api.cosmetics.BaseShape.line;
import static me.gamercoder215.starcosmetics.api.cosmetics.pet.HeadInfo.of;
import static me.gamercoder215.starcosmetics.util.selection.CapeSelection.cape;
import static me.gamercoder215.starcosmetics.util.selection.CapeSelection.of;
import static me.gamercoder215.starcosmetics.wrapper.cosmetics.CosmeticSelections.*;

final class CosmeticSelections1_16 implements CosmeticSelections {


    // Cosmetic Maps

    // Trails
    private static final List<CosmeticSelection<?>> PROJECTILE_TRAILS = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new TrailSelection("shroomlight", BaseTrail.PROJECTILE_TRAIL, Material.SHROOMLIGHT,
                    fromMined(110, Material.CRIMSON_STEM, Material.WARPED_STEM), COMMON))

            .add(new TrailSelection("blackstone", BaseTrail.PROJECTILE_TRAIL, "fancy_block:polished_blackstone_bricks",
                    fromCrafted(55, Material.POLISHED_BLACKSTONE_BRICKS), OCCASIONAL))

            .add(new TrailSelection("crying_obsidian", BaseTrail.PROJECTILE_TRAIL, Material.CRYING_OBSIDIAN,
                    fromMined(250, Material.OBSIDIAN), UNCOMMON))

            .add(new TrailSelection("netherite", BaseTrail.PROJECTILE_TRAIL, Material.NETHERITE_INGOT,
                    fromMined(120, Material.ANCIENT_DEBRIS), RARE))
            
            .add(new TrailSelection("super_netherite", BaseTrail.PROJECTILE_TRAIL, Material.NETHERITE_BLOCK,
                    fromMined(260, Material.ANCIENT_DEBRIS), EPIC))
            .add(new TrailSelection("pigstep", BaseTrail.PROJECTILE_TRAIL, Material.MUSIC_DISC_PIGSTEP,
                    fromKilled(500, EntityType.PIGLIN), EPIC))
            
            .add(new TrailSelection("netherite_sword", BaseTrail.PROJECTILE_TRAIL, "fancy_item:netherite_sword",
                    fromKilled(1250, EntityType.WITHER_SKELETON), MYTHICAL))
            
            .add(new TrailSelection("netherite_axe", BaseTrail.PROJECTILE_TRAIL, "fancy_item:netherite_axe",
                    fromKilled(2500, EntityType.WITHER_SKELETON), ULTRA))

            .build();

    // Ground Trails

    private static final List<CosmeticSelection<?>> GROUND_TRAILS = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new TrailSelection("crimson_nylium", BaseTrail.GROUND_TRAIL, "ground_block:crimson_nylium",
                    fromMined(130, Material.CRIMSON_STEM), OCCASIONAL))
            .add(new TrailSelection("warped_nylium", BaseTrail.GROUND_TRAIL, "ground_block:warped_nylium",
                    fromMined(130, Material.WARPED_STEM), OCCASIONAL))
            .add(new TrailSelection("crimson_planks", BaseTrail.GROUND_TRAIL, "ground_block:crimson_planks",
                    fromMined(80, Material.CRIMSON_ROOTS), OCCASIONAL))
            .add(new TrailSelection("warped_planks", BaseTrail.GROUND_TRAIL, "ground_block:warped_planks",
                    fromMined(80, Material.WARPED_ROOTS), OCCASIONAL))

            .add(new TrailSelection("blackstone", BaseTrail.GROUND_TRAIL, "ground_block:blackstone",
                    fromMined(200, Material.BLACKSTONE), UNCOMMON))
            .add(new TrailSelection("polished_basalt", BaseTrail.GROUND_TRAIL, "ground_block:polished_basalt",
                    fromMined(230, Material.BASALT), UNCOMMON))
            .add(new TrailSelection("basalt", BaseTrail.GROUND_TRAIL, "crack:basalt",
                    fromMined(210, Material.MAGMA_BLOCK), UNCOMMON))
            .add(new TrailSelection("crimson_slab", BaseTrail.GROUND_TRAIL, "ground_block:crimson_slab",
                    fromCrafted(185, Material.CRIMSON_PLANKS), UNCOMMON))
            .add(new TrailSelection("warped_slab", BaseTrail.GROUND_TRAIL, "ground_block:warped_slab",
                    fromCrafted(185, Material.WARPED_PLANKS), UNCOMMON))

            .add(new TrailSelection("soul_flame", BaseTrail.GROUND_TRAIL, Particle.SOUL_FIRE_FLAME,
                    fromMined(800, Material.SOUL_SAND, Material.SOUL_SOIL), RARE))
            .add(new TrailSelection("blackstone_pressure_plate", BaseTrail.GROUND_TRAIL, "side_block:polished_blackstone_pressure_plate",
                    fromMined(650, Material.BLACKSTONE), RARE))

            .add(new TrailSelection("crying_obsidian", BaseTrail.GROUND_TRAIL, "ground_block:crying_obsidian",
                    fromKilled(85, EntityType.GHAST), EPIC))
            .add(new TrailSelection("lodestone", BaseTrail.GROUND_TRAIL, "ground_block:lodestone",
                    fromMined(50000, Material.NETHERRACK), EPIC))

            .add(new TrailSelection("ancient_debris", BaseTrail.GROUND_TRAIL, "ground_block:ancient_debris",
                    fromKilled(10000, EntityType.WITHER_SKELETON), Rarity.LEGENDARY))

            .add(new TrailSelection("netherite_block", BaseTrail.GROUND_TRAIL, "ground_block:netherite_block",
                    fromMined(230, Material.ANCIENT_DEBRIS), MYTHICAL))

            .build();

    // Sound Trails

    private static final List<CosmeticSelection<?>> SOUND_TRAILS = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new TrailSelection("shroomlight_place", BaseTrail.SOUND_TRAIL, Sound.BLOCK_SHROOMLIGHT_PLACE,
                    fromMined(75, Material.CRIMSON_STEM, Material.WARPED_STEM), COMMON))

            .add(new TrailSelection("ancient_debris_place", BaseTrail.SOUND_TRAIL, Sound.BLOCK_ANCIENT_DEBRIS_PLACE,
                    fromMined(20, Material.ANCIENT_DEBRIS), RARE))

            .add(new TrailSelection("netherite_break", BaseTrail.SOUND_TRAIL, Sound.BLOCK_NETHERITE_BLOCK_BREAK,
                    fromKilled(490, EntityType.PIGLIN), EPIC))
            .add(new TrailSelection("bone_block_place", BaseTrail.SOUND_TRAIL, Sound.BLOCK_BONE_BLOCK_PLACE,
                    fromMined(80, Material.BONE_BLOCK), EPIC))
            .build();


    // Shapes

    private static final List<CosmeticSelection<?>> PARTICLE_SHAPES = ImmutableList.<CosmeticSelection<?>>builder()
            // Small Rings
            .add(new ParticleSelection("soul_flame", BaseShape.SMALL_RING, Particle.SOUL_FIRE_FLAME,
                    fromKilled(500, EntityType.WITHER_SKELETON), RARE))

            // Small Detailed Rings
            .add(new ParticleSelection("blackstone", BaseShape.SMALL_DETAILED_RING, Material.BLACKSTONE,
                    fromMined(230, Material.BLACKSTONE), RARE))
            .add(new ParticleSelection("warped_block", BaseShape.SMALL_DETAILED_RING, Material.WARPED_WART_BLOCK,
                    fromMined(145, Material.WARPED_NYLIUM), RARE))

            // Large Rings
            .add(new ParticleSelection("blackstone", BaseShape.LARGE_RING, Material.POLISHED_BLACKSTONE,
                    fromMined(100, Material.POLISHED_BLACKSTONE_BRICKS), UNCOMMON))

            // Small Triangles
            .add(new ParticleSelection("soul", BaseShape.SMALL_TRIANGLE, Particle.SOUL,
                    fromMined(110, Material.SOUL_SAND), UNCOMMON))

            // Combinations
            .add(new ParticleSelection("netherite_block", BaseShape.PENTAGON_RING, Material.NETHERITE_BLOCK,
                    fromMined(435, Material.ANCIENT_DEBRIS), MYTHICAL))
            
            .build();

    // Hats

    // Normal Hats

    private static final List<CosmeticSelection<?>> NORMAL_HATS = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new HatSelection("soul_soil", Material.SOUL_SOIL,
                    fromMined(45, Material.SOUL_SOIL), COMMON))

            .add(new HatSelection("polished_basalt", Material.POLISHED_BASALT,
                    fromMined(90, Material.BASALT), OCCASIONAL))

            .add(new HatSelection("blackstone", Material.BLACKSTONE,
                    fromMined(140, Material.BLACKSTONE), UNCOMMON))
            .add(new HatSelection("crying_obsidian", Material.CRYING_OBSIDIAN,
                    fromKilled(55, EntityType.GHAST), UNCOMMON))

            .add(new HatSelection("polished_blackstone", Material.POLISHED_BLACKSTONE,
                    fromMined(370, Material.BLACKSTONE), RARE))
            .add(new HatSelection("crimson_wart_block", Material.NETHER_WART_BLOCK,
                    fromMined(200, Material.CRIMSON_NYLIUM), RARE))
            .add(new HatSelection("warped_wart_block", Material.WARPED_WART_BLOCK,
                    fromMined(200, Material.WARPED_NYLIUM), RARE))

            .add(new HatSelection("lodestone", Material.LODESTONE,
                    fromMined(11000, Material.NETHERRACK), EPIC))

            .add(new HatSelection("netherite_block", Material.NETHERITE_BLOCK,
                    fromMined(260, Material.ANCIENT_DEBRIS), MYTHICAL))
            .build();

    // Gadgets

    private static final List<CosmeticSelection<?>> GADGETS = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new GadgetSelection("soul_flamethrower", Material.SOUL_TORCH, loc -> line(loc, Particle.SOUL_FIRE_FLAME, 9, 0, 0.005),
                    fromKilled(175, EntityType.WITHER_SKELETON), EPIC))
            .build();

    // Capes

    // Normal Capes
    private static final List<CosmeticSelection<?>> NORMAL_CAPES = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new CapeSelection("pig", cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.PINK, PatternType.PIGLIN)),
                    fromKilled(320, EntityType.PIGLIN), RARE))
            .build();

    // Animated Capes
    private static final List<CosmeticSelection<?>> ANIMATED_CAPES = ImmutableList.<CosmeticSelection<?>>builder()
            .add(new CapeSelection("animated_pig", of(10,
                    cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.RED, PatternType.PIGLIN)),
                    cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.ORANGE, PatternType.PIGLIN)),
                    cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.YELLOW, PatternType.PIGLIN)),
                    cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.LIME, PatternType.PIGLIN)),
                    cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.PURPLE, PatternType.PIGLIN)),
                    cape(StarMaterial.BLACK_BANNER, new Pattern(DyeColor.PINK, PatternType.PIGLIN))
            ), fromKilled(100000, EntityType.PIGLIN), MYTHICAL))
            .build();

    // Selections

    private static final Map<Cosmetic, List<CosmeticSelection<?>>> SELECTIONS = ImmutableMap.<Cosmetic, List<CosmeticSelection<?>>>builder()
            .put(BaseTrail.PROJECTILE_TRAIL, join(PROJECTILE_TRAILS, BaseTrail.PROJECTILE_TRAIL, "1_15"))
            .put(BaseTrail.SOUND_TRAIL, join(SOUND_TRAILS, BaseTrail.SOUND_TRAIL, "1_15"))
            .put(BaseTrail.GROUND_TRAIL, join(GROUND_TRAILS, BaseTrail.GROUND_TRAIL, "1_15"))

            .put(BaseShape.ALL, join(PARTICLE_SHAPES, BaseShape.ALL, "1_15"))

            .put(BaseHat.NORMAL, join(NORMAL_HATS, BaseHat.NORMAL, "1_15"))
            .put(BaseHat.ANIMATED, getForVersion(BaseHat.ANIMATED, "1_15"))

            .put(BaseGadget.INSTANCE, join(GADGETS, BaseGadget.INSTANCE, "1_15"))

            .put(BaseCape.NORMAL, join(NORMAL_CAPES, BaseCape.NORMAL, "1_15"))
            .put(BaseCape.ANIMATED, join(ANIMATED_CAPES, BaseCape.ANIMATED, "1_15"))
            .build();

    @Override
    public Map<Cosmetic, List<CosmeticSelection<?>>> getAllSelections() {
        return SELECTIONS;
    }

    @Override
    public void loadPets() {
        PET_MAP.putAll(
                ImmutableMap.<PetType, PetInfo>builder()
                        .put(PetType.DEMOGORGON, of(
                                "Demogorgon", Rarity.LEGENDARY,
                                petIcon("demogorgon_pet", "Demogorgon"), fromMined(500000, Material.BASALT), stand -> {
                                    if (r.nextInt(100) < 3) w.spawnFakeItem(new ItemStack(Material.BONE), head(stand), 5);
                                    if (r.nextInt(100) < 3) w.spawnFakeItem(new ItemStack(Material.BONE_MEAL), head(stand), 5);

                                    if (r.nextInt(100) < 10) stand.getWorld().spawnParticle(Particle.BLOCK_CRACK, head(stand), 5, Material.REDSTONE_BLOCK.createBlockData());
                                }
                        ))

                        .put(PetType.STRIDER, of(
                                "Strider", MYTHICAL,
                                petIcon("strider_pet", "Strider"), fromKilled(10000, EntityType.ZOMBIFIED_PIGLIN), stand -> {
                                    if (r.nextInt(100) < 5) {
                                        circle(head(stand), Particle.SOUL_FIRE_FLAME, 10, 0.6);
                                        circle(head(stand), Particle.FLAME, 5, 0.5);
                                    }
                                    if (r.nextInt(100) < 10) w.spawnFakeItem(new ItemStack(Material.STRING), head(stand), 5);
                                }
                        ))

                        .build()
        );

        loadExternalPets("1_15");
    }

}
