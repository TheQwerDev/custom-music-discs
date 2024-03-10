package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import theqwerdev.custommusicdiscs.CustomMusicDiscsConfig;
import theqwerdev.custommusicdiscs.CustomMusicDiscsMod;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.helper.SoundHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MusicDiscAdder {

	static int startingID = CustomMusicDiscsConfig.itemID;
	static Path musicPath = Paths.get(CustomMusicDiscsConfig.musicPath);
	static String[] exts = {".ogg", ".wav", ".mus"};

	ArrayList<Item> discs = new ArrayList<>();

	public void InitializeItems() {
		if(Files.exists(musicPath))
		{
			CustomMusicDiscsMod.LOGGER.info("Found music directory at '" + musicPath + '\'');
			CustomMusicDiscsMod.LOGGER.info("Adding custom music discs...");

			File musicFolder = musicPath.toFile();
			File[] musicFiles =  musicFolder.listFiles((dir, name) -> {
				for(String ext : exts)
					if(name.toLowerCase().endsWith(ext)) {
						return true;
					}
				CustomMusicDiscsMod.LOGGER.warn("Discarding '" + name + "'... (Invalid file extension)");
				return false;
			});

			if(musicFiles != null && musicFiles.length > 0) {
				//CustomMusicDiscsMod.LOGGER.warn(getClass().getResource("/lang/" + CustomMusicDiscsMod.MOD_ID + "/en_US.lang").getPath().replace("//", "/").trim());
				Path langPath = Paths.get("/lang/" + CustomMusicDiscsMod.MOD_ID + "/en_US.lang");
				try {
					Files.delete(langPath);
					Files.createFile(langPath);
				} catch (IOException e) {
					CustomMusicDiscsMod.LOGGER.warn(e.toString());
				}

				for (File musicFile : musicFiles) {
					String name = musicFile.getName();
					int extPos = name.lastIndexOf('.');
					name = name.substring(0, extPos);

					discs.add(ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
						new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), name), "custom" + (discs.size() + 1), "disc_custom.png"));

					try {
						Path tempPath = Files.copy(musicFile.toPath(), Paths.get(SoundHelper.streamingDirectory.getPath(), musicFile.getName()));
						tempPath.toFile().deleteOnExit();

						CustomMusicDiscsMod.LOGGER.info("Imported '" + musicFile.getName() + '\'');
					} catch (IOException e) {
						CustomMusicDiscsMod.LOGGER.warn(e.toString());
					}

					File langFile = langPath.toFile();
					try {
						FileWriter fw = new FileWriter(langFile, true);
						fw.write("item." + CustomMusicDiscsMod.MOD_ID + ".custom" + discs.size() + ".name=Custom Music Disc\n" +
							"item." + CustomMusicDiscsMod.MOD_ID + ".custom" + discs.size() + ".desc=" + name + "\n\n");
						//item.custommusicdiscs.church1.desc=§4Cantarile §4I §4si §4III, §4ectenia §4intreita
						fw.close();
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

		//discs.add(ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
			//new ItemCustomRecord("record.church1", startingID + discs.size(), "Cantarile I si III, ectenia intreita"), "church1", "disc_gold.png"));
		//discs.add(ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
			//new ItemCustomRecord("record.cawdgagdadg", startingID + discs.size(), "Cantarile I si III, ectenia intreita"), "agdagdagd", "disc_gold.png"));
	}

}
