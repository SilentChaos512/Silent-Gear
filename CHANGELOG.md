# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
- Shield blueprint/template now requires an iron ingot to craft

### [1.21.1-4.0.18] - 2025-04-08
### Changed
- Traits are now displayed as a bulleted list under gear properties. A key (shift by default) can be held to display descriptions for the traits
- Removed all mentions of trait cancelling from the en_us translation file
- (API) `getTooltipLines` and `getTooltipLinesUnchecked` in `GearProperty` have been deprecated and are no longer called anywhere. Override `buildTooltip` instead.
### Fixed
- Grader not grading compound materials correctly (BerzinsU) [#799, #798]

## [1.21.1-4.0.17] - 2025-03-29
### Added
- Arrow entities now have custom colored models (Electroely) [#794]
- Fortunate trait, which makes the tool behave like it has Fortune (see the notes for Silky under "Fixed" below!)
  - Only works up to level 3 (might be possible to change with a data pack, but I did not test this)
  - Uses three loot modifier instances, `silentgear:fortune_trait_1`, `...2` and `...3`
### Changed
- Azure silver tip upgrades now have the Fortunate trait (level 3) instead of Soft
### Fixed
- Silky trait now works, but differently from earlier versions. Functionally, it is almost identical to Silk Touch. [#795, #697]
  - Enchantment traits still do not exist and are not currently planned
  - It uses a loot modifier (`silentgear:silk_touch_trait`) to completely override the block drops
  - XP drops are also cancelled during an event
  - Only applies when breaking blocks, not other contexts (it will not activate if a mod checks for Silk Touch in an entity loot table or whatever)

## [1.21.1-4.0.16] - 2025-03-22
### Added
- Custom models for thrown tridents (Electroely) [#790]
- Tridents now use ranged weapon and projectile stats to determine thrown damage, charge time, and speed (Electroely) [#790]
- Gear parts can now be placed in the material grader to make grading attempts on their individual materials (JaisDK) [#792]
  - Added a config option to enable this feature, disabled by default (SilentChaos512)
### Changed
- Updated Japanese localization (twister716) [#784]
### Fixed
- Some gear items breaking completely in some cases, especially with the Brittle trait (shields still break...) [#791, #707]
- Tridents not being throwable on server (Electroely) [#790, #770]
- Materials dump command not including categories [#670]
- Materials dump command listing parent ID as the child ID

## [1.21.1-4.0.15] - 2025-03-14
### Fixed
- Gear items not being equal on client and server in some cases, creating issues with Refined Storage 2 and probably others [#753]
  - This was caused by two separate bugs related to how traits are encoded on the items

## [1.21.1-4.0.14] - 2025-03-09
### Fixed
- Curio slots not appearing
- Invalid attributes modifiers on some items (armor, shields, arrows, shears, fishing rods, and curios) [#741]

## [1.21.1-4.0.13] - 2025-02-15
### Added
- PartMaterialIngredient can now exclude specific material categories (JaisDK) [#780]
- Config option to make the salvager automatically break down parts into materials
### Changed
- Updated Japanese translation (twister716) [#750]
- Rods can no longer be salvaged into materials, since they can't return an appropriate amount of material
### Fixed
- Unbound keys causing log spam [#782]
- Stackable gear items being salvageable [#779]
- Allow salvager to keep running even when output isn't empty (kylev) [#768]

## [1.21.1-4.0.12] - 2025-01-25
### Fixed
- Part names not translating in some cases (kylev) [#767]
- Compound materials (alloys) not retaining all properties [#747, #736]

## [1.21.1-4.0.11] - 2025-01-09
### Added
- Magnetic Upgrade, which is now the only way to obtain the Magnetic trait [#697, #726]
- Configuration screen, accessible from the Mods menu
- Config option to make crouching/sneaking disable item magnet effects (enabled by default)
### Removed
- Some of the unused config options
### Changed
- All traits that previously had the Magnetic trait have had it removed
### Fixed
- Possibly fixed `ConcurrentModificationException` crash with magnet pull trait effects [#762]
- Keybindings for tooltips not working in some situations [#761]
- Most blocks should now interact correctly with pipes and other item transfer methods from other mods [#735]
- Magnetic trait not working [#697, #726]
- Miscellaneous bugs with item magnet effects

## [1.21.1-4.0.10] - 2024-12-20
### Fixed
- Keybindings not being present in the Controls menu. They can now be reassigned again. [#761]
- Changed `c:ores_in_ground/endstone` tags to `c:ores_in_ground/end_stone` [#760]
- Rework `enchantables` tags so more items can be enchanted [#754]
- Arrows now behave more or less as intended. They now stack and no longer have a damage value. They can no longer be "repaired," you must craft more instead. [#730]

## [1.21.1-4.0.9] - 2024-11-24
### Added
- A few missing tags [#743, #715]
### Fixed
- Renew trait not functioning correctly (VintageGnu) [#745]
- Armor items having incorrect armor toughness value [#742]
- Shears having zero attack speed (not lifting onto screen when equipped) [#740]
- Smithing recipes placing nothing in the output slot, but recipes still modify the input gear item for some reason... [#718]

## [1.21.1-4.0.8] - 2024-10-25
### Changed
- Make the new `level_hint` field of HarvestTier optional [#731]
- Implement `equals` and `hashcCode` on GearProperty and the subclasses of GearPropertyValue

## [1.21.1-4.0.7] - 2024-10-24
### Fixed
- Mod Kit causing a crash when used [#729]
- Use correct mod_loaded condition in recipes [#717]
- Tip upgrades not increasing harvest tiers [#716]
- Netherite missing harvest tier boost [#712]
- Material name prefixes not including spaces before the material name (e.g. "NetheriteDiamond" now contains a space)

## [1.21.1-4.0.6] - 2024-10-20
### Added
- `silentgear:attach_data_components` trait effect type. Allows a components patch to be applied to an item.
  - Added the "Yummy" trait as an example
- JEI support for super mixer
### Changed
- Replaced one of the new "gear damaged" sounds with two that are based of the item breaking sound (like older versions of the mod)
### Fixed
- Some alloy recipes not outputting the correct number of items [#723]
- JEI plugin errors
- JEI showing no ingredients on fabric alloy making recipes
- JEI showing empty tag items and not all valid items on metal/gem alloy making recipes
- Added missing lang keys for the super mixer and super alloy

## [1.21.1-4.0.5] - 2024-10-15
### Added
- New sound effects for the stone anvil. The sound played is determined by the recipe.
### Fixed
- Deepslate bort ore making stone sounds [#722]
- Correct some tool-related tags, such as `c:tools/knife` (was "knives") [#711]
- Bows, crossbows, and slingshots dealing vanilla damage, ignoring the ranged damage property [#708]
- Bows and crossbows not being enchantable (going to add different enchantments for slingshots... eventually)

## [1.21.1-4.0.4] - 2024-10-10
### Added
- Better Combat support [#694]
- Tags for netherwood fence gates [#680]
- A new creative tab with sample gear items, mostly intended to aid with testing
### Changed
- Furnace fuel burn times are now handled by the NeoForge data map; netherwood charcoal burn time config option removed
### Fixed
- Alloy makers missing their last input slot [#705]
- All sickle behavior errors [#703]
- Sinew and flax mapping to the wrong material [#700]
- Netherwood sticks becoming regular sticks during tool crafting
- Stone torches not connecting to the top of walls [#678]
- Cannot use netherwood sticks as fuel [#672]
- Fishing rods render upside down [#671] and have zero attack speed

## [1.21.1-4.0.3] - 2024-10-08
### Added
- New sounds to replace the "gear item damage factor change" (kachink) sound. Removed the config option. A resource pack could be used to easily change or remove these. [Related: #704]
### Fixed
- Crimson iron and azure silver ores not generating correctly
- Blocks sometimes failed to be broken [#704]
- Crash when feeding a material grader through the side with a hopper [#701]
- Crash when looking at sheet metal with no material [#696]
- Being able to combine two gear items into an invalid item in the crafting grid [#695]

## [1.21.1-4.0.2] - 2024-10-04
### Added
- Crude Knife and Crude Hammer. These are very cheap to craft and can be used with stone anvil recipes.
- Advancements for the stone anvil and crude knife
- Super Mixer. Can make alloys from anything. Work in progress, no recipe yet.
  - This effectively replaces the "legacy mixing" option if you were to add an extremely cheap recipe for it...
### Changed
- Default loot table for the blueprint package changed to give blueprints for: rod, pickaxe, shovel, axe, hoe, and sword
### Fixed
- Wielder effect traits like Flame Ward not working correctly
- Saws crashing the game when used [#692]
- Boots and leggings conversion recipes producing invalid items [#693]
- Gear items with invalid parts crashing the game [#693]

## [1.21.1-4.0.1] - 2024-10-03
### Fixed
- Build against current version of Caelus

## [1.21.1-4.0.0] - 2024-10-03
NeoForge 1.21.1 (and 1.21) port! Lots of internal changes: expect bugs!
### Known Bugs/Issues
- Wielder effect traits requiring a full suit of armor (e.g. Flame Ward) do not work
- Lots of balancing needs to be done, especially with the new synergy calculations
### Added
- Maces
- Necklaces
### Removed
- Legacy mixing. A new mixer block will be added later to replace this feature.
- Materials and gear items no longer have a "tier" property. Repairs can currently be done only with an identical material, but I'm working on some options to allow more flexibility.
### Changed
- Stats have been replaced with "properties." Properties can store just about any type of value, not just numbers.
  - Most are still simple number properties
  - harvest_tier is a unique object now
  - Traits are now stored in a traits list property
- Traits now contain a list of "effects" which determine what the trait actually does. This allows one trait to have multiple functions.
- Synergy calculations have been changed. Material categories now have the biggest impact, with rarity having a small impact. Expect further tweaking/changes.
- Compound materials are officially called "alloys" now.
- Material JSON format has changed substantially. Old data packs are NOT compatible.
- Lots of code refactoring/renaming, with more to come