package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//fix for shortcut components so i can add them to an option category
//taken from a bta nightly build so it should come fixed by default in future releases
@Mixin(value = ShortcutComponent.class, remap = false)
public class ShortcutComponentMixin {
	@Final
	@Shadow
	private static final int COMPONENT_SPACING = 2;

	@Final
	@Shadow
	private static final int BUTTON_WIDTH = 200;

	@Final
	@Shadow
	private GuiButton button;

	@Inject(at = @At("HEAD"), method = "onMouseClick")
	private void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY, CallbackInfo ci) {
		button.xPosition = x + width / 2 - BUTTON_WIDTH / 2;
		button.yPosition = y + COMPONENT_SPACING;
	}
}
