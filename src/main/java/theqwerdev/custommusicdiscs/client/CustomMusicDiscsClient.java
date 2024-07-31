package theqwerdev.custommusicdiscs.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.gui.ModOptionsPage;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import theqwerdev.custommusicdiscs.util.TexturePackGenerator;
import turniplabs.halplibe.helper.SoundHelper;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class CustomMusicDiscsClient implements ClientModInitializer, ClientStartEntrypoint {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
		ModConfig.initConfig(false);
		TexturePackGenerator.initializeTexturePackPath();
		ModDiscs.initializeItems();
		SoundHelper.addStreaming(MOD_ID, "placeholder.ogg"); //aww fiddlesticks, what now?!

		LOGGER.info("Custom Music Discs initialized.");
	}

	@Override
	public void beforeClientStart() {}

	@Override
	public void afterClientStart() {
		ModOptionsPage.registerOptionsPage();
		TexturePackGenerator.forceEnablePack();
	}
}
