package theqwerdev.custommusicdiscs.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.sound.SoundRepository;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.gui.ModOptionsPage;
import theqwerdev.custommusicdiscs.item.ItemCustomRecord;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import theqwerdev.custommusicdiscs.util.ResourcePackGenerator;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class CustomMusicDiscsClient implements ClientModInitializer {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void afterClientStart() {
		ModOptionsPage.registerOptionsPage();
		ResourcePackGenerator.forceEnablePack();
	}

	public static void initItemModels(ItemModelDispatcher dispatcher) {
		for(Item disc : ModDiscs.discs) {
			ItemModelStandard model = new ItemModelStandard(disc, null);
			ItemCustomRecord record = (ItemCustomRecord)disc;

			IconCoordinate icon;
			if(Objects.equals(record.translatedName, "placeholder") || !Files.exists(Paths.get(ResourcePackGenerator.texturePath + "/record_custom" + (record.id - ModConfig.itemID + 1) + ".png"))) {
				icon = TextureRegistry.getTexture(CustomMusicDiscsClient.MOD_ID + ":item/disc_placeholder");
			}
			else {
				icon = TextureRegistry.getTexture(record.namespaceID);
			}
			model.icon = icon;

			dispatcher.addDispatch(model);
		}
	}

	@Override
    public void onInitializeClient() {
		ModConfig.initConfig(false);
		HalpLibe.registerMod(MOD_ID);
		ResourcePackGenerator.initializeTexturePackPath();
		SoundRepository.namespaceAdded(MOD_ID); //registerNamespace
		ModDiscs.initializeItems();
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(MOD_ID), CustomMusicDiscsClient::afterClientStart);
		ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of(MOD_ID), CustomMusicDiscsClient::initItemModels);
		LOGGER.info("Custom Music Discs initialized.");
	}
}
