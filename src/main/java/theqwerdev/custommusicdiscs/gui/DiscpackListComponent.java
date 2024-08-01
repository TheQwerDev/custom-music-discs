package theqwerdev.custommusicdiscs.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.collection.Pair;
import org.lwjgl.opengl.GL11;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.item.ModDiscs;
import theqwerdev.custommusicdiscs.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//a lot of reused SelectedTexturePackListComponent code here lol
public class DiscpackListComponent implements OptionsComponent {
	protected static final int BUTTON_HEIGHT = 24;
	private final List<DiscpackButton> discpackButtons = new ArrayList<>();
	private int size;
	DiscpackButton draggedButton;

	public void tick() {
		if(this.size != ModDiscs.getTrackMapSize()) {
			this.size = ModDiscs.getTrackMapSize();
			this.createDiscpackButtons();
		}
	}

	public int getHeight() {
		return this.discpackButtons.isEmpty() ? 20 : 3 + this.discpackButtons.size() * (BUTTON_HEIGHT + 3);
	}

	public void render(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		if (this.discpackButtons.isEmpty()) {
			ButtonComponent.mc.fontRenderer.drawCenteredString(I18n.getInstance().translateKey("custommusicdiscs.options.label.nodiscs"), x + width / 2, y + 4, 0x5F7F7F7F);
		}

		for (DiscpackButton button : this.discpackButtons) {
			if (button != this.draggedButton) {
				int yOff = 0;
				if (button.draggable && this.draggedButton != null && relativeMouseX > 0 && relativeMouseX < width && relativeMouseY > 0 && relativeMouseY < this.getHeight()) {
					int shiftAmount = this.draggedButton.shiftAmount(relativeMouseY);
					if (shiftAmount < 0 && button.yPos < this.draggedButton.yPos && button.yPos >= this.draggedButton.yPos + shiftAmount * (BUTTON_HEIGHT + 3)) {
						yOff += BUTTON_HEIGHT + 3;
					}

					if (shiftAmount > 0 && button.yPos > this.draggedButton.yPos && button.yPos <= this.draggedButton.yPos + shiftAmount * (BUTTON_HEIGHT + 3)) {
						yOff -= BUTTON_HEIGHT + 3;
					}
				}

				button.render(this, x, y + yOff, width, relativeMouseX, relativeMouseY);
			}
		}

		if (this.draggedButton != null) {
			this.draggedButton.render(this, x, y, width, relativeMouseX, relativeMouseY);
		}
	}

	public void createDiscpackButtons() {
		this.discpackButtons.clear();

		DiscpackButton prevButton = new DiscpackButton(0, -BUTTON_HEIGHT, false, 0, null, null, null);

		ModDiscs.resetTrackMap();

		for (File track : ModDiscs.getTrackMap().values()) {
			int trackNumber = Integer.parseInt(track.getName());
			Pair<File, File> trackData = ModDiscs.extractTrackData(track);
			File audioFile = trackData.getLeft(), imageFile = trackData.getRight();

			if (audioFile == null)
				CustomMusicDiscsClient.LOGGER.warn("Failed to find audio file for track " + trackNumber);
			if (imageFile == null && !ModConfig.silenceImageFileWarnings)
				CustomMusicDiscsClient.LOGGER.warn("Failed to find image file for track " + trackNumber);

			prevButton = new DiscpackButton(0, prevButton.yPos + BUTTON_HEIGHT + 3, true, trackNumber, track, audioFile, imageFile);
			this.discpackButtons.add(prevButton);
		}
	}

