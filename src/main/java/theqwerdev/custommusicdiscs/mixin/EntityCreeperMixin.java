package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.Global;
import net.minecraft.core.entity.monster.EntityCreeper;
import net.minecraft.core.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ModDiscs;

@Mixin(value = EntityCreeper.class, remap = false)
public class EntityCreeperMixin {
	@ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/monster/EntityCreeper;spawnAtLocation(II)Lnet/minecraft/core/entity/EntityItem;"), index = 0)
	private int includeCustomDiscs(int itemID) {
		EntityCreeper leCreeper = (EntityCreeper)(Object)this;
		int lootGenCount = Global.isServer ? ModConfig.maxLootGenCount : ModDiscs.discCount;
		if(leCreeper.random.nextInt(2) == 0 || !ModConfig.doLootgen || lootGenCount > ModDiscs.maxDiscCount || lootGenCount == 0)
			return Item.record13.id + leCreeper.random.nextInt(11);
		else
			return ModConfig.itemID + leCreeper.random.nextInt(lootGenCount);
	}
}
