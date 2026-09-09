## v???(dev)
+ Added new food combinations (moved them from BTWR: Core) as it makes more sense for them to be in this mod as it changes food values
+ Added being able to craft ham and eggs with cooked scrambled eggs. This is a workaround which allows Ham and Eggs to be a valid recipe, which only normally requires cooked eggs from the Better With Time mod.
+ Added a new mod item tag for axes which can fully harvest wooden chests. This ties in with the change below for wooden chests
+ Added the ability for campfires to spread fire around them in the same way they do in BTW
+ Changed wooden chests to not drop the full block without the proper tool. They can now only be collected with an iron axe or above (excluding golden axes)
+ Fixed inserting an item in the brick oven causing the game to crash when Tough Environment is not present
+ Fixed cooked salmon and cooked cod giving only one shank when eaten instead of two
+ Fixed oven fuel insertion sound while lit being too high pitch
+ Fixed campfire burning non-fuel items on its middle flame setting. Now it will only burn them when on high flame
+ Fixed campfire making a sound when non-fuel items burn
+ Fixed campfire not extinguishing when rained on
+ Updated the LICENSE file in the source code to show the correct license
+ Updated the mod to Fabric Loader 0.19.5

## v0.6
### Warning, this is a game breaking update. Proceed with caution!

+ Oven changes/fixes:
  + Added mortared variant for the oven
  + Fixed fuel level displaying incorrectly
  + Changed a lot on how ovens work internally, so this will likely break stuff with existing ovens in your worlds


+ Torch fixes:
  + Fixed a missing texture/model for the unlit soul wall torch
  + Fixed crude torches to properly break if the block below them is broken
  + Fixed torch items not extinguishing when they fall into water
  + Fixed unlit torches to properly light up from falling lava and fire blocks


+ Misc
  + Added many dedicated sound events for things like using primitive firestarters, igniting stuff, knitting, etc.
  + Fixed wet brick block to only break when an entity collides on the exact shape of the block instead of when entering the full shape of the block space it's standing on
  + Fixed/added proper sound event for the wet brick when it breaks from being stepped on
  + Fixed double healing which happened when using Self Sustainable and Granular Hunger together
  + Updated the mod to Fabric API 0.116.15, Fabric Loader 0.19.3 & BTWR: Shared Library 0.8.5

## v0.5.3
+ Added LambDynamicLights compatibility for the crude torch items
+ Changed the bow drill to be craftable with other string items from the Fabric conventional "strings" item tag instead of just string
+ Updated the mod to Fabric API 0.116.11, Fabric Loader 0.19.2 & BTWR: Shared Library 0.8.4

## v0.5.2
+ Fixed crafting the knitting item causing a dupe of the ingredients
+ Fixed wool armor items not being craftable from different wool knits
+ Updated the mod to BTWR: Shared Library 0.8.2

## v0.5.1
+ Fixed a bug with crafting the wicker weaving item that caused a dupe of the ingredients
+ Changed the light value for smoldering torches to be the same as a lit torch

