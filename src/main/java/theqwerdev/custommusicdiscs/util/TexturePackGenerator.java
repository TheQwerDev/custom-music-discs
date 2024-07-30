package theqwerdev.custommusicdiscs.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TexturePackGenerator {
	private static final String packName = "CustomMusicDiscsResources";
	private static final Path baseFolderPath = Paths.get("./texturepacks/" + packName);
	private static final Path fullPath = Paths.get("./texturepacks/" + packName + "/assets/custommusicdiscs/textures/item");

	public static void addDiscTexture(File texture, int id) throws IOException {
		Path tempPath = Files.copy(texture.toPath(), Paths.get(fullPath.toString(), id + ".png"));
		tempPath.toFile().deleteOnExit();
	}

	public static void initializeTexturePackGenerator() {
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
