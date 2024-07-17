package theqwerdev.custommusicdiscs.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;
import turniplabs.halplibe.helper.SoundHelper;

public class CustomMusicDiscsClient implements ClientModInitializer {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
		new MusicDiscAdder().InitializeItems();
		SoundHelper.addStreaming(MOD_ID, "placeholder.ogg"); //aww fiddlesticks, what now?!
		LOGGER.info("Custom Music Discs initialized.");
    }
}
