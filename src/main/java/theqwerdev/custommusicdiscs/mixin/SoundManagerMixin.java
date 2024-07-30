package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;
import theqwerdev.custommusicdiscs.config.ModConfig;

@Mixin(value = SoundManager.class, remap = false)
public class SoundManagerMixin {
	@Shadow
	private static SoundSystem sndSystem;

	@Inject(method = "playStreaming", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;play(Ljava/lang/String;)V"))
	private void loopAudio(String soundPath, float x, float y, float z, float volume, float pitch, CallbackInfo ci) {
		sndSystem.setLooping("streaming", ModConfig.loopDiscAudio);
	}
}
