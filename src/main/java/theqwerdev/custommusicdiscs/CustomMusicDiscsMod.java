package theqwerdev.custommusicdiscs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.item.DiscItems;
import turniplabs.halplibe.helper.SoundHelper;

public class CustomMusicDiscsMod implements ModInitializer {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("your wallet has been stolen");

		SoundHelper.addStreaming(MOD_ID, "lobotomy.ogg");
		new DiscItems().InitializeItems();
    }
}
