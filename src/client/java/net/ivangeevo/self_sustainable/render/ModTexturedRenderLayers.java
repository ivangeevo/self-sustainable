package net.ivangeevo.self_sustainable.render;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class ModTexturedRenderLayers {

    private static final Identifier WICKER_BASKET_ATLAS_TEXTURE = Identifier.of(
            SelfSustainableMod.MOD_ID, "textures/atlases/wicker_basket.png"
    );

    private static final Identifier HAMPER_ATLAS_TEXTURE = Identifier.of(
            SelfSustainableMod.MOD_ID, "textures/atlases/hamper.png"
    );

    public static final RenderLayer WICKER_BASKET_RENDER_LAYER = RenderLayer.getEntityCutout(WICKER_BASKET_ATLAS_TEXTURE);

    public static final RenderLayer HAMPER_RENDER_LAYER = RenderLayer.getEntityCutout(HAMPER_ATLAS_TEXTURE);

    public static SpriteIdentifier getWickerBasket() {
        return new SpriteIdentifier(
                WICKER_BASKET_ATLAS_TEXTURE,
                Identifier.of(SelfSustainableMod.MOD_ID, "entity/wicker_basket")
        );
    }

    public static SpriteIdentifier getHamper() {
        return new SpriteIdentifier(
                HAMPER_ATLAS_TEXTURE,
                Identifier.of(SelfSustainableMod.MOD_ID, "entity/hamper")
        );
    }

}
