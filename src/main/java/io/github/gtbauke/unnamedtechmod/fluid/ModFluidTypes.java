package io.github.gtbauke.unnamedtechmod.fluid;

import com.mojang.math.Vector3f;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING = new ResourceLocation("block/water_flow");
    public static final ResourceLocation STEAM_OVERLAY = new ResourceLocation(
            UnnamedTechMod.MOD_ID,
            "misc/steam_overlay"
    );

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            ForgeRegistries.Keys.FLUID_TYPES,
            UnnamedTechMod.MOD_ID
    );

    public static final RegistryObject<FluidType> STEAM_FLUID_TYPE = register("steam_fluid", FluidType.Properties
            .create()
            .lightLevel(2)
            .density(5)
            .motionScale(1.2)
            .temperature(300)
            .canSwim(false)
            .viscosity(2)
            .supportsBoating(false)
            .sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK));

    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL, WATER_FLOWING, STEAM_OVERLAY,
                0x8b9496, new Vector3f(139f / 255f, 148f / 255f, 150f / 255f), properties));
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
    }
}
