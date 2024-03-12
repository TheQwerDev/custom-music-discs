package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.item.Item;
import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theqwerdev.custommusicdiscs.CustomMusicDiscsMod;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;

@Mixin(value=I18n.class, remap = false)
public class I18nMixin {
	@Inject(method="translateKey", at = @At("HEAD"), cancellable = true)
	public void translateKey(String s, CallbackInfoReturnable<String> cir) {
		if(s.contains("item." + CustomMusicDiscsMod.MOD_ID + ".record")) {
			ItemCustomRecord record = (ItemCustomRecord) Item.itemsList[Item.nameToIdMap.get(s.substring(0, s.length()-5))];

			if(s.endsWith("name"))
				cir.setReturnValue(record.translatedName);
			else if(s.endsWith("desc"))
				cir.setReturnValue(record.translatedDesc);
		}
	}

}
