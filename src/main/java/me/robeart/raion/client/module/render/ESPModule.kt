package me.robeart.raion.client.module.render

import dev.binclub.fps.client.utils.gl.GlShader
import dev.binclub.fps.client.utils.use
import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.EventStageable.EventStage.PRE
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.render.*
import me.robeart.raion.client.imixin.IMinecraft
import me.robeart.raion.client.imixin.IRenderManager
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.minecraft.ESP.GlowShader
import me.robeart.raion.client.util.minecraft.ESP.OutlineShader
import me.robeart.raion.client.util.minecraft.ESP.OutlineUtils
import me.robeart.raion.client.util.minecraft.GLUtils
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import me.robeart.raion.client.util.minecraft.RenderUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.ColorValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityHanging
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM
import org.lwjgl.opengl.GL20.glUseProgram
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.awt.Color
import java.util.Arrays
import kotlin.math.sqrt

/**
 * @author Robeart
 * Editied by cats
 * Transfered to kotlin by cookiedragon
 */
object ESPModule  //private List footstepDataList = new CopyOnWriteArrayList();
	: Module(
	"ESP",
	"Renders something allowing you to see players easier",
	Category.RENDER
) {
	val mode: ListValue = ListValue("Mode", "CoolShader", Arrays.asList("Outline", "ShaderOutline", "ShaderGlow", "2D", "CoolShader"))
	val width2d by ValueDelegate(FloatValue("Width 2D", 2f, 1f, 4f, 0.5f, mode, "2D"))
	val width by ValueDelegate(FloatValue("Line Width", 2f, 1f, 6f, 0.5f, mode, "Outline"))
	val radiusoutline by ValueDelegate(FloatValue("Width", 1.5f, 0.1f, 3f, .1f, mode, "ShaderOutline"))
	val radiusglow by ValueDelegate(FloatValue("Radius", 2.5f, 0.1f, 5f, .1f, mode, "ShaderGlow"))
	val players by ValueDelegate(BooleanValue("Players", true))
	val monsters by ValueDelegate(BooleanValue("Monsters", true))
	val animals by ValueDelegate(BooleanValue("Animals", true))
	val vehicles by ValueDelegate(BooleanValue("Vehicles", true))
	val items by ValueDelegate(BooleanValue("Items", true))
	val others by ValueDelegate(BooleanValue("Others", true))
	val invisible by ValueDelegate(BooleanValue("Invisible", true))
	val sounds by ValueDelegate(BooleanValue("Sounds", true))
	val mouseOver = BooleanValue("Selection", true)
	val colormouseover by ValueDelegate(ColorValue("Color", Color.RED, mouseOver))
	val mouseoverwidth by ValueDelegate(FloatValue("Line Width", 2f, 1f, 6f, 0.5f, mouseOver))
	val color by ValueDelegate(ColorValue("ZColor", Color.BLACK))
	
	val soundCache = HashSet<SoundInfo>()
	var renderNameTags = true
	
	override fun onDisable() {
		synchronized(sounds) {
			soundCache.clear()
		}
	}
	
	@Listener
	private fun onRender3D(event: Render3DEvent) {
		if (mc.player == null || mc.world == null || mc.renderManager.options == null) return
		
		val viewerYaw = mc.renderManager.playerViewY
		val isThirdPersonFrontal = mc.renderManager.options.thirdPersonView == 2
		val viewerPitch = (if (isThirdPersonFrontal) -1 else 1) * mc.renderManager.playerViewX
		
		val player = mc.renderViewEntity ?: mc.player
		
		val playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks.toDouble()
		val playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks.toDouble()
		val playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks.toDouble()
		
		if (sounds && soundCache.isNotEmpty()) {
			synchronized(soundCache) {
				val remove = arrayListOf<SoundInfo>()
				
				RenderUtils.beginRender()
				GlStateManager.depthMask(false)
				GlStateManager.disableDepth()
				for (soundInfo in soundCache) {
					val (sound, time) = soundInfo
					val distance = (mc.renderViewEntity ?: mc.player).getDistance(sound.x, sound.y, sound.z)
					
					if (distance > 40) {
						remove.add(soundInfo)
						continue
					}
					
					val x = sound.x - playerX
					val y = sound.y - playerY
					val z = sound.z - playerZ
					val name = sound.sound.soundName.path
					val scale = 0.1 / sqrt(distance)
					val width: Float = Raion.fontRenderer.getStringWidth(name) + 4
					GlStateManager.pushMatrix()
					GlStateManager.translate(x, y, z)
					GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
					GlStateManager.rotate(-viewerYaw, 0.0f, 1.0f, 0.0f)
					GlStateManager.rotate(viewerPitch, 1.0f, 0.0f, 0.0f)
					GlStateManager.scale(-scale, -scale, scale)
					GLUtils.drawRect(-width / 2f, -3f, width, 10f, 0xFFF)
					Raion.fontRenderer.drawCenteredString(name, 0f, -3f, -1)
					
					if (System.currentTimeMillis() - time >= 2000) {
						remove.add(soundInfo)
					}
					GlStateManager.popMatrix()
				}
				GlStateManager.enableDepth()
				GlStateManager.depthMask(true)
				RenderUtils.endRender()
				
				soundCache.removeAll(remove)
			}
		}
		if (mouseOver.value && mc.objectMouseOver != null) {
			RenderUtils.drawSelectionBoundingBox(
				mc.objectMouseOver,
				mouseoverwidth, colormouseover.rgb)
		}
		if (players && mode.value.equals("2D", ignoreCase = true) && mc.world.loadedEntityList.isNotEmpty()) {
			for (e in mc.world.loadedEntityList) {
				if (e == mc.renderViewEntity) continue
				if (e == mc.player.ridingEntity) continue
				//if (e !is EntityPlayer) continue
				if (e !is EntityLivingBase) continue
				
				e.alwaysRenderNameTag = false
				
				RenderUtils.draw2DESP(e, width2d, color.rgb)
			}
		}
	}
	
	@Listener
	fun onRenderName(event: RenderNameEvent) {
		if (!renderNameTags) event.isCanceled = true
		if (players && event.entity is EntityLivingBase && mode.value.equals("2D", ignoreCase = true)) {
			event.isCanceled = true
		}
	}
	
	@Listener
	fun onRenderModel(event: RenderModelEvent) {
		val e = event.entitylivingbaseIn
		if (!shouldEsp(e) || !(mode.value.equals("Outline", ignoreCase = true) || mode.value.equals(
				"ShaderOutline",
				ignoreCase = true
			) || mode.value.equals("ShaderGlow", ignoreCase = true))
		) return
		val visible: Boolean = !e.isInvisible
		val semiVisible = !visible && !e.isInvisibleToPlayer(mc.player)
		if (semiVisible) {
			GlStateManager.pushMatrix()
			GlStateManager.color(1.0f, 1.0f, 1.0f, 0.15f)
			GlStateManager.depthMask(false)
			GlStateManager.enableBlend()
			GlStateManager.blendFunc(770, 771)
			GlStateManager.alphaFunc(516, 0.003921569f)
		}
		val mc = Minecraft.getMinecraft()
		val fancyGraphics = mc.gameSettings.fancyGraphics
		mc.gameSettings.fancyGraphics = false
		val gamma = mc.gameSettings.gammaSetting
		mc.gameSettings.gammaSetting = 100000f
		if (mode.value.equals("Outline", ignoreCase = true)) {
			MinecraftUtils.disableFastRender()
			GlStateManager.resetColor()
			OutlineUtils.setColor(color)
			OutlineUtils.renderOne(width)
			event.mainModel.render(
				e,
				event.limbSwing,
				event.limbSwingAmount,
				event.ageInTicks,
				event.netHeadYaw,
				event.headPitch,
				event.scaleFactor
			)
			OutlineUtils.setColor(color)
			OutlineUtils.renderTwo()
			event.mainModel.render(
				e,
				event.limbSwing,
				event.limbSwingAmount,
				event.ageInTicks,
				event.netHeadYaw,
				event.headPitch,
				event.scaleFactor
			)
			OutlineUtils.setColor(color)
			OutlineUtils.renderThree()
			event.mainModel.render(
				e,
				event.limbSwing,
				event.limbSwingAmount,
				event.ageInTicks,
				event.netHeadYaw,
				event.headPitch,
				event.scaleFactor
			)
			OutlineUtils.setColor(color)
			OutlineUtils.renderFour(color)
			event.mainModel.render(
				e,
				event.limbSwing,
				event.limbSwingAmount,
				event.ageInTicks,
				event.netHeadYaw,
				event.headPitch,
				event.scaleFactor
			)
			OutlineUtils.setColor(color)
			OutlineUtils.renderFive()
			OutlineUtils.setColor(Color.WHITE)
		}
		mc.gameSettings.fancyGraphics = fancyGraphics
		mc.gameSettings.gammaSetting = gamma
		event.mainModel.render(
			e,
			event.limbSwing,
			event.limbSwingAmount,
			event.ageInTicks,
			event.netHeadYaw,
			event.headPitch,
			event.scaleFactor
		)
		if (semiVisible) {
			GlStateManager.disableBlend()
			GlStateManager.alphaFunc(516, 0.1f)
			GlStateManager.popMatrix()
			GlStateManager.depthMask(true)
		}
		event.isCanceled = true
	}
	
	private var prevShader = 0
	private val coolShader = GlShader.createShader("cool")
	var shadows: Boolean = false
	var forceRendering = false
	
	@Listener
	private fun onEntityRender(event: RenderEntityEvent) {
		if (mode.value == "CoolShader") {
			event.isCanceled = this.state && !forceRendering
			return
			if (event.stage == PRE) {
				shadows = mc.gameSettings.entityShadows
				mc.gameSettings.entityShadows = false
				
				prevShader = glGetInteger(GL_CURRENT_PROGRAM)
				coolShader.bind()
				coolShader["tex"] = 0
				coolShader["depth"] = false
				coolShader["viewPos"] = mc.renderManager.let {
					val renderManager = mc.renderManager as IRenderManager
					Vec3d(event.x + renderManager.renderPosX, event.y + renderManager.renderPosY, event.z + renderManager.renderPosZ)
				}
				GlStateManager.disableBlend()
			} else {
				GlStateManager.enableBlend()
				glUseProgram(prevShader)
				
				mc.gameSettings.entityShadows = shadows
			}
		}
	}
	
	fun doCoolShader() {
		val renderManager = mc.renderManager as IRenderManager
		val renderPosX = renderManager.renderPosX
		val renderPosY = renderManager.renderPosY
		val renderPosZ = renderManager.renderPosZ
		
		renderNameTags = false
		shadows = mc.gameSettings.entityShadows
		mc.gameSettings.entityShadows = false
		prevShader = glGetInteger(GL_CURRENT_PROGRAM)
		coolShader.use {
			coolShader["tex"] = 0
			
			forceRendering = true
			GlStateManager.pushMatrix()
			GlStateManager.color(1f, 1f, 1f, 1f)
			
			// Render once without depth
			coolShader["depth"] = false
			GlStateManager.disableDepth()
			renderCoolShaderEntities(renderPosX, renderPosY, renderPosZ)
			
			// Overlay the previous render with depth tested model
			coolShader["depth"] = true
			GlStateManager.enableDepth()
			renderCoolShaderEntities(renderPosX, renderPosY, renderPosZ)
			
			GlStateManager.disableDepth()
			GlStateManager.popMatrix()
			forceRendering = false
		}
		glUseProgram(prevShader)
		mc.gameSettings.entityShadows = shadows
		renderNameTags = true
	}
	
	fun renderCoolShaderEntities(renderPosX: Double, renderPosY: Double, renderPosZ: Double) {
		val partialTicks = mc.renderPartialTicks.toDouble()
		for (e in mc.world.loadedEntityList) {
			if (!shouldEsp(e)) continue
			if (e.ticksExisted == 0) {
				e.lastTickPosX = e.posX
				e.lastTickPosY = e.posY
				e.lastTickPosZ = e.posZ
			}
			
			val d0 = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks
			val d1 = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks
			val d2 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks
			val f = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks
			var i = e.brightnessForRender
			
			if (e.isBurning) {
				i = 15728880
			}
			
			val j = i % 65536
			val k = i / 65536
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat(), k.toFloat())
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
			
			coolShader["viewPos"] = Vec3d(d0 + renderPosX, d1 + renderPosY, d2 + renderPosZ)
			
			mc.renderManager.renderEntity(e, d0 - renderPosX, d1 - renderPosY, d2 - renderPosZ, f.toFloat(), partialTicks.toFloat(), true)
		}
	}
	
	@Listener
	fun onRender(event: Render3DEvent) {
		if (mode.value == "CoolShader") {
			doCoolShader()
		}
	}
	
	@Listener
	private fun onRender2D(event: Render2DEvent) {
		val shader = (when (mode.value) {
			"ShaderOutline" -> OutlineShader.OUTLINE_SHADER
			"ShaderGlow" -> GlowShader.GLOW_SHADER
			else -> return
		})
		
		shader.startDraw(event.partialTicks)
		
		renderNameTags = false
		forceRendering = true
		for (e in mc.world.loadedEntityList) {
			if (!shouldEsp(e)) continue
			mc.renderManager.renderEntityStatic(e, (mc as IMinecraft).timer.renderPartialTicks, true)
		}
		forceRendering = false
		renderNameTags = true
		
		val radius: Float = when (mode.value) {
			"ShaderOutline" -> radiusoutline
			"ShaderGlow" -> radiusglow
			else -> 1f
		}
		shader.stopDraw(color, radius, 1f)
	}
	
	@Listener
	private fun onPacketReceive(event: PacketReceiveEvent) {
		if (sounds && event.packet is SPacketSoundEffect && event.stage == PRE) {
			synchronized(soundCache) {
				soundCache.add(SoundInfo(event.packet as SPacketSoundEffect, System.currentTimeMillis()))
			}
		}
	}
	
	private fun shouldEsp(e: Entity): Boolean {
		if (!e.isEntityAlive || e == mc.renderViewEntity || (!invisible && e.isInvisible)) return false
		return (players && e is EntityPlayer) || (monsters && MinecraftUtils.isMobAggressive(e))
		|| (animals && (MinecraftUtils.isPassive(e) || MinecraftUtils.isNeutralMob(e)))
		|| (vehicles && (e is EntityBoat || e is EntityMinecart)) || (items && e is EntityItem)
		|| (others && (e is EntityEnderCrystal || e is EntityArmorStand || e is EntityHanging))
	}
	
	data class SoundInfo(val sound: SPacketSoundEffect, val time: Long)
}

