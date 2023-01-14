package io.github.gtbauke.unnamedtechmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Configs for Unnamed Tech Mod");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
