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

-----------------------------------

## Making an Add-on

To use Silent Gear in your project, you need to add dependencies for Silent Gear, Silent Lib, and silent-utils. Add the following to your `build.gradle`.

You alse need to generate a GitHub token and add it along with your GitHub username to your personal `gradle.properties` file in `C:\Users\YOUR_USERNAME\.gradle` or `~/.gradle/gradle.properties`. This file may not exist, and you would have to create it yourself.

GitHub tokens can be generated [here](https://github.com/settings/tokens). Click _Generate New Token_ and click the checkmark for _read:packages_

Example of `gradle.properties` file in `C:\Users\YOUR_USERNAME\.gradle` or `~/.gradle/gradle.properties`

```gradle
//Your GitHub username
gpr.username=SilentChaos512

// Your GitHub generated token (bunch of hex digits) with read permission
gpr.token=paste_your_token_here
```

-----------------------------------

Code to add to `build.gradle`. _Note that "silentlib" is not hyphenated. I was following a different naming convention when the repo was created._

I prefer to assign my authentication details to a variable to reduce duplication and make the build file look cleaner.

```gradle
// Authentication details for GitHub packages
// This can also go in the `repositories` block or you can inline it if you prefer
def gpr_creds = {
    username = property('gpr.username')
    password = property('gpr.token')
}
```

Add all the necessary repositories...

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/silentchaos512/silent-gear")
        credentials gpr_creds
    }
    maven {
        url = uri("https://maven.pkg.github.com/silentchaos512/silentlib")
        credentials gpr_creds
    }
    maven {
        url = uri("https://maven.pkg.github.com/silentchaos512/silent-utils")
        credentials gpr_creds
    }
}
```

And finally, add dependencies for Silent Gear and Silent Lib (which will include silent-utils for you).

```gradle
dependencies {
    // Replace VERSION with the version you need, in the form of "MC_VERSION-MOD_VERSION"
    // Example: compile fg.deobf("net.silentchaos512:silent-gear:1.16.3-2.+")
    // Available builds can be found here: https://github.com/SilentChaos512/silent-gear/packages
    // The "exclude module" lines will prevent import errors in some cases
    compile fg.deobf("net.silentchaos512:silent-gear:VERSION") {
        exclude module: 'forge'
        exclude module: 'jei-1.18.2'
        exclude module: 'silent-lib'
        exclude module: 'curios-forge'
    }

    // Same as before, VERSION is in the form "MC_VERSION-MOD_VERSION" (eg, 1.18.2-6.+)
    // https://github.com/SilentChaos512/silentlib/packages
    compile fg.deobf("net.silentchaos512:silent-lib:VERSION") {
        exclude module: "forge"
    }
}
```
