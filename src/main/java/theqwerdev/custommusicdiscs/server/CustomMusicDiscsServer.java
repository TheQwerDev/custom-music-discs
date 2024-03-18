package theqwerdev.custommusicdiscs.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;
import turniplabs.halplibe.helper.ItemHelper;

public class CustomMusicDiscsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		for(int i = 0; i < MusicDiscAdder.maxDiscCount; i++) {
			ItemHelper.createItem(CustomMusicDiscsClient.MOD_ID,
				new ItemCustomRecord("record.custom" + (i + 1), MusicDiscAdder.startingID + i, "placeholder", "Custom Music Disc"), "disc_gold.png");
		}
	}
}
