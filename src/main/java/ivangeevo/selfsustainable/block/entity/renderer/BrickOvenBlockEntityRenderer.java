package ivangeevo.selfsustainable.block.entity.renderer;

import ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class BrickOvenBlockEntityRenderer implements BlockEntityRenderer<BrickOvenBlockEntity> {

    public BrickOvenBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }



    private int manuallySetLightLevel(World world, BlockPos pos) {
        // Return a constant light level of 8
        return LightmapTextureManager.pack(8, 15);
    }
    @Override
    public void render(BrickOvenBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getRenderStack();

        matrices.push();

        matrices.translate(0.3f, 0.57f, 0.5f);

        Direction facing = entity.getCachedState().get(BrickOvenBlock.FACING);
        float yawDegrees = facing.asRotation();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yawDegrees));

        matrices.scale(0.35f, 0.35f, 0.35f);

        //int customLight = calculateAverageLight(entity.getWorld(), entity.getPos());

        int setLightLevel = manuallySetLightLevel(entity.getWorld(), entity.getPos());


        itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GUI, setLightLevel, overlay, matrices, vertexConsumers, 1);

        matrices.pop();


    }





}
