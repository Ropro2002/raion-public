package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.imixin.IMixinItemRenderer;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.FloatValue;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 */
public class LowOffHandModule extends Module {
	
	private final IMixinItemRenderer itemRenderer = (IMixinItemRenderer) mc.entityRenderer.itemRenderer;
	public FloatValue mainHeight = new FloatValue("Mainhand Height", 70f, 0f, 100f, 0.1f);
	public FloatValue offHeight = new FloatValue("Offhand Height", 70f, 0f, 100f, 0.1f);
	
	
	public LowOffHandModule() {
		super("LowHand", "Makes your hands appear lower", Category.RENDER);
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		itemRenderer.setEquippedProgressMainHand((this.mainHeight.getValue() / 100));
		itemRenderer.setEquippedProgressOffHand((this.offHeight.getValue() / 100));
	}
}
