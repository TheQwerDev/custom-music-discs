package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.ItemRecord;

public class ItemCustomRecord
	extends ItemRecord {

	public String translatedName;
	public String translatedDesc;

	public ItemCustomRecord(String name, int id, String s, String displayName) {
		super(name, id, s, null);
		translatedName = displayName;
		translatedDesc = s;
	}
}

