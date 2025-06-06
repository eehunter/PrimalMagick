package com.verdantartifice.primalmagick.common.tiles.devices;

import com.verdantartifice.primalmagick.common.capabilities.IItemHandlerPM;
import com.verdantartifice.primalmagick.common.capabilities.IManaStorage;
import com.verdantartifice.primalmagick.common.capabilities.ManaStorage;
import com.verdantartifice.primalmagick.common.components.DataComponentsPM;
import com.verdantartifice.primalmagick.common.crafting.IDissolutionRecipe;
import com.verdantartifice.primalmagick.common.crafting.RecipeTypesPM;
import com.verdantartifice.primalmagick.common.menus.DissolutionChamberMenu;
import com.verdantartifice.primalmagick.common.sources.IManaContainer;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.tiles.BlockEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.AbstractTileSidedInventoryPM;
import com.verdantartifice.primalmagick.common.wands.IWand;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Definition of a dissolution chamber tile entity.  Performs the processing for the corresponding block.
 * 
 * @author Daedalus4096
 * @see com.verdantartifice.primalmagick.common.blocks.devices.DissolutionChamberBlock
 */
public abstract class DissolutionChamberTileEntity extends AbstractTileSidedInventoryPM implements MenuProvider, IManaContainer, StackedContentsCompatible {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int OUTPUT_INV_INDEX = 0;
    public static final int INPUT_INV_INDEX = 1;
    public static final int WAND_INV_INDEX = 2;
    
    protected int processTime;
    protected int processTimeTotal;
    protected ManaStorage manaStorage;

