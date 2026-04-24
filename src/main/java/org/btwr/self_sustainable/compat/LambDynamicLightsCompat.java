package org.btwr.self_sustainable.compat;


import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;

public class LambDynamicLightsCompat implements DynamicLightsInitializer {

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {}

    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {}
}
