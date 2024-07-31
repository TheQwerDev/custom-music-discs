package theqwerdev.custommusicdiscs.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import turniplabs.halplibe.helper.ItemBuilder;

public class CustomMusicDiscsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		ModConfig.initConfig(true);
		for(int i = 0; i < ModDiscs.maxDiscCount; i++) {
			new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			    .setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_placeholder")
    			.build(
	   		    	new ItemCustomRecord("record.custom" + (i + 1), ModConfig.itemID + i, "placeholder"));
		}
	}
}
