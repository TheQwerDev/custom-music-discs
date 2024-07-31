package theqwerdev.custommusicdiscs.config;

import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class ModConfig {
	public static boolean useSongAsItemName;
	public static boolean loopDiscAudio;

	// The biggest number that can be added to starting_item_id to get a random custom disc for loot generation
	// Should usually be set to the amount of audio files that the user would like to add
	// Values beyond the audio file count can cause placeholder discs to generate
	// Values beyond 256 surpass the hardcoded disc limit
	public static int maxLootGenCount;
	public static boolean doLootgen;
	public static boolean hideDiscpackSettings;
	public static boolean silenceImageFileWarnings;
	public static int itemID;

	public static ConfigHandler config;

	public static void updateValues(boolean isServer) {
		if(isServer) {
			maxLootGenCount = config.getInt("max_lootgen_count");
		} else {
			useSongAsItemName = config.getBoolean("use_song_as_item_name");
			loopDiscAudio = config.getBoolean("loop_disc_audio");
			hideDiscpackSettings = config.getBoolean("hide_discpack_settings");
			silenceImageFileWarnings = config.getBoolean("silence_image_file_warnings");
		}

		doLootgen = config.getBoolean("do_lootgen");
	}

	public static void initConfig(boolean isServer) {
		Properties prop = new Properties();

		if(isServer) {
			//server only
			prop.setProperty("max_lootgen_count", "5 #");
			prop.setProperty("do_lootgen", "false");
		}
		else {
			//client only
			prop.setProperty("use_song_as_item_name", "true");
			prop.setProperty("loop_disc_audio", "false");
			prop.setProperty("hide_discpack_settings", "false");
			prop.setProperty("do_lootgen", "true");
			prop.setProperty("silence_image_file_warnings", "false");
		}

		//common config
		prop.setProperty("starting_item_id", "25000");

		config = new ConfigHandler(CustomMusicDiscsClient.MOD_ID + (isServer ? "_server" : ""), prop);

		updateValues(isServer);

		itemID = config.getInt("starting_item_id");

		config.updateConfig();
	}
}
