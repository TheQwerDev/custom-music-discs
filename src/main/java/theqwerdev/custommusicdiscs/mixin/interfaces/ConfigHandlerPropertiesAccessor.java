package theqwerdev.custommusicdiscs.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;

@Mixin(value = ConfigHandler.class, remap = false)
public interface ConfigHandlerPropertiesAccessor {
	@Accessor
	Properties getProperties();
	@Accessor
	Properties getDefaultProperties();
}
