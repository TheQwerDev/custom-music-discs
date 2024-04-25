package theqwerdev.custommusicdiscs;

import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class CustomMusicDiscsConfig {
	public static int itemID;
	public static boolean useSongAsItemName;
	public static String musicPath;
	static {
		Properties prop = new Properties();
		prop.setProperty("music_path", "./custom_discs");
		prop.setProperty("use_song_as_item_name", "true");
		prop.setProperty("starting_item_id", "25000");

		ConfigHandler config = new ConfigHandler(CustomMusicDiscsClient.MOD_ID, prop);

		itemID = config.getInt("starting_item_id");
		useSongAsItemName = config.getBoolean("use_song_as_item_name");
		musicPath = config.getString("music_path");

		config.updateConfig();
	}
}
