package net.ivangeevo.self_sustainable.block;

public class NewCampfireBlockManager
{
    private static NewCampfireBlockManager instance = new NewCampfireBlockManager();
    private NewCampfireBlockManager(){}
    public static NewCampfireBlockManager getInstance()
    {
        return instance;
    }
}
