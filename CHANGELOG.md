# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.10.12] - 2022-08-16
### Added
- Tridents

## [2.10.11] - 2022-08-15
### Changed
- Main part items (tool heads, etc.) now store durability. When swapping parts, the main part item now retains the gear's damage. Fixes some repair exploits.

## [2.10.10] - 2022-07-07
### Fixed
- Paxels not working on many blocks, pickaxe spoon upgrade not working [#518]

## [2.10.9] - 2022-07-06
### Changed
- Blueprints for armor, elytra, and shields now display their armor durability modifier [#521]
### Fixed
- Block placer traits not working on pickaxes, paxels, and hammers [#515]

## [2.10.8] - 2022-06-21
### Changed
- All gear items have had their dummy item tiers changed from "diamond" to "wood" or "leather". This is to test the Minecolonies compatibility issue [#328]
- Hide part synergy when legacy material mixing is disabled

## [2.10.7] - 2022-05-30
### Fixed
- Imperial and Gold Digger traits not working [#509]
- Magmatic not working [#504]

## [2.10.6] - 2022-05-22
### Added
- Block mining speed traits (`silentgear:block_mining_speed`) which increase the mining speed for blocks in given tags
- Greedy trait, which increases the mining speed on ores (`forge:ores`)
- Replaced Soft with Greedy on Blaze Gold main material

## [2.10.5] - 2022-05-13
### Fixed
- Mattocks not tilling dirt [#502]
- Shears mining all blocks faster [#411]

## [2.10.4] - 2022-05-13
### Added
- Translation keys for issue [#508]
  - Material categories
  - JEI gear crafting recipe handler (material categories, part types, and gear types text)
  - Fully-Loaded Blueprint Book name
  - Unimplemented guide book item messages
### Changed
- High-Carbon Steel is now a permanent material and has a translation key [#508]
- Unimplemented warning message for guide book [#415]
### Fixed
- Missing translation for refined glowstone material [#508]

## [2.10.3] - 2022-05-08
### Added
- Korean translation (gjeodnd12165) [#507]

## [2.10.2] - 2022-05-02
### Fixed
- Amethyst clusters not dropping the correct number of items with gear mining tools [#484]
- Axes not disabling shields [#467]
- Possibly some other issues related to pickaxes, shovels, and axes not behaving quite like their vanilla counterparts

## [2.10.1] - 2022-04-25
### Fixed
- Materials with "tag not empty" conditions (extra mod metals) not loading [#503]

## [2.10.0] - 2022-03-31
**Requires Silent Lib 6.2.0 or higher and Minecraft 1.18.2!**
- Updated to Minecraft 1.18.2. Some fixes may be slightly hacky, but they are functional.

## [2.9.1] - 2022-03-15
- Rebuild to fix crash [#494]

## [2.9.0] - 2022-02-24
**Requires Silent Lib 6.1.0 or higher and Minecraft 1.18.1!**
### Fixed
- Block entities (grader, charger, etc.) losing their inventories [#487]
- Amethyst material missing translation (ZOrangeBandit) [#492, #469]

## [2.8.8] - 2022-02-06
### Fixed
- The `silentgear:netherwood_soil` tag now uses the `minecraft:dirt` tag instead of the `forge:dirt` tag, which was removed in newer versions of Forge

## [2.8.7] - 2022-02-03
### Fixed
- Fishing rods not working at all [#425, #476]
  - Known bug: The fishing line always renders on the offhand side

## [2.8.6] - 2022-01-20
### Fixed
- Possible crash when placing netherwood saplings (tag not bound error) [#482]
- Moonwalker trait not working on curios [#458]

## [2.8.5] - 2022-01-18
### Added
- Config to make it so old parts are not returned when swapping (`gear.upgrades.destroySwappedParts`) [#483]
### Fixed
- Empty NBT tag being set on empty `ItemStack`s [#481]
- Missing dyed fluffy block recipes

## [2.8.4] - 2022-01-09
### Added
- Snow Walker trait. It allows the player to walk on powder snow when the trait is on either armor or a curio.
  - Found on leather, wool, and fine silk, but is restricted to boots.
### Changed
- Updated the color of leather to match the vanilla armor textures

## [2.8.3] - 2022-01-07
### Fixed
- Wild flax and fluffy plants not generating or being very rare [#457]

## [2.8.2] - 2021-12-22
### Fixed
- Machetes not working on cobwebs [#452]
- Durability bars for gear and repair kits not working correctly [#451]

## [2.8.1] - 2021-12-16
### Fixed
- Old data packs and some mods making it impossible to load into a world [#450]

## [2.8.0] - 2021-12-05
- Updated to Minecraft 1.18
### Removed
- Ore chunk items removed (deprecated Silent's Mechanisms compatibility)
### Changed
- Bort generation
  - Now spawns between heights -60 and 10 (triangular height placement, so most will be farther down)
  - Default config changed to 6 ores per chunk
- Bowstring renamed to "cord" (relevant item IDs, tags, and the name used in material JSON files have changed; data packs must be updated)
- All trait types have had "_trait" removed from their ID for consistency (data packs must update)

## [2.7.10] - 2021-11-28
### Fixed
- Compound materials (alloys) now ignore grades and other enhancements of their component materials.
- Stat modifier traits not applying stat modifiers when first added to an existing item (via an upgrade)

## [2.7.9] - 2021-11-26
### Fixed
- Some stats like reach distance being missing from gear

## [2.7.8] - 2021-11-26
### Added
- Material modifiers, a new system for altering materials. This is used for grades and starcharging. [#433]
### Changed
- The starcharged effect applied by the starlight charger is no longer an enchantment, it is a material modifier. The old enchantment still exists for backwards compatibility (for now) [#433]
- Grades are now a material modifier. The NBT used by grades is still the same, this is just an internal change.
- Materials listed in gear tooltips will display their starcharged level now

## [2.7.7] - 2021-11-25
### Added
- Config options to control the starlight charger's energy charge rate and max stored energy [#446]
- Some tips regarding JEI to blueprints

## [2.7.6] - 2021-11-07
### Added
- New config options for material and part tooltips, found in the client config (Rimevel) [#421]
### Changed
- Removed WIP labels from compound materials and compounder blocks

## [2.7.5] - 2021-11-01
### Changed
- Allow Moonwalker on Curios
### Fixed
- Caelus API support [#437]
- Compounder block screen's "Work: On/Off" button being invisible (metal alloyer, recrystallizer, etc.)

## [2.7.4] - 2021-10-09
Requires Forge 37.0.59 or higher
### Changed
- Materials can now specify rendering information in the data file, making the assets file optional. This will only work with built-in textures.
### Fixed
- Blueprint book not allowing items to be taken or moved [#436]
- Harvestability issues with the spoon upgrade and paxels [#429]
- Fireproof trait now works again

## [2.7.3] - 2021-09-04
### Added
- JEI support is back!
### Fixed
- Crash on launch in `NerfedGear` [#427]
- Mattocks being effective on stone [#420]
- Armor and elytra not recognizing valid materials correctly [#418]
- Some recipes not respecting the `allowLegacyMaterialMixing` config and allowing mixing anyway

## [2.7.2] - 2021-08-22
### Added
- Traits for amethyst
- A "splotches" texture for main parts, used by basalt and blackstone
### Fixed
- Screens (blueprint book, machines, etc.) not rendering correctly

## [2.7.1] - 2021-08-12
Requires Forge 37.0.31 or higher!
### Changed
- Updated for Forge's new harvest tool system (37.0.31+ required)
- Stat modifiers for katana and machetes switched (with small changes). Katana now deal more damage and are slower, while machetes deal less damage and are faster. This change was made to give both sword types a purpose.
- Classic mixing is now disabled by default. The config was renamed to "allowLegacyMaterialMixing" to force it to reset.
- All materials now use the Silent Gear Chargeability stat, instead of referencing the old Silent's Gems stat
### Fixed
- Deepslate bort ores not spawning
- Blocks missing their "mineable with" tags and not being affected by vanilla tools

## [2.7.0] - 2021-08-08
### Changed
- Updated for Minecraft 1.17.1. This version is _not_ complete! Some changes for 1.17 still need to be done and the mod has not been tested. Use with caution!

## [2.6.30] - 2021-07-12
This version _does NOT_ load with Silent Lib 4.10.x! Continue using 4.9.6 for now.
### Added
- Deepslate bort ore (unused, 1.17 prep)
### Removed
- Silent Gear's old `ExclusionIngredient` type (replaced by Silent Lib equivalent)
### Changed
- Bort ore textures tweaked

## [2.6.29] - 2021-07-08
### Added
- Two config options related to gear enchanting (both in `gear.enchanting`). One allows or disallows normal enchanting (sets enchantability to zero). The other will forcibly remove all enchantments from existing items except those added by traits. [#375]
### Changed
- Copper's stats
### Fixed
- Adamant, Aquatic, and Chilled traits not having their bonus damage effect [#373]

## [2.6.28] - 2021-07-03
### Added
- Enchantment description for Star Charged (TomasBorsje) [#399, #388]
- Fireproof trait: makes it so item cannot be destroyed in fire or lava. Flame Ward also has the same effect. [#309]
- Configs to control the distribution of grades produced by the material grader
### Fixed
- Numerous issues related to salvaging compound materials and fragments [#364]

## [2.6.27] - 2021-06-19
### Changed
- Updated pt_br.json [#385]
- Mattocks now have the hoe tool type (might fix [#328]?)
- Tripled the speed at which the starlight charger stores energy

## [2.6.26] - 2021-06-02
### Changed
- Tweaked some attack speed values, notably for gold and iron to match vanilla [#394]
- Armor model colors are now cached for speed, like with item models
### Fixed
- Crash in some cases due to accidental use of `joptsimple.internal.Strings` class [#393]
- Armor model color not matching item color [#392, #192]

## [2.6.25] - 2021-05-26
### Added
- Missing description for road maker upgrade
### Changed
- Potion effect traits now refresh every 10 ticks instead of every tick and eliminated use of streams API in tick method (optimization) [#389]
- Magic damage stat is now hidden from material tooltips

## [2.6.24] - 2021-05-09
### Added
- Raw ores and raw ore blocks for crimson iron and azure silver
### Changed
- Crimson iron and azure silver ores now drop raw ores (unless silk touched)
- Textures of crimson iron and azure silver ingots and ores
### Fixed
- Items made of compound materials not rendering correctly [#378]
- New block models causing other blocks to not render on adjacent sides [#374]
- Shears not taking damage when breaking blocks with no hardness (grass, etc.) [#337]

## [2.6.23] - 2021-05-03
### Added
- Registered custom gems and ingots (such as dimerald) will now show in JEI
- Missing JEI support for refabricator (partial, mixed fabric examples not working)
- Missing refabricator recipe
- German translation (CptPICHU)

## [2.6.22] - 2021-04-23
### Added
- A new grade, MAX, which is above SSS. Has a 30% stat bonus. Will this stop the questions about the max grade? Probably not...
- Config to disable WIP tooltip on some items

## [2.6.21] - 2021-04-21
### Fixed
- Conditional traits sometimes not appearing gear items (magmatic, soft, etc.) [#372, #359]
- Traits not ticking for armor and elytra when in non-armor slots (inventory, hands) [#365]

## [2.6.20] - 2021-04-21
### Added
- Refabricator, a compound maker that processes cloth, fiber, and slime materials [#344]
- New models for most machine/crafting blocks (naj77)

## [2.6.19] - 2021-04-11
### Changed
- Starlight charger model (naj77)
- Metal alloyer textures (naj77)
- Metal press model (naj77)
- Salvager model (naj77)
### Fixed
- Star Charged enchanted books existing (hopefully)

## [2.6.18] - 2021-04-07
### Changed
- Reworded the "incorrect network version" message to hopefully be clearer [#367]
### Fixed
- Stack overflow exception with new saw code (also added a `gear.saw.recursionDepth` config option) [#366]
- Starlight charger displaying incorrect/negative stored charge on servers

## [2.6.17] - 2021-04-04
### Fixed
- Starlight charger applying multiple instances of enchantment at different levels [#360]
- Saws not working on fungi and some trees (especially ones with non-straight trunks) [#300]
  - Now considers both leaves and wart blocks to be "foliage"

## [2.6.16] - 2021-03-29
### Added
- Missing recipes for dusts (hammer + ingot)
- Blueprints for gear items now display a list of supported part types
- Harvest level tips to ore item tooltip
- Something for April Fools Day
### Fixed
- Material grader shift-click appearing to not work on the client [#353]
- Misspelling of recrystallizer container title

## [2.6.15] - 2021-03-28
### Changed
- Changed how material NBT is read and written in some cases. This makes the NBT a little more compact in most cases. Old items will still function normally.
### Fixed
- Alloy ingots not turning into or working as sheet metal [#356]
- Compound materials always having a tier of 0 [#355]

## [2.6.14] - 2021-03-24
### Added
- Config to disable debug logging related to world generator features [#354]
### Changed
- Increased repair efficiency for many tools: hammer and saw to 150%, excavator to 200%, mattock and paxel to 120%, katana to 100%
- Hammers now work on glass [#69]
- AOE tools (hammer, excavator) no longer play the "break event" (sound and particles) for each block broken, just the targeted one
- Tweaked colors of blaze gold, blaze rod, and gold materials
- `/sgear_traits dump_md` command now includes the version of loaded mods
### Fixed
- Some model logging not being disabled with the config

## [2.6.13] - 2021-03-21
### Added
- Chargeability stat (was part of Silent's Gems)
- Star Charged enchantment, which boosts the stats of materials, like a weaker version of supercharging
- Starlight Charger. Gives the Star Charged enchantment to materials. Must be exposed to the sky during nighttime to store energy for charging.
- Fishing rods. Requires a main part, rod, and a bowstring (which will be renamed to "cord" in the future)
  - Known issue: model does not change after rod is cast
- Some missing translations
### Fixed
- Previous changes to shears durability being completely wrong

## [2.6.12] - 2021-03-20
### Added
- Widen trait. Increases the area-of-effect for hammers and excavators. Max level 3, only 1 is obtainable by default.
- Wide plate upgrade, which gives the Widen trait (level 1)
- Config option to disable all conversion recipes
### Changed
- Small change to durability and repair efficiency of shears

## [2.6.11] - 2021-03-19
### Added
- A guide book... Don't get too excited, it only gives a link to the wiki right now
### Changed
- Model and texture debug logging (`debug.logging.modelAndTexture`) is now disabled by default, since no recent reports of the texture issue have been made. Update your config if desired.

## [2.6.10] - 2021-03-18
### Added
- Example recipes for generic compounding (metal alloyer, recrystallizer) to JEI [#346]
### Changed
- Update pt_br.json (SAMUELPV) [#329]
- Change how skipped materials are logged to hopefully reduce confusion

## [2.6.9] - 2021-03-10
### Added
- Detect network version on login and give the client a less cryptic error message if their version does not match
### Fixed
- Traits not appearing on anything (gear or materials) [#348]

## [2.6.8] - 2021-03-09
(.1 patch) - fixes incorrect version format
### Added
- Gear Smithing Table block, currently non-functional, no recipe yet
- Gear Smith villager profession (workstation is the gear smithing table)
  - Trades are incomplete
  - Does not spawn naturally
### Fixed
- "Useful Flora" advancement not having fluffy puff/seeds criteria

## [2.6.7] - 2021-03-07
### Added
- Sheet metal materials. Metals can be pressed into sheets. Not suitable for tools and makes weaker armor than the base materials, but can also be used to craft elytra.
- Metal press, used to craft sheet metal
- Missing translation keys for fluffy plants
### Changed
- Tyrian steel recipe now requires crushed shulker shell and ancient debris instead of nether stars
- Elytra now have a 25x durability modifier (up from 12x)
  - Elytra armor durability stat of phantom membrane and fine silk reduced to roughly match previous values
- Seeds (flax and fluffy) can now be fed to chickens and parrots, but will not cause them to follow the player
### Fixed
- Toughness of refined obsidian much lower than Mekanism values [#343]

## [2.6.6] - 2021-02-28
### Added
- Exception handling code which _might_ prevent some Optifine crashes [#334]
- Gear crafting recipes in JEI now display descriptive text, including material restrictions
- Tooltip to sinew and fine silk describing how to get them
### Changed
- Fluffy puffs and fluffy seeds can now be composted

## [2.6.5] - 2021-02-24
### Added
- Self repair traits, which can repair (or damage) gear gradually over time.
- Renew trait, a self repair trait that restores durability slowly. Level 1 given to phantom membrane material.
- Bounce trait. On boots, it nullifies fall damage, consuming durability based on the fall distance. On armor, it knocks back attackers.
  - Found on slime lining (existing items in your inventory will gain the trait)
  - Note: This is also intended to cause the wearer to bounce, as if they landed on slime blocks. This does not work well, so that code is currently disabled.
- Traits can now specify conditions. Trait instances on materials and parts can still have conditions as well, but most of the `gear_type` conditions have moved to the trait files.
### Changed
- Manually split the armor stats of extra mod metals and made some slight tweaks on some
- Slime material now requires slime blocks instead of balls
### Fixed
- Compounding recipes not being detected correctly [#333]
- Stellar trait not repairing items, now set to 2% chance per level per second [#312]
- Magnetic trait's pull strength (was cut in half by a recent update)

## [2.6.4] - 2021-02-21
### Fixed
- Compound materials not splitting armor stats correctly [#327]
- Colors of lumium and signalum [#318]
- Armor displaying toughness and magic armor values incorrectly
- Armor plates showing some incorrect stats, including the wrong durability stat

## [2.6.3] - 2021-02-20
### Added
- Horse armor salvaging recipes (Gmoney2123) [#331]
- Some work on new materials (items added in 2.6.2), incomplete
### Fixed
- Stat modifiers not applying in some cases. Might fix the issue with Silent's Gems' gear souls.

## [2.6.2] - 2021-02-19
### Added
- JEI support for compounders (metal alloyer and recrystallizer)
- All fluffy puff-related stuff moved to Silent Gear from Silent's Gems (old Gems version will remain until 1.17)
  - Seeds are obtained from wild fluffy plants. Search in less-dry biomes, like forests.
  - Fluffy blocks now bounce the player, similar to beds (they still reduce damage based on stack depth as well)
  - Added fluffy feathers and fluffy string, which can be crafted into vanilla items if necessary
  - New textures for some blocks and items
  - Materials coming soon
- Fine Silk (rare drop from spiders, double rate for cave spiders) and Fine Silk Sloth. Both will be materials.

## [2.6.1] - 2021-02-18
### Added
- Tyrian Steel: a new metal alloy and gear material
  - Recipe (may change, JEI support coming soon): 1 each of crimson steel ingot, azure electrum ingots, and nether star in the metal alloyer
- Sturdy trait: has a high chance of reducing damage taken by gear
- Void Ward trait: attempts to save the player from falling out of the world (only works on armor). Knocks the player upward and gives levitation and slow falling when taking void damage. 
- Materials can now be searched for traits, categories, and part types in JEI, but JEI's `SearchAdvancedTooltips` config must be enabled
  - This works by dumping a list of terms on the last line of the tooltip when building "advanced" tooltips (F3+H mode)
- New materials: bamboo (rod) and paper (fletching)
- Stats for vine binding
- Blaze rod and end rod materials (rod only part subs)

## [2.6.0] - 2021-02-10
### Added
- Compound materials. These are still being fine-tuned, so expect changes. They are completely optional and nothing else has changed yet. [#326]
  - The plan for 1.17 is to disallow material mixing in the crafting grid by default, requiring compound materials to get mixing bonuses
  - Currently there are two compound crafters, the Metal Alloyer and Recrystallizer
  - Recipes can also be added to either block for crafting custom materials or other items
### Changed
- Tooltip keybindings will now be ignored and default to old behavior if unbound or bound to either left/right modifier keys [#319]
- New shield model and texture (UnmovingJaveline)
- Fix spear model offsets (UnmovingJaveline)

## [2.5.5] - 2021-01-31
### Added
- Config option to allow repairs without repair kits (item.repairKits.efficiency.missing). Default value is zero, which requires repair kits. If enabled, repair kits may still be used. [#316]
- Display a chat message on player login when part/material models fail to load properly, directing the player to the log file
### Fixed
- double_crimson_iron_ore_veins configured feature not registered [#317]
- Non-main parts not displaying stats in tooltip [#314]
- World generation configs not working

## [2.5.4] - 2021-01-25
### Added
- Gear item name and NBT will now be shown in crash reports whenever the game crashes while recalculating stats and traits
### Fixed
- `set_damage` command not triggering a gear stat recalculation
- (maybe) A crash (CME) when enchantment traits are updating an item's enchantments

## [2.5.3] - 2021-01-24
### Added
- Fragments can now be used to fill repair kits (1/8 of a unit per) [#311]
- Add netherwood wood (log with the side texture on all side), with stripped variant [#301]
- Config to disable material mixing in the crafting grid when crafting parts. This is left enabled (old behavior by default).
### Fixed
- Elytra not working on back slot with Caelus API [#302]
- Netherwood logs not being strippable [#301]
- Armor plates displaying the wrong armor value

## [2.5.2] - 2021-01-17
### Fixed
- Crash caused when attempting to get the part from an item in some cases [#310]

## [2.5.1] - 2021-01-14
### Added
- Config to toggle model/texture debug logging (left enabled by default)
### Fixed
- Second attempt at fixing crash when creating gear models in some cases (likely a third-party issue)
- Lucky and Cursed traits not working on curios
- Equipped curios not recalculating stats and traits on login
- Crimson iron having duplicate armor stat modifiers

## [2.5.0] - 2021-01-12
(API) Some refactoring was done (no changes to trait classes)
### Added
- Items to `minecraft:piglin_loved` tag: golden nether banana and blaze gold block/dust/ingot [#306]
- Red card trait, found on the red card part (function is the same as before, it's just a trait now)
- Bort (gem, block, ore). Spawns rarely in the overworld (a few single blocks per chunk). Will be required to craft adornments in the future, but is optional for now.
- (API) GearApi class, which contains methods that redirect to many commonly used methods (GearData, TraitHelper, etc.)
### Fixed
- Some armor items having incorrect armor values [#304]
- Crash when creating gear models in some cases
- A large number of false missing texture errors showing in the log
- Blueprints from other mods not showing up in recipes (but still working)

## [2.4.11] - 2021-01-02
### Changed
- Reduced impact of tier differences on synergy by half
- Grades no longer affect rarity
### Fixed
- Should fix a crash caused by stat rework from last version [#303]

## [2.4.10] - 2021-01-02
### Added
- Stats can now be specified per gear type. For instance, this allows the armor stat to be manually set for each piece, rather than using a predefined ratio. Old files will still work as before.
- Turtle (scute) material. Can be used to craft helmets only.
- Turtle trait. Mimics the vanilla turtle shell helmet's effect.
- Netherwood charcoal, burns 2400 ticks (smelts 12 items, configurable)
- Netherwood charcoal blocks, 10 times the configured burn time
### Changed
- Netherite-coated armor now uses the vanilla model texture
- Updated pt_br.json (SAMUELPV) [#282]
### Fixed
- Elytra having no model [#297]
- Potion effect traits crashing if arrays are missing elements

## [2.4.9] - 2020-12-26
### Added
- New trait type, `silentgear:cancel_effects`: Cancels specific potion effects when equipped (see Cure Poison and Cure Wither for examples)
- New trait type, `silentgear:bonus_drops`: Gives a chance of increasing loot drops for a given ingredient/item/tag (see Imperial and Gold Digger for examples)
- New traits (no default materials): Cure Poison, Cure Wither, Gold Digger, Imperial
### Changed
- Most model debug logging should now appear in latest.log. This will add a lot of spam to the log file, but it may help debug the texture issues that some users have.
### Fixed
- Adornments not being restricted to curios
- Crossbow charged arrow/firework textures broken

## [2.4.8] - 2020-12-22
### Added
- Broken models/textures for most gear items
### Fixed
- Potion effect traits (Mighty) not working on curios [#293]
- Add missing elytra blueprint/template recipes

## [2.4.7] - 2020-12-21
### Added
- Vein count configs for crimson iron and azure silver ore [#269]
- Configs for wild flax generation [#269]
- Elytra (requires Caelus API to work) [#147]
  - Elytra wings are crafted from main materials with armor durability in the cloth category (wool, leather, phantom membrane)
  - Elytra are crafted from elytra wings and a binding
- Phantom membrane material (armor/elytra main, grips)
### Changed
- Gear models can now optionally have broken textures (see elytra)
- Wool can now be used to craft armor and elytra
- Default vein counts for crimson iron and azure silver reduced by almost half
- Crimson iron vein count is doubled in basalt deltas and soul sand valley
### Fixed
- Gear not returning swapped parts when applying a material substitute part (sticks, bones, etc) [#287]
- Reverted a change that made main parts not always be first in the list, causing several bugs

## [2.4.6] - 2020-12-20
### Fixed
- Ranged weapons missing ranged weapon stats [#292]
- Holy and Chilled traits not working [#290]
- Material/trait dump commands outputting to server instead of client [#253]
- Gear shears not working on beehives [#200]

## [2.4.5] - 2020-12-20
### Fixed
- Swinging gear melee weapons crashing servers [#291]
- Nether portal effect broken (missing transparency) [#289]
- Fix durability traits (malleable, brittle, etc) not working in some cases [#286]
- Broken shears being usable [#285]
- Attribute traits not stacking effects with items with same trait in different slots
- Armor-only materials not working for new armor parts

## [2.4.4] - 2020-12-19
### Hotfix
- Fix client-only class (`GearHudOverlay`) referenced on server
### Added
- Attack reach stat. Weapons with bonus reach will attack mobs outside the normal vanilla range (the attack indicator will be green in the extra range). Decreasing attack reach will not work. [#65]
- Salvager recipes for vanilla netherite gear [#281]
- Detailed logging for textures custom models are attempting to load, may help debug [#190]
- Dedicated items for the main parts of each armor type
  - Blueprint + main materials will craft the new part items
  - Craft the main part to make the final armor item
- Hint to fragment item tooltip about how to combine
### Changed
- Tools can no longer have zero attack speed [#183]
- Magnetic now respects the `PreventRemoteMovement` from Demagnetize
- Magnetic no longer pulls items with a pickup timer (thrown) or when the player is sneaking
### Fixed
- Shields not craftable with armor-only materials [#268]
- Fragments for materials with exclusion ingredients (ie "wooden") sometimes not combining together
- Very crude repair kit missing from "Handyman" advancement

## [2.4.3] - 2020-12-07
### Added
- Some alternative recipes for coatings and tip upgrades, to help players who have recently updated
- pt_br translation (SAMUELPV) [#272]
### Changed
- ru_ru translation updated (Voknehzyr) [#260]
### Fixed
- Enchantment traits not being removed when the trait is lost [#58]
- Enchantment traits not adding enchantments in most cases

## [2.4.2] - 2020-12-06
### Changed
- Halved the armor bonus from Bastion (now +1 per level)
### Fixed
- Jeweler tools not working from within the blueprint book for some recipes [#277]
- Not being able to equip bracelets [#275]
- Missing recipes for bracelet blueprints [#275]
- ITrait#onAttackEntity being called on the client

## [2.4.1] - 2020-12-03
### Added
- Bracelets (requires Curios). Functionally identical to rings, but they go in the bracelet slots.
- Config to disable the "tool broken" message
### Removed
- Broken, unnecessary Botany Pots recipe for flax [#263]
### Fixed
- (Partial fix) Gear crossbows destroying gear arrows. Vanilla crossbows not fixed. [#270]

## [2.4.0] - 2020-11-29
### Added
- Rings. Requires Curios to function correctly. The shank can only be crafted with metals (this can be overridden with data packs if desired).
- Adornments, a new part type. Currently only works with rings.
- Jeweler Tools (texture WIP, but usable). Used to craft adornments and attach them to ring shanks.
- Knives. Weaker and slower than daggers, but more durable. Tagged as `forge:knives`, same as daggers, so they work for template crafting.
- Missing recipe for road maker upgrade
- Several traits from the Just Gems data packs: crackler, floatstoner, ignite, kitty vision, mighty, and sharp
### Changed
- Potion effects traits now have a "type" enum for each effect instead of just a boolean for "full set required"
    - Type values: `trait_level` (based on level of trait), `piece_count` (number of armor pieces with trait, same as false in old files), `full_set_only` (same as true in old files)
    - Old files should still load correctly for now
- Replaced dagger blueprint with knife blueprint in the blueprint package
- Survival Tool advancement will now trigger with either a knife or a dagger

## [2.3.10] - 2020-11-28
### Added
- Material categories. These are unused currently, but will be used for synergy calculations in the future. Categories can be any string and may be defined by mods or data packs. [#267]
- Individual traits can now supply extra wiki lines in their JSON. _This is not synced with the client on dedicated servers._ Useful for adding more information to frequently confused traits, like Lucky.
### Changed
- Materials will no longer inherit stats and traits from their parent if they provide any of their own [related to #266]
- Improvements to formatting of the trait wiki page dump command
    - Material list is formatted for easier reading
    - Shows list of parts that provide traits (useful for upgrades)
### Fixed
- Crimson wood and warped wood having the flammable trait [#266]
- Damage on use for block filler traits (Road Maker) being loaded incorrectly (value was truncated to an int)

## [2.3.9] - 2020-11-26
### Added
- New projectile stats to most basic materials (mains only right now)
### Fixed
- Flax recipe for Botany Pots [#263]
- Arrows not being repairable

## [2.3.8] - 2020-11-22
### Added
- Arrows
    - Crafted with an arrow blueprint, main material, rod, and fletching
    - Can be repaired to refill arrows (hold 256 total)
    - Right-click with arrows in your hand to merge similar stacks together
    - Currently missing the new projectile stats on all materials, will fix next version
- Block filler traits. Replaces targeted block(s) with a given block when item is used on a block.
- Road Maker trait and upgrade for excavators. This block filler trait replaces grass blocks with grass paths in a 3x3 area
- Dagger blueprint to blueprint package loot table
### Fixed
- Gear item models added by other mods crashing the game
- Repair values on many materials being much higher than intended

## [2.3.7] - 2020-11-17
- Hotfix 1: Fix server crash on launch [#261]
### Added
- (API) `onItemSwing` method for `ITrait`, which is called when the player left-clicks without targeting a block or entity
### Changed
- Small optimizations made by replacing stream API use with loops in some places [#259]
### Fixed
- Cache example output of gear recipes (performance improvement in some cases) [#259]
- Possibly fixes missing textures for pack-added materials [#190, #183, maybe #257]

## [2.3.6] - 2020-11-16
### Added
- Configs to disable the "kachink" sound when tool stats are recalculated [#256]
- Target effect traits. Will apply potion effects to the target when attacked. See Venom trait for an example.
- Added a command to generate an MD file containing details on loaded traits. You can find built-in traits [here](https://github.com/SilentChaos512/Silent-Gear/wiki/Trait-List)
### Changed
- Russian translation of grade (Voknehzyr) [#255]
- Update Chinese translation (XuyuEre) [#248]
### Fixed
- Leather's armor value was wrong

## [2.3.5] - 2020-11-08
- DEVS: Updated mappings to 20201028-1.16.3
### Changed
- Removed max value caps on all armor stats. Normally, values under 40 are "sane", but this should allow greater mod compatibility [#231]
- Removed max value caps on enchantability, rarity, harvest level, harvest speed, and melee/magic/ranged damage (they already had extremely high values anyway)
- "MAX" stat modifiers (common with harvest level) now display an up arrow instead of a caret (^) before the number
### Fixed
- Repair efficiency of shields [#246]
- Netherwood blocks not breaking quickly with axes [#245]
- Synergy and grades making negative stat modifiers more negative (they now become less negative, improving stats) [#212]

## [2.3.4] - 2020-11-07
- Marked compatible with 1.16.4 (untested)
### Fixed
- Missing textures for armor tip upgrades [#226, #233, #247]

## [2.3.3] - 2020-11-01
### Added
- Simple parts can now add layers to gear models (it works, but needs some improvements still)
- Gear mod kit, which can remove some types of parts from gear items
- Very crude repair kit, for those who struggle to find iron
- Prismarine coating material
- Texture for spoon upgrade part
- Flax flowers, dropped by flax plants (non-wild), can be crafted into blue dye

## [2.3.2] - 2020-10-22
### Added
- Basalt material
### Changed
- Small tweaks to blackstone and netherrack
### Fixed
- Coating upgrades being stackable [#242]
- Ores generating only on the corners of chunks [#241]
- Wrong textures used for netherrack mains
- Missing bone material translation

## [2.3.1] - 2020-10-12
### Removed
- (API) Most PartType.create overloads removed, leaving only the Builder variant
### Changed
- (API) PartTypes can now specify the maximum number allowed in an item based on GearType
- (API) PartTypes can now return a CompoundPartItem based on the GearType

## [2.3.0] - 2020-10-10
### Added
- Harvest level hints for levels 0-4. This can be changed or added to by changing the lang file with a resource pack (keys are "misc.silentgear.harvestLevel.<level>") [#228]
### Changed
- Spreadsheet columns reordered (traits before tier now), removed deprecated rendering info columns
- (API) Some IMaterial methods now have an IMaterialInstance parameter. The old signatures were removed, possibly breaking compatibility with some mods.
### Fixed
- Various server issues caused by "tag not bound" bug [#224, #232, #234]
- pack.mcmeta using wrong format number

## [2.2.0] - 2020-09-30
Port to 1.16.3
### Changed
- Increase crimson iron and azure silver ore vein size from 6 to 8
### Fixed
- Break speed events not handled correctly (cech12) [#225]
- Possible divide by zero crash (12emin34) [#223]

## [2.1.1] - 2020-08-26
### Added
- Glowing dust, which replaces glowstone dust as the tier 1 grader catalyst
- Alternative recipes for grip/bowstring blueprints for broken mod packs
- Add Mekanism materials
- Add bone material (bone block main, bones as rod sub)
### Changed
- Phantom lights can now be waterlogged
- Grader now shows the last rolled grade (to make the process of re-grading more obvious)
- The lock stats and recalculate stats commands are now subcommands of `/sgear_stats`
### Fixed
- Saws firing block break event on client, potentially crashing with certain mods [#222]
- Some conditional traits not showing on part items
- Some advancement issues

## [2.1.0] - 2020-08-17
- Lots of refactoring done. I tried my best not to break anything, but keep an eye out for new bugs.
### Added
- Repair value stat, a multiplier for durability restored by materials during repairs
- JEI support for material grader, which shows available catalysts [#211]
- New traits, heavy and light
- `sgear_stats info` command, which lists modifiers for each stat on the gear item in the player's hand
- Config to disable stat logging
### Changed
- Limit stats displayed on compound parts to only relevant stats [#213]
- Armor protection ratios to match vanilla diamond and netherite
- Knockback resistance stat now scaled to the vanilla display value, rather than the modifier value (0.1 -> 1)
### Fixed
- Blueprint book issue with Quark [#219]
- Possible crash with blueprint book [#218]
- Netherite missing armor durability and armor toughness [#210]
- Anvil repairs not working
- AOE tools (hammer, etc.) not rendering outline on each block to be broken
- Fragment colors

## [2.0.9] - 2020-08-10
### Added
- Salvager recipes for vanilla bows and crossbows
- Colors to material names in tooltips
### Fixed
- Armor crafting not working
- Diamond tip upgrade having the wrong harvest level

## [2.0.8] - 2020-08-09
### Fixed
- Prospector hammer crashing servers

## [2.0.7] - 2020-08-09
### Added
- Fragments. These are partial material items which may be returned by the salvager [#191]
- Prospector hammer. Works like a pickaxe, works in any recipe requiring a hammer. Using on the side of a block (not the top or bottom) will search a small area in that direction for ores. The blocks the prospector hammer searches for can be set in the `silentgear:prospector_hammer_targets` block tag (not an item tag). The tag contains just the `forge:ores` tag by default.
- Some nether chests can now contain netherwood saplings and Nether metal ingots/nuggets
### Fixed
- Quick gear recipes creating the wrong tool head for some tools

## [2.0.6] - 2020-08-07
### Added
- Highlight selected slot of blueprint book
### Changed
- Crimson steel is now tier 4
- Armor textures updated
- Tweaked binding and grip textures to fit the model more tightly
- Some advancement icons now have appropriate materials set for compound parts and gear
### Fixed
- Maybe fix model crash [#206]
- Tools accepted tool heads of other tool types [#205]
- All gear boots triggering moonwalker advancement [#204]
- Armor having terrible repair efficiency
- Broken gear having traits

## [2.0.5] - 2020-08-04
### Added
- Azure silver and azure electrum. Azure silver ore is found in The End. Azure electrum is a craftable alloy.
- Accelerate trait. Increases harvest speed, attack speed, and ranged speed by fixed amounts as gear is damaged
- Moonwalker trait. Reduces gravity, but only works on boots
- Azure repair kit
- Magic armor stat now actually works
### Changed
- Trait calculations for gear items. They now take the highest level of any trait from any part. Compound parts still follow the old logic. [#93]
- Gear type stat modifiers have been moved to the tool head part JSON files. This allows them to be customized with data packs.
- Default capacity of repair kits to 16, 32, 48, and 64
### Fixed
- Stacked blueprint dupe [#199]
- Harvest levels incorrect on tool heads with mixed materials
- Armor not giving a "has broken" message

## [2.0.4] - 2020-08-03
### Added
- Brilliant trait, makes piglins non-aggressive on armor. Requires the primary material to have the trait.
- New trait condition `silentgear:primary_material`, requires the trait to be on the primary (first) material
- Conflicting material crafting item detection, displays potential conflicts in chat on login
### Fixed
- Crimson iron ore harvesting without tools [#201]
- GetMaterialStatsEvents not firing for graded materials (Silent's Gems supercharging)
- Crude tool advancement not triggering

## [2.0.3] - 2020-07-30
### Added
- Fully-loaded blueprint book with all blueprints in creative menu
- Netherwood fence gate block (totally missed that somehow)
- Recipes for the Woodcutter mod
- Russian translation (KhottyManatee55) [#197]
### Changed
- Doubled the inventory size of the blueprint book
- Make filling repair kit more obvious (hopefully)
- Long traits list on gear and trait descriptions on now shown when pressing Shift (can be rebound)
- Display gear construction key can be rebound
### Fixed
- Part textures sometime rendering in the wrong order (bowstrings, saw blade) [#196]
- Tooltip colors! Also improved some tooltips for gear.
- (Maybe) Occasional crash in ColorUtils, exact cause unknown

## [2.0.2] - 2020-07-29
1.16.1-only bug fix
### Added
- Part substitute items for materials. This allows items to be specified as parts made of their respective material. [#194]
- Rod substitutes for iron, stone, wood, netherwood, and rough wood [#194]
### Fixed
- Rough tool recipes not working [#194]

## [2.0.1] - 2020-07-28
### Added
- Granite, diorite, and andesite materials (same stats as stone by default) [#193]
### Changed
- Repair kit will now discard materials if less than 0.01 units remain (no longer shows 0.0 units)
### Fixed
- Temp fix for salvager dupe bug. Compound parts other than tool heads can no longer be broken down into materials for now. [#191]
- Armor and shield blueprints not working from the blueprint book
- Repair kits repairing items with lower tier materials
- Block placer traits not consuming durability or playing sound effects
- Pressing "Cycle Back" when viewing material info causing a crash in some cases

## [2.0.0] - 2020-07-26
Major overhaul of model system (again)
(hotfix) Fixed blueprint book being consumed when crafting
### Added
- Blueprint book. Yeah, I finally figured it out. Stores blueprints, but not templates. Use Z or C to cycle stored blueprints, or open the book and Ctrl+click the blueprint you want. The cycle keys will work whenever your mouse cursor is over the book. It will even update the crafting result when cycling.
- Repair kits now store materials instead of "repair value". Any repair value in existing kits will be lost. The number of materials that can be stored is configurable. Repairs can use partial materials, so there is still no waste.
- Repair kits now have different "efficiency" levels, giving more incentive to upgrade. Values are configurable.
### Changed
- Gear and part models now support custom textures added with resource packs.
- Material rendering info is now loaded from `assets/silentgear/silentgear_materials` (that's "assets", not "data")
- 'server' config is now 'common' and will appear in the config folder, like most configs do
- Cycle material info key binding changed to "Cycle Next". The "Cycle Back" key will also work with material tooltips. These are the same keys that blueprint books use.
### Fixed
- Shears not losing durability on leaves [#188]
- Parts not showing some conditional traits [#186]
- Some missing lang keys
