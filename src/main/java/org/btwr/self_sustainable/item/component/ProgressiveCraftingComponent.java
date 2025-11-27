package org.btwr.self_sustainable.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

import org.btwr.self_sustainable.item.items.ProgressiveCraftingItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ProgressiveCraftingComponent(Optional<ItemStack> usingConvertsTo) {

    private static final float DEFAULT_CRAFTING_SECONDS = ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE;
    public static final Codec<ProgressiveCraftingComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemStack.UNCOUNTED_CODEC
                                    .optionalFieldOf("using_converts_to")
                                    .forGetter(ProgressiveCraftingComponent::usingConvertsTo)
                    )
                    .apply(instance, ProgressiveCraftingComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, ProgressiveCraftingComponent> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC.collect(PacketCodecs::optional),
            ProgressiveCraftingComponent::usingConvertsTo,
            ProgressiveCraftingComponent::new
    );

    public static class Builder {
        private Optional<ItemStack> usingConvertsTo = Optional.empty();

        public ProgressiveCraftingComponent.Builder usingConvertsTo(ItemConvertible item) {
            this.usingConvertsTo = Optional.of(new ItemStack(item));
            return this;
        }

        public ProgressiveCraftingComponent build() {
            return new ProgressiveCraftingComponent(this.usingConvertsTo);
        }
    }

}