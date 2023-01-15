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

    public static final RegistryObject<Item> TIN_PLATE = ITEMS.register(
            "tin_plate",
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
    
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
