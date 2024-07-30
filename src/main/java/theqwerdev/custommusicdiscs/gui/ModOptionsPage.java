package theqwerdev.custommusicdiscs.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.options.GuiOptions;
import net.minecraft.client.gui.options.components.*;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.core.item.Item;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import theqwerdev.custommusicdiscs.util.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.zip.ZipOutputStream;

public class ModOptionsPage {
	public static OptionsPage optionsPage;
	private static final String[] exts = {".ogg", ".wav", ".mus"};
	private static final FileFilter audioFileFilter = new FileFilter() {
		@Override
		public boolean accept(File f) {
			for(String ext : exts)
				if(f.getName().endsWith(ext) || f.isDirectory())
					return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "Audio File (*.ogg, *.wav, *.mus)";
		}
	};



	private static void selectFiles() {
		File audioFile = FileUtils.fileSelectionPrompt("Select an audio file...", audioFileFilter);
		if(audioFile == null) {
			CustomMusicDiscsClient.LOGGER.info("Disc importing cancelled.");
			return;
		}

		File imageFile = FileUtils.fileSelectionPrompt("Select an image file...", new FileNameExtensionFilter("Image File (*.png)", "png"));
		if(imageFile == null) {
			CustomMusicDiscsClient.LOGGER.info("Disc importing cancelled.");
			return;
		}

		Path newDiscFolder = Paths.get(ModDiscs.musicPath + "/" + (ModDiscs.getTrackMapSize() + 1));
		try {
			if(!Files.exists(newDiscFolder))
				Files.createDirectories(newDiscFolder);

			Files.copy(audioFile.toPath(), Paths.get(newDiscFolder.toString(), audioFile.getName()));
			Files.copy(imageFile.toPath(), Paths.get(newDiscFolder.toString(), "texture.png"));
			ModDiscs.addToTrackMap(ModDiscs.getTrackMapSize() + 1, newDiscFolder.toFile());
			CustomMusicDiscsClient.LOGGER.info("Added Track " + ModDiscs.getTrackMapSize() + " (Audio: '" + audioFile.getName() + "', Image: '" + imageFile.getName() + "')");
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}
	}

	private static void importDiscpack() {
		File zipFile = FileUtils.fileSelectionPrompt("Select a discpack to import (NOTE: WILL DELETE YOUR CURRENT DISCPACK)", new FileNameExtensionFilter("Zip File (*.zip)", "zip"));

		if(zipFile != null) {
			try {
				File discpack = new File("./discpack");

				if(!FileUtils.deleteDirectory(discpack)) {
					CustomMusicDiscsClient.LOGGER.warn("Failed to delete current discpack! Aborting discpack import...");
					return;
				}

				CustomMusicDiscsClient.LOGGER.info("Importing discpack from '" + zipFile.getPath() + '\'');
				FileUtils.unzipFile(zipFile);
			} catch (Exception e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}

			ModDiscs.resetTrackMap();
		}
		else {
			CustomMusicDiscsClient.LOGGER.info("Discpack importing cancelled.");
		}
	}

	private static void exportDiscpack() {
		JFileChooser saveZip = new JFileChooser(".");
		saveZip.setDialogTitle("Choose the discpack export location...");
		saveZip.setFileFilter(new FileNameExtensionFilter("Zip File (*.zip)", "zip"));
		saveZip.setDialogType(JFileChooser.SAVE_DIALOG);
		saveZip.setAcceptAllFileFilterUsed(false);
		saveZip.setSelectedFile(new File("./discpack.zip"));

		int dialogResult = saveZip.showSaveDialog(null);
		if(dialogResult == JFileChooser.APPROVE_OPTION) {
			File zipFile = saveZip.getSelectedFile();

			if(!zipFile.getPath().endsWith(".zip"))
				zipFile = new File(zipFile.getPath() + ".zip");

			CustomMusicDiscsClient.LOGGER.info("Exporting discpack at '" + zipFile.getPath() + '\'');

			try {
				File discpack = new File("./discpack/");
				ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()));
				FileUtils.zipFile(discpack, discpack.getName(), zipOut);
				zipOut.close();
			} catch (Exception e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}
		else {
			CustomMusicDiscsClient.LOGGER.info("Discpack exporting cancelled.");
		}
	}

	public static GuiOptions getOptionsPage(GuiScreen parent) {
		return new GuiOptions(parent, optionsPage);
	}

	public static void registerOptionsPage() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}

		optionsPage = new OptionsPage("custommusicdiscs.options.title", Item.record13.getDefaultStack())
			.withComponent(new OptionsCategory("custommusicdiscs.options.category.general")
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.use_song_as_item_name", "use_song_as_item_name"))
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.loop_disc_audio", "loop_disc_audio"))
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.do_lootgen", "do_lootgen")))
			.withComponent(new OptionsCategory("custommusicdiscs.options.category.discpacksettings")
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.importdisc", ModOptionsPage::selectFiles))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.importdiscpack", ModOptionsPage::importDiscpack))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.exportdiscpack", ModOptionsPage::exportDiscpack))
				.withComponent(new DiscpackListComponent()));

		OptionsPages.register(optionsPage);
	}
}
