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

public class ResourcePackGenerator {
	public static final String packName = "CustomMusicDiscsResources";
	public static final Path baseFolderPath = Paths.get("./texturepacks/" + packName);
	public static final Path texturePath = Paths.get("./texturepacks/" + packName + "/assets/custommusicdiscs/textures/item");
	public static final Path audioPath = Paths.get("./texturepacks/" + packName + "/assets/custommusicdiscs/sounds");
	public static final Path recordPath = Paths.get(audioPath + "/record");

	public static void addDiscTexture(File texture, int id) {
		Path copyPath = Paths.get(texturePath.toString(), "record_custom" + id + ".png");
		try {
			Path tempPath = Files.copy(texture.toPath(), copyPath);
			tempPath.toFile().deleteOnExit();
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
			try {
				Files.delete(copyPath);
				addDiscTexture(texture, id);
			} catch (IOException e2) {
				CustomMusicDiscsClient.LOGGER.warn(e2.toString());
			}
		}
	}

	public static void initializeTexturePackPath() {
		Path packpng = Paths.get(baseFolderPath.toString(), "pack.png");
		Path manifest = Paths.get(baseFolderPath.toString(), "manifest.json");
		Path placeholderAudio = Paths.get(audioPath.toString(), "record/placeholder.ogg");

		try {
			if(!Files.exists(texturePath))
				Files.createDirectories(texturePath);

			if(!Files.exists(recordPath))
				Files.createDirectories(recordPath);

			if(!Files.exists(packpng))
				Files.copy(ResourcePackGenerator.class.getResourceAsStream("/assets/custommusicdiscs/icon.png"), packpng);

			if(!Files.exists(manifest))
				Files.copy(ResourcePackGenerator.class.getResourceAsStream("/assets/custommusicdiscs/manifest.json"), manifest);

			if(!Files.exists(placeholderAudio))
				Files.copy(ResourcePackGenerator.class.getResourceAsStream("/assets/custommusicdiscs/sounds/record/placeholder.ogg"), placeholderAudio);
		}
		catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}
	}

	public static void forceEnablePack() {
		TexturePackList texturePackList = Minecraft.getMinecraft().texturePackList;
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
