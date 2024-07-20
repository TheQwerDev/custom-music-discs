package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import theqwerdev.custommusicdiscs.CustomMusicDiscsConfig;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.SoundHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MusicDiscAdder {
	public static int maxDiscCount = 256;
	public static int startingID = CustomMusicDiscsConfig.itemID;
	static boolean useSongAsItemName = CustomMusicDiscsConfig.useSongAsItemName;
	static Path musicPath = Paths.get(CustomMusicDiscsConfig.musicPath);
	static String[] exts = {".ogg", ".wav", ".mus"};

	ArrayList<Item> discs = new ArrayList<>();

	public void InitializeItems() {
		if(Files.exists(musicPath)) {
			CustomMusicDiscsClient.LOGGER.info("Found music directory at '" + musicPath + '\'');
			CustomMusicDiscsClient.LOGGER.info("Adding custom music discs...");

			File musicFolder = musicPath.toFile();
			File[] musicFiles =  musicFolder.listFiles((dir, name) -> {
				for(String ext : exts)
					if(name.toLowerCase().endsWith(ext)) {
						return true;
					}
				if(dir.isFile())
					CustomMusicDiscsClient.LOGGER.warn("Discarding '" + name + "'... (Invalid file extension)");
				return false;
			});

			if(musicFiles != null && musicFiles.length > 0) {
				for (File musicFile : musicFiles) {
					String name = musicFile.getName();
					int extPos = name.lastIndexOf('.');
					name = name.substring(0, extPos);

					if(discs.size() < maxDiscCount) {
						discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
						    .setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_gold")
						    .build(
							new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), name, (useSongAsItemName ? name : "Custom Music Disc"))));
					}
					else {
						CustomMusicDiscsClient.LOGGER.warn("Reached maximum disc count of " + maxDiscCount + ". Unable to import '" + musicFile.getName() + '\'');
					}

					try {
						Path tempPath = Files.copy(musicFile.toPath(), Paths.get(SoundHelper.streamingDirectory.getPath(), musicFile.getName()));
						tempPath.toFile().deleteOnExit();

						CustomMusicDiscsClient.LOGGER.info("Imported '" + musicFile.getName() + '\'');
					} catch (IOException e) {
						CustomMusicDiscsClient.LOGGER.warn(e.toString());
					}
				}
			}
			else {
				CustomMusicDiscsClient.LOGGER.warn("No custom music found. Are you sure you've placed your songs in '" + musicPath + "'?");
			}
		}
		else
		{
			CustomMusicDiscsClient.LOGGER.warn("Failed to find specified music directory!");
			CustomMusicDiscsClient.LOGGER.warn("Creating folder at specified location in '.minecraft'!");
			CustomMusicDiscsClient.LOGGER.warn("Drag and drop music files in the newly created folder at '" + musicPath + "' to add music discs to your game!");
			CustomMusicDiscsClient.LOGGER.warn("A restart is required to apply new music files.");

			try {
				Files.createDirectory(musicPath);
			}
			catch(IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}

		//filler discs so multiplayer support doesn't bite me in the ass
		while(discs.size() < maxDiscCount) {
			discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			    .setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_gold")
			    .build(
				new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), "placeholder", "Custom Music Disc")).withTags(ItemTags.NOT_IN_CREATIVE_MENU));
		}
	}
}
