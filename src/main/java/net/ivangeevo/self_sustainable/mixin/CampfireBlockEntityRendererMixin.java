package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.self_sustainable.block.entity.util.CampfireExtinguisher;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntityRenderer.class)
public abstract class CampfireBlockEntityRendererMixin
{

    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject(method = "render(Lnet/minecraft/block/entity/CampfireBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
    private void injectedCustomRender(CampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci)
    {

        this.renderCookItem(campfireBlockEntity,matrixStack, vertexConsumerProvider, i, j);
        ci.cancel();
    }

    @Unique
    private void renderCookItem(CampfireBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Get the itemBeingCooked from the entity
        ItemStack cookStack = ((CampfireBlockEntityAdded)entity).getItemBeingCooked();

        if (cookStack != ItemStack.EMPTY)
        {
            //DefaultedList<ItemStack> itemsBeingCooked = entity.getCookStack();
            Direction facing = entity.getCachedState().get(CampfireBlock.FACING);

            matrices.push();

            // Move to the center of the block
            matrices.translate(0.5f, 0.6f, 0.5f);

            // Integrate the visual offset logic
            applyVisualOffset(matrices, facing);

            // Rotate based on the facing direction
            RotationAxis rotationAxis = getRotationAxis(facing);
            matrices.multiply(rotationAxis.rotationDegrees(facing.asRotation()));

            // Scale the item to an appropriate size
            matrices.scale(0.35f, 0.35f, 0.35f);

            // Use the itemRenderer to render the item
            this.itemRenderer.renderItem(cookStack, ModelTransformationMode.GUI,
                    LightmapTextureManager.pack(8, 15), OverlayTexture.DEFAULT_UV,
                    matrices, vertexConsumers, entity.getWorld(), 1);

            matrices.pop();
        }

    }


        private void applyVisualOffset (MatrixStack matrices, Direction facing) {
            float visualOffset = 0.25f;

            switch (facing) {
                case NORTH:
                    matrices.translate(0.0f, 0.0f, -0.5f + visualOffset);
                    break;
                case SOUTH:
                    matrices.translate(0.0f, 0.0f, 0.5f - visualOffset);
                    break;
                case WEST:
                    matrices.translate(-0.5f + visualOffset, 0.0f, 0.0f);
                    break;
                case EAST:
                    matrices.translate(0.5f - visualOffset, 0.0f, 0.0f);
                    break;
            }
        }

    // Get RotationAxis based on facing direction
    @Unique
    private RotationAxis getRotationAxis(Direction facing) {
        return switch (facing) {
            case NORTH -> RotationAxis.POSITIVE_Z;
            case SOUTH -> RotationAxis.POSITIVE_Z;
            case WEST -> RotationAxis.POSITIVE_Y;
            case EAST -> RotationAxis.POSITIVE_Y;
            default -> RotationAxis.POSITIVE_Y; // Default to Y-axis if facing direction is not recognized
        };
    }

}




