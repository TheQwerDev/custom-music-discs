# Custom Music Discs for BTA 7.3_04
This mod allows you to add custom discs to play on your jukebox by simply importing audio and image files! Now with ingame UI for easily adding new discs!
NOTE FOR SERVER USAGE: Every player on the server is required to have the exact same audio files (with the exact same discpack order) for songs to play properly for everybody.

Custom Music Discs only supports the audio file extensions that Minecraft BTA does. (.ogg, .wav, .mus)

NOTE: This readme only applies for mod versions 3.2.5 and above!
## Adding Discs
Adding custom discs can be done in two ways:
- Through the ingame UI (Options -> Music Discs). You can either add discs one by one with the "Import Disc" button, or you can add an entire discpack created by another user with the "Import Discpack" button. You can also export discpacks so that other people can easily use them!
- Manually, by adding audio and image files into unique folders for each disc in `.minecraft/discpack`. Each folder should also contain a config file that specifies at least a track name and a position in the discpack list.

The general structure of the discpack folder is as follows:

![image](https://github.com/user-attachments/assets/171e15e9-7d21-455e-9355-546992689940)

### Notes
- Make sure you use square textures when adding the image file for a disc.
- The manual method of adding discs allows users to skip adding image files. Discs without custom textures will use a placeholder texture instead. 
- The placeholder texture can be changed by adding a '.png' file named `disc_placeholder.png` in the item folder of the "CustomMusicDiscsResources" texture pack that automatically gets created on boot.
- Any change to the discpack will require a restart of the game to reset the item list.

## Disc Configuration
Each disc folder should contain a config file which allows the user to specify the desired properties for that disc.

![image](https://github.com/user-attachments/assets/d537781b-96a0-4274-93e7-3e717f0f1a07)

### Required Properties
- `track_name`: The item name that will be displayed ingame.
- `pos`: The position in the discpack list (a positive integer that does not surpass 256).

### Optional Properties
- `author_name` (default: `none`): The name of the audio's creator. Appears alongside `track_name` in the "Now Playing" jukebox message.
- `volume` (default: `0.5`): Specifies how loud the audio should be.
- `style` (default: `none`): Sets the text style for `author_name` and `track_name`. Multiple values can be specified at the same time and should be separated by semicolons (;).
  - Valid `style` values: white, orange, magenta, light blue, aqua, yellow, lime, lime green, pink, gray, grey, light gray, light grey, silver, cyan, turquoise, purple, blue, brown, green, red, black, obfuscated, bold, strikethrough, underline, italic, reset

## Mod Configuration
The config file can be found at `[minecraft directory]/config`. The variables it stores depend on the type of instance that's running the mod (client or server).
Client-side variables can also be changed in the ingame UI.

### Common Variables:
- `do_lootgen` (default: `true` on client-side, `false` on server-side): Allows custom discs to appear in dungeons and in creeper drops as loot.
- `starting_item_id` (default: `25000`): Sets the ID at which custom discs should start appearing in the game's item list. Change only if there are ID conflicts with other mods.

### Client-side Variables:
- `use_song_as_disc_name` (default: `true`): Decides if the song name should appear as the item's name or as the item's description.
- `loop_disc_audio` (default: `false`): Loops the currently inserted disc once audio playback ends.
- `silence_image_file_warnings` (default: `false`): Stops the displaying of "Failed to find image" warnings. Useful for people that don't want to add custom textures.
- `hide_discpack_settings` (FOR MODPACK DEVELOPERS) (default: `false`): Prevents the user from accessing the "Discpack Settings" tab in the ingame options menu.

### Server-side Variables:
- `max_lootgen_count` (default: 5): Sets the maximum ID number that should be chosen when adding custom discs to loot.