    // Define a container-trackable representation of this tile's relevant data
    protected final ContainerData chamberData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
            case 0:
                return DissolutionChamberTileEntity.this.processTime;
            case 1:
                return DissolutionChamberTileEntity.this.processTimeTotal;
            case 2:
                return DissolutionChamberTileEntity.this.manaStorage.getManaStored(Sources.EARTH);
            case 3:
                return DissolutionChamberTileEntity.this.manaStorage.getMaxManaStored(Sources.EARTH);
            default:
                return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            // Don't set mana storage values
            switch (index) {
            case 0:
                DissolutionChamberTileEntity.this.processTime = value;
                break;
            case 1:
                DissolutionChamberTileEntity.this.processTimeTotal = value;
                break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    
    public DissolutionChamberTileEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesPM.DISSOLUTION_CHAMBER.get(), pos, state);
        this.manaStorage = new ManaStorage(10000, 1000, 1000, Sources.EARTH);
    }

    public IManaStorage<?> getUncachedManaStorage() {
        return this.manaStorage;
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.processTime = compound.getInt("ProcessTime");
        this.processTimeTotal = compound.getInt("ProcessTimeTotal");
        ManaStorage.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), compound.get("ManaStorage")).resultOrPartial(msg -> {
            LOGGER.error("Failed to decode mana storage: {}", msg);
        }).ifPresent(mana -> mana.copyManaInto(this.manaStorage));
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putInt("ProcessTime", this.processTime);
        compound.putInt("ProcessTimeTotal", this.processTimeTotal);
        ManaStorage.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.manaStorage).resultOrPartial(msg -> {
            LOGGER.error("Failed to encode mana storage: {}", msg);
        }).ifPresent(encoded -> compound.put("ManaStorage", encoded));
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new DissolutionChamberMenu(windowId, playerInv, this.getBlockPos(), this, this.chamberData);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    protected int getProcessTimeTotal() {
        return 100;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DissolutionChamberTileEntity entity) {
        boolean shouldMarkDirty = false;
        
        if (!level.isClientSide) {
            // Fill up internal mana storage with that from any inserted wands
            ItemStack wandStack = entity.getItem(WAND_INV_INDEX, 0);
            if (!wandStack.isEmpty() && wandStack.getItem() instanceof IWand wand) {
                int centimanaMissing = entity.manaStorage.getMaxManaStored(Sources.EARTH) - entity.manaStorage.getManaStored(Sources.EARTH);
                int centimanaToTransfer = Mth.clamp(centimanaMissing, 0, 100);
                if (wand.consumeMana(wandStack, null, Sources.EARTH, centimanaToTransfer, level.registryAccess())) {
                    entity.manaStorage.receiveMana(Sources.EARTH, centimanaToTransfer, false);
                    shouldMarkDirty = true;
                }
            }

            SingleRecipeInput testInv = new SingleRecipeInput(entity.getItem(INPUT_INV_INDEX, 0));
            RecipeHolder<IDissolutionRecipe> recipe = level.getServer().getRecipeManager().getRecipeFor(RecipeTypesPM.DISSOLUTION.get(), testInv, level).orElse(null);
            if (entity.canDissolve(testInv, level.registryAccess(), recipe)) {
                entity.processTime++;
                if (entity.processTime >= entity.processTimeTotal) {
                    entity.processTime = 0;
                    entity.processTimeTotal = entity.getProcessTimeTotal();
                    entity.doDissolve(testInv, level.registryAccess(), recipe);
                    shouldMarkDirty = true;
                }
            } else {
                entity.processTime = Mth.clamp(entity.processTime - 2, 0, entity.processTimeTotal);
            }
        }

        if (shouldMarkDirty) {
            entity.setChanged();
            entity.syncTile(true);
        }
    }
    
    protected boolean canDissolve(SingleRecipeInput inputInv, RegistryAccess registryAccess, RecipeHolder<IDissolutionRecipe> recipe) {
        if (!inputInv.isEmpty() && recipe != null) {
            ItemStack output = recipe.value().getResultItem(registryAccess);
            if (output.isEmpty()) {
                return false;
            } else if (this.getMana(Sources.EARTH) < recipe.value().getManaCosts().getAmount(Sources.EARTH)) {
                return false;
            } else {
                ItemStack currentOutput = this.getItem(OUTPUT_INV_INDEX, 0);
                if (currentOutput.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItem(currentOutput, output)) {
                    return false;
                } else if (currentOutput.getCount() + output.getCount() <= this.itemHandlers.get(OUTPUT_INV_INDEX).getSlotLimit(0) && 
                        currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize()) {
                    return true;
                } else {
                    return currentOutput.getCount() + output.getCount() <= output.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }
    
    protected void doDissolve(SingleRecipeInput inputInv, RegistryAccess registryAccess, RecipeHolder<IDissolutionRecipe> recipe) {
        if (recipe != null && this.canDissolve(inputInv, registryAccess, recipe)) {
            ItemStack recipeOutput = recipe.value().assemble(inputInv, registryAccess);
            ItemStack currentOutput = this.getItem(OUTPUT_INV_INDEX, 0);
            if (currentOutput.isEmpty()) {
                this.setItem(OUTPUT_INV_INDEX, 0, recipeOutput);
            } else if (ItemStack.isSameItemSameComponents(recipeOutput, currentOutput)) {
                currentOutput.grow(recipeOutput.getCount());
            }
            
            for (int index = 0; index < inputInv.size(); index++) {
                ItemStack stack = inputInv.getItem(index);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                }
            }
            this.setMana(Sources.EARTH, this.getMana(Sources.EARTH) - recipe.value().getManaCosts().getAmount(Sources.EARTH));
        }
    }
    
    @Override
    public void setItem(int invIndex, int slotIndex, ItemStack stack) {
        ItemStack slotStack = this.getItem(invIndex, slotIndex);
        super.setItem(invIndex, slotIndex, stack);
        if (invIndex == INPUT_INV_INDEX && (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, slotStack))) {
            this.processTimeTotal = this.getProcessTimeTotal();
            this.processTime = 0;
            this.setChanged();
        }
    }

    @Override
    public int getMana(Source source) {
        return this.manaStorage.getManaStored(source);
    }

    @Override
    public SourceList getAllMana() {
        SourceList.Builder mana = SourceList.builder();
        for (Source source : Sources.getAllSorted()) {
            int amount = this.manaStorage.getManaStored(source);
            if (amount > 0) {
                mana.with(source, amount);
            }
        }
        return mana.build();
    }

    @Override
    public int getMaxMana() {
        // TODO Fix up
        return this.manaStorage.getMaxManaStored(Sources.EARTH);
    }

    @Override
    public void setMana(Source source, int amount) {
        this.manaStorage.setMana(source, amount);
        this.setChanged();
        this.syncTile(true);
    }

    @Override
    public void setMana(SourceList mana) {
        this.manaStorage.setMana(mana);
        this.setChanged();
        this.syncTile(true);
    }

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        for (int invIndex = 0; invIndex < this.getInventoryCount(); invIndex++) {
            for (int slotIndex = 0; slotIndex < this.getInventorySize(invIndex); slotIndex++) {
                stackedContents.accountStack(this.getItem(invIndex, slotIndex));
            }
        }
    }

    @Override
    protected int getInventoryCount() {
        return 3;
    }

    @Override
    protected int getInventorySize(int inventoryIndex) {
        return switch (inventoryIndex) {
            case INPUT_INV_INDEX, OUTPUT_INV_INDEX, WAND_INV_INDEX -> 1;
            default -> 0;
        };
    }

    @Override
    public Optional<Integer> getInventoryIndexForFace(@NotNull Direction face) {
        return switch (face) {
            case DOWN -> Optional.of(OUTPUT_INV_INDEX);
            default -> Optional.of(INPUT_INV_INDEX);
        };
    }

    @Override
    protected NonNullList<IItemHandlerPM> createItemHandlers() {
        NonNullList<IItemHandlerPM> retVal = NonNullList.withSize(this.getInventoryCount(), Services.ITEM_HANDLERS.create(this));
        
        // Create input handler
        retVal.set(INPUT_INV_INDEX, Services.ITEM_HANDLERS.create(this.inventories.get(INPUT_INV_INDEX), this));
        
        // Create fuel handler
        retVal.set(WAND_INV_INDEX, Services.ITEM_HANDLERS.builder(this.inventories.get(WAND_INV_INDEX), this)
                .itemValidFunction((slot, stack) -> stack.getItem() instanceof IWand)
                .build());

        // Create output handler
        retVal.set(OUTPUT_INV_INDEX, Services.ITEM_HANDLERS.builder(this.inventories.get(OUTPUT_INV_INDEX), this)
                .itemValidFunction((slot, stack) -> false)
                .build());
        
        return retVal;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        pComponentInput.getOrDefault(DataComponentsPM.CAPABILITY_MANA_STORAGE.get(), ManaStorage.EMPTY).copyManaInto(this.manaStorage);
    }

    @Override
    protected void collectImplicitComponents(Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        pComponents.set(DataComponentsPM.CAPABILITY_MANA_STORAGE.get(), this.manaStorage);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag pTag) {
        pTag.remove("ManaStorage");
    }
}
