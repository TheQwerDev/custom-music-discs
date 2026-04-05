package theqwerdev.custommusicdiscs.gui;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.options.ScreenOptions;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.core.item.Items;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import theqwerdev.custommusicdiscs.util.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
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

	private static String[] prompt() {
		final String[] result = {null, null};
		JFrame frame = new JFrame("Input Track Info (can be left empty to use audio file name)");
		JPanel panel = new JPanel();

		JLabel tfTitle1 = new JLabel("Author Name"), tfTitle2 = new JLabel("Track Name");
		JTextField tf1 = new JTextField(16), tf2 = new JTextField(16);
		JButton submit = new JButton("Submit");

		submit.addActionListener(e -> {
			String s = e.getActionCommand();
			if(s.equals("Submit")) {
				synchronized (result) {
					result[0] = tf1.getText();
					result[1] = tf2.getText();
					result.notify();
				}
				frame.dispose();
			}
		});

		panel.add(tfTitle1); panel.add(tf1);
		panel.add(tfTitle2); panel.add(tf2);
		panel.add(submit);

		frame.add(panel);
		frame.setSize(400, 100);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.pack();

		synchronized (result) {
			while(result[0] == null) {
				try {
					result.wait();
				} catch (InterruptedException e) {
					CustomMusicDiscsClient.LOGGER.warn(e.toString());
				}
			}

			return result;
		}
	}

	private static void importDisc() {
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

		String[] propInputs = prompt();
		CustomMusicDiscsClient.LOGGER.error(propInputs[0] + ' ' + propInputs[1]);

		String audioNameNoExt = audioFile.getName();
		int extPos = audioNameNoExt.lastIndexOf('.');
		audioNameNoExt = audioNameNoExt.substring(0, extPos);

		Path newDiscFolder = Paths.get(ModDiscs.musicPath + "/" + audioNameNoExt);
		try {
			if(!Files.exists(newDiscFolder))
				Files.createDirectories(newDiscFolder);

			Files.copy(audioFile.toPath(), Paths.get(newDiscFolder.toString(), audioFile.getName()));
			Files.copy(imageFile.toPath(), Paths.get(newDiscFolder.toString(), "texture.png"));

			Properties prop = new Properties();
			if(!propInputs[0].isEmpty())
				prop.setProperty("author_name", propInputs[0]);
			if(!propInputs[1].isEmpty())
				prop.setProperty("track_name", propInputs[1]);
			prop.setProperty("pos", Integer.toString(++ModDiscs.tracksSize));
			prop.store(new FileWriter(newDiscFolder + "/info.txt"), null);

			ModDiscs.tracks[ModDiscs.tracksSize] = newDiscFolder.toFile();
			CustomMusicDiscsClient.LOGGER.info("Added Track " + ModDiscs.tracksSize + " (Audio: '" + audioFile.getName() + "', Image: '" + imageFile.getName() + "')");
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

			ModDiscs.resetTrackList();
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

	public static ScreenOptions getOptionsPage(Screen parent) {
		return new ScreenOptions(parent, optionsPage);
	}

	public static void registerOptionsPage() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			CustomMusicDiscsClient.LOGGER.warn(e.toString());
		}

		optionsPage = new OptionsPage("custommusicdiscs.options.title", Items.RECORD_13.getDefaultStack())
			.withComponent(new OptionsCategory("custommusicdiscs.options.category.general")
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.use_song_as_item_name", "use_song_as_item_name"))
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.loop_disc_audio", "loop_disc_audio"))
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.do_lootgen", "do_lootgen"))
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.silence_image_file_warnings", "silence_image_file_warnings")));

		if(!ModConfig.hideDiscpackSettings) {
			optionsPage.withComponent(new OptionsCategory("custommusicdiscs.options.category.discpacksettings")
				.withComponent(new ConfigBooleanOptionComponent("custommusicdiscs.options.button.hide_discpack_settings", "hide_discpack_settings"))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.importdisc", ModOptionsPage::importDisc))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.importdiscpack", ModOptionsPage::importDiscpack))
				.withComponent(new ShortcutComponent("custommusicdiscs.options.button.exportdiscpack", ModOptionsPage::exportDiscpack))
				.withComponent(new DiscpackListComponent()));
		}

		OptionsPages.register(optionsPage);
	}
}
