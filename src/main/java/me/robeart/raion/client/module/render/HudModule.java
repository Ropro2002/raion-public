package me.robeart.raion.client.module.render;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.events.events.render.Render2DEvent;
import me.robeart.raion.client.gui.hud.Popup;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import me.robeart.raion.client.util.minecraft.GLUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.FloatValue;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HudModule extends Module {

	public final BooleanValue bitmap = new BooleanValue("Bitmap Font", false);

	public final FloatValue moduleSlideSpeed = new FloatValue("Slide Speed", 0.25f, 0.1f, 1f, 0.05f);
	public final ListValue color = new ListValue("Color", "Static", Arrays.asList("Static", "Rainbow"));
	public final IntValue delayrainbow = new IntValue("Rainbow Delay", 10, 1, 50, 1, color, "Rainbow");
	public final FloatValue saturation = new FloatValue("Saturation", 1, 0.1f, 1, 0.1f, color, "Rainbow");
	public final FloatValue lightness = new FloatValue("Lightness", 1, 0.1f, 1, 0.1f, color, "Rainbow");
	public final IntValue red = new IntValue("Red", 255, 0, 255, 1, color, "Static");
	public final IntValue green = new IntValue("Green", 100, 0, 255, 1, color, "Static");
	public final IntValue blue = new IntValue("Blue", 255, 0, 255, 1, color, "Static");

	public final BooleanValue tooltips = new BooleanValue("Gui Tooltips", true);

	public final BooleanValue nightMode = new BooleanValue("Night Mode", false);
	public final IntValue nightRed = new IntValue("Night Red", 255, 0, 255, 1, this.nightMode);
	public final IntValue nightGreen = new IntValue("Night Green", 100, 0, 255, 1, this.nightMode);
	public final IntValue nightBlue = new IntValue("Night Blue", 255, 0, 255, 1, this.nightMode);

	public final BooleanValue coords = new BooleanValue("Coords", true);
	public final BooleanValue line = new BooleanValue("1 Line", true, coords);
	public final BooleanValue shadow = new BooleanValue("Shadow", false);
	public final BooleanValue watermark = new BooleanValue("Watermark", true);
	public final BooleanValue serverBrand = new BooleanValue("Server Brand", false);
	public final BooleanValue weeb = new BooleanValue("Weeb", false);
	public final ListValue mode = new ListValue("Mode", "Both", Arrays.asList("Both", "Logo", "Text"), watermark);
	public final BooleanValue notResponding = new BooleanValue("Server Lag", true);
	public final IntValue responseDelay = new IntValue("Lag Delay", 1000, 0, 10000, 250, notResponding);
	private final MinecraftFontRenderer font = Fonts.INSTANCE.getFont36();
	private final StrLengthComparator strLengthComparator = new StrLengthComparator();
	private List<Popup> queue = new ArrayList<>();
	private long now = System.currentTimeMillis();
	private ResourceLocation logo = new ResourceLocation("textures/gui/logo.png");

	public HudModule() {
		super("Hud", "Enable/Disable the Hud", Category.RENDER, false);
	}

    /*@Listener
    private void onToggle(ToggleEvent event) {
        if (mc.world != null && !(mc.currentScreen instanceof ClickGui)) {
            Raion.INSTANCE.getPopupManager().add(new TogglePopup(event.getModule(), event.getModule().getState()));
        }
    }*/

	@Listener
	private void onPacketReceive(PacketReceiveEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		this.now = System.currentTimeMillis();
		if (this.nightMode.getValue()
			&& event.getPacket() instanceof SPacketTimeUpdate) event.setCanceled(true);
	}

	@Listener
	private void onRender2D(Render2DEvent event) {
		if (!mc.gameSettings.hideGUI && mc.currentScreen == null) {
			ScaledResolution res = new ScaledResolution(mc);
			drawNamelist(res);
			if (serverBrand.getValue()) drawBrand(res);
			if (coords.getValue()) drawCoords(res);
			if (watermark.getValue()) drawWatermark(res);
			if (this.notResponding.getValue()) {
				if (System.currentTimeMillis() - this.now >= this.responseDelay.getValue()) {
					font.drawCenteredString("Server has not responded for " + new DecimalFormat("0.0").format((System.currentTimeMillis() - this.now) / 1000.0D) + "s", res
						.getScaledWidth() / 2f, 3, 0xFFa7a7a8);
				}
			}
		}
	}

	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (this.nightMode.getValue()) {
			mc.world.setWorldTime(18000);
		}
	}

	private void drawWatermark(ScaledResolution res) {
		String raion = "Raion";
		if (weeb.getValue()) {
			raion = "\u30e9\u30a4\u30aa\u30f3";
		}
		switch (mode.getValue()) {
			case "Both":
				font.drawString(raion + " v" + Raion.VERSION, res.getScaledHeight() / 20 + 1, 0, GLUtils.getColor(100));
				mc.getTextureManager().bindTexture(logo);
				GLUtils.drawCompleteImage(2, 1, res.getScaledHeight() / 20, res.getScaledHeight() / 20);
				break;
			case "Logo":
				GLUtils.glColor(GLUtils.getColor(100));
				mc.getTextureManager().bindTexture(logo);
				GLUtils.drawCompleteImage(2, 2, res.getScaledHeight() / 20, res.getScaledHeight() / 20);
				break;
			case "Text":
				font.drawString(raion + " v" + Raion.VERSION, 2, 0, GLUtils.getColor(100));
		}
	}

	private void drawBrand(ScaledResolution res) {
		int offset = mc.currentScreen instanceof GuiChat ? 25 : 12;
		String brand = mc.player.getServerBrand();
		if (brand == null) return;
		font.drawString("Brand ", 2, res.getScaledHeight() - 16 - offset, GLUtils.getColor(100));
		font.drawString(brand, 2 + font.getStringWidth("Brand "), res.getScaledHeight() - 16 - offset, 0xFFa7a7a8);
	}

	private void drawCoords(ScaledResolution res) {
		int offset = mc.currentScreen instanceof GuiChat ? 25 : 12;
		Entity entity = mc.getRenderViewEntity();
		if (entity == null) {
			entity = mc.player;
		}
		double x = MathUtils.round(entity.posX, 1);
		double y = MathUtils.round(entity.posY, 1);
		double z = MathUtils.round(entity.posZ, 1);
		float pitch = MathUtils.round(MathHelper.wrapDegrees(entity.rotationPitch), 1);
		float yaw = MathUtils.round(MathHelper.wrapDegrees(entity.rotationYaw), 1);
		double netx = MathUtils.round(x / 8, 1);
		double netz = MathUtils.round(z / 8, 1);
		double owx = MathUtils.round(x * 8, 1);
		double owz = MathUtils.round(z * 8, 1);
		String direction;
		if (entity.getHorizontalFacing().getName().equals("south")) direction = "South +Z";
		else if (entity.getHorizontalFacing().getName().equals("north")) direction = "North -Z";
		else if (entity.getHorizontalFacing().getName().equals("east")) direction = "East +X";
		else direction = "West -X";
		String coords = x + ", " + y + ", " + z;
		String owcoords = owx + ", " + y + ", " + owz;
		String nethercoords = netx + ", " + y + ", " + netz;
		font.drawString(direction, 2, res.getScaledHeight() - offset, GLUtils.getColor(100));
		font.drawString(" [" + yaw + ", " + pitch + "]", font.getStringWidth(direction) + 2, res.getScaledHeight() - offset, 0xFFa7a7a8);
		if (line.getValue()) {
			String coordsfinal = entity.dimension == 0 ? coords + " (" + nethercoords + ")" : entity.dimension == -1 ? coords + "(" + owcoords + ")" : coords;
			font.drawString("XYZ ", 2, res.getScaledHeight() - 8 - offset, GLUtils.getColor(100));
			font.drawString(coordsfinal, font.getStringWidth("XYZ ") + 2, res.getScaledHeight() - 8 - offset, 0xFFa7a7a8);
		}
		else {
			font.drawString("XYZ ", 2, entity.dimension == 1 ? res.getScaledHeight() - 8 - offset : res.getScaledHeight() - 16 - offset, GLUtils
				.getColor(100));
			font.drawString(coords, font.getStringWidth("XYZ ") + 2, entity.dimension == 1 ? res.getScaledHeight() - 8 - offset : res
				.getScaledHeight() - 16 - offset, 0xFFa7a7a8);
			if (entity.dimension == 0) {
				font.drawString("Nether  ", 2, res.getScaledHeight() - 8 - offset, GLUtils.getColor(100));
				font.drawString(nethercoords, font.getStringWidth("Nether ") + 2, res.getScaledHeight() - 8 - offset, 0xFFa7a7a8);
			}
			if (entity.dimension == -1) {
				font.drawString("Overworld  ", 2, res.getScaledHeight() - 8 - offset, GLUtils.getColor(100));
				font.drawString(owcoords, font.getStringWidth("Overworld ") + 2, res.getScaledHeight() - 8 - offset, 0xFFa7a7a8);
			}
		}

	}

	private void drawNamelist(ScaledResolution res) {
		float ycount = 2f;
		
		List<Module> originalList = Raion.INSTANCE.getModuleManager().getModuleList();
		List<Module> list = new ArrayList<>(originalList.size());
		for (Module module : originalList) {
			if (module.getVisible()) {
				module.recalculateWidth(font, moduleSlideSpeed.getValue());
				list.add(module);
			}
		}
		list.sort((module1, module2) -> Float.compare(module2.totalWidth, module1.totalWidth));
		
		for (Module module : list) {
			String name = module.getName();
			String info = module.getHudInfo();
			if (info != null) info = " " + info;
			
			if (!Interpolation.isNearlyZero(module.currentWidth, 2)) {
				float height = Math.max(font.getStringHeight(name), info == null ? 0 : font.getStringHeight(info));
				
				float thisHeight = module.calculateCurrentHeight(ycount, moduleSlideSpeed.getValue());
				
				font.drawString(name, res.getScaledWidth() - module.currentWidth - 2, thisHeight, GLUtils.getColor(100));
				if (info != null) {
					float infoOffset = module.currentWidth - module.nameWidth;
					font.drawString(info, res.getScaledWidth() - infoOffset - 2, thisHeight, 0xFFa7a7a8);
				}
				ycount += height + 2;
			}
		}
	}

	class StrLengthComparator implements java.util.Comparator<String> {
		public int compare(String h1, String h2) {
			final float h1Width = font.getStringWidth(h1);
			final float h2Width = font.getStringWidth(h2);
			return Float.compare(h2Width, h1Width);
		}
	}
}
