package org.btwr.self_sustainable.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ModEvents {

    public static void register() {
        //UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {});

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack handStack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();

            if (handStack.getItem() instanceof FlintAndSteelItem) {
                if (world.canPlayerModifyAt(player, pos)) {
                    performUseEffects(world, pos, player, hand);

                    if (!world.isClient) {
                        //notifyNearbyAnimalsOfAttempt(player);

                        if (checkChanceOfStart(handStack, world.random)) {
                            attemptToLightBlock(handStack, world, pos, hitResult.getSide());
                        }
                    }

                    float exhaustionPerUse = 0.01F;
                    player.addExhaustion(exhaustionPerUse * world.getDifficulty().btwr$getHungerIntensiveActionCostMultiplier());
                    handStack.damage(1, player, EquipmentSlot.MAINHAND);

                    return ActionResult.SUCCESS;
                }

                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }

    public static void performUseEffects(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);

        if (!player.getWorld().isClient()) {
            for (int var3 = 0; var3 < 5; ++var3) {
                Vec3d var4 = new Vec3d((player.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);

                var4 = var4.rotateX(-player.getPitch() * (float)Math.PI / 180.0f);
                var4 = var4.rotateY(-player.getYaw() * (float)Math.PI / 180.0f);

                Vec3d var5 = new Vec3d((player.getRandom().nextFloat() - 0.5) * 0.3, (-player.getRandom().nextFloat()) * 0.6 - 0.3, 0.6);

                var5 = var5.rotateX(-player.getPitch() * (float)Math.PI / 180.0f);
                var5 = var5.rotateY(-player.getYaw() * (float)Math.PI / 180.0f);

                var5 = var5.add(player.getX(), player.getY() + player.getEyeHeight(player.getPose()), player.getZ());

                player.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, player.getStackInHand(hand)), var5.getX(), var5.getY(), var5.getZ(), var4.getX(), var4.getY() + 0.05, var4.getZ());
            }
        }
    }

    public static boolean checkChanceOfStart(ItemStack stack, Random rand) {
        return rand.nextInt(4) == 0;
    }

    public static boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing) {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if (targetBlock != null && targetBlock.btwr$getCanBeSetOnFireDirectlyByItem(world, pos)) {
            return targetBlock.btwr$setOnFireDirectly(world, pos);
        }

        return false;
    }
}
