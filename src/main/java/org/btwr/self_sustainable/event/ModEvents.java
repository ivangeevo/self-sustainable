package org.btwr.self_sustainable.event;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.entity.VariableCampfireBE;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.block.utils.CampfireState;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.shared_library.mixin.accessors.ItemEntryAccessor;
import org.btwr.shared_library.mixin.accessors.LootPoolBuilderAccessor;
import org.btwr.shared_library.util.utils.IdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock.*;

public class ModEvents {

    private static final String BASE_SHEEP_LOOT_TABLE = "entities/sheep/";

    public static void register() {
        // Campfire block usage modifications
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ItemStack heldStack = player.getStackInHand(hand);

            // Cancel food cooking for soul campfire as it's not modified as the normal one in this mod
            if (state.isOf(Blocks.SOUL_CAMPFIRE))  {
                // Allow only shovel extinguishing & igniters usages
                if (!heldStack.isIn(ItemTags.SHOVELS) || !heldStack.isIn(ModTags.Items.CAN_START_FIRE_ON_USE)) {
                    return ActionResult.FAIL;
                }
            }

            // Usage modifications for the normal campfire
            if (state.isOf(Blocks.CAMPFIRE)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);

                if (blockEntity instanceof VariableCampfireBE campfireBE) {
                    // Extinguish with a shovel
                    if (heldStack.getItem() instanceof ShovelItem && state.get(FIRE_LEVEL) > 0) {
                        if (!world.isClient) {
                            campfireBE.changeFireLevel(world, 0);
                        }
                        IgnitableBlock.playExtinguishSound(world, pos, false);
                        return ActionResult.SUCCESS;
                    }

                    // Igniting
                    // Firestarter ignite — let the item handle it
                    if (heldStack.isIn(ModTags.Items.FIRESTARTERS)) {
                        return ActionResult.PASS;
                    }

                    if (state.get(FUEL_STATE) == CampfireState.NORMAL && state.get(FIRE_LEVEL) == 0) {
                        // Direct ignite (torch/fire charge)
                        if (heldStack.isIn(ModTags.Items.DIRECT_IGNITERS)) {
                            if (!world.isClient) {
                                state.getBlock().btwr$setOnFireDirectly(world, pos);
                            }
                            return ActionResult.SUCCESS;
                        }
                    }

                    Optional<RecipeEntry<CampfireCookingRecipe>> optional;

                    // Adding a spit
                    if (!state.get(HAS_SPIT)) {
                        if (heldStack.isOf(Items.STICK)) {
                            if (!world.isClient) {
                                world.setBlockState(pos, state.with(HAS_SPIT, true));
                                heldStack.decrementUnlessCreative(1, player);
                            }
                            return ActionResult.SUCCESS;
                        }
                    }
                    // Adding a cooking item
                    else {
                        Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.createFuelTimeMap();

                        // The food item that is currently on the campfire
                        ItemStack cookStack = campfireBE.getItemsBeingCooked().getFirst();

                        // Try adding a food item
                        if (!cookStack.isEmpty()) {
                            // Allow retrieval if hand is empty or if the held item is neither ignitable nor fuel
                            if (heldStack.isEmpty() || (!heldStack.isIn(ModTags.Items.CAN_BE_SET_ON_FIRE_ON_USE) && !fuelMap.containsKey(heldStack.getItem()))) {
                                campfireBE.retrieveItem(world, campfireBE, player);
                                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F,
                                        ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1F ) * 2F);
                                return ActionResult.SUCCESS;
                            }
                        }

                        // Retrieve a placed spit
                        if (heldStack.isEmpty() && cookStack.isEmpty()) {
                            if (!world.isClient) {
                                world.setBlockState(pos, state.with(HAS_SPIT, false));
                                player.giveItemStack(new ItemStack(Items.STICK));
                            }
                            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F,
                                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1F ) * 2F);
                            return ActionResult.SUCCESS;
                        }
                        // Try adding a cook stack
                        else if ((optional = campfireBE.getRecipeFor(heldStack)).isPresent()) {
                            if (cookStack.isEmpty()) {
                                campfireBE.addItem(player,
                                        player.getAbilities().creativeMode
                                                ? heldStack.copy()
                                                : heldStack,
                                        optional.get().value().getCookingTime()
                                );
                                return ActionResult.SUCCESS;
                            }
                        }

                        // Try adding fuel
                        if (state.get(FIRE_LEVEL) > 0 || state.get(FUEL_STATE) == CampfireState.SMOULDERING) {
                            int itemBurnTime = 0;
                            if (!heldStack.isEmpty()) {
                                itemBurnTime = AbstractFurnaceBlockEntity.createFuelTimeMap()
                                        .getOrDefault(heldStack.getItem(), 0);
                            }

                            // Disallow using log blocks as fuel (this doesn't disallow normal block placing while right-clicking on it though)
                            if (heldStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().getDefaultState().isIn(BlockTags.LOGS)) {
                                return ActionResult.PASS;
                            }

                            // Add a valid fuel item to the burn time
                            if (heldStack.getItem().btwr$getCanBeFedDirectlyIntoCampfire(heldStack)) {
                                if (!world.isClient) {
                                    campfireBE.addBurnTime(state, itemBurnTime);
                                    heldStack.decrementUnlessCreative(1, player);
                                }
                                IgnitableBlock.playLitFX(world, pos);
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }

                // Pass to remove vanilla campfire interactions, but allow others
                return ActionResult.PASS;
            }

            return ActionResult.PASS;
        });

        // Flint and steel modification as a firestarter
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
                // Fail to disable normal flint and steel behavior
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Replaces sheep wool block drops with the mod wool items
            for (DyeColor color : DyeColor.values()) {
                Identifier sheepLootTableId = Identifier.ofVanilla(BASE_SHEEP_LOOT_TABLE + color.getName());
                RegistryKey<LootTable> SHEEP_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, sheepLootTableId);

                String baseId = color.getName() + "_wool";
                Item woolItem = Registries.ITEM.get(IdUtils.ofSS(baseId));
                Item woolBlock = Registries.ITEM.get(IdUtils.ofMC(baseId));

                if (SHEEP_LOOT_TABLE.equals(key)) {
                    replaceItemsInPools(tableBuilder, woolBlock, woolItem);
                }
            }
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

    private static void replaceItemsInPools(LootTable.Builder tableBuilder, Item target, Item replacement) {
        tableBuilder.modifyPools(poolBuilder -> {
            List<LootPoolEntry> entries = new ArrayList<>(((LootPoolBuilderAccessor) poolBuilder).getEntries().build());
            entries.replaceAll(entry -> {
                if (!(entry instanceof ItemEntry itemEntry)) return entry;
                if (((ItemEntryAccessor) itemEntry).getItem().value() != target) return entry;
                ((ItemEntryAccessor) entry).setItem(Registries.ITEM.getEntry(replacement));
                return entry;
            });
            ((LootPoolBuilderAccessor) poolBuilder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(entries));
        });
    }
}
