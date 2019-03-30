# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Stone torches (craft with coal/charcoal and stone rods)
### Changed
- Rewrote gear crafting recipe code to allow recipes to show up in the recipe book and REI (probably JEI as well, have not tested). The recipe JSON files are still the same. If you didn't know, you can completely replace these recipes with data packs!
- Better temporary models for everything but bows. Still waiting on Forge for the proper model system to come back. These new models will likely become a config option, as they are probably much easier on less powerful hardware.
### Fixed
- Block-placing feature is back. Right-click while holding a compatible tool (SGear pickaxe, shovel, or axe by default) to place the block in the slot after the tool, or the last slot of your hotbar. Also works with the torch bandolier (from the Torch Bandolier mod).
- Lustrous trait being 15 times more powerful than intended. Also increased the bonus from sky light; block light is 75% as effective.

## [1.0.5] - 2019-03-19
Tweaks some things which will be needed for the next Silent's Gems release, which will add gear souls (replaces tool souls from older versions). Minor API changes.
### Added
- Lock stats command is back
### Fixed
- Armor recipes now reference the correct blueprints
- (Partially) When connecting to a dedicated server, information about traits and gear parts is synced to the client. Unfortunately, this happens too late and tooltips usually do not update.

## [1.0.4] - 2019-03-08
Traits are mostly implemented. There are a few that do not function yet, but it should be possible to create custom traits again.
### Fixed
- Tools having wrong attack damage and speed modifiers (removed equipment JSON files entirely) [#24]
- Example gear generates correctly
- Armor no longer says it's missing a rod
- Fix armor item color (custom worn armor is still colorless)
- A few misc tooltip-related issues

## [1.0.3] - 2019-03-07
Temporary workaround for Forge issue #5577, Silent Gear [issue #25](https://github.com/SilentChaos512/Silent-Gear/issues/25). This allows players to connect to servers and LAN games. I have noticed their are issues with gear parts not syncing to the client, which I am pretty sure is unrelated. Gear items can still be crafted and used normally as far as I know, they just look wrong.

## [1.0.2] - 2019-03-03
### Added
- Improved temporary gear models. They do not display upgrades or support unique textures, but... it's something. Now we wait for Forge to fix stuff.
- Grade argument to "sgear_parts add" command.

## [1.0.1] - 2019-02-28
### Added
- World generation (flowers, netherwood, and crimson iron)
### Changed
- Crafting station part slots removed. This leaves just the crafting grid and side storage.
### Fixed
- Blocks with GUIs can be opened again
- Flower and netherwood sapling models corrected

## [1.0.0] - 2019-02-24
Port to 1.13.2. Bumped version to 1.0.0 because of major changes, but this is still an alpha of course.
- Old part files are not compatible and must be updated.
- Parts are now added with **datapacks**. Files should be in `data/<namespace>/silentgear/parts/`, where `<namespace>` is a unique ID for your datapack. You can organize files into subfolders as well (e.g. have a folder for main parts, a separate folder for rods, etc.) This is optional, but encouraged.
- Gear **models do not work**, you will see white outlines instead (the items still work, of course)
- Tool heads removed... sort of. Crafting a blueprint with main parts only creates a rodless tool (head).
    - Tool heads are technically their respective tool. You can use them, but some stats are reduced until you attach a rod. May add additional penalties later.
    - You can also place the tool rod (and bowstring for bows) in the crafting grid at the same time to craft the whole tool in one go!
- Traits are broken right now
- None of the blocks function yet
