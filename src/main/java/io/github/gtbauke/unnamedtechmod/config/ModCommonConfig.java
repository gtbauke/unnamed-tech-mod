package io.github.gtbauke.unnamedtechmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> TIN_ORE_VEINS_PER_CHUNK;
    public static final ForgeConfigSpec.ConfigValue<Integer> TIN_ORE_VEIN_SIZE;

    static {
        BUILDER.push("Configs for Unnamed Tech Mod");

        TIN_ORE_VEINS_PER_CHUNK = BUILDER.comment("Tin Ore Veins per chunk").define("Veins per chunk", 7);
        TIN_ORE_VEIN_SIZE = BUILDER.comment("Tin Ore Blocks in one vein").defineInRange("Vein Size", 9, 4, 20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
