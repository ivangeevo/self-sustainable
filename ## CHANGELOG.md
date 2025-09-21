# v0.2.1
+ Fixed a bug(for real now) with unlit crude torches that would decrement unlit torches twice when trying to light one
+ Fixed crude torches to properly transition into a smoldered state when there's 30 seconds of fuel left.
+ Fixed recipes for oven to show up properly.
+ Removed unfinished Wicker Weaving item from having a recipe because it's still unused
+ Updated the mod to Fabric API 0.116.6, Fabric Loader 0.17.2 & BTWR: Shared Library 0.58

# v0.2
+ Added compatibility for the Brick Oven recipe to work conditionally with Tough Environment. When its loaded it requires Loose Brick Slab, and when it's not it requires normal vanilla Bricks Slab
+ Reworked how torches work internally to fix a few bugs related to lighting them/lighting up blocks with them
+ Changed torches in the inventory to not get destroyed by any type of water when the player is in creative or spectator mode
+ Fixed a bug related to torches getting extinguished in water that would crash the game sometimes due to checking a non-existing slot in the inventory
+ Fixed a bug with crude torches that would decrement unlit torches twice when trying to light one
+ Updated the mod to Fabric API 0.116.4 & BTWR: Shared Library 0.56

# v0.1.2
+ Added EMI support for the Brick Oven (Oven Cooking) recipes.
+ Removed old recipes for cooking with vanilla furnaces that were leftover since adding the Brick Oven.

# v0.1.1
+ Improved logic when right-clicking on a Brick Oven to work much better.
+ Fixed a bug that was causing the game to crash when breaking a torch block that had a fuel component, but wasn't initialized properly for it's intended blocks
+ Fixed some bugs related to oven recipes to be more consistent and register as intended.
+ Updated the mod to Fabric API 0.116.1

# v0.1
+ Initial release