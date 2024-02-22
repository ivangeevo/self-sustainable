package net.ivangeevo.selfsustainable.item.interfaces;

public interface ItemAdded {

    int defaultFurnaceBurnTime = 0;

    boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage);

    boolean getCanItemBeSetOnFireOnUse(int iItemDamage);

    boolean getCanItemStartFireOnUse(int iItemDamage);

    int getFurnaceBurnTime(int iItemDamage);

}
