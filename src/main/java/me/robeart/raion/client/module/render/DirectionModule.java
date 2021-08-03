package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.render.Render3DEvent;
import me.robeart.raion.client.imixin.IRenderManager;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Vector3f;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.Color;

import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author cookiedragon234 03/Feb/2020
 */
public class DirectionModule extends Module {
	
	public DirectionModule() {
		super("Direction", "Renders the direction of players", Category.RENDER);
	}
	
	
	@Listener
	public void onRender3D(Render3DEvent event) {
		
		final IRenderManager renderManager = (IRenderManager) Minecraft.getMinecraft().getRenderManager();
		
		final double x = renderManager.getRenderPosX();
		final double y = renderManager.getRenderPosY();
		final double z = renderManager.getRenderPosZ();
		
		pushAttrib();
		RenderUtils.beginRender();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.glLineWidth(10.0f);
		disableTexture2D();
		GlStateManager.enableDepth();
		depthMask(true);
		shadeModel(GL_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		
		final Color startHSBColor = Color.getHSBColor(110f / 360, 1f, 1f);
		final Color endHSBColor = Color.getHSBColor(340f / 360, 1f, 1f);
		
		Vector3f startColour = new Vector3f(startHSBColor.getRed() / 255f, startHSBColor.getGreen() / 255f, startHSBColor
			.getBlue() / 255f);
		
		Vector3f endColour = new Vector3f(endHSBColor.getRed() / 255f, endHSBColor.getGreen() / 255f, endHSBColor.getBlue() / 255f);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

/*        Vec3d pos = mc.player.getPositionVector().subtract(x, y - 0.1, z);
        final double scale = 20;
        Vec3d nextPos = new Vec3d((mc.player.posX - mc.player.prevPosX) * scale,
                (mc.player.posY - mc.player.prevPosY) * scale,
                (mc.player.posZ - mc.player.prevPosZ) * scale);
        Vec3d endPos = pos.add(mc.player.motionX * 5, mc.player.motionY * 2, mc.player.motionZ * 5);

        buffer.begin(GL_LINE_STRIP, POSITION_COLOR);
        buffer.pos(pos.x, pos.y, pos.z)
                .color(startColour.x, startColour.y, startColour.z, 1f)
                .endVertex();
        buffer.pos(endPos.x, endPos.y, endPos.z)
                .color(endColour.x, endColour.y, endColour.z, 1f)
                .endVertex();
        tessellator.draw();*/
		
		for (Entity entity : mc.world.loadedEntityList) {
			//if (entity instanceof EntityPlayer) {
			Vec3d pos = entity.getPositionVector().subtract(x, y - 0.1, z);
			final double scale = 20;
			Vec3d nextPos = new Vec3d(
				(entity.posX - entity.prevPosX) * scale,
				(entity.posY - entity.prevPosY) * scale,
				(entity.posZ - entity.prevPosZ) * scale
			);
			Vec3d endPos = pos.add(nextPos);
			//Vec3d endPos = pos.add(entity.motionX * 5, entity.motionY / 100, entity.motionZ * 5);
			
			buffer.begin(GL_LINE_STRIP, POSITION_COLOR);
			buffer.pos(pos.x, pos.y, pos.z)
				.color(startColour.x, startColour.y, startColour.z, 1f)
				.endVertex();
			buffer.pos(endPos.x, endPos.y, endPos.z)
				.color(endColour.x, endColour.y, endColour.z, 1f)
				.endVertex();
			tessellator.draw();
			//}
		}
		
		RenderUtils.endRender();
		enableDepth();
		depthMask(true);
		enableTexture2D();
		popAttrib();
	}

	/*
	val maxSpeed = 4f
	val maxHsb = 110f
	private fun getColour(speed_: Float): Color {
		val speed = min(maxSpeed, abs(speed_))

		val multiplier = maxSpeed / speed

		return Color.getHSBColor((maxHsb * multiplier) / 360, 1f, 1f)
	}*/
	
	/**
	 * gets the direction and speed for players instead of using motion, because motion acts weird
	 * @param speed the speed constant to use
	 * @param entity the entity to put in
	 * @return the 2d direction vector
	 *//*
    private static double[] directionSpeed(double speed, EntityLivingBase entity) {
        float forward = entity.moveForward;
        ChatUtils.message(String.valueOf(entity.moveForward));
        float side = entity.moveStrafing;

        ChatUtils.message(String.valueOf(entity.moveStrafing));
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0) {
            if (side > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (side < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            side = 0;

            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[] { posX, posZ };
    }*/
}
