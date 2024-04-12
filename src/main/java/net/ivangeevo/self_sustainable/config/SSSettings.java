package net.ivangeevo.self_sustainable.config;

public class SSSettings
{
        public boolean ovenNoGUI = true;
        public boolean extinguishCampfires = true;
        public boolean extinguishJackOLanterns = true;
        public boolean extinguishCandles = true;
        public boolean extinguishTorches = true;


        public int campfireBurnDuration = 0;
        public int torchBurnDuration = 0;

        public int jackOLanternBurnDuration = 0;
        public int candleBurnDuration = 0;

        public float campfireExtinguishInRainChance = 0f;
        public float candleExtinguishInRainChance = 0f;
        public float torchExtinguishInRainChance = 0f;

        public float jackOLanternExtinguishInRainChance = 0f;



        public boolean isExtinguishCandlesEnabled()
        { return extinguishCandles; }
        public boolean isExtinguishJackOLanternsEnabled()
        { return extinguishJackOLanterns; }
        public boolean isExtinguishCampfiresEnabled()
        { return extinguishCampfires; }




        public boolean isOvenHasNoGUI()
        { return ovenNoGUI; }

}
