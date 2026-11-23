package com.liangforstudy.deaththekid.registry;

import com.liangforstudy.deaththekid.DeathTheKid;
import com.liangforstudy.deaththekid.data.SoulTier;
import com.liangforstudy.deaththekid.item.KishinSoulItem;
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

    public static final RegistryObject<KishinSoulItem> MINOR_KISHIN_SOUL = ITEMS.register("minor_kishin_soul",
            () -> new KishinSoulItem(SoulTier.MINOR, new Item.Properties()));
    public static final RegistryObject<KishinSoulItem> CORRUPTED_KISHIN_SOUL = ITEMS.register("corrupted_kishin_soul",
            () -> new KishinSoulItem(SoulTier.CORRUPTED, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<KishinSoulItem> ELITE_KISHIN_SOUL = ITEMS.register("elite_kishin_soul",
            () -> new KishinSoulItem(SoulTier.ELITE, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<KishinSoulItem> BOSS_KISHIN_SOUL = ITEMS.register("boss_kishin_soul",
            () -> new KishinSoulItem(SoulTier.BOSS, new Item.Properties().rarity(Rarity.EPIC)));

    public static Item soulFor(SoulTier tier) {
        return switch (tier) {
            case MINOR -> MINOR_KISHIN_SOUL.get();
            case CORRUPTED -> CORRUPTED_KISHIN_SOUL.get();
            case ELITE -> ELITE_KISHIN_SOUL.get();
            case BOSS -> BOSS_KISHIN_SOUL.get();
        };
    }

    private ModItems() {}
}
