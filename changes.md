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