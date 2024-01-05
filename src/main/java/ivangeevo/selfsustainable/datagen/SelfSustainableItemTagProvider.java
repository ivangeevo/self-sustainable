package ivangeevo.selfsustainable.datagen;

import ivangeevo.selfsustainable.ModItems;
import ivangeevo.selfsustainable.tag.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;

public class SelfSustainableItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public SelfSustainableItemTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {

        getOrCreateTagBuilder(ModTags.Items.WOOL_KNIT_ITEMS)
                .add(ModItems.WOOL_KNIT);

        getOrCreateTagBuilder(ModTags.Items.WOOL_ITEMS)
                .add(ModItems.WHITE_WOOL)
                .add(ModItems.ORANGE_WOOL)
                .add(ModItems.MAGENTA_WOOL)
                .add(ModItems.LIGHT_BLUE_WOOL)
                .add(ModItems.YELLOW_WOOL)
                .add(ModItems.LIME_WOOL)
                .add(ModItems.PINK_WOOL)
                .add(ModItems.GRAY_WOOL)
                .add(ModItems.LIGHT_GRAY_WOOL)
                .add(ModItems.CYAN_WOOL)
                .add(ModItems.PURPLE_WOOL)
                .add(ModItems.BLUE_WOOL)
                .add(ModItems.BROWN_WOOL)
                .add(ModItems.GREEN_WOOL)
                .add(ModItems.RED_WOOL)
                .add(ModItems.BLACK_WOOL);



    }


}
