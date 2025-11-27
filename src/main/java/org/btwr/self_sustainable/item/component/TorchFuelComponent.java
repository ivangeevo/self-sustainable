package org.btwr.self_sustainable.item.component;

import com.mojang.serialization.Codec;

public class TorchFuelComponent {

    private int fuel;
    public static int MAX_FUEL = 24000;

    public TorchFuelComponent() {
        this(MAX_FUEL);
    }

    public TorchFuelComponent(int fuel) {
        this.fuel = fuel;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public void decrement() {
        this.fuel--;
    }

    public static final Codec<TorchFuelComponent> CODEC = Codec.INT.xmap(
            TorchFuelComponent::new,
            TorchFuelComponent::getFuel
    );

}