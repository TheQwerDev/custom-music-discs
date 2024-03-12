package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=I18n.class, remap = false)
public class I18nMixin {

	@Shadow
	private I18n currentLanguage;

	@Redirect(method="translateKey", at = @At("HEAD"))
	public String translateKey(String s, CallbackInfoReturnable<String> cir) {
		return "balls";
	}

}
