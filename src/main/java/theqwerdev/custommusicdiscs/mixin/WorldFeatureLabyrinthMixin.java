package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.Global;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemRecord;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ModDiscs;

import java.util.Random;

@Mixin(value = WorldFeatureLabyrinth.class, remap = false)
public class WorldFeatureLabyrinthMixin {
	@Inject(method = "pickCheckLootItem", at = @At(value = "RETURN"), cancellable = true)
	private void includeCustomDiscs(Random random, CallbackInfoReturnable<ItemStack> cir) {
	    if (cir.getReturnValue() != null && cir.getReturnValue().getItem() instanceof ItemRecord) {
			Item loot;
			int lootGenCount = Global.isServer ? ModConfig.maxLootGenCount : ModDiscs.discCount;
			if(random.nextInt(2) == 0 || !ModConfig.doLootgen || lootGenCount > ModDiscs.maxDiscCount || lootGenCount == 0)
				loot = Item.itemsList[Item.record13.id + random.nextInt(9)];
			else
				loot = Item.itemsList[ModConfig.itemID + random.nextInt(lootGenCount)];

			cir.setReturnValue(new ItemStack(loot));
		}
	}
}
