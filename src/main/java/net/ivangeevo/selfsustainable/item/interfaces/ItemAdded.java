package net.ivangeevo.selfsustainable.item.interfaces;

public interface ItemAdded {

    int defaultFurnaceBurnTime = 0;
    int getOvenBurnTime(int fuelTicks);

    boolean getCanItemBeSetOnFireOnUse(int fuelTicks);
    boolean getCanItemStartFireOnUse(int fuelTicks);
    boolean getCanBeFedDirectlyIntoBrickOven(int fuelTicks);



}
