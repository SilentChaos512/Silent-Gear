# Silent-Gear

Modular tool/armor mod for Minecraft. Crafting is handled with blueprints, which eliminate all recipe conflicts. Materials and parts can be added with JSON files via data packs. Gear crafting recipes (number of required materials, required parts, etc.) can also be changed with data packs.

This is based on and completely replaced the tool/armor system from Silent's Gems, but has various changes and improvements.

Add-on mods can add new part types, gear types, and trait types, as well as anything a data pack can do. 

## Links and Downloads

- [CurseForge](https://minecraft.curseforge.com/projects/silent-gear) (downloads and more information)
- [Wiki](https://github.com/SilentChaos512/Silent-Gear/wiki) (advanced information)
- [GitHub repository](https://github.com/SilentChaos512/Silent-Gear) (source code)
- [Issue Tracker on GitHub](https://github.com/SilentChaos512/Silent-Gear/issues) (bug reports and feature requests)
- [Discord Server](https://discord.gg/Adyk9zHnUn) (easiest way to get quick questions answered, do not use to report bugs)

### Note on Downloads

**I only upload builds to Minecraft CurseForge.** If you downloaded the mod from somewhere other than Curse/CurseForge or the Twitch launcher (or as part of a modpack in some cases), I cannot make any guarantees about the file or its contents, as it was uploaded without my permission.

## Making an Add-on

I currently upload to Bintray. If you want to use Silent Gear in your add-on, add Silent Gear, Silent Lib and silent-utils to your dependencies:

```groovy
repositories {
    maven {
        url "https://dl.bintray.com/silentchaos512/silent-gear/"
    }
    maven {
        url "https://dl.bintray.com/silentchaos512/silent-lib"
    }
    maven {
        url "https://dl.bintray.com/silentchaos512/silent-utils"
    }
}

dependencies {
    compile("net.silentchaos512:silent-gear-{mc-version}:{version}")
    compile("net.silentchaos512:silent-lib-{mc-version}:{version}")
    compile("net.silentchaos512:silent-utils:{version}")
}
```
