package io.github.gtbauke.unnamedtechmod.init;

import com.google.common.base.Suppliers;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, UnnamedTechMod.MOD_ID);

    public static final Supplier<List<OreConfiguration.TargetBlockState>> OVERWORLD_TIN_ORE =
            Suppliers.memoize(() -> List.of(
                    OreConfiguration.target(
                            OreFeatures.STONE_ORE_REPLACEABLES,
                            ModBlocks.TIN_ORE.get().defaultBlockState()
                    ),
                    OreConfiguration.target(
                            OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
                            ModBlocks.DEEPSLATE_TIN_ORE.get().defaultBlockState()
                    ))
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> TIN_ORE = CONFIGURED_FEATURE.register(
            "tin_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                    OVERWORLD_TIN_ORE.get(),
                    7 // Vein Size, should be changeable in the config
            ))
    );
}
