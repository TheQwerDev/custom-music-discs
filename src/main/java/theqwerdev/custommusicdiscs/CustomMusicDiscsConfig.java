package theqwerdev.custommusicdiscs;

import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class CustomMusicDiscsConfig {
	public static int itemID;
	public static boolean useSongAsItemName;

	// The biggest number that can be added to starting_item_id to get a random custom disc for loot generation
	// Should usually be set to the amount of audio files that the user would like to add
	// Values beyond the audio file count can cause placeholder discs to generate
	// Values beyond 256 surpass the hardcoded disc limit
	public static int maxLootGenCount;
	public static boolean doLootgen;

	public static String musicPath;
	static {
		Properties prop = new Properties();
		prop.setProperty("music_path", "./custom_discs");
		prop.setProperty("use_song_as_item_name", "true");
		prop.setProperty("max_lootgen_count", "5");
		prop.setProperty("do_lootgen", "false");
		prop.setProperty("starting_item_id", "25000");

		ConfigHandler config = new ConfigHandler(CustomMusicDiscsClient.MOD_ID, prop);

		itemID = config.getInt("starting_item_id");
		useSongAsItemName = config.getBoolean("use_song_as_item_name");
		maxLootGenCount = config.getInt("max_lootgen_count");
		doLootgen = config.getBoolean("do_lootgen");
		musicPath = config.getString("music_path");

		config.updateConfig();
	}
}
