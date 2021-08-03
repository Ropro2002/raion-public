package me.robeart.raion.client.module.combat;

import com.google.common.collect.Maps;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.network.PlayerJoinEvent;
import me.robeart.raion.client.events.events.network.PlayerLeaveEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.util.Utils;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import me.robeart.raion.client.util.minecraft.GLUtils;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.FloatValue;
import me.robeart.raion.client.value.IntValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Robeart
 */
public class NotificationsModule extends Module {

	private final MinecraftFontRenderer font = Fonts.INSTANCE.getFont20();
	public BooleanValue slimes = new BooleanValue("Slime Chunks", true);
	public BooleanValue logout = new BooleanValue("Log Out Spots", true);
	public BooleanValue background = new BooleanValue("Background", true, logout);
	public IntValue red = new IntValue("Red", 255, 0, 255, 1, logout);
	public IntValue green = new IntValue("Green", 100, 0, 255, 1, logout);
	public IntValue blue = new IntValue("Blue", 255, 0, 255, 1, logout);
	public IntValue alpha = new IntValue("Alpha", 255, 0, 255, 1, logout);
	public BooleanValue visualrange = new BooleanValue("Visual Range", true);
	public BooleanValue enderpearl = new BooleanValue("Pearl Throws", true);
	public BooleanValue totems = new BooleanValue("Totems", true);
	//HTR
	public BooleanValue HTR = new BooleanValue("Hit Range", false);
	public BooleanValue selfh = new BooleanValue("Self", true, HTR);
	public BooleanValue otherh = new BooleanValue("Others", true, HTR);
	public DoubleValue rangeh = new DoubleValue("Range", 6, 1, 10, 0.1, HTR);
	public IntValue drawmodeh = new IntValue("Mode", 1, 1, 14, 1, HTR);
	public FloatValue widthh = new FloatValue("Width", 2, 0.1f, 10, 0.1f, HTR);
	//PLR
	public BooleanValue PLR = new BooleanValue("Place Range", false);
	public BooleanValue selfp = new BooleanValue("Self", true, PLR);
	public BooleanValue otherp = new BooleanValue("Others", true, PLR);
	//public IntValue alphah = new IntValue("Alpha", 80, 0, 100, 1, HTR);
	public DoubleValue rangep = new DoubleValue("Range", 4, 1, 10, 0.1, PLR);
	public IntValue drawmodep = new IntValue("Mode", 1, 1, 14, 1, PLR);
	public FloatValue widthp = new FloatValue("Width", 2, 0.1f, 10, 0.1f, PLR);
	private Map<String, EntityPlayer> logoutCache = Maps.newConcurrentMap();
	private HashMap<EntityOtherPlayerMP, Integer> totemCounter = new HashMap<>();
	public NotificationsModule() {
		super("Notifications", "Alerts you when something pvp related happens", Category.COMBAT);
	}
	//public IntValue alphahp = new IntValue("Alpha", 80, 0, 100, 1, PLR);

	@Listener
	private void onPacket(PacketReceiveEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (totems.getValue() && event.getPacket() instanceof SPacketEntityStatus) {
			SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
			if (packet.getOpCode() == 35) {
				Entity entity = packet.getEntity(mc.world);
				if (entity instanceof EntityOtherPlayerMP) {
					EntityOtherPlayerMP player = (EntityOtherPlayerMP) entity;
					Integer currentTotems = totemCounter.get(player);
					if (currentTotems == null) {
						currentTotems = 0;
					}
					currentTotems += 1;
					totemCounter.put(player, currentTotems);
					ChatUtils.message("Player [" + player.getName() + "] has popped " + currentTotems + " totems");
				}
			}
		}
	}

	@Listener
	private void onUpdate(OnUpdateEvent event) {
		if ((mc.world == null || !logout.getValue()) && logoutCache.size() != 0) logoutCache.clear();

		if (mc.world != null) {
			Iterator<Map.Entry<EntityOtherPlayerMP, Integer>> totemCounts = totemCounter.entrySet().iterator();
			while (totemCounts.hasNext()) {
				Map.Entry<EntityOtherPlayerMP, Integer> entry = totemCounts.next();
				EntityOtherPlayerMP entity = entry.getKey();
				int popped = entry.getValue();
				if (entity.isDead || !entity.isAddedToWorld()) {
					if (entity.isAddedToWorld()) {
						ChatUtils.message("[" + entity.getName() + "] died after popping " + popped + " totems");
					}
					else {
						ChatUtils.message("[" + entity.getName() + "]  left render distance after popping " + popped + " totems");
					}
					totemCounts.remove();
				}
			}
		}
	}

	@Listener
	private void onUnloadWorld(WorldEvent.Unload event) {
		totemCounter.clear();
	}

