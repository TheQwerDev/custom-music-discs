package theqwerdev.custommusicdiscs.mixin;

import net.minecraft.core.InventoryAction;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet102WindowClick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.io.DataInputStream;
import java.io.IOException;

@Mixin(value = Packet102WindowClick.class, remap = false)
public class Packet102WindowClickMixin {
	@Shadow
	public int window_Id;
	@Shadow
	public InventoryAction action;
	@Shadow
	public int[] args;
	@Shadow
	public short actionId;
	@Shadow
	public ItemStack itemStack;

	@Inject(method = "readPacketData", at = @At("HEAD"), cancellable = true)
	public void readPacketData(DataInputStream dis, CallbackInfo ci) throws IOException {
		this.window_Id = dis.readByte();
		this.action = InventoryAction.get(dis.readByte());
		int size = dis.readByte();
		this.args = new int[size];
		for (int i = 0; i < size; ++i) {
			this.args[i] = dis.read();
		}
		this.actionId = dis.readShort();
		short word0 = dis.readShort();
		if (word0 >= 0) {
			byte byte0 = dis.readByte();
			short word1 = dis.readShort();
			this.itemStack = new ItemStack(word0, (int)byte0, (int)word1);
		} else {
			this.itemStack = null;
		}
		ci.cancel();
	}
}
