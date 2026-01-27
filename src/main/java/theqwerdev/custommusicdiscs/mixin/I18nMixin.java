package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemDiscMusic;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;

@Mixin(value = I18n.class, remap = false)
public class I18nMixin {
	@Inject(method = "translateKey", at = @At("HEAD"), cancellable = true)
	public void translateKey(String s, CallbackInfoReturnable<String> cir) {
		if(s.contains(CustomMusicDiscsClient.MOD_ID +  ":record")) { //now playing message
			ItemCustomRecord record = (ItemCustomRecord) Item.itemsList[Item.nameToIdMap.get("item." + CustomMusicDiscsClient.MOD_ID + '.' + s.substring(CustomMusicDiscsClient.MOD_ID.length() + 1))];
			cir.setReturnValue(record.translatedName);
		}

		if(s.contains("item." + CustomMusicDiscsClient.MOD_ID + ".record") || s.contains("item.record"))
		{
			String itemSubstr = s.substring(0, s.length()-5);

			if(s.contains("item." + CustomMusicDiscsClient.MOD_ID + ".record")) { //custom disc display name
				ItemCustomRecord record = (ItemCustomRecord) Item.itemsList[Item.nameToIdMap.get(itemSubstr)];

				if(s.endsWith("name"))
					cir.setReturnValue(ModConfig.useSongAsItemName ? record.translatedName : "Custom Music Disc");
				else if(s.endsWith("desc"))
					cir.setReturnValue(ModConfig.useSongAsItemName ? "Custom Music Disc" : record.translatedName);
			}
			else if(s.contains("item.record")) { //vanilla disc display name
				ItemDiscMusic record = (ItemDiscMusic) Item.itemsList[Item.nameToIdMap.get(itemSubstr)];

				if(s.endsWith("name"))
					cir.setReturnValue(ModConfig.useSongAsItemName ? Language.Default.INSTANCE.translateKey(record.getDefaultStack().getItemKey() + ".desc") : Language.Default.INSTANCE.translateKey(record.getDefaultStack().getItemKey() + ".name"));
				else if(s.endsWith("desc"))
					cir.setReturnValue(ModConfig.useSongAsItemName ? Language.Default.INSTANCE.translateKey(record.getDefaultStack().getItemKey() + ".name") : Language.Default.INSTANCE.translateKey(record.getDefaultStack().getItemKey() + ".desc"));
			}
		}
	}
}
