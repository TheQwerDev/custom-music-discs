package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import theqwerdev.custommusicdiscs.CustomMusicDiscsConfig;
import theqwerdev.custommusicdiscs.CustomMusicDiscsMod;
import turniplabs.halplibe.helper.CreativeHelper;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.helper.SoundHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MusicDiscAdder {
	static int maxDiscCount = 256;
	static int startingID = CustomMusicDiscsConfig.itemID;
	static Path musicPath = Paths.get(CustomMusicDiscsConfig.musicPath);
	static String[] exts = {".ogg", ".wav", ".mus"};

	ArrayList<Item> discs = new ArrayList<>();

	public void InitializeItems() {
		if(CustomMusicDiscsMod.isClient) {
			if(Files.exists(musicPath)) {
				CustomMusicDiscsMod.LOGGER.info("Found music directory at '" + musicPath + '\'');
				CustomMusicDiscsMod.LOGGER.info("Adding custom music discs...");

				File musicFolder = musicPath.toFile();
				File[] musicFiles =  musicFolder.listFiles((dir, name) -> {
					for(String ext : exts)
						if(name.toLowerCase().endsWith(ext)) {
							return true;
						}
					if(dir.isFile())
						CustomMusicDiscsMod.LOGGER.warn("Discarding '" + name + "'... (Invalid file extension)");
					return false;
				});

				if(musicFiles != null && musicFiles.length > 0) {
					for (File musicFile : musicFiles) {
						String name = musicFile.getName();
						int extPos = name.lastIndexOf('.');
						name = name.substring(0, extPos);

						if(discs.size() < maxDiscCount) {
							discs.add(ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
								new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), name, "Custom Music Disc"), "disc_gold.png"));
						}
						else {
							CustomMusicDiscsMod.LOGGER.warn("Reached maximum disc count of " + maxDiscCount + ". Unable to import '" + musicFile.getName() + '\'');
						}

						try {
							Path tempPath = Files.copy(musicFile.toPath(), Paths.get(SoundHelper.streamingDirectory.getPath(), musicFile.getName()));
							tempPath.toFile().deleteOnExit();

							CustomMusicDiscsMod.LOGGER.info("Imported '" + musicFile.getName() + '\'');
						} catch (IOException e) {
							CustomMusicDiscsMod.LOGGER.warn(e.toString());
						}
					}
				}
				else {
					CustomMusicDiscsMod.LOGGER.warn("No custom music found. Are you sure you've placed your songs in '" + musicPath + "'?");
				}
			}
			else
			{
				CustomMusicDiscsMod.LOGGER.warn("\nFailed to find specified music directory!\n" +
					"Creating folder at specified location in '.minecraft'!\n" +
					"Drag and drop music files in the newly created folder at '" + musicPath + "' to add music discs to your game!\n" +
					"A restart is required to apply new music files.");

				try {
					Files.createDirectory(musicPath);
				}
				catch(IOException e) {
					CustomMusicDiscsMod.LOGGER.warn(e.toString());
				}
			}
		}

		//filler discs so multiplayer support doesn't bite me in the ass
		while(discs.size() < maxDiscCount) {
			discs.add(ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
				new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), "placeholder", "Custom Music Disc"), "disc_gold.png").setNotInCreativeMenu());
		}
	}
}
