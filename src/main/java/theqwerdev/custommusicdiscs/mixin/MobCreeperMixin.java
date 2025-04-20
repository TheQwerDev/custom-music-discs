package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.Global;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ModDiscs;

@Mixin(value = MobCreeper.class, remap = false)
public class MobCreeperMixin {
	@ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/monster/MobCreeper;dropItem(II)Lnet/minecraft/core/entity/EntityItem;"), index = 0)
	private int includeCustomDiscs(int itemID) {
		MobCreeper leCreeper = (MobCreeper)(Object)this;
		int lootGenCount = Global.isServer ? ModConfig.maxLootGenCount : ModDiscs.discCount;
		if(leCreeper.random.nextInt(2) == 0 || !ModConfig.doLootgen || lootGenCount > ModDiscs.maxDiscCount || lootGenCount == 0)
			return Items.RECORD_13.id + leCreeper.random.nextInt(11);
		else
			return ModConfig.itemID + leCreeper.random.nextInt(lootGenCount);
	}
}
