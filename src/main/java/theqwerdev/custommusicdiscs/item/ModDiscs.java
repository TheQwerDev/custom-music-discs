package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.net.command.TextFormatting;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.util.ResourcePackGenerator;
import turniplabs.halplibe.helper.ItemBuilder;


import java.io.File;
import java.io.FileInputStream;
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
	private static final String[] exts = {".ogg", ".wav", ".mus", ".png", ".txt"};

	public static int discCount = 0;
	public static final List<Item> discs = new ArrayList<>();
	public static int tracksSize = 0;
	public static final File[] tracks = new File[maxDiscCount + 1];

	public static File[] extractTrackData(File track) {
		File[] trackData = track.listFiles((dir, name) -> {
			for (String ext : exts)
				if (name.toLowerCase().endsWith(ext)) {
					return true;
				}
			return false;
		});

		if (trackData == null) {
			return new File[]{null, null, null};
		}

		File audioFile = null, imageFile = null, propFile = null;
		for (File data : trackData) {
			String name = data.getName();
			if (audioFile == null && !name.endsWith(".png") && !name.endsWith(".txt")) {
				audioFile = data;
			} else if (imageFile == null && name.endsWith(".png")) {
				imageFile = data;
			} else if (propFile == null && name.endsWith(".txt")) {
				propFile = data;
			}
		}

		return new File[]{audioFile, imageFile, propFile};
	}

	public static void resetTrackList() {
		tracksSize = 0;
		BitSet checkedID = new BitSet(maxDiscCount);

		if (!Files.exists(musicPath)) {
			try {
				Files.createDirectory(musicPath);
			} catch (IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}

		File musicFolder = musicPath.toFile();

		File[] trackListUnfiltered = musicFolder.listFiles((dir, name) -> (dir.isDirectory()));

		if(trackListUnfiltered == null) {
			CustomMusicDiscsClient.LOGGER.warn("No custom discs found.");
			return;
		}

		int maxTrackID = 0;

		for (File track : trackListUnfiltered) {
			String folderName = track.getName();
			if (!track.isDirectory()) {
				continue;
			}

			File[] trackData = extractTrackData(track);
			File propFile = trackData[2];
			if(propFile == null) {
				CustomMusicDiscsClient.LOGGER.warn("Failed to find properties file in folder '{}'. Unable to import!", folderName);
				continue;
			}
			Properties prop = new Properties();
			int trackNumber;
			try {
				FileInputStream propInputStream = new FileInputStream(propFile);
				prop.load(propInputStream);
				propInputStream.close();
				String trackNumberStr = prop.getProperty("pos");
				if (trackNumberStr == null || trackNumberStr.isEmpty()) {
					CustomMusicDiscsClient.LOGGER.warn("Invalid track position for track '{}'. Skipping...", folderName);
					continue;
				}
				trackNumber = Integer.parseInt(trackNumberStr);
			} catch (Exception e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
				continue;
			}

			if (trackNumber <= 0) {
				CustomMusicDiscsClient.LOGGER.warn("Track ID {} is invalid. Ignoring.", trackNumber);
				continue;
			}

			if (trackNumber > maxDiscCount) {
				CustomMusicDiscsClient.LOGGER.warn("Track ID {} surpasses maximum disc count of " + maxDiscCount + ". Ignoring.", trackNumber);
				continue;
			}

			if (checkedID.get(trackNumber - 1)) {
				CustomMusicDiscsClient.LOGGER.warn("Duplicate track ID {} found. Ignoring.", trackNumber);
				continue;
			}

			tracks[trackNumber] = track;
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
					File track = tracks[nextID + 1];
					String folderName = track.getName();
					File[] trackData = extractTrackData(track);
					File propFile = trackData[2];
					if(propFile == null) {
						CustomMusicDiscsClient.LOGGER.warn("Failed to find properties file in folder '{}'. Unable to import!", folderName);
						continue;
					}
					Properties prop = new Properties();
					try {
						FileInputStream propInputStream = new FileInputStream(propFile);
						prop.load(propInputStream);
						propInputStream.close();
						prop.setProperty("pos", Integer.toString(i));
						FileWriter propWriter = new FileWriter(propFile);
						prop.store(propWriter, null);
						propWriter.close();
						tracks[i] = track;
					} catch (IOException e) {
						CustomMusicDiscsClient.LOGGER.warn(e.toString());
					}
				}
			}
		}
		tracksSize = trackIDRange.cardinality();
	}

	private static void addAudio(File audioFile) {
		Path copyPath = Paths.get(String.valueOf(ResourcePackGenerator.recordPath), audioFile.getName());
		try {
			Path tempPath = Files.copy(audioFile.toPath(), copyPath);
			tempPath.toFile().deleteOnExit();

			CustomMusicDiscsClient.LOGGER.info("Imported '{}'", audioFile.getName());
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
			try {
				Files.delete(copyPath);
				addAudio(audioFile);
			} catch (IOException e2) {
				CustomMusicDiscsClient.LOGGER.warn(e2.toString());
			}
		}
	}

	private static void registerDiscs() {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(ResourcePackGenerator.audioPath + "/sounds.json");
			fileWriter.append("{\n");
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}

		for (int i = 1; i <= tracksSize; i++) {
			File track = tracks[i];
			String folderName = track.getName();
			File[] trackData = extractTrackData(track);
			File audioFile = trackData[0], imageFile = trackData[1], propFile = trackData[2];

			if (discs.size() >= maxDiscCount) {
				CustomMusicDiscsClient.LOGGER.warn("Reached maximum disc count of " + maxDiscCount + ". Unable to import '{}'", folderName);
				continue;
			}

			if (audioFile == null) {
				CustomMusicDiscsClient.LOGGER.warn("Failed to find audio file in folder '{}'. Unable to import!", folderName);
				continue;
			}

			String authorName, volume, style;

			String name = audioFile.getName();
			int extPos = name.lastIndexOf('.');
			name = name.substring(0, extPos);

			if (propFile == null) {
				CustomMusicDiscsClient.LOGGER.warn("Failed to find properties file in folder '{}'. Unable to import!", folderName);
				continue;
			} else {
				Properties prop = new Properties();
				try {
					FileInputStream propInputStream = new FileInputStream(propFile);
					prop.load(propInputStream);
					propInputStream.close();
				} catch (IOException e) {
					CustomMusicDiscsClient.LOGGER.warn(e.toString());
					continue;
				}

				String nameProp = prop.getProperty("track_name");
				if (nameProp != null && !nameProp.isEmpty()) {
					name = prop.getProperty("track_name");
				}
				authorName = prop.getProperty("author_name");
				if (authorName != null && authorName.isEmpty()) {
					authorName = null;
				}
				volume = prop.getProperty("volume");
				if (volume == null || volume.isEmpty()) {
					volume = "0.5";
				} else {
					try {
						if (Float.parseFloat(volume) < 0) {
							volume = "0.5";
						}
					} catch (NumberFormatException e) {
						CustomMusicDiscsClient.LOGGER.warn(e.toString());
						volume = "0.5";
					}
				}
				style = prop.getProperty("style");
				if (style != null && !style.isEmpty()) {
					String[] formats = style.split(";");
					style = "";
					for (int j = 0; j < formats.length; j++) {
						formats[j] = formats[j].trim().toLowerCase();
					}

					for(TextFormatting textFormat : TextFormatting.FORMATTINGS) {
						String[] textFormatNames = textFormat.getNames();
						for(int j = 0; j < textFormatNames.length; j++) {
							textFormatNames[j] = textFormatNames[j].toLowerCase();
						}
						for(String formatName : textFormatNames) {
							for(String format : formats) {
								if(Objects.equals(format, formatName)) {
									style += textFormat;
								}
							}
						}
					}

					name = style + name;
					if (authorName != null) {
						authorName = style + authorName;
					}
				}
			}

			//CustomMusicDiscsClient.LOGGER.info(CustomMusicDiscsClient.MOD_ID + ":item/record_custom" + trackNumber);
			addAudio(audioFile);

			if (imageFile == null) {
				if (!ModConfig.silenceImageFileWarnings)
					CustomMusicDiscsClient.LOGGER.warn("Failed to find image file in folder '{}'", folderName);
			} else {
				ResourcePackGenerator.addDiscTexture(imageFile, i);
			}

			discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
				.build(new ItemCustomRecord("record.custom" + i,
					CustomMusicDiscsClient.MOD_ID + ":item/record_custom" + i,
					startingID + i - 1,
					CustomMusicDiscsClient.MOD_ID + ":record.custom" + i,
					name, authorName)));
			discCount++;

			//i am NOT learning how to properly write a json file just for this one use case
			try {
				fileWriter.append("	\"record.custom" + i + "\": {\n" +
								"		\"sounds\": [\n" +
								"			{\n" +
								"				\"name\": \"record/" + audioFile.getName() + "\",\n" +
								"				\"volume\": " + volume + ",\n" +
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
						"placeholder", null))
					.withTags(ItemTags.NOT_IN_CREATIVE_MENU));
			}
		}

		discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			.build(new ItemCustomRecord("record.placeholder",
				CustomMusicDiscsClient.MOD_ID + ":item/record_placeholder",
				startingID + maxDiscCount,
				CustomMusicDiscsClient.MOD_ID + ":record.placeholder",
				"placeholder", null))
			.withTags(ItemTags.NOT_IN_CREATIVE_MENU));
	}

	public static void initializeItems () {
		resetTrackList();
		registerDiscs();
	}
}
