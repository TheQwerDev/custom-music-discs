package theqwerdev.custommusicdiscs.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.Screen;
import theqwerdev.custommusicdiscs.gui.ModOptionsPage;

import java.util.function.Function;

public class ModMenuModule implements ModMenuApi {
	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return ModOptionsPage::getOptionsPage;
	}
}
