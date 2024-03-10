package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value= net.minecraft.client.render.RenderGlobal.class, remap=false)
public class RenderGlobalMixin {

	@Unique
	String[] ost = {"13", "cat", "blocks", "chirp", "far", "mall", "mellohi", "stal", "strad", "ward", "wait", "dog"};

	@Shadow
	private Minecraft mc;

	@Inject(at = @At("HEAD"), method = "playStreamingMusic", cancellable = true)
	public void playStreamingMusic(String soundPath, int x, int y, int z, CallbackInfo ci)
	{
		//MinecraftServer.getInstance().serverCommandHandler.getPlayer("andreiqwer").remove();
		if (soundPath != null) {
			if(Arrays.asList(ost).contains(soundPath))
				mc.ingameGUI.setRecordPlayingMessage("C418 - " + soundPath);
			else
				mc.ingameGUI.setRecordPlayingMessage(soundPath);
		}
		mc.sndManager.playStreaming(soundPath, x, y, z, 1.0f, 1.0f);
		ci.cancel();
	}
}
