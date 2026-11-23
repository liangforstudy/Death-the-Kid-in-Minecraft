package com.liangforstudy.deaththekid.registry;

import com.liangforstudy.deaththekid.DeathTheKid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DeathTheKid.MOD_ID);

    // Placeholder: becomes DeathContractItem (grants Liz and Patty) in Phase D.
    public static final RegistryObject<Item> SHINIGAMI_PACT = ITEMS.register("shinigami_pact",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    private ModItems() {}
}
