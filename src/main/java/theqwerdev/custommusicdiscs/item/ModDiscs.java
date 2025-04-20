package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.collection.Pair;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.util.ResourcePackGenerator;
import turniplabs.halplibe.helper.ItemBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	public static final List<Item> discs = new ArrayList<>();
	private static SortedMap<Integer, File> trackMap;

	public static int getTrackMapSize() {
		return trackMap.size();
	}

	public static SortedMap<Integer, File> getTrackMap() {
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
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(ResourcePackGenerator.audioPath + "/sounds.json");
			fileWriter.append("{\n");
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}

		for (File track : trackMap.values()) {
			int trackNumber = Integer.parseInt(track.getName());
			Pair<File, File> trackData = extractTrackData(track);
			File audioFile = trackData.getLeft(), imageFile = trackData.getRight();

			if (audioFile == null) {
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

			//CustomMusicDiscsClient.LOGGER.info(CustomMusicDiscsClient.MOD_ID + ":item/record_custom" + trackNumber);
			try {
				Path tempPath = Files.copy(audioFile.toPath(), Paths.get(String.valueOf(ResourcePackGenerator.recordPath), audioFile.getName()));
				tempPath.toFile().deleteOnExit();

				CustomMusicDiscsClient.LOGGER.info("Imported '" + audioFile.getName() + '\'');
			} catch (IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}

			if (imageFile == null) {
				if (!ModConfig.silenceImageFileWarnings)
					CustomMusicDiscsClient.LOGGER.warn("Failed to find image file for track " + trackNumber);
			} else {
				ResourcePackGenerator.addDiscTexture(imageFile, trackNumber);
			}

			discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
				.build(new ItemCustomRecord("record.custom" + trackNumber,
					CustomMusicDiscsClient.MOD_ID + ":item/record_custom" + trackNumber,
					startingID + trackNumber - 1,
					CustomMusicDiscsClient.MOD_ID + ":record.custom" + trackNumber,
					name)));
			discCount++;

			//i am NOT learning how to properly write a json file just for this one use case
			try {
				fileWriter.append("	\"record.custom" + trackNumber + "\": {\n" +
								"		\"sounds\": [\n" +
								"			{\n" +
								"				\"name\": \"record/" + audioFile.getName() + "\",\n" +
								"				\"volume\": 0.5,\n" +
								"				\"attenuation_distance\": 64,\n" +
								"				\"stream\": true\n" +
								"			}\n" +
								"		]\n" +
								"	},\n");
			} catch (IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}

		try {
			fileWriter.append("	\"record.placeholder\": {\n" +
							"		\"sounds\": [\n" +
							"			{\n" +
							"				\"name\": \"record/placeholder.ogg\",\n" +
							"				\"volume\": 0.5,\n" +
							"				\"attenuation_distance\": 64,\n" +
							"				\"stream\": true\n" +
							"			}\n" +
							"		]\n" +
							"	}\n" +
							"}");
			fileWriter.close();
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}

		//filler discs so multiplayer support doesn't bite me in the ass
		for(int i = startingID; i < startingID + maxDiscCount; i++) {
			if(Item.itemsList[i] == null) {
				discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
					//.setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_placeholder")
					.build(new ItemCustomRecord("record.custom" + (i - startingID + 1),
						CustomMusicDiscsClient.MOD_ID + ":item/record_custom" + (i - startingID + 1),
						i,
						CustomMusicDiscsClient.MOD_ID + ":record.placeholder",
						"placeholder"))
					.withTags(ItemTags.NOT_IN_CREATIVE_MENU));
			}
		}

		discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			.build(new ItemCustomRecord("record.placeholder",
				CustomMusicDiscsClient.MOD_ID + ":item/record_placeholder",
				startingID + maxDiscCount,
				CustomMusicDiscsClient.MOD_ID + ":record.placeholder",
				"placeholder"))
			.withTags(ItemTags.NOT_IN_CREATIVE_MENU));
	}

	public static void initializeItems () {
		resetTrackMap();
		registerDiscs();
	}
}
