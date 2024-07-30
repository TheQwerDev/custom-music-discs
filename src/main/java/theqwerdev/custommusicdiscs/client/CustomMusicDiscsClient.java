package theqwerdev.custommusicdiscs.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.gui.ModOptionsPage;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import turniplabs.halplibe.helper.SoundHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class CustomMusicDiscsClient implements ClientModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
		ModDiscs.initializeItems();
		SoundHelper.addStreaming(MOD_ID, "placeholder.ogg"); //aww fiddlesticks, what now?!

		LOGGER.info("Custom Music Discs initialized.");
	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		ModOptionsPage.registerOptionsPage();
	}
}
