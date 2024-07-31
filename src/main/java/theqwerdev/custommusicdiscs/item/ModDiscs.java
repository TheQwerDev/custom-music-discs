package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.collection.Pair;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.util.TexturePackGenerator;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.SoundHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ModDiscs {
	public static final int maxDiscCount = 256;
	private static final int startingID = ModConfig.itemID;
	public static final Path musicPath = Paths.get("./discpack");
	private static final String[] exts = {".ogg", ".wav", ".mus", ".png"};

	public static int discCount = 0;
	private static final List<Item> discs = new ArrayList<>();
	private static Map<Integer, File> trackMap;

	public static int getTrackMapSize() {
		return trackMap.size();
	}

	public static Map<Integer, File> getTrackMap() {
		return trackMap;
	}

	public static void addToTrackMap(int key, File value) {
		trackMap.put(key, value);
	}

	public static void removeFromTrackMap(int index) {
		trackMap.remove(index);
	}

	public static void resetTrackMap() {
		trackMap = new TreeMap<>();
		BitSet checkedID = new BitSet(maxDiscCount);

		if (!Files.exists(musicPath)) {
			try {
				Files.createDirectory(musicPath);
			} catch (IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}

		File musicFolder = musicPath.toFile();

		File[] trackListUnfiltered = musicFolder.listFiles((dir, name) -> {
			if (dir.isDirectory()) {
				Scanner sc = new Scanner(name.trim());
				if (!sc.hasNextInt(10))
					return false;

				sc.nextInt(10);
				return !sc.hasNext();
			}

			return false;
		});

		if(trackListUnfiltered == null) {
			CustomMusicDiscsClient.LOGGER.warn("No custom discs found.");
			return;
		}

		int maxTrackID = 0;

		for (File track : trackListUnfiltered) {
			int trackNumber = Integer.parseInt(track.getName());

			if (trackNumber <= 0) {
				CustomMusicDiscsClient.LOGGER.warn("Track ID " + trackNumber + " is invalid. Ignoring.");
				continue;
			}

			if (trackNumber > maxDiscCount) {
				CustomMusicDiscsClient.LOGGER.warn("Track ID " + trackNumber + " surpasses maximum disc count of " + maxDiscCount + ". Ignoring.");
				continue;
			}

			if (checkedID.get(trackNumber - 1)) {
				CustomMusicDiscsClient.LOGGER.warn("Duplicate track ID " + trackNumber + " found. Ignoring.");
				continue;
			}

			trackMap.put(trackNumber, track);
			checkedID.set(trackNumber - 1);
			if (trackNumber > maxTrackID)
				maxTrackID = trackNumber;
		}

		BitSet trackIDRange = checkedID.get(0, maxTrackID);
		if (trackIDRange.cardinality() != maxTrackID) {
			CustomMusicDiscsClient.LOGGER.warn("Rearranging discpack...");

			for (int i = 1; i <= maxTrackID; i++) {
				if (!trackIDRange.get(i - 1)) {
					int nextID = trackIDRange.nextSetBit(i - 1);

					if (nextID == -1)
						break;

					trackIDRange.set(nextID, false);
					trackIDRange.set(i - 1);
					File track = trackMap.get(nextID + 1);
					if (!track.renameTo(new File("./discpack/" + i)))
						CustomMusicDiscsClient.LOGGER.warn("Failed to set track ID " + i + " to track ID " + nextID + ".");
					else
						trackMap.put(i, new File("./discpack/" + i));

					trackMap.remove(nextID + 1);
				}
			}
		}
	}

	public static Pair<File, File> extractTrackData(File track) {
		File[] trackData = track.listFiles((dir, name) -> {
			for (String ext : exts)
				if (name.toLowerCase().endsWith(ext)) {
					return true;
				}
			return false;
		});

		if (trackData == null) {
			return Pair.of(null, null);
		}

		File audioFile = null, imageFile = null;
		for (File data : trackData) {
			String name = data.getName();
			if (audioFile == null && !name.endsWith(".png")) {
				audioFile = data;
			} else if (imageFile == null && name.endsWith(".png")) {
				imageFile = data;
			}
		}

		return Pair.of(audioFile, imageFile);
	}

	private static void registerDiscs() {
		for (File track : trackMap.values()) {
			int trackNumber = Integer.parseInt(track.getName());
			Pair<File, File> trackData = extractTrackData(track);
			File audioFile = trackData.getLeft(), imageFile = trackData.getRight();

			if(audioFile == null) {
				CustomMusicDiscsClient.LOGGER.warn("Failed to find audio file for track " + trackNumber);
				continue;
			}

			String name = audioFile.getName();

			int extPos = name.lastIndexOf('.');
			name = name.substring(0, extPos);

			if (discs.size() >= maxDiscCount) {
				CustomMusicDiscsClient.LOGGER.warn("Reached maximum disc count of " + maxDiscCount + ". Unable to import '" + name + '\'');
				continue;
			}

			discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
				.setIcon(CustomMusicDiscsClient.MOD_ID + ":item/" + (imageFile == null ? "disc_placeholder" : trackNumber))
				.build(new ItemCustomRecord("record.custom" + trackNumber, startingID + trackNumber - 1, name)));
			discCount++;

			try {
				Path tempPath = Files.copy(audioFile.toPath(), Paths.get(SoundHelper.streamingDirectory.getPath(), audioFile.getName()));
				tempPath.toFile().deleteOnExit();

				CustomMusicDiscsClient.LOGGER.info("Imported '" + audioFile.getName() + '\'');
			} catch (IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}

			if (imageFile == null) {
				if(!ModConfig.silenceImageFileWarnings)
					CustomMusicDiscsClient.LOGGER.warn("Failed to find image file for track " + trackNumber);
			}
			else {
				TexturePackGenerator.addDiscTexture(imageFile, trackNumber);
			}
		}

		//filler discs so multiplayer support doesn't bite me in the ass
		for(int i = startingID; i < startingID + maxDiscCount; i++) {
			if(Item.itemsList[i] == null) {
				discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
					.setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_placeholder")
					.build(new ItemCustomRecord("record.custom" + (i - startingID + 1), i, "placeholder")).withTags(ItemTags.NOT_IN_CREATIVE_MENU));
			}
		}
	}

	public static void initializeItems () {
		resetTrackMap();
		registerDiscs();
	}
}
