package ivangeevo.selfsustainable.block.entity;

import ivangeevo.selfsustainable.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class StoneOvenBlockEntity extends AbstractOvenBlockEntity {
    public StoneOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.Blocks.OVEN_STONE, pos, state, RecipeType.BLASTING);
    }

    protected Text getContainerName() {
        return Text.translatable("container.oven_stone");
    }



    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BlastFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public ItemStack getRenderStack() {
        ItemStack normalVariant = this.getStack(0); // Assuming 0 is the slot for normal variant
        ItemStack cookedVariant = this.getStack(1); // Assuming 1 is the slot for cooked variant

        // Check if the cooked variant is present, and return it if it is, otherwise return the normal variant
        return cookedVariant.isEmpty() ? normalVariant : cookedVariant;
    }


    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

}
