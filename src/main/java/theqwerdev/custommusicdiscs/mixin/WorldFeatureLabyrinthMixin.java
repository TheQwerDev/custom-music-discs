package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theqwerdev.custommusicdiscs.CustomMusicDiscsConfig;

import java.util.Random;

@Mixin(value = WorldFeatureLabyrinth.class, remap = false)
public class WorldFeatureLabyrinthMixin {
	@Inject(method = "pickCheckLootItem", at = @At(value = "RETURN", ordinal = 8), cancellable = true)
	private void includeCustomDiscs(Random random, CallbackInfoReturnable<ItemStack> cir) {
		cir.setReturnValue(new ItemStack(random.nextInt(2) == 0 || !CustomMusicDiscsConfig.doLootgen ? Item.itemsList[Item.record13.id + random.nextInt(9)] : Item.itemsList[CustomMusicDiscsConfig.itemID + random.nextInt(CustomMusicDiscsConfig.maxLootGenCount)]));
	}
}
