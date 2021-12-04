## Independent Pets / IndyPets

This is the official continuation of lizin5ths's [Independent Pets v0.4.1 on CurseForge](https://www.curseforge.com/minecraft/mc-mods/indypets), licensed under [CC0](https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt).  
(The original source code can be found [here](https://pastebin.com/Q7WX2tUX) and it was specified to be CC0 [here](https://www.curseforge.com/minecraft/mc-mods/indypets?comment=98).)

### Short Description

    Disable the follow+teleport behavior of all tamed mobs, including modded ones!
    Selective Following! Interact while sneaking to toggle for individual pets.

- requires [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) and Fabric API
- works on all Minecraft 1.16, 1.17 and 1.18 versions
- works server-only, client-side installation allows for config synchronization and message translation
- works on singleplayer if installed on the client

### Changelog

- fix inconsistencies with mob interactions (like not being able to interact with parrots - or interacting twice at once)
- block further mob interactions if switching independence (like sitting down/standing up)
- slightly adjusted chat messages that can be translated (if mod is installed client-side)
- a new JSON config with Mod Menu support replaces the old 'properties' one
- client configs are synchronized with the server, so each player can have their own settings
- `/indypets whistle` and `unwhistle` commands to set follow behavior for all nearby pets (of optionally specified type)