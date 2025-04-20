package theqwerdev.custommusicdiscs.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
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
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.ModelEntrypoint;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class CustomMusicDiscsClient implements ClientModInitializer, ClientStartEntrypoint, ModelEntrypoint {
    public static final String MOD_ID = "custommusicdiscs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
    public void onInitializeClient() {
		ModConfig.initConfig(false);
		ResourcePackGenerator.initializeTexturePackPath();
		SoundRepository.registerNamespace(MOD_ID);
		ModDiscs.initializeItems();

		LOGGER.info("Custom Music Discs initialized.");
	}

	@Override
	public void beforeClientStart() {}

	@Override
	public void afterClientStart() {
		ModOptionsPage.registerOptionsPage();
		ResourcePackGenerator.forceEnablePack();
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		for(Item disc : ModDiscs.discs) {
			ModelHelper.setItemModel(disc, () -> {
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

				return model;
			});
		}
	}

	//I LOVE USELESS BOILERPLATE!!!
	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {}
}
