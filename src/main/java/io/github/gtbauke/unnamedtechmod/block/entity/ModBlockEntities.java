package io.github.gtbauke.unnamedtechmod.block.entity;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, UnnamedTechMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<BasicAlloySmelterEntity>> BASIC_ALLOY_SMELTER =
            BLOCK_ENTITIES.register(
                    "basic_alloy_smelter",
                    () -> BlockEntityType.Builder.of(
                            BasicAlloySmelterEntity::new,
                            ModBlocks.BASIC_ALLOY_SMELTER.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<ManualMaceratorEntity>> MANUAL_MACERATOR =
            BLOCK_ENTITIES.register(
                    "manual_macerator",
                    () -> BlockEntityType.Builder.of(
                            ManualMaceratorEntity::new,
                            ModBlocks.MANUAL_MACERATOR.get()
                    ).build(null)
            );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
