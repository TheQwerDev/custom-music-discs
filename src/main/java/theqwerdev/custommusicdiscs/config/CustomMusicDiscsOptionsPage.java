package theqwerdev.custommusicdiscs.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.options.GuiOptions;
import net.minecraft.client.gui.options.components.*;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.core.item.Item;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.item.MusicDiscAdder;
import theqwerdev.custommusicdiscs.util.ZipUtils;
import turniplabs.halplibe.util.GameStartEntrypoint;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class CustomMusicDiscsOptionsPage implements GameStartEntrypoint {
	public static OptionsPage optionsPage;
	private static Map<Integer, File> discpackMap;

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}
	}

	private static final String[] exts = {".ogg", ".wav", ".mus"};
	FileFilter audioFileFilter = new FileFilter() {
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

	private File fileSelectionPrompt(String dialogTitle, FileFilter filter) {
		File file = null;

		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle(dialogTitle);
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int dialogResult = fileChooser.showOpenDialog(null);
		if(dialogResult == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		}

		return file;
	}

	private void selectFiles() {
		File audioFile = fileSelectionPrompt("Select an audio file...", audioFileFilter);
		if(audioFile == null) {
			CustomMusicDiscsClient.LOGGER.info("Disc importing cancelled.");
			return;
		}

		File imageFile = fileSelectionPrompt("Select an image file...", new FileNameExtensionFilter("Image File (*.png)", "png"));
		if(imageFile == null) {
			CustomMusicDiscsClient.LOGGER.info("Disc importing cancelled.");
			return;
		}

		Path newDiscFolder = Paths.get(MusicDiscAdder.musicPath + "/" + (discpackMap.size() + 1));
		try {
			if(!Files.exists(newDiscFolder))
				Files.createDirectories(newDiscFolder);

			Files.copy(audioFile.toPath(), Paths.get(newDiscFolder.toString(), audioFile.getName()));
			Files.copy(imageFile.toPath(), Paths.get(newDiscFolder.toString(), "texture.png"));
			discpackMap.put(discpackMap.size() + 1, newDiscFolder.toFile());
			CustomMusicDiscsClient.LOGGER.info("Added Track " + discpackMap.size() + " (Audio: '" + audioFile.getName() + "', Image: '" + imageFile.getName() + "')");
		} catch (IOException e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}
	}

	private void importDiscpack() {
		File zipFile = fileSelectionPrompt("Select a discpack to import (NOTE: WILL DELETE YOUR CURRENT DISCPACK)", new FileNameExtensionFilter("Zip File (*.zip)", "zip"));

		if(zipFile != null) {
			try {
				File discpack = new File("./discpack");

				if(!ZipUtils.deleteDirectory(discpack)) {
					CustomMusicDiscsClient.LOGGER.warn("Failed to delete current discpack! Aborting discpack import...");
					return;
				}

				CustomMusicDiscsClient.LOGGER.info("Importing discpack from '" + zipFile.getPath() + '\'');
				ZipUtils.unzipFile(zipFile);
			} catch (Exception e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}

			MusicDiscAdder.getTrackMap();
			discpackMap = MusicDiscAdder.trackMap;
		}
		else {
			CustomMusicDiscsClient.LOGGER.info("Discpack importing cancelled.");
		}
	}

	private void exportDiscpack() {
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
				ZipUtils.zipFile(discpack, discpack.getName(), zipOut);
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

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		discpackMap = MusicDiscAdder.trackMap;
		optionsPage = new OptionsPage("custommusicdiscs.options.title", Item.record13.getDefaultStack())
			.withComponent(new OptionsCategory("custommusicdiscs.options.category.general"))
			.withComponent(new OptionsCategory("custommusicdiscs.options.category.discpacksettings")
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.importdisc", this::selectFiles))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.importdiscpack", this::importDiscpack))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.exportdiscpack", this::exportDiscpack)))
			.withComponent(new OptionsCategory("custommusicdiscs.options.category.discpackorder")
				.withComponent(new SelectedTexturePackListComponent()));

		OptionsPages.register(optionsPage);
	}
}