/*@Listener
private fun onRender2d(event: Render2DEvent) {
if (mc.player == null || mc.world == null || mc.renderManager.options == null) return

val player = mc.renderViewEntity ?: mc.player

val playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks.toDouble()
val playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks.toDouble()
val playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks.toDouble()

if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
    e = mc.objectMouseOver.entityHit
}
if (e != null) {
    val bounds = GLUtils.convertBounds(e!!, event.partialTicks, event.scaledResolution.scaledWidth, event.scaledResolution.scaledHeight)

    if (bounds != null) {
        println("- ${bounds.contentToString()}")
        GLUtils.drawRect(bounds[0], bounds[1], bounds[2], bounds[3], Color.BLACK.rgb)
    }

    //val box = FrustumUtils.getEntity2dBox(e!!, event.partialTicks, event.scaledResolution) ?: return
    //FrustumUtils.get2DFrom3D(pos.x, pos.y, pos.z)
    //GLUtils.drawRect(box.posX, box.posY , box.sizeX, box.sizeY, Color.BLACK.rgb)
    //println("--- $box")
}
}*/

/*GlStateManager.pushMatrix()
				RenderUtils.beginRender()
				GlStateManager.depthMask(false)
				GlStateManager.disableDepth()
				//GlStateManager.disableLighting()
				//GlStateManager.disableFog()
				//GlStateManager.disableTexture2D()
				//GL11.glEnable(GL11.GL_LINE_SMOOTH)
				//GlStateManager.glLineWidth(2f)
				GlStateManager.translate(pos.x, pos.y, pos.z)
				GlStateManager.rotate(-viewerYaw, 0.0f, 1.0f, 0.0f)
				//GlStateManager.rotate(viewerPitch, (if (isThirdPersonFrontal) -1 else 1).toFloat(), 0.0f, 0.0f)
				val bb = e.entityBoundingBox
				val width = (bb.maxX - bb.minX).toFloat() + ((bb.maxX - bb.minX).toFloat() * 0.6f)
				val x = -width / 2.0f
				val height = (bb.maxY - bb.minY).toFloat() + ((bb.maxY - bb.minY).toFloat() * 0.4f)
				val y = -((bb.maxY - bb.minY).toFloat() * 0.4f) / 2f
				//GLUtils.drawBorder(2f, x, y, x + width, y + height)
				//GLUtils.drawBorder(2f, 0f, 0f, 0f, -1f)
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
				//GL11.glDisable(GL11.GL_LINE_SMOOTH)
				RenderUtils.endRender()
				GlStateManager.popMatrix()*/

