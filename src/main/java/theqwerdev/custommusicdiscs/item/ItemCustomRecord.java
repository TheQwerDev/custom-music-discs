package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemRecord;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;

public class ItemCustomRecord
	extends ItemRecord {

	String translatedName;

	protected ItemCustomRecord(String name, int id, String s, String displayName) {
		super(name, id, s);
		translatedName = displayName;
		this.getDefaultStack().setCustomName(translatedName);
	}

	/*public Item setKey(String s) {
		return this;
	}*/

	public String getTranslatedName(ItemStack itemstack) {
		return translatedName;
	}
}

