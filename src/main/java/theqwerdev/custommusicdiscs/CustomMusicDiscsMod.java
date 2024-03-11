package theqwerdev.custommusicdiscs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class CustomMusicDiscsMod implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isClient = FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);

    @Override
    public void onInitialize() {
		new MusicDiscAdder().InitializeItems();

		LOGGER.info("Custom Music Discs initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}
}
