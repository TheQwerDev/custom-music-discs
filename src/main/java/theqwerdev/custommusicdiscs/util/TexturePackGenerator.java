package theqwerdev.custommusicdiscs.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TexturePackGenerator implements GameStartEntrypoint {
	private static final String packName = "CustomMusicDiscsResources";
	private static final Path baseFolderPath = Paths.get("./texturepacks/" + packName);
	private static final Path fullPath = Paths.get("./texturepacks/" + packName + "/assets/custommusicdiscs/textures/item");

	public static void addDiscTexture(File texture, int id) {
		try {
			Path tempPath = Files.copy(texture.toPath(), Paths.get(fullPath.toString(), id + ".png"));
			tempPath.toFile().deleteOnExit();
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}
	}

	static {
		Path packpng = Paths.get(baseFolderPath.toString(), "pack.png");
		Path manifest = Paths.get(baseFolderPath.toString(), "manifest.json");

		try {
			if(!Files.exists(fullPath))
				Files.createDirectories(fullPath);

			if(!Files.exists(packpng))
				Files.copy(TexturePackGenerator.class.getResourceAsStream("/assets/custommusicdiscs/icon.png"), packpng);

			if(!Files.exists(manifest))
				Files.copy(TexturePackGenerator.class.getResourceAsStream("/assets/custommusicdiscs/manifest.json"), manifest);
		}
		catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}
	}

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void afterGameStart() {
		//auto enable texture pack on boot
		TexturePackList texturePackList = Minecraft.getMinecraft(Minecraft.class).texturePackList;
		List<TexturePack> availableTexturePacks = texturePackList.availableTexturePacks();
		for(TexturePack texturePack : availableTexturePacks) {
			if(texturePack.fileName.equals(packName)) {
				texturePackList.setTexturePack(texturePack);
				texturePackList.refresh();
				break;
			}
		}
	}
}
