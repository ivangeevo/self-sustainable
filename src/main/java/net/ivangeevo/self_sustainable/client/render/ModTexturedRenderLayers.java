package net.ivangeevo.self_sustainable.client.render;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class ModTexturedRenderLayers {

    public static final Identifier WICKER_BASKET_ATLAS_TEXTURE = Identifier.of(
            SelfSustainableMod.MOD_ID,
            "textures/atlas/wicker_basket.png"
    );

    public static final Identifier HAMPER_ATLAS_TEXTURE = Identifier.of(
            SelfSustainableMod.MOD_ID,
            "textures/atlas/hamper.png"
    );

    private static final RenderLayer WICKER_BASKET_RENDER_LAYER = RenderLayer.getEntityCutout(WICKER_BASKET_ATLAS_TEXTURE);
    private static final RenderLayer HAMPER_RENDER_LAYER = RenderLayer.getEntityCutout(HAMPER_ATLAS_TEXTURE);

    public static final SpriteIdentifier WICKER_BASKET = new SpriteIdentifier(
            WICKER_BASKET_ATLAS_TEXTURE,
            Identifier.of(SelfSustainableMod.MOD_ID,"entity/wicker_basket/")
    );

    public static final SpriteIdentifier HAMPER = new SpriteIdentifier(
            HAMPER_ATLAS_TEXTURE, Identifier.of(SelfSustainableMod.MOD_ID,
            "entity/hamper")
    );

}
