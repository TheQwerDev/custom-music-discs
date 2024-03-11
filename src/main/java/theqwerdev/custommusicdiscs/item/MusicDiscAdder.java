package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import theqwerdev.custommusicdiscs.CustomMusicDiscsConfig;
import theqwerdev.custommusicdiscs.CustomMusicDiscsMod;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.helper.SoundHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class MusicDiscAdder {
	public static Properties dynLang;
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
				if(dir.isFile())
					CustomMusicDiscsMod.LOGGER.warn("Discarding '" + name + "'... (Invalid file extension)");
				return false;
			});

			if(musicFiles != null && musicFiles.length > 0) {
				if(CustomMusicDiscsMod.isClient) {
					//CustomMusicDiscsMod.LOGGER.warn(getClass().getResource("/lang/" + CustomMusicDiscsMod.MOD_ID + "/en_US.lang").getPath());
					//File langFile = new File(Objects.requireNonNull(getClass().getResource("/lang/" + CustomMusicDiscsMod.MOD_ID + "/en_US.lang")).getPath()); //yeah, this sucks

					Path langPath = Paths.get(musicPath + "/dynamic_lang/en_US.lang");

					try {
						Files.createDirectories(langPath);
					} catch (IOException e) {
						CustomMusicDiscsMod.LOGGER.warn(e.toString());
					}

					File langFile = new File(langPath.toString());
					langFile.delete();
					try {
						langFile.createNewFile();
					} catch (IOException e) {
						CustomMusicDiscsMod.LOGGER.warn(e.toString());
					}

					for (File musicFile : musicFiles) {
						String name = musicFile.getName();
						int extPos = name.lastIndexOf('.');
						name = name.substring(0, extPos);

						discs.add(ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
							new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), name, "Custom Music Disc"), "disc_gold.png"));

						try {
							Path tempPath = Files.copy(musicFile.toPath(), Paths.get(SoundHelper.streamingDirectory.getPath(), musicFile.getName()));
							tempPath.toFile().deleteOnExit();

							CustomMusicDiscsMod.LOGGER.info("Imported '" + musicFile.getName() + '\'');

							FileWriter fw = new FileWriter(langFile, true);
							fw.write("item." + CustomMusicDiscsMod.MOD_ID + ".record.custom" + discs.size() + ".name=Custom Music Disc\n" +
								"item." + CustomMusicDiscsMod.MOD_ID + ".record.custom" + discs.size() + ".desc=" + name + "\n\n");
							fw.close();

							dynLang = new Properties();
							InputStream stream = Files.newInputStream(langPath);
							InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
							try {
								dynLang.load(reader);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						} catch (IOException e) {
							CustomMusicDiscsMod.LOGGER.warn(e.toString());
						}
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
}
