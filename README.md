## IndyPets Fork v0.5.0

This is a fork of lizin5ths's [Independent Pets v0.4.1 on CurseForge](https://www.curseforge.com/minecraft/mc-mods/indypets), licensed under [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode).

 - works on Minecraft 1.16 and 1.17
 - works server-only
 - works on singleplayer if installed on the client

The main changes in this fork are bugfixes and more consistent behavior.  

Sneak right-clicking a pet should now *always* switch between 'following' and 'independent' and it will block further actions like making the pet sit down/stand.  
Note this might cause some mod incompatibility with other mods using sneak right-click as well. [Pettable](https://www.curseforge.com/minecraft/mc-mods/pettable) is an example using the same key combination, but it still seems to work when not holding an item in your hand.

One notable example which caused issues previously are Parrots:  
Sitting Parrots could not be switched because they would simply never call the mixed in `MobEntity.interactMob()` method.  
Flying Parrots had this weird issue where they would be interacted with twice, once with each hand.

Chat messages were also slightly adjusted and can be translated:
 - "Your pet \"%s\" is following you"
 -  "Your pet \"%s\" is independent"