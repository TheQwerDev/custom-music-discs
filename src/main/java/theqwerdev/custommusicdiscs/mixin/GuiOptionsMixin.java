package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.gui.options.GuiOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theqwerdev.custommusicdiscs.config.ModConfig;

//i love creating a whole new file for every little change i want to make to minecraft's code
//it makes me think nice thoughts :)
@Mixin(value = GuiOptions.class, remap = false)
public class GuiOptionsMixin {
	@Inject(method = "onClosed", at = @At("TAIL"))
	private void saveConfig(CallbackInfo ci) {
		ModConfig.config.updateConfig();
		ModConfig.updateValues();
	}
}
