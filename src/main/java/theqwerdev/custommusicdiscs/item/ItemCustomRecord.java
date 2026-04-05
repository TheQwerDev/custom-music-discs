package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.ItemDiscMusic;

public class ItemCustomRecord extends ItemDiscMusic {
	public String translatedName;

	public ItemCustomRecord(String name, String namespaceId, int id, String recordName, String displayName, String recordAuthor) {
		super(name, namespaceId, id, recordName, recordAuthor);
		translatedName = displayName;
	}
}