//val ts = Tessellator.getInstance()

/*kotlin.run {
    val vb = ts.buffer
    val red = 0f
    val green = 1f
    val blue = 0f
    val alpha = 1f

    vb.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR)
    vb.pos((-e.width).toDouble(), (e.height).toDouble(), 0.0).color(red, green, blue, alpha).endVertex()
    vb.pos((-e.width).toDouble(), 0.0, 0.0).color(red, green, blue, alpha).endVertex()
    vb.pos((e.width).toDouble(), 0.0, 0.0).color(red, green, blue, alpha).endVertex()
    vb.pos((e.width).toDouble(), (e.height).toDouble(), 0.0).color(red, green, blue, alpha).endVertex()
    ts.draw()
}

kotlin.run {
    val maxHealth = if (e is EntityPlayer) 20f else e.maxHealth

    val health = clamp(e.health / maxHealth, 0f, 1f) // health as a range from 0-1
    val rgb = MathUtils.getBlendedColor(health).rgb
    val (red, green, blue, alpha) = MathUtils.getRgba(rgb)

    val vb = ts.buffer
    vb.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR)
    vb.pos((e.width + 0.1).toDouble(), (e.height * health).toDouble(), 0.0).color(red, green, blue, alpha).endVertex()
    vb.pos((e.width + 0.1).toDouble(), 0.0, 0.0).color(red, green, blue, alpha).endVertex()
    ts.draw()

    GlStateManager.rotate(180f, 0f, 0f, 1f)
    //GlStateManager.rotate(180f, -1f, 0f, 0f)
    //GlStateManager.rotate(180f, 0f, 0f, -1f)

    //GlStateManager.rotate(90, 0f, 1f, 0f)
    //GlStateManager.scale(0.05, 0.05, 0.05)
    GlStateManager.enableTexture2D()
    val formattedName = e.displayName.formattedText
    val nWidth = mc.fontRenderer.getStringWidth(formattedName)
    val healthStr = ceil(e.health).toInt().toString()
    val hWidth = mc.fontRenderer.getStringWidth(healthStr)

    val totalWidth = hWidth + nWidth
    val scale = (e.width * 2)/(totalWidth + 1.5f)
    GlStateManager.scale(scale, scale, scale)
    val rScale = 1f/scale

    mc.fontRenderer.drawString(formattedName, -e.width * rScale, (-e.height * rScale) - mc.fontRenderer.FONT_HEIGHT, 0xffffff, false)
    mc.fontRenderer.drawString(healthStr, (e.width * rScale - hWidth), (-e.height * rScale) - mc.fontRenderer.FONT_HEIGHT, rgb, false)
}*/