## v0.5
+ Added wicker weaving!
+ Added knitting!
+ Added knitting needles (Crafted from two sticks placed diagonally)
+ Added wool knits (Made from knitting)
+ Added wool items (By default, they craft into a full block from four wool items in the crafting grid)
+ Added wool armor (It's dyeable!)
+ Added wicker basket!
+ Added hamper! Recipe is slightly tweaked from original BTW recipe to require a wicker basket in the middle instead of a plank
+ Changed the recipe for chest to require a hamper and an iron ingot. This is necessary also for progression reasons
+ Changed sheep to drop the new wool items instead of the vanilla wool blocks
+ Changed brick ovens to have one light level higher to get somewhat closer to the BTW CE luminance look. Also made them non-opaque blocks internally
+ Fixed firestarters removing a placed raw item from the campfire after the campfire has been ignited when being held down
+ Fixed/added translations(proper names) for all tags from the mod
+ Moved the ProgressiveCraftingItem class to the BTWR: Shared Library mod since some other progressive crafting items will be used in other mods as well
+ Updated the mod to Fabric API 0.116.9, Fabric Loader 0.18.4 BTWR: Shared Library 0.8.1

## v0.4
### WARNING! Game breaking update, proceed with caution.
#### Torches:
+ Completely changed how torches work internally to make them work better and not cause as many issues in the future
+ Normal unlit torch blocks now have a custom class, so existing unlit torch blocks in your world will get removed
+ Added unlit variant for the soul torch
+ Added the ability to light up torches from fire/lava
+ Fixed crude torches now change to the smoldering item texture in the inventory when they reach the smoldering state
+ Fixed the wooden base in the of unlit torches block texture to match that of the lit one 
+ Fixed a bug that caused lit torches to not be placeable since last update's changes to code

### Tags:
+ Added a new block tag "vanilla_lit_torches"
+ Added a new item tag "firestarters". This bundles all firestarter items from the mod and the flint and steel
+ Added a new item tag "campfire_spits". It's currently unused, but in the future it will allow other mods to specify their custom viable spit items for the campfire

#### Other:
+ Added an EMI world interaction recipe/tooltip for brick sun drying so that it helps the player understand how bricks are supposed to be acquired in the early game
+ Added a recipe for cooking unfired brick in the brick oven
+ Disabled the furnace recipe for cooking brick from clay ball
+ Changed the fuel values for items in the mod. They match BTW values now and this mostly changes planks having much less fuel value than they should've had
+ Completely rewrote how fire related modification like lighting up blocks/lighting items from blocks are handled by mostly using Fabric events instead of mixins. This should generally make things work better and cause less incompatibilities/issues in the future
+ Cleaned up/optimized a bunch of code from CampfireBlock's mixin class
+ Removed the LitBlockRegistry class from last update since it became redundant

## v0.3.1
+ Changed the modification for Flint and Steel's firestarter overhaul to be handled with Fabric Events instead of mixins
+ Changed how the mod registers block that are considered lit up blocks. Custom blocks that have the LIT property or any other condition to be considered "lit up" must now be registered with the LitBlockRegistry class. This allows other mods to add their custom blocks to work with the overhauled firestarter/s that Self-Sustainable adds
+ Updated the mod to BTWR: Shared Library 0.7

## v0.3
+ Refactored pretty much the whole code; mainly for readability and cleaning up, but also so it's more in order with other mods from the BTWR project
+ Updated the mod to Fabric API 0.116.7, Fabric Loader 0.17.3 & BTWR: Shared Library 0.6.5

## v0.2.1
+ Fixed a bug(for real now) with unlit crude torches that would decrement unlit torches twice when trying to light one
+ Fixed crude torches to properly transition into a smoldered state when there's 30 seconds of fuel left.
+ Fixed recipes for oven to show up properly.
+ Removed unfinished Wicker Weaving item from having a recipe because it's still unused
+ Updated the mod to Fabric API 0.116.6, Fabric Loader 0.17.2 & BTWR: Shared Library 0.58

## v0.2
+ Added compatibility for the Brick Oven recipe to work conditionally with Tough Environment. When its loaded it requires Loose Brick Slab, and when it's not it requires normal vanilla Bricks Slab
+ Reworked how torches work internally to fix a few bugs related to lighting them/lighting up blocks with them
+ Changed torches in the inventory to not get destroyed by any type of water when the player is in creative or spectator mode
+ Fixed a bug related to torches getting extinguished in water that would crash the game sometimes due to checking a non-existing slot in the inventory
+ Fixed a bug with crude torches that would decrement unlit torches twice when trying to light one
+ Updated the mod to Fabric API 0.116.4 & BTWR: Shared Library 0.56

## v0.1.2
+ Added EMI support for the Brick Oven (Oven Cooking) recipes.
+ Removed old recipes for cooking with vanilla furnaces that were leftover since adding the Brick Oven.

## v0.1.1
+ Improved logic when right-clicking on a Brick Oven to work much better.
+ Fixed a bug that was causing the game to crash when breaking a torch block that had a fuel component, but wasn't initialized properly for it's intended blocks
+ Fixed some bugs related to oven recipes to be more consistent and register as intended.
+ Updated the mod to Fabric API 0.116.1

## v0.1
+ Initial release