	public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		for (DiscpackButton button : this.discpackButtons) {
			if (button.isClickable() && button.isHovered(relativeMouseX, relativeMouseY, width) && button.onClick(this, mouseButton, x, y, width, relativeMouseX, relativeMouseY)) {
				break;
			}
		}
	}

	public void onMouseMove(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
	}

	public void onMouseRelease(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		for (DiscpackButton button : this.discpackButtons) {
			if (button.onRelease(this, x, y, width, relativeMouseX, relativeMouseY)) {
				break;
			}
		}
	}

	public void onKeyPress(int keyCode, char character) {}

	public boolean matchesSearchTerm(String term) {
		return false;
	}

	private static class DiscpackButton {
		private BufferedImage discImageBuffer;
		private int discImage = -1;
		private final GuiButton button;
		public boolean draggable;
		public final File parentFolder, audioFile, imageFile;
		public final int height = BUTTON_HEIGHT;
		public int trackNumber;
		public int xPos;
		public int yPos;
		private int clickX = -1;
		private int clickY = -1;

		public DiscpackButton(int xPos, int yPos, boolean draggable, int trackNumber, File parentFolder, File audioFile, File imageFile) {
			this.draggable = draggable;
			this.parentFolder = parentFolder;
			this.audioFile = audioFile;
			this.imageFile = imageFile;
			this.trackNumber = trackNumber;
			this.xPos = xPos;
			this.yPos = yPos;
			this.button = new GuiButton(0, 0, 0, 20, 20, "-");

			if(imageFile != null) {
				try {
					this.discImageBuffer = ImageIO.read(imageFile);
				} catch (IOException e) {
					CustomMusicDiscsClient.LOGGER.warn("Failed to load disc texture for track " + trackNumber);
				}
			}
		}

		public boolean isClickable() {
			return true;
		}

		public boolean isHovered(int mouseX, int mouseY, int width) {
			if (this.isDragged()) {
				int dX = mouseX - this.clickX;
				int dY = mouseY - this.clickY;
				mouseX -= dX;
				mouseY -= dY;
			}

			return mouseX >= 0 && mouseX <= width && mouseY >= this.yPos && mouseY <= this.yPos + height;
		}

		public boolean isDragged() {
			return this.clickX > 0 || this.clickY > 0;
		}

		public boolean onClick(DiscpackListComponent component, int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
			this.setupButton(x, y, width);
			if (mouseButton == 0 && this.button.isHovered(x + relativeMouseX, y + relativeMouseY)) {
				this.deleteDisc(component);
				ButtonComponent.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
				component.draggedButton = null;
				return true;
			}

			if (mouseButton == 1) {
				this.deleteDisc(component);
				ButtonComponent.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
				return true;
			}

			if (!this.draggable) {
				return false;
			} else {
				this.clickX = relativeMouseX;
				this.clickY = relativeMouseY;
				component.draggedButton = this;
				return true;
			}
		}

		public boolean onRelease(DiscpackListComponent component, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
			if (this.isDragged()) {
				if (relativeMouseX >= 0 && relativeMouseX <= x + width && relativeMouseY >= 0 && relativeMouseY <= y + component.getHeight()) {
					this.shiftDisc(this.parentFolder, this.shiftAmount(relativeMouseY));
					this.clickX = -1;
					this.clickY = -1;
					component.draggedButton = null;
					component.createDiscpackButtons();
				} else {
					this.deleteDisc(component);
				}
				return true;
			} else {
				this.clickX = -1;
				this.clickY = -1;
				return false;
			}
		}

		private void shiftDisc(File trackToShift, int shiftAmount) {
			if (!ModDiscs.getTrackMap().containsValue(trackToShift) || shiftAmount == 0)
				return;

			int newIndex = this.trackNumber + shiftAmount;
			if (newIndex < 1)
				newIndex = 1;
			if (newIndex > ModDiscs.getTrackMapSize())
				newIndex = ModDiscs.getTrackMapSize();

			ModDiscs.removeFromTrackMap(this.trackNumber);
			Object[] trackArray = ModDiscs.getTrackMap().values().toArray();

			try {
				Files.move(trackToShift.toPath(), Paths.get("./discpack/temp"));
				trackToShift = new File("./discpack/temp");

				if (shiftAmount > 0) {
					for (int i = 0; i < trackArray.length; i++) {
						File track = (File) trackArray[i];

						int trackNumber = Integer.parseInt(track.getName());
						if (trackNumber <= newIndex && trackNumber > this.trackNumber) {
							Files.move(track.toPath(), Paths.get("./discpack/", Integer.toString(trackNumber - 1)));
						}
					}
				}
				else {
					for (int i = trackArray.length - 1; i >= 0; i--) {
						File track = (File) trackArray[i];

						int trackNumber = Integer.parseInt(track.getName());
						if (trackNumber >= newIndex && trackNumber < this.trackNumber) {
							Files.move(track.toPath(), Paths.get("./discpack/", Integer.toString(trackNumber + 1)));
						}
					}
				}

				Files.move(trackToShift.toPath(), Paths.get("./discpack/", Integer.toString(newIndex)));
			}
			catch (IOException e) {
				CustomMusicDiscsClient.LOGGER.warn(e.toString());
			}
		}

		public void deleteDisc(DiscpackListComponent component) {
			ModDiscs.removeFromTrackMap(Integer.parseInt(this.parentFolder.getName()));
			FileUtils.deleteDirectory(this.parentFolder);
			component.draggedButton = null;
			component.createDiscpackButtons();
		}

		public int draggedX(int mouseX) {
			return mouseX - this.clickX;
		}

		public int draggedY(int mouseY) {
			return mouseY - this.clickY;
		}

		public int shiftAmount(int mouseY) {
			return this.draggedY(mouseY) / height;
		}

		public void render(DiscpackListComponent component, int x, int y, int width, int mouseX, int mouseY) {
			Tessellator tessellator = Tessellator.instance;
			FontRenderer fontRenderer = ButtonComponent.mc.fontRenderer;
			this.setupButton(x, y, width);
			int xMove, yMove;
			if (this.isDragged()) {
				xMove = this.draggedX(mouseX);
				yMove = this.draggedY(mouseY);
				x += xMove;
				y += yMove;
			}

			if (this.isDragged() || this.isHovered(mouseX, mouseY, width) && component.draggedButton == null) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(3553);
				tessellator.startDrawingQuads();
				tessellator.setColorOpaque_I(0x7F808080);
				tessellator.addVertexWithUV(x + this.xPos - 2, y + this.yPos + height + 2, 0.0, 0.0, 1.0);
				tessellator.addVertexWithUV(x + this.xPos + width + 2, y + this.yPos + height + 2, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x + this.xPos + width + 2, y + this.yPos - 2, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x + this.xPos - 2, y + this.yPos - 2, 0.0, 0.0, 0.0);
				tessellator.setColorOpaque_I(0);
				tessellator.addVertexWithUV(x + this.xPos - 1, y + this.yPos + height + 1, 0.0, 0.0, 1.0);
				tessellator.addVertexWithUV(x + this.xPos + width + 1, y + this.yPos + height + 1, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x + this.xPos + width + 1, y + this.yPos - 1, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x + this.xPos - 1, y + this.yPos - 1, 0.0, 0.0, 0.0);
				tessellator.draw();
				GL11.glEnable(3553);
			}

			if(this.discImageBuffer != null && this.discImage < 0) {
				discImage = ButtonComponent.mc.renderEngine.allocateAndSetupTexture(discImageBuffer);
			}
			if (this.discImageBuffer != null) {
				ButtonComponent.mc.renderEngine.bindTexture(this.discImage);
			} else {
				GL11.glBindTexture(3553, ButtonComponent.mc.renderEngine.getTexture("/assets/custommusicdiscs/textures/item/disc_placeholder.png"));
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(0xFFFFFF);
			tessellator.addVertexWithUV(x + this.xPos, y + this.yPos + height, 0.0, 0.0, 1.0);
			tessellator.addVertexWithUV(x + this.xPos + height, y + this.yPos + height, 0.0, 1.0, 1.0);
			tessellator.addVertexWithUV(x + this.xPos + height, y + this.yPos, 0.0, 1.0, 0.0);
			tessellator.addVertexWithUV(x + this.xPos, y + this.yPos, 0.0, 0.0, 0.0);
			tessellator.draw();

			String trackName;

			if(this.audioFile != null) {
				trackName = this.audioFile.getName();
				int extPos = trackName.lastIndexOf('.');
				trackName = trackName.substring(0, extPos);
			}
			else {
				trackName = "Unknown Track " + trackNumber;
			}

			fontRenderer.drawString(trackName, x + this.xPos + height + 2, y + this.yPos + 1, 0xFFFFFF);

			if (this.isHovered(mouseX, mouseY, width) && component.draggedButton == null) {
				this.button.drawButton(ButtonComponent.mc, mouseX + x, mouseY + y);
			}
		}

		public void setupButton(int x, int y, int width) {
			this.button.setX(x + this.xPos + width - this.button.width - 1);
			this.button.setY(y + this.yPos + (height - this.button.height) / 2);
		}
	}
}
