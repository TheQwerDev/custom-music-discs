package theqwerdev.custommusicdiscs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSwitchButton;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.core.lang.I18n;
import theqwerdev.custommusicdiscs.config.ModConfig;
import theqwerdev.custommusicdiscs.mixin.interfaces.ConfigHandlerPropertiesAccessor;

import java.util.Properties;

public class ConfigBooleanOptionComponent extends ButtonComponent {
	private Properties properties;
	private Properties defaultProperties;
	private final String optionName;
	private final GuiSwitchButton button;

	public ConfigBooleanOptionComponent(String translationKey, String optionName) {
		super(translationKey);
		this.optionName = optionName;
		this.button = new GuiSwitchButton(0, 0, 0, 150, 20, false, "", "");
		this.button.setOn(ModConfig.config.getBoolean(optionName));
		this.button.displayString = this.getDisplayString();
	}

	public String getDisplayString() {
		I18n trans = I18n.getInstance();
		if(ModConfig.config.getBoolean(optionName))
			return trans.translateKey("options.on");
		else
			return trans.translateKey("options.off");
	}

	@Override
	public void resetValue() {
		String defaultValueString = defaultProperties.getProperty(optionName);
		boolean defaultValue = Boolean.parseBoolean(defaultValueString);
		properties.setProperty(optionName, defaultValueString);
		this.button.setOn(defaultValue);
		this.button.displayString = this.getDisplayString();
	}

	@Override
	public boolean isDefault() {
		return ModConfig.config.getBoolean(optionName) == Boolean.parseBoolean(defaultProperties.getProperty(optionName));
	}

	@Override
	public void init(Minecraft mc) {
		ConfigHandlerPropertiesAccessor accessor = ((ConfigHandlerPropertiesAccessor) ModConfig.config);
		properties = accessor.getProperties();
		defaultProperties = accessor.getDefaultProperties();
		this.button.setOn(ModConfig.config.getBoolean(optionName));
		this.button.displayString = this.getDisplayString();
	}

	@Override
	protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
		properties.setProperty(optionName, Boolean.toString(!ModConfig.config.getBoolean(optionName)));
		this.button.setOn(ModConfig.config.getBoolean(optionName));
		this.button.displayString = this.getDisplayString();
	}

	protected void renderButton(int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY) {
		super.renderButton(x, y, relativeButtonX, relativeButtonY, buttonWidth, buttonHeight, relativeMouseX, relativeMouseY);
		this.button.xPosition = x + relativeButtonX;
		this.button.yPosition = y + relativeButtonY;
		this.button.width = buttonWidth;
		this.button.height = buttonHeight;
		this.button.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
	}
}
