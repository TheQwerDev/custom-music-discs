package theqwerdev.custommusicdiscs;

import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

public class CustomMusicDiscsConfig {
	public static int itemID;
	public static String musicPath;
	static {
		Properties prop = new Properties();
		prop.setProperty("music_path", "./custom_discs");
		prop.setProperty("starting_item_id", "25000");

		ConfigHandler config = new ConfigHandler(CustomMusicDiscsMod.MOD_ID, prop);

		itemID = config.getInt("starting_item_id");
		musicPath = config.getString("music_path");

		config.updateConfig();
	}
}
