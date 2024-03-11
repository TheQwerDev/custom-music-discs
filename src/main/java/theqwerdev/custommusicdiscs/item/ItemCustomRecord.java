package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.ItemRecord;
import net.minecraft.core.item.ItemStack;

public class ItemCustomRecord
	extends ItemRecord {

	String translatedName;

	protected ItemCustomRecord(String name, int id, String s, String displayName) {
		super(name, id, s);
		translatedName = displayName;
	}

	public String getTranslatedName(ItemStack itemstack) {
		return translatedName;
	}
}

