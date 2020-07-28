# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

## [1.10.2] - 2020-07-23
### Added
- (1.16.1) Coating part type
- (1.16.1) Netherite coating. Combine any item with a netherite ingot in a smithing table. Gear item does not need to be diamond, it can be made of anything.
- (1.16.1) Blackstone and crimson/warped wood materials
- Racker trait (moving from Silent's Gems). It places netherrack.
- Stripped netherwood log added to logs tag [#185]
- Missing binding and grip textures
- `set_damage` command (for testing purposes)
### Changed
- Damage on use for refractive trait increased to 4, terminus and racker increased to 3
### Fixed
- (1.16.1) A bug causing stat calculations of compound parts to be wrong (fix pending for 1.15.2)
- Part swapping and upgrade parts not working [#178, #172]
- Blended colors not working on tools
- Trait conditions not working with new materials. Some existing items may lose traits as a result of this fix. You may need to adjust the ratio of materials to get traits like flame ward again. 

## [1.10.1] - 2020-07-21
- This is a 1.16.1 bugfix update! 1.15.2 will skip this version as nothing needs to change.
### Removed
- Unused legacy items (old tip upgrades and bowstrings, etc.)
### Fixed
- Magnetic trait crashing on server [#180]
- Magnetic trait pushing items away instead of pull them [#179]
- Advancements displaying red X's instead of gear items
- Tools not returning their actual harvest level

## [1.10.0] - 2020-07-19
### Removed
- (1.16.1) Crafting station
### Changed
- Major overhaul of item models. Unlimited layers, defined in material files. Can "stack" multiple layers in the material file.
- (1.16.1) Lumber axe renamed to saw
- (1.16.1) All blueprint items changed their ID to `type_blueprint` (was `blueprint_type`)
### Fixed
- Armor models missing [#170]
- Gear items not displaying a synergy value (just taking the average of all parts for now, since the value is just for show) [#169]
- Missing blueprint flavor text (shears, slingshot, crossbow)
- Blueprint flavor text lang key not using correct namespace

## [1.9.6] - 2020-07-18
### Fixed
- Upgrade parts from other mods not working
- Random bows generating with a random part instead of bowstrings
- Slingshots failing to fire in creative mode

## [1.9.5] - 2020-07-17
### Fixed
- GetMaterialStatsEvent is now correctly fired (needed for supercharging fix) [Silent's Gems #472]

## [1.9.4] - 2020-07-14
### Fixed
- Tool head swapping not working with new tool heads
- Repair kits stacking and duplicating
- Repair efficiency calculation for gear items being wrong

## [1.9.3] - 2020-07-13
Requires Silent Lib 4.6.6
### Added
- Crafting a repair kit with a stick will empty it completely (materials cannot be returned)
- JEI support for salvager
### Changed
- Reworked advancements [#151]
### Fixed
- Crash on server startup (KeyTracker and KeyBinding)
- Materials not displaying their grade

## [1.9.2] - 2020-07-12
### Added
- Tooltips for new materials, overrides old part tooltips when material is registered. Keys can be rebound, defaults to control to show info and C to cycle part types.
- Salvager recipes, can add new recipes with data packs
### Changed
- Updated salvager to handle new gear items correctly. It can also salvage compound parts now.
### Fixed
- Compound bowstring not working (part file was missing)

## [1.9.1] - 2020-07-10
### Added
- Compound bowstring part
- Compound fletching part (will be used for arrows, not implemented)
- Missing netherwood blocks (stripped log, fence, trapdoor, and door) [#88]
### Changed
- Most Netherwood textures updated [#88]
- Blaze gold's color to be more orange
### Fixed
- Wild flax plants being harvestable by right-click to harvest mods [#159, #82]
- Missing localizations [#144]

## [1.9.0] - 2020-07-09
Very big internal changes. We are in the process of fully integrating the new material system with the mod! Old data packs should continue working where possible, but I recommend updating.
### Added
- Tool heads (compound main parts). The crafting process is mostly the same as before, but the new tool heads are separate items which are crafted with the new material system.
- Compound binding part
- Automatic material adapters. The mod will try to create materials from the parts in older data packs and mods. This will be removed in version 2.0 (Minecraft 1.16.x). The log will contain details on what it decides to do with each part, and why adapters are not created.
- (API) Material serializers can now be registered. Shouldn't be needed in most cases.
### Changed
- Gear generator (commands, loot tables) now create items entirely of compound parts.
- Log now lists the number of compound parts vs simple/legacy parts, in addition to the total
- Log now lists the number of adapter materials, as well as the total.
### Fixed
- Possible prevent a ColorHandler crash, cause unknown. Some item colors will break if this occurrs. [#162]

## [1.8.2] - 2020-07-02
### Added
- Missing slingshot blueprint/template recipes
- Materials (new system) for all common mod metals and Silent's Mechanisms metals
- Hard trait. Increases harvest speed slightly, decreases ranged damage (cancels with Soft)
### Changed
- Switched over to Forge's config system
### Fixed
- Stone torch recipe incorrect [#152]

## [1.8.1] - 2020-06-27
More work on moving to data generators! Keep an eye out for missing/incorrect recipes.
### Added
- Repair kits. These are now required for quick repairs, but they can also (optionally) store the repair materials. [#147]
    - Note: You cannot get items back after storing them in the repair kit!
    - Craft a repair kit with materials to fill it
    - One repair kit can store multiple tiers of materials and will use the lowest first when repairing
    - The capacity of the repair kit is shared by all tiers
    - Quick repair recipe is now: gear + repair kit + optional extra materials
### Removed
- Tags for specific types of strings and bowstring. Flax string and sinew fibers are still tagged as `forge:string`.
### Changed
- Gear damaing recipes (`silentgear:damage_item`) can now specify a minimum gear tier (`minGearTier`)
### Fixed
- Color of compound part items with no NBT

## [1.8.0] - 2020-06-20
### Changed
- Data generator for material files. Small tweaks to some materials, but should be functionally the same as the manually created files.
    - Files are now located in `/src/generated/resources/`
### Fixed
- Might fix [#143]

## [1.7.5] - 2020-06-16
### Added
- Template boards, which are now used to craft templates. Requires a dagger, or anything tagged `forge:knives`.
- Crude dagger recipe (one main part + rough rod)
- Compound tip upgrade part and tip upgrade blueprint
- Config to disable new material tooltips
- Compound part items display the materials they were crafted with in the tooltip
- Recipe to craft compound grips with two materials [#138]
### Changed
- Shift ingredients in JEI recipes to hopefully make mixed materials more obvious [#142]
- Ranged damage and speed are now displayed as multipliers [#140]
- Slingshot ranged damage reduced by 75% [#140]
- Bow and crossbow damage reduced by 2 (base projectile damage should be equal to ranged damage now) [#140]
- (API) Any item stat can be set to display as a multiplier, like armor durability
### Fixed
- Random items sometimes being displayed as SGear materials [#141]
- Gear items being destroyed in certain recipes (pebbles, blaze gold dust, template boards)

## [1.7.4] - 2020-06-13
Requires Silent Lib 4.6.3
### Added
- Compound grip parts and grip blueprint
- New material grader (only works with the new material system). Works like the part analyzer, but with some changes. [#83]
    - Catalysts are now **required**.
    - There are now five catalyst tier tags. Tier 1 has a median grade of C. Tiers 4 & 5 are empty by default.
    - Graded materials (less than SSS) can now be "re-rolled" by placing them back into the input slot. The grader will consume catalyst until a higher grade is rolled. If a lower grade is rolled, the item is unchanged and remains in the input slot.
- Blaze gold, can be used for crafting with the new material system
- Blazing dust, tier 2 grader catalyst
### Changed
- Grade bonus percentages - no more negative values, but the top grades have had there bonuses reduced since they apply to non-main parts now.
- Glittery dust is now a tier 3 grader catalyst
- Materials dump command (`/sgear_mats dump`)
    - Clicking the file name in chat will open the generated file
    - Sorted entries by part type then material ID
### Fixed
- Crash with `ShapelessCompoundPartRecipe` and `MaterialInstance` [#135]
- Compound rods being craftable with non-rod materials

## [1.7.3] - 2020-06-10
- Requires Silent Lib 4.6.2
- 'b' build updates mods.toml to contain correct Silent Lib version
- 'c' build fixes a crash in `ExclusionIngredient`
### Added
- Slingshots (finally). Faster to draw but weaker than bows. Uses pebbles for ammo. Ammo cannot be retrieved.
- Synergy traits. This allows the existing Synergistic and Crude traits to be customized, as well as allowing new synergy traits to be added.
- Rustic trait. Increases synergy, but only if the base value is between 75% and 100%.
- Compound rods are technically craftable and usable in this build. Not thoroughly tested yet! The recipe does not show in JEI. It's a rod blueprint and two materials.
    - Note this uses the new material system! Any main parts previously added by data packs will not work.
### Changed
- Some changes to how colors are handled. All layer colors are now cached in NBT. Locked and unlocked gear should update.
- Gear items will now exclude some stats from their NBT, such as armor stats on tools. This should have no impact on how the mod plays.
- Debug log will now contain detailed information on stat recalculations (forced on for now)
- (API) `ICoreItem` now has a method to return an `IItemColor`, which will be automatically registered. This defaults to the appropriate method for most items.
- (API) `ICoreItem` now has a method called `getExcludedStats`. Stats returned here will not be calculated or stored in NBT. The default implementations should be suitable for most items.

## [1.7.2] - 2020-06-05
Requires Forge 31.2.5 or higher
### Added
- Moar new traits!
    - Cursed - Reduces luck. Cancels with Lucky. Max level 7, same as Lucky.
    - Magnetic - Pulls in nearby items, range based on level
    - Terminus - Creates and places stone blocks, costs 1 durability per use
    - A couple of secret traits of questionable usefulness...
- Shears (NOTE: these do not function correctly because of missing Forge patches!) [#103]
- `/sgear_mats` command for interacting with new WIP material system (currently unused)
### Changed
- The `silentgear:refractive` trait type has been replaced with `silentgear:block_placer`, which can be made to place any block!
### Fixed
- Flammable's item destroyed message not giving the item name
- Shields not being enchantable

## [1.7.1] - 2020-05-29
### Added
- Flammable trait (only on wood by default). Flammable items take damage if the player is on fire and the item is equipped. They can also be used as fuel in furnaces, with the burn time being proportional to the item's durability.
### Fixed
- Attribute traits (Lucky) stacking each time item is equipped [#129]
- Parts GUI not displaying stats correctly [#128]

## [1.7.0] - 2020-05-20
### Added
- Command to export part data to a TSV file, `/sgear_parts dump`. A TSV file can be imported into any spreadsheet program.
- Spoon trait. This allows any part to function like a spoon upgrade. It will only work on pickaxes, same as before.
- Crushing trait. Increases armor and decreases attack damage as gear is damaged.
- ru_ru.json lang file (vanja-san) [#118]
### Changed
- Reworked some gear tooltip stuff. Synergy is under stats. Traits are more clearly labeled.
- (API) - Stats are now a Forge registry and should be registered using the Register event.
    - Stat names with the `silentgear` namespace can continue to omit the namespace in JSON files, others should include it.
    - The stat multiplier config settings are currently broken
### Fixed
- Balanced armor and shield repairs (now based on armor durability stat) [#123]
- Blueprint paper recipe not accepting tagged dyes [#122]
- Duplication of upgrade parts in some cases [#121]
- `silentgear:has_part` and `silentgear:has_trait` loot conditions not working for weapons

## [1.6.4] - 2020-05-13
### Added
- Indestructible trait, prevents durability loss (not found on any default part for now, but the trait does work)
- Lucky trait, increases luck when in main or off hand
### Changed
- Refractive trait now places phantom lights on right-click at the cost of 5 durability [#105]
- Phantom lights now generate particles
- Broken armor no longer renders on the player
- Prettified stat tooltips on gear items (with colors!)
- (API) Traits can now modify the right-click action of tools
### Fixed
- Ranged speed stat not working on bows [#119]
- Bows and crossbows displaying "When in main hand" with no modifiers

## [1.6.3] - 2020-05-07
### Changed
- Shield durability is now based on the "armor durability" stat. Iron shields should be the same durability as vanilla shields.
- Reworked the way stat modifiers are read from part JSONs. Old files are still compatible. New style is less verbose.
### Fixed
- Shield models in wrong location (Partonetrain) [#117]
- Shields not having any color [#114]
- Netherwood sticks will no longer lower durability below 20
- Multiple modifiers on a single stat for a part not working

## [1.6.2] - 2020-05-02
### Fixed
- Possible crash with armor and Quark [#115]
- Possible solution for CME in PartManager [#104]
- "Add harvest level" stat modifiers not working (ornate gold rod from SGems). This moves the "max" operation to right after the "avg" operation. Shouldn't effect stat calculations in most other cases. [#70]
- Armor showing "When in main hand" in tooltip

## [1.6.1] - 2020-04-27
### Added
- Paxels are back! Requires 5 mains and a rod. Small durability bonus, small penalty to harvest speed and enchantability. [#114]
- Shields are here (technically). Model currently has issues and colors do not work. Not sure how to fix this just yet. [#114]
### Fixed
- Netherwood trees not spawning [#110]
- A possible lumber axe crash when used on large trees [#84]
- (API) `PartData.onAddedToGear` being called too often [[Super Multi-Drills #19](https://github.com/SilentChaos512/SuperMultiDrills/issues/19)]

## [1.6.0] - 2020-04-20
### Added
- (API) `IGearPart.randomizeData`, creates a PartData object used during random gear generation
### Changed
- Move all gear crafting recipes to their own tab in JEI [#106]
- (API) PartTypes can now specify a fallback part, deprecates `PartManager.tryGetFallback`
- (API) Gear class maps in ModItems now use ResourceLocations for keys, to allow other mods to add gear types
### Fixed
- Swapping gear parts will now attempt to return the replaced parts in the crafting grid
- Part names being underlined after viewing them in the parts menu
- Gear randomizer not selecting parts in some cases
- Random gear command not working with command blocks
- Salvager attaching grade NBT to returned items

## [1.5.8] - 2020-04-15
### Fixed
- Server crash caused by new command added in 1.5.7 [#112]
- Issues with armor coloring being incorrect, adds a couple new texture properties to part JSONs (see [Silent's Gems #454](https://github.com/SilentChaos512/SilentGems/issues/454))
    - `armor_color` - The color of the armor model (defaults to `fallback_color`)
    - `armor_texture` - The prefix of the armor texture path (defaults to `texture_suffix`)
    - You can leave these new properties out (default) in most cases
    - This fix disables color blending for armor models (items will still show blending)

## [1.5.7] - 2020-04-12
Requires Forge 31.1.18 or higher
### Added
- Command to show parts GUI (`/sgear_parts show_gui`). Does not require OP perms. [#107]
- Crimson iron and steel blocks [#88]
- Golden nether banana [#88]
### Changed
- Updated compatible items to remove from drop system of Mine and Slash (AzureDoom) [#97]
- `/sgear_parts list` no longer requires OP perms
### Fixed
- Magmatic trait not working [#109]

## [1.5.6] - 2020-04-07
### Added
- Attribute modifier traits. These will apply attribute modifiers to gear items based on the type of gear and the trait level.
### Changed
- (API) Replace Lombok getters with normal getters. API unchanged, just makes the code clearer.

## [1.5.5] - 2020-04-03
### Removed
- Part analyzer advancement
### Fixed
- Possible solution for CME in PartManager [#104]

## [1.5.4] - 2020-03-18
### Fixed
- Main parts still showing grade tooltip [#83]

## [1.5.3] - 2020-03-06
### Added
- Mine and Slash integration via new data pack system (AzureDoom) [#94]

## [1.5.2] - 2020-02-20
### Fixed
- Stone torch (wall version) transparency issue

## [1.5.1] - 2020-02-11
### Fixed
- Crimson iron dust and ore chunks are no longer hidden when Silent's Mechanisms is not installed [#87]
- Cutout blocks (flax, stone torch) rendering incorrectly [#86]
- Version in crafting station screen showing 1.14.4 instead of actual Minecraft version [#85]

## [1.5.0] - 2020-02-01
Ported to Minecraft 1.15.2
### Added
- Login message to warn the player when parts and traits fail to load. Instructs the player on what to search for in their log file ("Failed to reload data packs").
### Removed
- The part analyzer and part grading. See issue [#83](https://github.com/SilentChaos512/Silent-Gear/issues/83) for plans/discussion.

## [1.4.6] - 2019-12-25
### Fixed
- Tooltip crash with gear, possibly caused by removed traits [#81]
- Fix some harmless(?) warning about material grade command arguments [#80]

## [1.4.5] - 2019-11-25
### Changed
- Armor now gains Mine and Slash stats (AzureZhen) [#78, #79]
- Remove material mixing tooltip from blueprints, as they are no longer true [#73)]

## [1.4.4] - 2019-11-05
### Added
- End stone main part
### Changed
- Increased lumber axe melee damage
### Fixed
- Possibly fixes part crafting items being scrambled in worlds where items have been added/removed [#74]

## [1.4.3] - 2019-10-01
### Added
- Lumber axe (finally). Chops down entire trees by breaking a single block. Trees are defined as logs with leaves attached. The standard block tags for logs and leaves are used. Textures may need some work...
### Fixed
- Possibly fixed crash caused by Console HUD [#68, #53]

## [1.4.2] - 2019-09-29
### Added
- (API) `IGearPart#onGearDamaged`
### Fixed
- Crafting station dropping inventory when rotated

## [1.4.1] - 2019-09-18
### Fixed
- Part analyzer not dropping inventory when broken [#62]
- Axes breaking permanently when stripping bark [#63]
- Broken shovels being usable with Dirt2Path [#63]

## [1.4.0] - 2019-09-13
Bump required Forge version to 28.1+, Silent Lib 4.4.0
### Fixed
- Should make part and trait maps thread-safe (I hope) [#59]
### Fixed
- Upgrade parts applying to invalid gear items

## [1.3.11] - 2019-09-10
### Changed
- Data paths for parts and traits changed. silentgear/parts is now silentgear_parts and silentgear/traits is now silentgear_traits. Files in the old location will still load for now.
### Fixed
- Hidden parts appearing on the parts screen in the crafting station
- Sound type of flax plants

## [1.3.10] - 2019-09-04
Update requires Forge 28.0.83 or higher
### Added
- A joke part: barrier
### Changed
- Part names and prefixes are now serialized like vanilla text (`translate` to translate, `text` for literal text). Old part files will load correctly.
- Part name prefixes no longer nested in `name`, moved to `name_prefix`
### Fixed
- Armor not using blended colors
- Should fix barrier items being produced when salvaging some items

## [1.3.9] - 2019-08-19
### Added
- Gear name prefixes. Can be added to individual parts (see rough rods for any example), or through an event (GearNamePrefixesEvent)
### Changed
- Mine and Slash random drops are now tier 1 minimum
- Mine and Slash random drops should now be salvagable
### Fixed
- Hammer dupe [#51]
- JEI failing to load on servers [#52]
- JEI complaining about QuickRepairRecipe

## [1.3.8] - 2019-08-13
### Fixed
- Mine and Slash compatibility race condition

## [1.3.7] - 2019-08-13
### Added
- Basic compatibility for Mine and Slash
### Fixed
- Parts blacklisted for "all" gear types not actually being blacklisted

## [1.3.6] - 2019-08-12
### Added
- Stat multiplier configs for gear. These allow the stat values of all items to be increased or decreased without overwriting every part file.
- Gear type blacklist to part tooltips
- Texture for phantom light item, translated name [#44]
### Fixed
- Ranged damage stat being ignored

## [1.3.5] - 2019-08-06
Updated for Forge 28.0.45
### Fixed
- Tool head swapping restoring durability in some cases... for real this time [#45]

## [1.3.4] - 2019-08-02
### Added
- Custom tipped upgrades should now show up in JEI and creative tabs
- A couple more advancements
- Sort button to parts GUI. This is no longer a drop down list, it just cycles the options (just wanted a quick fix for right now)
### Fixed
- Parts with malformed JSON not raising an error (may not detect all cases still...) [#41]
- Tool head swapping restoring durability in some cases [#43]
- Some armor items missing color

## [1.3.3] - 2019-07-30
### Changed
- Make parsing of part crafting items less strict. The `item` property will be removed if `tag` is present. Undefined tags will no longer prevent the part from loading (but you still won't be able to craft with it, of course)
### Fixed
- Hammers destroying the NBT of certain blocks, like shulker boxes and soul urns [Silent's Gems #384]
- Color handlers crashing the game in some cases [#39]

## [1.3.2] - 2019-07-28
### Added
- Custom tipped upgrades. This is a single item which allows the part to be changed with NBT. Just set "PartID" to the ID of the part. For the crafting items of the part, replace the usual "normal" object with an empty "custom_tipped_upgrade" object. You will need to create the recipe for the upgrade yourself.
- The "nerfed gear" config is back. Disabled by default. You can set any damageable item to have reduced durability, to encourage use of Silent Gear over vanilla items (or items from other mods if you wish). This may not work for all items.
### Changed
- Part analyzer optimized a bit. Should reduce tick time when it's not working.
- Lite gear models reworked. Added grip layer to tools. Some layers support a very limited set of alternate textures.
- Internal changes to how parts are detected. May reduce performance slightly in some cases. This was needed for custom tipped upgrades.

## [1.3.1] - 2019-07-24
### Added
- A `hidden` property for traits. Hidden traits will not be shown in the list of traits on an item, unless advanced tooltips (F3+H) are enabled.
- Trait conditions, which allow conditions to be assigned to traits on parts. The part will not apply the trait to a gear item if the conditions are not met.
### Fixed
- Some gear crafting recipes not working [#36]
- Diamond and glowstone tip upgrades not having the Lustrous trait

## [1.3.0] - 2019-07-23
- Update to 1.14.4

## [1.2.4] - 2019-07-22
### Fixed
- Random crash with Silent's Gems

## [1.2.3] - 2019-07-21
### Added
- Config to enable additional logging for loading and syncing parts and traits
- Tier 3 analyzer catalyst tag (no items in it by default)
- Recipe to replace the head of a tool. Craft a tool with a tool head (a tool with only main parts). The old head is retained.
- Spear blueprints to the "swords" loot pool for the blueprint package
### Changed
- Trait descriptions are now shown in item tooltips when the Alt key is held down
- Parts with zero armor durability are automatically blacklisted for armor crafting. Similarly, zero durability will blacklist all other gear types.
### Fixed
- Traits not loading on OS X
- Gear type blacklist for parts now actually works
- Some armor textures being broken
- Overrode vanilla's dumb repair recipe again... This disables crafting grid "repairs" of Silent Gear items, but the grindstone still works.

## [1.2.2] - 2019-07-17
### Added
- New trait type, `silentgear:nbt_trait`. This will add arbitrary NBT to gear items when crafted.
- Missing models for crossbow blueprints and templates
- Crusher recipes (Silent's Mechanisms) for crimson iron ore
- Notify players on login of part and trait files that failed to load
### Changed
- Trait lists in tooltips tweaked (hopefully they won't be confused for enchantments?)
- Sickles reset sweet berry bushes to age 1 (same as picking berries from them normally)
- Machetes can now break multiple bamboo blocks at once
### Fixed
- Sickles duplicating dirt from grass blocks, possibly some other related issues

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
