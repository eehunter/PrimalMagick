package com.verdantartifice.primalmagick.common.research.keys;

import java.util.Objects;

import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.common.research.IconDefinition;
import com.verdantartifice.primalmagick.common.research.requirements.RequirementCategory;
import com.verdantartifice.primalmagick.common.util.ItemUtils;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemScanKey extends AbstractResearchKey<ItemScanKey> {
    public static final MapCodec<ItemScanKey> CODEC = ItemStack.SINGLE_ITEM_CODEC.fieldOf("stack").xmap(ItemScanKey::new, key -> key.stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemScanKey> STREAM_CODEC = ItemStack.STREAM_CODEC.map(ItemScanKey::new, key -> key.stack);
    
    private static final String PREFIX = "!";
    
    protected final ItemStack stack;
    
    public ItemScanKey(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Item stack may not be null or empty");
        }
        this.stack = stack.copy();
    }

    public ItemScanKey(ItemLike itemLike) {
        this(new ItemStack(itemLike.asItem()));
    }
    
    @Override
    public String toString() {
        return PREFIX + ItemUtils.getHashCode(this.stack, true);
    }

    @Override
    public RequirementCategory getRequirementCategory() {
        return RequirementCategory.RESEARCH;
    }

    @Override
    protected ResearchKeyType<ItemScanKey> getType() {
        return ResearchKeyTypesPM.ITEM_SCAN.get();
    }

    @Override
    public IconDefinition getIcon(RegistryAccess registryAccess) {
        return IconDefinition.of(this.stack.getItem());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ForgeRegistries.ITEMS.getKey(this.stack.getItem()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemScanKey other = (ItemScanKey) obj;
        return ItemStack.isSameItem(this.stack, other.stack);
    }
}
