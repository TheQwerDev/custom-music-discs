package theqwerdev.custommusicdiscs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;

public class CustomMusicDiscsMod implements ModInitializer {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
		//SoundHelper.addStreaming(MOD_ID, "Cantarile I si III, ectenia intreita.ogg");
		new MusicDiscAdder().InitializeItems();

		LOGGER.info("Custom Music Discs initialized.");
    }
}
