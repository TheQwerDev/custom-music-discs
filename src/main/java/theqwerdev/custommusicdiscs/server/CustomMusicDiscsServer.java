package theqwerdev.custommusicdiscs.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;
import turniplabs.halplibe.helper.ItemBuilder;

public class CustomMusicDiscsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		for(int i = 0; i < MusicDiscAdder.maxDiscCount; i++) {
			new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			    .setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_placeholder")
    			.build(
	   		    	new ItemCustomRecord("record.custom" + (i + 1), MusicDiscAdder.startingID + i, "placeholder", "Custom Music Disc"));
		}
	}
}
