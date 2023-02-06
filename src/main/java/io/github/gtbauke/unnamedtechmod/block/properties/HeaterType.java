package io.github.gtbauke.unnamedtechmod.block.properties;

import net.minecraft.util.StringRepresentable;

public enum HeaterType implements StringRepresentable {
    UNCONNECTED("unconnected"),
    CONNECTED_TO_PRESS("connected_to_press");

    private final String name;

    HeaterType(String pName) {
        name = pName;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
