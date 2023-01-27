package io.github.gtbauke.unnamedtechmod.screen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, UnnamedTechMod.MOD_ID);

    public static final RegistryObject<MenuType<BasicAlloySmelterMenu>> BASIC_ALLOY_SMELTER_MENU =
            registerMenuType(BasicAlloySmelterMenu::new, "basic_alloy_smelter_menu");

    public static final RegistryObject<MenuType<ManualMaceratorMenu>> MANUAL_MACERATOR_MENU =
            registerMenuType(ManualMaceratorMenu::new, "manual_macerator_menu");

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
