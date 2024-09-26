package net.ivangeevo.self_sustainable.item.component;

public class ProgressiveCraftingItemComponent
{
    private long timeOfLastUse;
    private float accumulatedChance;

    public ProgressiveCraftingItemComponent() {
        this.timeOfLastUse = -1;
        this.accumulatedChance = 0.0f;
    }

    public long getTimeOfLastUse() {
        return timeOfLastUse;
    }

    public void setTimeOfLastUse(long timeOfLastUse) {
        this.timeOfLastUse = timeOfLastUse;
    }

    public float getAccumulatedChance(float defaultChance) {
        return accumulatedChance > 0 ? accumulatedChance : defaultChance;
    }

    public void setAccumulatedChance(float accumulatedChance) {
        this.accumulatedChance = accumulatedChance;
    }
}
