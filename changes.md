## 1.4.11

- add "Whistle Radius" config option

## 1.4.10

- [1.21+] fix independent pets being able to teleport to their owner when they are panicking

Babyproof their homes!

## 1.4.9

- port to 1.21.9

## 1.4.8

- port to 1.21.6

note: Vanilla added a "home" feature to all mobs (via the `home_pos`, `home_radius` fields).  
  
These homes have some inherent limitations and issues. They are more temporary (e.g. leashing a mob sets a home, unleashing unsets the home) and mobs are not allowed to move outside their homes, which also means that mobs can get stuck if they somehow end up outside of it.  
  
Due to that, IndyPets will keep using it's own home system like before, but will give priority to the vanilla homes if they are set.

## 1.4.7

- fix right click interact changing sitting/independence when e.g. feeding a pet

## 1.4.6

- port to 1.21.5 (thanks to OpenBagTwo for the help!)
- fix parrots losing their independence when dropping from a shoulder
- fix vanilla pets loading from chunks for the first time being independent

## 1.4.5

- [1.20.1, 1.19.2] compatibility for Critters and Companions' Dragonfly (they won't leave their home)
- require at least Fabric Loader 0.16.1

## 1.4.4

- \[1.21.4\] fix crash when spawning particles

## 1.4.3

- \[1.19.2+\] update Friends & Foes compatibility for 3.0.0+  
  (note: also removes compatibility code for older versions, so please update Friends & Foes)
- right click interact should be compatible with more mods

## 1.4.2

- Pets will try to escape when they are about to drown
- Cats will try to escape dangers and put themselves out of fire (vanilla bug fix for 1.16-1.18)
- add Chinese (zh_cn) translation, thanks to suoyukii!

## 1.4.1

- Goat Horns can be configured as a whistle! Each horn can be set individually with differing settings:  
  Whistle only, Unwhistle only, Toggle (which alternates between whistling and unwhistling) and Whistle / Sneak Unwhistle (which whistles while standing and unwhistles while sneaking)  
  Vanilla clients can use the `/indypets horns <horn_type> <setting>` command to configure each horn  
	`/indypets horn` by itself prints all current horn settings
- don't send player config to vanilla servers
- delay saving of server config to server shutdown

## 1.4.0

This is a large update to the mod, please make sure to read!

- Pets are **not** independent by default anymore and the relevant config options have been removed!  
  This is to be more inline with how vanilla works, especially with the next point:
- A pet's state is changed just like sitting is changed, i.e. Right Click will cycle between sitting, following and independent.  
  (Shift + Right click and whistling will still toggle following and independent directly, just like before.)  
- There's new options to completely turn off the Right Click or Shift + Right Click interactions if wanted  
 (So you could use only Right Click, or only Shift + Right Click, or only IndyPets' Interact hotkey)
- Status messages for single pet interactions will now show above the hotbar instead of inside the chat
- Status messages now also say if the pet is sitting (`" (but sits)"`)
- Vanilla clients can use the new `/indypets config` command, allowing to view (`get`), change (`set`), and get descriptions for (`help`) the (limited amount of) options.  
  These settings are stored server-side.
- Some inconsistent interactions, e.g. relating to the blocklist, have been fixed.
- The project's code is now licensed under MIT (previously CC0).
  No change for assets (CC0).