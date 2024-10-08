# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.21.1-4.0.3] - Unreleased
### Added
- New sounds to replace the "gear item damage factor change" (kachink) sound. Removed the config option. A resource pack could be used to easily change or remove these. [Related: #704]
### Fixed
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