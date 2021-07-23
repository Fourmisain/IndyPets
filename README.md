## Indipendent Pets / IndyPets

This is a continuation of lizin5ths's [Independent Pets v0.4.1 on CurseForge](https://www.curseforge.com/minecraft/mc-mods/indypets), licensed under [CC0](https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt).  
The original source code can be found [here](https://pastebin.com/Q7WX2tUX) and it was specified to be CC0 [here](https://www.curseforge.com/minecraft/mc-mods/indypets?comment=98).

- works on Minecraft 1.16 and 1.17
- works server-only
- works on singleplayer if installed on the client

### Changes
The main changes so far are bugfixes and more consistent behavior with sneak-interacting pets and a new config with support for Mod Menu.

Sneak-interacting a pet should now *always* switch between 'following/teleporting' and 'independent' and it will block further actions like making the pet sit down/stand.  
Note this might cause some mod incompatibility with other mods using sneak-interact as well.  
[Pettable](https://www.curseforge.com/minecraft/mc-mods/pettable) is an example using the same key combination, but it still seems to work when not holding an item in your hand.

One notable example which caused issues previously are Parrots:  
Sitting Parrots could not be switched because they would simply never call the mixed in `TameableEntity.interactMob()` method.  
Flying Parrots had this weird issue where they would be interacted with twice, once with each hand.  
This and other issues were fixed by mixing into `MobEntity.interact()` instead.

Chat messages were also slightly adjusted and can be translated.

A new config using JSON was made to replace the old 'properties' one and it integrates with Mod Menu as well.