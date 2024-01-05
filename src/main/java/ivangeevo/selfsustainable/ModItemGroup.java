package ivangeevo.selfsustainable;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup GROUP_BTWR = FabricItemGroupBuilder.build(new Identifier(SelfSustainableMod.MOD_ID, "group_btwr"), () -> new ItemStack(ModItems.GROUP_BTWR));


}
