package net.ivangeevo.self_sustainable.block.entity.renderer;

import com.google.common.collect.Sets;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.Set;

public class ModEntityModelLayers {

    private static final String MAIN = "main";
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();

    public static final EntityModelLayer WICKER_BASKET = registerMain("wicker_basket");
    public static final EntityModelLayer HAMPER = registerMain("hamper");

    private static EntityModelLayer registerMain(String id) {
        return register(id, MAIN);
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        }

        return entityModelLayer;
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.of(SelfSustainableMod.MOD_ID, id), layer);
    }

}