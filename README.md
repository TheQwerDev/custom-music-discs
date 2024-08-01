# Custom Music Discs for BTA 7.2
This mod allows you to add custom discs to play on your jukebox by simply importing audio and image files! Now with ingame UI for easily adding new discs!

Custom Music Discs only supports the audio file extensions that Minecraft BTA does. (.ogg, .wav, .mus)

NOTE: This readme only applies for mod versions made for 7.2-pre2 and up!
## Adding Discs
Adding custom discs can be done in two ways:
- Through the ingame UI (Options -> Music Discs). You can either add discs one by one with the "Import Disc" button, or you can add an entire discpack created by another user with the "Import Discpack" button. You can also export discpacks so that other people can easily use them!
- Manually, by adding audio and image files into numbered folders in ".minecraft/discpack" (Note that the audio file name is the song name that will be used ingame).

The general structure of the discpack folder is as follows:

![image](https://github.com/user-attachments/assets/025d980a-8036-4956-b467-cce4e57806f2)

### Notes
- The manual method of adding discs allows users to skip adding image files. Discs without custom textures will use a placeholder texture instead. 
- The placeholder texture can be changed by adding a '.png' file named "disc_placeholder.png" in the item folder of the "CustomMusicDiscsResources" texture pack that automatically gets created on boot.
- Any change to the discpack will require a restart of the game to reset the item list.

## Configuration
The config file can be found at "[minecraft directory]/config". The variables it stores depends on the type of instance that's running the mod (client or server).
Client-side variables can also be changed in the ingame UI.

### Common Variables:
- do_lootgen (default: true on client-side, false on server-side): Allows custom discs to appear in dungeons and in creeper drops as loot.
- starting_item_id (default: 25000): Sets the ID at which custom discs should start appearing in the game's item list. Change only if there are ID conflicts with other mods.

### Client-side Variables:
- use_song_as_disc_name (default: true): Decides if the song name should appear as the item's name or as the item's description. Does not affect vanilla discs.
- loop_disc_audio (default: false): Loops the currently inserted disc once audio playback ends.
- silence_image_file_warnings (default: false): Stops the displaying of "Failed to find image" warnings. Useful for people that don't want to add custom textures.

### Server-side Variables:
- max_lootgen_count (default: 5): Sets the maximum ID number that should be chosen when adding custom discs to loot.
