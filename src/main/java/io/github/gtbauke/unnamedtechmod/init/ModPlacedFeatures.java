package io.github.gtbauke.unnamedtechmod.init;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.config.ModCommonConfig;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(
            Registry.PLACED_FEATURE_REGISTRY,
            UnnamedTechMod.MOD_ID
    );

    public static final RegistryObject<PlacedFeature> TIN_ORE_PLACED = registerCommonOre(
            "tin_ore_placed",
            ModConfiguredFeatures.TIN_ORE,
            ModCommonConfig.TIN_ORE_VEINS_PER_CHUNK
    );

    public static final RegistryObject<PlacedFeature> LEAD_ORE_PLACED = registerCommonOre(
            "lead_ore_placed",
            ModConfiguredFeatures.LEAD_ORE,
            ModCommonConfig.LEAD_ORE_VEINS_PER_CHUNK
    );

    private static RegistryObject<PlacedFeature> registerCommonOre(String name, RegistryObject<ConfiguredFeature<?, ?>> ore, ForgeConfigSpec.ConfigValue<Integer> veinsPerChunkConfig) {
        return PLACED_FEATURES.register(
                name,
                () -> {
                    int veinsPerChunk;

                    try {
                        veinsPerChunk = veinsPerChunkConfig.get();
                    } catch (Exception e) {
                        veinsPerChunk = veinsPerChunkConfig.getDefault();
                    }

                    return new PlacedFeature(
                            ore.getHolder().get(),
                            commonOrePlacement(veinsPerChunk, HeightRangePlacement.triangle(
                                    VerticalAnchor.aboveBottom(-80),
                                    VerticalAnchor.aboveBottom(80)
                            ))
                    );
                }
        );
    }

    public static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier2) {
        return List.of(modifier, InSquarePlacement.spread(), modifier2, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int a, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(a), modifier);
    }

    public static List<PlacementModifier> rareOrePlacement(int a, PlacementModifier modifier) {
        return orePlacement(RarityFilter.onAverageOnceEvery(a), modifier);
    }
}
