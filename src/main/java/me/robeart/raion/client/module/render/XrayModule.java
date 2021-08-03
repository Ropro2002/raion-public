package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.render.SetOpaqueCubeEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.IntValue;
import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeModContainer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;

public class XrayModule extends Module {
	
	//TODO Fix crash on restart
	
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	public IntValue opacity = new IntValue("Opacity", 180, 0, 255, 1);
	private boolean forgeLightPipelinEnabled;
	private float lastGamma;
	private int lastAO;
	
	public XrayModule() {
		super("Xray", "Allows you to see through walls", Category.RENDER);
	}
	
	public static boolean shouldXray(Block block) {
		return blocks.contains(block);
	}
	
	public static void initblocks() {
		blocks.add(Block.getBlockById(14));
		blocks.add(Block.getBlockById(15));
		blocks.add(Block.getBlockById(16));
		blocks.add(Block.getBlockById(21));
		blocks.add(Block.getBlockById(56));
		blocks.add(Block.getBlockById(73));
		blocks.add(Block.getBlockById(74));
		blocks.add(Block.getBlockById(129));
		blocks.add(Block.getBlockById(153));
		blocks.add(Block.getBlockById(11));
		blocks.add(Block.getBlockById(10));
		blocks.add(Block.getBlockById(52));
		blocks.add(Block.getBlockById(30));
	}
	
	@Override
	public void onEnable() {
		forgeLightPipelinEnabled = ForgeModContainer.forgeLightPipelineEnabled;
		lastGamma = mc.gameSettings.gammaSetting;
		lastAO = mc.gameSettings.ambientOcclusion;
		ForgeModContainer.forgeLightPipelineEnabled = false;
		mc.gameSettings.gammaSetting = 100.0F;
		mc.gameSettings.ambientOcclusion = 0;
		if (mc.world != null) {
			mc.renderGlobal.loadRenderers();
		}
		
	}
	
	@Override
	public void onDisable() {
		ForgeModContainer.forgeLightPipelineEnabled = forgeLightPipelinEnabled;
		mc.gameSettings.gammaSetting = lastGamma;
		mc.gameSettings.ambientOcclusion = lastAO;
		if (mc.world != null) {
			mc.renderGlobal.loadRenderers();
		}
	}
	
	@Listener
	private void onSetOpaqueCube(SetOpaqueCubeEvent event) {
		event.setCanceled(true);
	}

    /*@Listener
    private void onChangeValue(ChangeValueEvent event) {
        if (event.getValue() == presets) {
            switch (presets.getValue()) {
                case "Custom":
                    blocks = custom;
                    break;
                case "blocks":
                    blocks = blocks;
                    break;
                case "Portals":
                    blocks = portals;
                    break;
                case "Travel":
                    blocks = travel;
                    break;
                case "PvP":
                    blocks = pvp;
                    break;
                case "Redstone":
                    blocks = redstone;
                    break;
                default:
                    blocks = custom;
            }
            mc.renderGlobal.loadRenderers();
        }
        if (event.getValue() == opacity) mc.renderGlobal.loadRenderers();
    }*/
	
	
}
