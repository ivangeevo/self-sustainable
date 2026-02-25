package org.btwr.self_sustainable.block.entity.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.TexturedModelData;
import org.btwr.self_sustainable.SelfSustainableClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.btwr.self_sustainable.block.entity.render.model.WickerBasketModel;
import org.btwr.shared_library.util.utils.IdUtils;

public class ModEntityModelLayers {

    public static EntityModelLayer WICKER_BASKET = registerModelLayer(
            "wicker_basket", WickerBasketModel.getTexturedModelData()
    );

    private static EntityModelLayer registerModelLayer(String name, TexturedModelData data) {
        EntityModelLayer layer = new EntityModelLayer(IdUtils.ofSS(name), "main");
        EntityModelLayerRegistry.registerModelLayer(layer, () -> data);
        return layer;
    }

    public static void register() {
        SelfSustainableClient.LOGGER.info("Registering model entity layers for: " + SelfSustainableClient.MOD_ID);
    }

}