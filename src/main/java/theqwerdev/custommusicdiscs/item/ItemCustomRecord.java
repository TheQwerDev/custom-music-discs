package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.ItemDiscMusic;

public class ItemCustomRecord extends ItemDiscMusic {
	public String translatedName;

	public ItemCustomRecord(String name, String namespaceId, int id, String recordName, String displayName) {
		super(name, namespaceId, id, recordName, null);
		translatedName = displayName;
	}
}

