# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
- (Technical) GearConstructionData#parts is now guaranteed to be immutable to prevent accidental modification.
### Changed
- Simple material names ("gold" as opposed to "golden") are now used wherever it makes sense grammatically
- Repair kit now colors the names of materials for easier identification
### Fixed
- Repair kits not storing some materials correctly (such as alloys) [#793]
  - Existing repair kits should retain any simple materials and will save their contents using a new codec
  - "Unknown Alloys" may still appear until overwritten; they cannot be retrieved
- Smithing tables erroneously modifying the input gear item [#894, #878]
- Server connection issue relating to null parts when constructing creative tab [#895]
- Possible crash when filling repair kits [#892]
- Property visibility fixed; gear no longer stores irrelevant properties

## [1.21.1-4.1.2] - 2026-03-03
- (Technical) Various method signature changes, mostly related to computing gear property values. This may cause incompatibilities with some add-on mods.
### Added
- More detailed tooltips for traits on materials when a bound key (default Shift) is held
### Changed
- Removed the armor type condition from Flame Ward, so other items can benefit from the fireproof effect (not being destroyed when dropped in lava)
- (Technical) added/fixed some `toString` overrides to aid with debugging
### Fixed
- Some traits with conditions attached being lost from the final gear item [#890, #885]

## [1.21.1-4.1.1] - 2026-02-18
### Added
- Config option to disable spawning with a material book
### Changed
- Expanded traits information in material book, add explanation of trait conditions (the asterisk) [#882]
- Trait condition English localizations changed to read like plain English
### Fixed
- Material list in Material Book now clips text too long for the page (such as missing translations) [#882]
- Material book will now show icons for materials with only part substitutes (such as blaze rod, etc.) [#882]

## [1.21.1-4.1.0.2] - 2026-02-16
### Fixed
- Crash with Forgified Fabric API [#882]

## [1.21.1-4.1.0.1] - 2026-02-15
### Fixed
- Crash on servers [#881]

## [1.21.1-4.1.0] - 2026-02-15
### Added
- A material book, which will list all materials and their properties. The list can be sorted by name, ID, or numerical property.
  - The material book is currently given for free when you join/rejoin the world, or it can be crafted with a book and a blueprint paper.
  - This may eventually change into a full guide book if time allows me to do so, but I consider this sufficient for now
### Changed
- Updated command system (HMRich) [#872]
- (Internal) large refactoring of material tooltip code; should have no visible effects in-game
### Fixed
- Crude/Super Mixers causing an X-ray effect (added an appropriate collision shape to fix the issue) [#866]

## [1.21.1-4.0.30] - 2025-11-23
### Added
- The crude mixer, an early game alternative to the super mixer. It does that same thing (makes an alloy out of any two to four materials) but produces a weaker result.
  - The "Crude" penalty can be changed in the config (compounds.crude_mixer.property_multiplier). The default value of 0.8 gives a 20% penalty (affects all properties that synergy affects).
  - Has a recipe requiring stone, planks, and flax string
- A default super mixer recipe, which requires blocks of tyrian steel, a beacon, and a dimerald
- Models for crude mixer and super mixer by giok3r
### Changed
- Updated pt_br localization (PrincessStelllar) [#829]
- AOE tools (hammer, etc.) now mine 50% faster when targeting a single block by crouching
- Spears no longer perform a sweeping attack and Sweeping Edge does nothing for them
### Fixed
- AOE tools (hammer, etc.) mining blocks instantly while sneaking [#856]
- Snow golems not "damaging"/aggro-ing mobs  [#852]
- Armadillos not being damaged correctly (sigmusdewn) [#846, #847, #857]
- Spoon upgrade not working [#858]
- Correct entity interaction range for weapons (Electroely) [#845]

## [1.21.1-4.0.29] - 2025-09-23
### Added
- Alloy Forge recipe for Azure Electrum [#839]
### Fixed
- Remove unused data components ("base_properties" and "bonus_properties") to hopefully fix some compatibility issues [#842, #800, #777, #773]
- Arrow duplication with repair kit [#841]
- Glowstone attack speed modifier not working correctly [#836]
- Items with the Fireproof trait burning up in fire/lava [#832]

## [1.21.1-4.0.28.1] - 2025-08-30
### Added
- Check for null elements in `PartList` in order to create a more useful error message for [#834]

## [1.21.1-4.0.28] - 2025-08-21
### Added
- Breeze Rod material
- Wind Blast trait (found on breeze rod)
### Fixed
- Gear mod kit missing the "Tip Upgrade" option [#831]
- Crafted gear items showing as "example items" in the tooltip
  - If existing items have this bug, simply modify them in some way (add a grip or change the head, etc.) to clear the example flag

## [1.21.1-4.0.27.1] - 2025-08-17
### Fixed
- Crash when accessing the list of materials in some cases (caused by cached lists added in version 4.0.27) [#828, #827, #826]

## [1.21.1-4.0.27] - 2025-08-16
### Added
- JEI search aliases for materials. You can now search for "materials" or the category (such as "metal" or "advanced").
### Changed
- Cache lists of all materials to possibly fix [#826] and improve performance slightly
### Fixed
- Tridents not working with channeling [#816]
  - To fix this, I had to override the channeling.json file and added an entity type tag, `c:tridents`, to match compatible trident entities.
- Maces not working correctly (no smash attack or wind burst) [#805]
- Data-less items (in JEI, etc.) having an unintended gold coating (literally) and appearing yellow

## [1.21.1-4.0.26] - 2025-08-10
### Fixed
- Some parts and materials not displaying all properties [#822, #806]
- Block destroy speed on hammers and excavators now depends on all blocks being broken, not just the target [#820]
- Gear recipes not accepting materials with more specific gear type restrictions
- Add missing "shine" layer to tip upgrade model

## [1.21.1-4.0.25] - 2025-07-12
### Added
- A new trait effect, "silentgear:negates_damage". On armor, it reduces damage taken from specific damage types determined by a tag. [#821]
- Heat-Resistant trait, which reduces fire damage (fire, lava, magma, etc.) and prevents the item from being destroyed by fire when dropped. Added to crimson iron.
### Fixed
- Crash when logging in with items that have traits that no longer exist
- Bounce trait now actually bounces the player when they land after falling more than 3 blocks, as originally intended

## [1.21.1-4.0.24] - 2025-06-26
### Fixed
- Possible crash when mining a block with a missing loot table (seelderr) [#818]

## [1.21.1-4.0.23] - 2025-06-08
### Added
- New config options for the starlight charger
  - `work_time` - Sets the time of day energy can be gathered (daytime, nighttime, or anytime)
  - `requires_view_of_sky` - If disabled, energy can be gathered even with solid blocks above the starlight charger
- Starlight charger now displays the structure level in the menu screen
### Changed
- Cache gear item names in a data component
- Material dump command now displays the parent as a name instead of ID, pack name is now shown correctly
### Fixed
- Elytra not working in the curio back slot [#814, #804, #720]
- Crash when computing gear item names
- Attribute modifiers not updating in some cases [#813]

## [1.21.1-4.0.22] - 2025-05-25
### Changed
- Shield plates can now be crafted and used to craft shields. The old "quick" recipe still exists. [#810]
- Shield plate textures
### Fixed
- Void Ward not respecting the true lower world limit if mods change it [#787, #803]

## [1.21.1-4.0.21] - 2025-05-18
### Added
- Many materials now support rod substitutes using common rods (`c:rods/...`)
- Some missing tags including `c:tools/melee_weapon`
### Changed
- Vanilla items now retain all data components when converted to Silent Gear items. Theoretically, this should include Apotheosis modifiers. 

## [1.21.1-4.0.20] - 2025-04-21
### Fixed
- Revert materials with empty tags not being loaded. In many cases this would cause most materials to erroneously fail to load on the first time loading a world.
  - Instead, materials now have an `isValid` method which checks the tags. If the material is determined to be invalid, it will stay loaded but will not be displayed in most contexts.

## [1.21.1-4.0.19] - 2025-04-20
### Added
- Additive property for materials. Additive materials can be used to make compound materials, but cannot be used to craft gear directly.
- Boolean property type (used for additive property)
- Crushed shulker shell material (additive only)
- Option to allow starlight charger to charge the materials of a gear part (disabled by default) (JaisDK) [#797]
### Changed
- Shield blueprint/template now requires an iron ingot to craft
- Materials that have an empty tag as their crafting item will no longer be loaded. This can be changed in the config file.
### Fixed
- Gear using block interaction range for attack range (STS15) [#796]

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