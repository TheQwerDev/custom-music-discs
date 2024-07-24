package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemRecord;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.generate.feature.WorldFeatureDungeon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theqwerdev.custommusicdiscs.CustomMusicDiscsConfig;

import java.util.Random;

@Mixin(value = WorldFeatureDungeon.class, remap = false)
public class WorldFeatureDungeonMixin {
	@Inject(method = "pickCheckLootItem", at = @At(value = "RETURN"), cancellable = true)
	private void includeCustomDiscs(Random random, CallbackInfoReturnable<ItemStack> cir) {
	    if (cir.getReturnValue() != null && cir.getReturnValue().getItem() instanceof ItemRecord) {
			Item loot;
			if(random.nextInt(2) == 0 || !CustomMusicDiscsConfig.doLootgen)
				loot = Item.itemsList[Item.record13.id + random.nextInt(9)];
			else
				loot = Item.itemsList[CustomMusicDiscsConfig.itemID + random.nextInt(CustomMusicDiscsConfig.maxLootGenCount)];

    		cir.setReturnValue(new ItemStack(loot));
		}
	}
}
