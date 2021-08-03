package me.robeart.raion.client.command;


import net.minecraft.item.ItemShulkerBox;
import net.minecraft.tileentity.TileEntityShulkerBox;

public class PeekCommand extends Command {
	
	public static TileEntityShulkerBox sb;
	
	public PeekCommand() {
		super("Peek", "See inside a shulkerbox", "peek");
	}
	
	@Override
	public void call(String[] args) {
		if (mc.player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) {
		}
	}
	
}
