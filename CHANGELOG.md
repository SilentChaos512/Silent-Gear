# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.20.1-3.6.4] - 2024-06-18
### Fixed
- Possible fix for ConcurrentModificationException in PartGearKey

## [1.20.1-3.6.3] - 2024-03-20
### Fixed
- The patch from last version sometimes crashing, specifically if a file with a missing harvest tier has the old harvest level stat given as a JSON object. [#660]
  - Again, this patch will be removed no later than Minecraft 1.21, at which point harvest tiers _must_ be used.

## [1.20.1-3.6.2] - 2024-03-19
### Added
- A temporary compatibility patch to make pre-3.6.0 material JSON files load. This should allow most mods and data packs to function like they did before. A "material has no harvest tier" warning will be logged for each material that has a "guessed" harvest tier.
  - This will be removed no later than Minecraft 1.21

## [1.20.1-3.6.1] - 2024-03-17
Please read the changes for 3.6.0 as well!
### Changed
- Added resource ID and pack name to error messages related to loading of materials, parts, and traits. This should help track down mods and data packs that are not updated for 3.6.0.

## [1.20.1-3.6.0] - 2024-03-16
MAJOR BREAKING CHANGE! Please read...
### Changed
- BREAKING: Replaced the harvest level stat with a harvest tier property. This is a **breaking change** for mods and data packs! This change should fix compatibility with mods that add their own `Tier`s for block harvest checks.
  - To fix material JSON files, simply add a `harvest_tier` property to the root of the file (same level as `type` and `simple`). The value is a string, which is the name of the Tier, such as `minecraft:iron`.
### Fixed
- Added a proper texture and localized name for Coating Smithing Template

## [1.20.1-3.5.4] - 2024-02-24
### Added
- Some missing item descriptions in JEI (msueberkrueb) [#648]
### Fixed
- Netherwood saplings not growing. They can now be grown on dirt or netherrack. (gamehunt) [#657, fixes #636]

## [1.20.1-3.5.3] - 2023-12-25
### Fixed
- Salvagers checking recipes even with no input items, causing lag [#641]
- No animations on bows and slingshots [#613]
- Saw model held strangely in third person

## [1.20.1-3.5.2] - 2023-11-14
### Fixed
- Silent Gear loot not generating in chests or dropping from mobs [#637, #625]
- Crossbow model positioning (LuXeZs) [#634]
- Dummy tool/armor tier configs resetting on load (medsal15) [#635]

## [1.20.1-3.5.1] - 2023-09-03
### Fixed
- Elytra being equipped to curios back slot crashing the game [#616, #611]
- Broken elytra still allowing flight [#511]

## [1.20.1-3.5.0] - 2023-06-19
- Updated for Minecraft 1.20.1. Incomplete, but playable.
- Armor trims are not working. More research is required...
### Changed
- Applying a coating now requires a Coating Smithing Template in the smithing table

## [3.4.1] - 2023-06-09
### Changed
- Added a color cache for gear item models, which should improve performance [#566, #599, #602]

## [3.4.0] - 2023-05-18
- Updated for Minecraft 1.19.4

## [3.3.0] - 2023-03-21
- Updated for Minecraft 1.19.3

## [3.2.5] - 2023-02-15
### Fixed
- Set EMPTY stack size crash [#584]

## [3.2.4] - 2023-02-05
### Added
- Turkish translation (vemamr)
### Fixed
- Not being able to throw tridents when playing on a server [#575]
- Mattocks not being effective on crimson/warped stems and netherwood [#526]
- Tridents not losing durability when thrown

## [3.2.3] - 2023-01-29
### Fixed
- Bonemeal on grass blocks creating wild flax instead of flowers [#540]

## [3.2.2] - 2023-01-28
### Added
- Hoes! They're just hoes. They till dirt and stuff. Enjoy.
- Compound materials now display their synergy value
### Changed
- Paxels can now strip logs and perform all axe tool actions [#227]
- Vanilla netherite items can now be converted into equivalent gear items like other tools and armor
### Fixed
- Fragments now have a temporary model [#551]

## [3.2.1] - 2023-01-23
### Fixed
- Not being able to join servers due to changes made in 3.2.0 [#581]

## [3.2.0] - 2023-01-21
### Fixed
- Curios (rings, bracelets) with the Brilliant trait not warding off piglins
- The `silentgear:set_parts` loot function has been updated. It can now be used to set materials, even with modifiers (grade, starcharged).

## [3.1.5] - 2023-01-01
### Fixed
- Possible error when a gear item determines which model to use [#572]

## [3.1.4] - 2022-12-30
### Added
- Metal Alloyer recipes for blaze gold ingots and crimson steel ingots (they're cheaper!)
### Changed
- Elytra blueprint and template recipes no longer require netherite ingots (they use gold ingots instead)
### Fixed
- Compounder recipes with less than 4 ingredients not showing up in JEI (dimerald, etc.) [#570, #562, #552]
- Add colors to gear part models. Removed "TEMP MDL" layer even though the models are still temporary.

## [3.1.3] - 2022-12-24
### Changed
- Most tools can now display tip upgrades and grips on their models
- Materials using "lc" main textures should now display properly for most tools
### Fixed
- Tooltip cycle keys (Z/C) not working correctly [#567]
- Missing translation for potted netherwood sapling (ochotonida) [#536]
- Gear models not displaying their coating color
- Yet another attempt at working around DataResource crashes...

## [3.1.2] - 2022-12-22
### Fixed
- Another attempt at working around DataResource crashes

## [3.1.1] - 2022-12-17
### Fixed
- More block transparency issues [#537, #565]
- Add deprecated version of DataResource to fix compatibility issues with other mods

## [3.1.0] - 2022-12-09
### Changed
- Refactored some API classes

## [3.0.8] - 2022-12-06
### Fixed
- Potential fix for crash on login [#558]
- Add missing translation for Bending trait

## [3.0.7] - 2022-11-19
### Added
- Back slot for Curios
### Changed
- Replaced the new temporary shield model with the 1.18 model
### Fixed
- Caelus integration [#542]

## [3.0.6] - 2022-11-06
### Fixed
- Bort ores being impossible to find (new generation settings only affect new chunks) [#541]

## [3.0.5] - 2022-10-28
### Added
- Config options to set the dummy item tier and armor material for gear items. This can be used to work around mod compatibility bugs in some cases. [from 2.10.16 for MC 1.18.2]
### Fixed
- Possible fix for "unknown trait" error for existing traits [#545]

## [3.0.4] - 2022-10-05
### Changed
- Improved the temporary models for gear items [#538]
- Example gear items in creative menu are now made with specific materials, instead of random ones

## [3.0.3] - 2022-09-28
### Added
- The `/sgear_grade command` added in 2.10.15
- `silentgear:material` recipe ingredient improvements from 2.10.15
### Fixed
- Transparency issues with some blocks [#537]

## [3.0.2] - 2022-09-25
### Changed
- Replaced gear-damaging recipes with temporary substitutes
### Fixed
- Some issues with JEI plugin

## [3.0.1] - 2022-09-22
### Changed
- Tweaking material stats and traits. Notably, rods and other upgrade parts no longer get durability modifiers.
- Temp models for gear items now match the color of the main part

## [3.0.0] - 2022-09-20
Minecraft 1.19.2 port. Beginning of a compatibility-breaking overhaul.
### Added
- Blackstone crimson iron ore (behaves just like crimson iron ore, but spawns exclusively in blackstone)
- Bending trait
### Removed
- Long rod
- Some pointless and outdated advancements
### Changed
- Ore generation is now handled with data files. World generation config settings removed.
- "Enchantability" and "chargeability" stats renamed to "enchantment value" and "charging value"
