package theqwerdev.custommusicdiscs.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import theqwerdev.custommusicdiscs.config.CustomMusicDiscsConfig;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.texture.TextureManager;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.SoundHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MusicDiscAdder {
	public static int maxDiscCount = 256;
	public static int startingID = CustomMusicDiscsConfig.itemID;
	private static final boolean useSongAsItemName = CustomMusicDiscsConfig.useSongAsItemName;
	public static final Path musicPath = Paths.get("./discpack");
	private static final String[] exts = {".ogg", ".wav", ".mus", ".png"};

	ArrayList<Item> discs = new ArrayList<>();
	public static Map<Integer, File> trackMap = new HashMap<>();
	BitSet checkedID = new BitSet(maxDiscCount);

	public void InitializeItems() {
		if(!Files.exists(musicPath)) {
			try {
				Files.createDirectory(musicPath);
			}
			catch(IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}

		File musicFolder = musicPath.toFile();

		File[] trackList = musicFolder.listFiles((dir, name) ->{
			if(dir.isDirectory())
			{
				Scanner sc = new Scanner(name.trim());
				if(!sc.hasNextInt(10))
					return false;

				sc.nextInt(10);
				return !sc.hasNext();
			}

			return false;
		});

		if(trackList != null && trackList.length > 0) {
			int maxTrackID = 0;

			for(File track : trackList) {
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
			if(trackIDRange.cardinality() != maxTrackID) {
				CustomMusicDiscsClient.LOGGER.warn("Have the track IDs been tampered with? Rearranging discpack...");

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

			for(File track : trackMap.values()) {
				int trackNumber = Integer.parseInt(track.getName());
				File[] trackData = track.listFiles((dir, name) -> {
					for (String ext : exts)
						if (name.toLowerCase().endsWith(ext)) {
							return true;
						}
					return false;
				});

				if (trackData == null) {
					continue;
				}

				boolean foundAudioFile = false, foundTextureFile = false;

				for (File data : trackData) {
					String name = data.getName();
					if (!foundTextureFile && name.equals("texture.png")) {
						foundTextureFile = true;
						TextureManager.addDiscTexture(data, trackNumber);
					} else if (!foundAudioFile && !name.endsWith(".png")) {
						foundAudioFile = true;

						int extPos = name.lastIndexOf('.');
						name = name.substring(0, extPos);

						if (discs.size() >= maxDiscCount) {
							CustomMusicDiscsClient.LOGGER.warn("Reached maximum disc count of " + maxDiscCount + ". Unable to import '" + name + '\'');
							continue;
						}

						discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
							.setIcon(CustomMusicDiscsClient.MOD_ID + ":item/" + trackNumber)
							.build(
								new ItemCustomRecord("record.custom" + trackNumber, startingID + trackNumber - 1, name, (useSongAsItemName ? name : "Custom Music Disc"))));

						try {
							Path tempPath = Files.copy(data.toPath(), Paths.get(SoundHelper.streamingDirectory.getPath(), data.getName()));
							tempPath.toFile().deleteOnExit();

							CustomMusicDiscsClient.LOGGER.info("Imported '" + data.getName() + '\'');
						} catch (IOException e) {
							CustomMusicDiscsClient.LOGGER.warn(e.toString());
						}
					}
				}

				if (!foundTextureFile) {
					CustomMusicDiscsClient.LOGGER.warn("Couldn't find 'texture.png' for track " + trackNumber);
				}
			}
		}
		else {
			CustomMusicDiscsClient.LOGGER.warn("No custom discs found.");
		}


		//filler discs so multiplayer support doesn't bite me in the ass
		while(discs.size() < maxDiscCount) {
			discs.add(new ItemBuilder(CustomMusicDiscsClient.MOD_ID)
			    .setIcon(CustomMusicDiscsClient.MOD_ID + ":item/disc_placeholder")
			    .build(
				new ItemCustomRecord("record.custom" + (discs.size() + 1), startingID + discs.size(), "placeholder", (useSongAsItemName ? "placeholder" : "Custom Music Disc"))).withTags(ItemTags.NOT_IN_CREATIVE_MENU));
		}
	}
}
