# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
