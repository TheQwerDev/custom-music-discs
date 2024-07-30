package theqwerdev.custommusicdiscs.config;

import net.minecraft.core.Global;
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
	public static int itemID;

	public static ConfigHandler config;

	public static void updateValues() {
		useSongAsItemName = config.getBoolean("use_song_as_item_name");
		loopDiscAudio = config.getBoolean("loop_disc_audio");
		doLootgen = config.getBoolean("do_lootgen");
		hideDiscpackSettings = config.getBoolean("hide_discpack_settings");
	}

	static {
		Properties prop = new Properties();

		//client only
		prop.setProperty("use_song_as_item_name", "true");
		prop.setProperty("loop_disc_audio", "true");
		prop.setProperty("hide_discpack_settings", "false");

		//server config
		if(Global.isServer)
			prop.setProperty("max_lootgen_count", "5");

		//common config
		prop.setProperty("do_lootgen", "true");
		prop.setProperty("starting_item_id", "25000");

		config = new ConfigHandler(CustomMusicDiscsClient.MOD_ID + (Global.isServer ? "_server" : ""), prop);

		updateValues();

		if(Global.isServer)
			maxLootGenCount = config.getInt("max_lootgen_count");
		itemID = config.getInt("starting_item_id");

		config.updateConfig();
	}
}
