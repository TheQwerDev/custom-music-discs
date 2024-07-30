package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.ItemRecord;

public class ItemCustomRecord
	extends ItemRecord {

	public String translatedName;

	public ItemCustomRecord(String name, int id, String s) {
		super(name, id, s, null);
		translatedName = s;
	}
}

