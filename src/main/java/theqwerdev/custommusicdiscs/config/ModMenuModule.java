package theqwerdev.custommusicdiscs.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.GuiScreen;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;

import java.util.function.Function;

public class ModMenuModule implements ModMenuApi {
	@Override
	public String getModId() {
		return CustomMusicDiscsClient.MOD_ID;
	}

	@Override
	public Function<GuiScreen, ? extends GuiScreen> getConfigScreenFactory() {
		return CustomMusicDiscsOptionsPage::getOptionsPage;
	}
}