	@Listener
	private void onRender3D(RenderWorldLastEvent event) {
		if (HTR.getValue()) {
			if (selfh.getValue())
				GLUtils.circleESP(mc.player.posX, mc.player.posY, mc.player.posZ, rangeh.getValue(), Color.RED.getRGB(), drawmodeh
					.getValue(), widthh.getValue());
			if (otherh.getValue()) {
				for (Entity e : mc.world.loadedEntityList) {
					if (e instanceof EntityPlayer && e != mc.player) {
						GLUtils.circleESP(e.posX, e.posY, e.posZ, rangeh.getValue(), Color.RED.getRGB(), drawmodeh.getValue(), widthh
							.getValue());
					}
				}
			}
		}
		if (PLR.getValue()) {
			if (selfp.getValue())
				GLUtils.circleESP(mc.player.posX, mc.player.posY, mc.player.posZ, rangep.getValue(), Color.GREEN.getRGB(), drawmodep
					.getValue(), widthp.getValue());
			if (otherp.getValue()) {
				for (Entity e : mc.world.loadedEntityList) {
					if (e instanceof EntityPlayer && e != mc.player) {
						GLUtils.circleESP(e.posX, e.posY, e.posZ, rangeh.getValue(), Color.GREEN.getRGB(), drawmodeh.getValue(), widthh
							.getValue());
					}
				}
			}
		}
		if (logout.getValue()) {
			int c = Utils.getRgb(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
			logoutCache.forEach((uuid, player) -> {
				GlStateManager.pushMatrix();
				RenderUtils.drawEntityBoundingBox(player, c, event.getPartialTicks());
				String text = player.getName() + " (" + MathUtils.round(mc.player.getDistance(player), 1) + "m)";
				Vec3d pos = player.getEntityBoundingBox().grow(0.0020000000949949026D).getCenter();
				boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
				RenderManager renderManager = mc.getRenderManager();
				float scale = 0.05f;
				float width = font.getStringWidth(text) + 4;
				float heigth = font.getStringHeight(text);
				RenderUtils.beginRender();
				GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
				GlStateManager.translate((float) pos.x, (float) pos.y + 1.4f, (float) pos.z);
				GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(-scale, -scale, scale);
				GlStateManager.depthMask(false);
				GlStateManager.disableDepth();
				if (background.getValue()) GLUtils.drawRect(-width / 2f, 0, width, heigth - 0.5f, 0x80000000);
				font.drawCenteredString(text, 0, 1, -1, false);
				RenderUtils.endRender();
				GlStateManager.enableDepth();
				GlStateManager.depthMask(true);
				GlStateManager.popMatrix();
			});
		}
	}


	@Listener
	private void onEntitySpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() == mc.player) return;
		if (event.getEntity() instanceof EntityPlayer && visualrange.getValue()) {
			EntityPlayer entity = (EntityPlayer) event.getEntity();
			ChatUtils.message(entity.getName() + " has entered your render distance at: " + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ);
		}
		if (event.getEntity() instanceof EntityEnderPearl && enderpearl.getValue()) {
			EntityEnderPearl entity = (EntityEnderPearl) event.getEntity();
			EntityPlayer entityPlayer = getClosestToEntity(entity.getEntityWorld(), entity);
			if (entityPlayer == mc.player) return;
			String name = entityPlayer == null ? "Unknown" : entityPlayer.getName();
			ChatUtils.message(name + " threw an enderpearl");
		}
		if (slimes.getValue() && event.getEntity() instanceof EntitySlime) {
			EntitySlime slime = (EntitySlime) event.getEntity();
			if (slime.posY <= 40) {
				double x = MathUtils.round(slime.posX, 1);
				double y = MathUtils.round(slime.posY, 1);
				double z = MathUtils.round(slime.posZ, 1);
				ChatUtils.message("Slime spawned below y 40 at {" + x + "," + y + "," + z + "}");
			}
		}
	}


	@Listener
	private void onPlayerLeave(PlayerLeaveEvent event) {
		if (!logout.getValue()) return;
		EntityPlayer player = mc.world.getPlayerEntityByUUID(event.getGameProfile().getId());
		if (player != null && mc.player != null && player != mc.player) {
			String uuid = player.getUniqueID().toString();
			if (logoutCache.get(uuid) == null) {
				logoutCache.put(uuid, player);
				ChatUtils.message(player.getName() + " has logged out at: " + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ);
			}
		}
	}

	@Listener
	private void onPlayerJoin(PlayerJoinEvent event) {
		if (!logout.getValue()) return;
		if (mc.player != null) {
			String uuid = event.getGameProfile().getId().toString();
			if (logoutCache.get(uuid) != null) {
				EntityPlayer player = logoutCache.get(uuid);
				ChatUtils.message(player.getName() + " has logged back in last location: " + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ);
				logoutCache.remove(uuid);
			}
		}
	}

	private EntityPlayer getClosestToEntity(World world, Entity entity) {
		float distance = Float.MAX_VALUE;
		EntityPlayer entityPlayer = null;
		for (EntityPlayer e : world.playerEntities) {
			if (distance > entity.getDistance(e)) {
				distance = entity.getDistance(e);
				entityPlayer = e;
			}
		}
		return entityPlayer;
	}

}
