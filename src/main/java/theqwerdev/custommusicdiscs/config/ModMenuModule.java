package theqwerdev.custommusicdiscs.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.Screen;
import theqwerdev.custommusicdiscs.client.CustomMusicDiscsClient;
import theqwerdev.custommusicdiscs.gui.ModOptionsPage;

import java.util.function.Function;

public class ModMenuModule implements ModMenuApi {
	@Override
	public String getModId() {
		return CustomMusicDiscsClient.MOD_ID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return ModOptionsPage::getOptionsPage;
	}
}
