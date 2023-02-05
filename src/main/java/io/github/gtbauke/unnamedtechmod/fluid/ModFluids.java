package io.github.gtbauke.unnamedtechmod.fluid;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(
            ForgeRegistries.FLUIDS,
            UnnamedTechMod.MOD_ID
    );

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
