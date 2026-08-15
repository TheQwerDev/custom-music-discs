package theqwerdev.custommusicdiscs.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

public class CustomMusicDiscsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		ModConfig.initConfig(true);
		for(int i = 0; i < ModDiscs.maxDiscCount; i++) {
			new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
				.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
    			.build(new ItemCustomRecord("record.custom" + (i + 1),
					CustomMusicDiscsClient.MOD_ID + ":item/record_custom" + (i + 1),
					ModConfig.itemID + i,
					CustomMusicDiscsClient.MOD_ID + ":record.custom" + (i + 1),
					"placeholder", null));
		}

		new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			.build(new ItemCustomRecord("record.placeholder",
				CustomMusicDiscsClient.MOD_ID + ":item/record_placeholder",
				ModConfig.itemID + ModDiscs.maxDiscCount,
				CustomMusicDiscsClient.MOD_ID + ":record.placeholder",
				"placeholder", null));
	}
}
