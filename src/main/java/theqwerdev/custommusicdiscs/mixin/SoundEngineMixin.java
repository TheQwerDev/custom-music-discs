package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;
import theqwerdev.custommusicdiscs.config.ModConfig;

@Mixin(value = SoundEngine.class, remap = false)
public class SoundEngineMixin {
	@Shadow
	private static @Nullable SoundSystem soundSystem;

	@Inject(method = "playMusic(Lnet/minecraft/client/sound/SoundEntry;FFFFF)V", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;play(Ljava/lang/String;)V"))
	private void loopAudio(SoundEntry entry, float x, float y, float z, float volume, float pitch, CallbackInfo ci) {
		soundSystem.setLooping("Streaming", ModConfig.loopDiscAudio);
	}
}
