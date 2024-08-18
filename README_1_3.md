## IndyPets - Independent Pets
Free Your Pets!  
🐱🦜🐺  
<br>
Brought over from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/indypets), originally developed by lizin5ths, now officially continued by yours truly.  

## Features:
 - Disable the follow+teleport behavior of all tamed mobs, including modded ones!
 - Sneak interact to toggle for individual pets
 - Whistle to rally your army of pets!
 - Pets have a "home" they don't stray too far of
 - Vanilla client compatible - though it is recommended to install the mod client-side too!
 - Lots of mod compatibility options

## Description:  
You can own a lot of pets in Minecraft but three in particular have a behavior that causes them to teleport to you unless you force them to sit (which removes their interesting behaviors) or put them on leashes: **cats**, **parrots**, and **wolves**. With IndyPets, you can have a giant pen of cats, house of parrots, or den of wolves and not have to worry about them trying to follow you en masse unless you want them to!
<br>
You can watch cats act like cats and roam! Or just sit on beds and inventory chests... 

- for Fabric Minecraft 1.16 - 1.21
- requires Fabric API and [Cloth Config](https://modrinth.com/mod/cloth-config)

## Info:  
By default, pets will not follow+teleport to you unless you tell them to do so by sneak-interacting (Shift + Right Click) or whistling (J).  
<br>
Whistling make all nearby pets follow you or be dismissed again.  
Vanilla clients can use the `/indypets whistle` or `unwhistle` commands. Both commands can take an optional type of pets you want to address (Auto-completion will show you which types of pets are around, so no need for guessing).  
<br>
Pets can freely roam within their home. "Home" is where they were last set independent.  
The Home Radius can be set in the config.  
<br>
The mod can run completely server-side but it's recommended clients install it too for hotkeys, client → server config synchronization and translated text messages.  

## Mod conflicts / workarounds:

[Pettable](https://modrinth.com/mod/pettable) uses shift-interact to pet mobs but it checks for an empty hand, so you can still use IndyPets while holding an item.  
[Capybara](https://www.curseforge.com/minecraft/mc-mods/capybara-fabric) uses shift-interact while holding a stick to change between sitting/standing. Set IndyPets to require an item other than a stick to be able to interact with the capybara.  

You can also use the Interact hotkey (H by default) to bypass having to use shift interact.

## (Very Old) Credits by lizin5ths:

Shoutout to panicnot422 for [Wandering Pets](https://www.curseforge.com/minecraft/mc-mods/wandering-pets)

Config logic used up to v0.4.1 taken from: https://github.com/ladysnake/illuminations in file: Illuminations/src/main/java/ladysnake/illuminations/client/Config.java by replacing relevant arguments with my own. It was CC BY-NC-SA 4.0 at the time.

Also, this video from TechnoVision really helped me figure out the initial setup.

More old stuff:

[Old source for v0.4.1 without config (CC0 license)](https://pastebin.com/Q7WX2tUX)
[Old source for v0.4.1 (CC BY-NC-SA 4.0 license)](https://pastebin.com/zF40ic5R)
[Source for old Forge version (MIT license)](https://pastebin.com/4nu3etNi)

Please someone make a raccoon mod for Minecraft I want to hug a raccoon and feed it trash.