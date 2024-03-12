package theqwerdev.custommusicdiscs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;
import turniplabs.halplibe.helper.SoundHelper;

public class CustomMusicDiscsMod implements ModInitializer {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isClient = FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);

    @Override
    public void onInitialize() {
		new MusicDiscAdder().InitializeItems();
		SoundHelper.Client.addStreaming(MOD_ID, "placeholder.ogg"); //aww fiddlesticks, what now?!
		LOGGER.info("Custom Music Discs initialized.");
    }
}
