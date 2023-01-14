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
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC)
            )
    );

    public static final RegistryObject<Item> RAW_TIN_ORE = ITEMS.register(
            "raw_tin_ore",
            () -> new Item(
                    new Item.Properties()
                            .tab(CreativeModeTab.TAB_MATERIALS)
            )
    );
    
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
