// FCMOD

package net.ivangeevo.self_sustainable.item.util;

public enum FurnaceBurnTime
{
	NONE( 0 ),
	DAMP_VEGETATION( 10 ), // flowers, etc.
	KINDLING( 25 ), // saw dust, bark, etc.
	SMALL_FUEL( 50 ), // shafts, etc.
	WOOD_TOOLS(SMALL_FUEL.burnTime * 4 ),
	PLANKS_OAK( 400 ),
	PLANKS_SPRUCE(PLANKS_OAK.burnTime * 3 / 4 ),
	PLANKS_BIRCH(PLANKS_OAK.burnTime * 5 / 4 ),
	PLANKS_JUNGLE(PLANKS_OAK.burnTime / 2 ),
	PLANKS_BLOOD(PLANKS_OAK.burnTime / 2 ),
	WOOD_BASED_BLOCK(PLANKS_OAK.burnTime / 2 ),
	COAL( 1600 ),
	BLAZE_ROD( 12800 ),
	LAVA_BUCKET( 20000 );
	
	public static final FurnaceBurnTime
		WOOL = KINDLING,
		WOOL_KNIT = SMALL_FUEL,
		SHAFT = SMALL_FUEL,
		WICKER_PIECE = SMALL_FUEL; 
	
	
	public final int burnTime;
	
    private FurnaceBurnTime(int iBurnTime )
    {
		burnTime = iBurnTime;
    }    
}
