package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.ItemRecord;

public class ItemCustomRecord
	extends ItemRecord {

	public String translatedName;
	public String translatedDesc;

	protected ItemCustomRecord(String name, int id, String s, String displayName) {
		super(name, id, s);
		translatedName = displayName;
		translatedDesc = s;
	}
}

