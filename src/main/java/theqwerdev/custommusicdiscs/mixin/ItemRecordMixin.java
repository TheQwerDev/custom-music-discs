package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(value=RenderGlobal.class, remap=false)
public class ItemRecordMixin {

	@Shadow
	private Minecraft mc;

	@Inject(at = @At("HEAD"), method = "playStreamingMusic", cancellable = true)
	public void playStreamingMusic(String soundPath, int x, int y, int z, CallbackInfo ci)
	{
		if (soundPath != null) {
			if(soundPath.equals("Cantarile I si III, ectenia intreita"))
				mc.ingameGUI.setRecordPlayingMessage("Cantarile I si III, ectenia intreita");
			else
				mc.ingameGUI.setRecordPlayingMessage("C418 - " + soundPath);
		}
		mc.sndManager.playStreaming(soundPath, x, y, z, 1.0f, 1.0f);
		ci.cancel();
	}
}
