package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.enums.EnumOS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	//this won't work for macos btw
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void javaSwingWorkaround(CallbackInfo ci) {
		if (Minecraft.getOs() != EnumOS.macos)
			System.setProperty("java.awt.headless", "false");
		else
			CustomMusicDiscsClient.LOGGER.warn("Disc import UI is currently incompatible with macOS! If you want to import any discs, you can still do so manually.");
	}
}
