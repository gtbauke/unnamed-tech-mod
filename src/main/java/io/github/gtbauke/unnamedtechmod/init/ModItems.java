package io.github.gtbauke.unnamedtechmod.init;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, UnnamedTechMod.MOD_ID);

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register(
            "example_item",
            () -> new Item(
                    new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> RAW_TIN_ORE = ITEMS.register(
            "raw_tin_ore",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> TIN_DUST = ITEMS.register(
            "tin_dust",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> IRON_DUST = ITEMS.register(
            "iron_dust",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> GOLD_DUST = ITEMS.register(
            "gold_dust",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> COPPER_DUST = ITEMS.register(
            "copper_dust",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> TIN_PLATE = ITEMS.register(
            "tin_plate",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> IRON_PLATE = ITEMS.register(
            "iron_plate",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> GOLD_PLATE = ITEMS.register(
            "gold_plate",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> COPPER_PLATE = ITEMS.register(
            "copper_plate",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register(
            "tin_ingot",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> LIGHT_CLAY_BALL = ITEMS.register(
            "light_clay_ball",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> LIGHT_BRICK = ITEMS.register(
            "light_brick",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> REDSTONE_COPPER_ALLOY = ITEMS.register(
            "redstone_copper_alloy",
            () -> new Item(
                    new Item.Properties()
                            .tab(UnnamedTechMod.MOD_TAB)
            )
    );

    public static final RegistryObject<Item> BRONZE_INGOT = registerIngot("bronze");
    public static final RegistryObject<Item> BRONZE_PLATE = registerPlate("bronze");
    public static final RegistryObject<Item> BRONZE_DUST = registerDust("bronze");

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private static RegistryObject<Item> registerIngot(String name) {
        return ITEMS.register(name + "_ingot", () -> new Item(
                new Item.Properties()
                        .tab(UnnamedTechMod.MOD_TAB)
        ));
    }

    private static RegistryObject<Item> registerPlate(String name) {
        return ITEMS.register(name + "_plate", () -> new Item(
                new Item.Properties()
                        .tab(UnnamedTechMod.MOD_TAB)
        ));
    }

    private static RegistryObject<Item> registerDust(String name) {
        return ITEMS.register(name + "_dust", () -> new Item(
                new Item.Properties()
                        .tab(UnnamedTechMod.MOD_TAB)
        ));
    }
}
