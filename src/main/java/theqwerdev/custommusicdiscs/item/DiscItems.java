package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import theqwerdev.custommusicdiscs.CustomMusicDiscsMod;
import turniplabs.halplibe.helper.ItemHelper;

public class DiscItems {

	public static Item disc1;

	public void InitializeItems() {
		disc1 = ItemHelper.createItem(CustomMusicDiscsMod.MOD_ID,
			new ItemCustomRecord("record.lobotomy", 16550, "lobotomy"), "lobotomy", "disc1.png");
	}

}
