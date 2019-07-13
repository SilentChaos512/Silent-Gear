# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Missing models for crossbow blueprints and templates
- Crusher recipes (Silent's Mechanisms) for crimson iron ore

## [1.2.1] - 2019-07-11
### Added
- Crossbows. These have some minor animation issues (because vanilla is dumb), but they function correctly.
### Changed
- Random grading now assigns the same grade to all parts. Added a config which can revert to the old behavior.
### Fixed
- Some issues with the JEI plugin on servers
- Blended head colors including non-main parts
- Possibly improves part analyzer performance

## [1.2.0] - 2019-07-01
Updated for Minecraft 1.14.3

## [1.1.3] - 2019-06-24
JEI plugin is up to date. Recommended Forge is 26.0.51 or later.
### Added
- A few new traits, assigned to some existing parts
### Changed
- Small change to trait level calculations
- Blueprint paper recipe now uses the vanilla blue dye item only, until Forge fixes the blue dye tag

## [1.1.2] - 2019-06-21
### Added
- Loot condition `silentgear:has_part`, which can check if a gear item has a particular part. Can also check the grade of the part.
- Loot condition `silentgear:has_trait`, which can check if a gear item has a given trait
- Gear stat changes are now logged in the debug log. There will be a config for this later, but for right now it is forced on.
### Fixed
- Issue where trait data was not being sent completely during client login

## [1.1.1] - 2019-06-20
### Added
- Netherwood slabs and stairs
- Recipe advancements (most recipes will show in the recipe book when you get certain items)
- New command, `sgear_random_gear` which will give randomized gear items
- Loot table function for setting specific parts on a gear item
- Chinese translation (zh_cn, by XuyuEre)
### Fixed
- Example gear items not appearing in the creative tab

## [1.1.0] - 2019-06-17
### Added
- Configs for random grading. You can adjust the mean, standard deviation, and max grade.
- Gear with mixed main parts will blend the colors, but with much greater weight on earlier parts
### Removed
- Blue flower and black/blue dyes (use cornflowers and new vanilla dyes instead)
### Changed
- Netherwood tree generation (more leaves, subject to change)

## [1.0.12] - 2019-05-11
### Changed
- Blueprints and templates now have the output item in their name, instead of the tooltip
### Fixed
- Stats not calculating correctly in some cases (e.g. tip upgrades) [#27]
- Bow "lite" models are animated now
- Rod and bowstring blueprints/templates being consumed during crafting

## [1.0.11] - 2019-05-08
### Added
- Command to recalculate stats on all of a player's gear (can target multiple players)
- Command to list all registered parts. Also shows how many of each type there are.
### Fixed
- Parts and traits should now sync all needed data with clients
- Fix gear losing their stats during recalculations (e.g. ka-chink)
- Flax plants not growing

## [1.0.10] - 2019-05-08
### Added
- Config to disable enchanted effect on gear items (this adds a new client config file). Since the effect is broken on vanilla models, this is disabled by default.
- Spears. They are weaker than swords, but have a much longer reach. These may need some balancing still.
### Fixed
- Should fix gear parts and traits not syncing to clients on LAN games

## [1.0.9] - 2019-04-24
### Added
- JEI plugin has been updated
### Fixed
- "Unknown part type" error when connecting to servers
- Bows not being enchantable
- Blocks connecting to analyzers improperly

## [1.0.8] - 2019-04-15
Small tweak needed by Silent's Gems 3.0.11.

## [1.0.7] - 2019-04-07
### Added
- Analyzer catalyst, which will increase the median grade when grading parts. These are set by item tags and there are two tiers (`silentgear:analyzer_catalyst/tier1` and `silentgear:analyzer_catalyst/tier2`). By default, tier 1 is glowstone dust, and tier 2 is glittery dust, a new item.
### Changed
- Rod-less tools (heads) will now hurt you when used. Ouch!
- Hammers are no longer effective on "extra materials" like circuits (redstone, ladders) and glass
- Durability max is now Integer.MAX_VALUE (2^32-1) and Armor Durability max is 1/16th of that (134,217,727)
### Fixed
- Hammers breaking unbreakable blocks
- Parts GUI is working again (access in the crafting station GUI)
- Some broken/missing translations

## [1.0.6] - 2019-03-30
### Added
- Stone torches (craft with coal/charcoal and stone rods)
- Potted blue flower and netherwood saplings (just use the block on a flower pot)
- Flax is obtainable, look for wild flax plants in plains or mountain biomes.
- Sinew is now obtainable
### Removed
- Item of flax plant (block still exists, but you will get a warning on world load)
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
