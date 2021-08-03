package me.robeart.raion.client.events.events.render;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author Robeart
 */
public class RenderModelEvent extends EventCancellable {
	
	private EntityLivingBase entitylivingbaseIn;
	private float limbSwing;
	private float limbSwingAmount;
	private float ageInTicks;
	private float netHeadYaw;
	private float headPitch;
	private float scaleFactor;
	private ModelBase mainModel;
	
	public RenderModelEvent(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, ModelBase mainModel) {
		this.entitylivingbaseIn = entitylivingbaseIn;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.ageInTicks = ageInTicks;
		this.netHeadYaw = netHeadYaw;
		this.headPitch = headPitch;
		this.scaleFactor = scaleFactor;
		this.mainModel = mainModel;
	}
	
	public ModelBase getMainModel() {
		return mainModel;
	}
	
	public void setMainModel(ModelBase mainModel) {
		this.mainModel = mainModel;
	}
	
	public float getAgeInTicks() {
		return ageInTicks;
	}
	
	public void setAgeInTicks(float ageInTicks) {
		this.ageInTicks = ageInTicks;
	}
	
	public float getHeadPitch() {
		return headPitch;
	}
	
	public void setHeadPitch(float headPitch) {
		this.headPitch = headPitch;
	}
	
	public float getLimbSwing() {
		return limbSwing;
	}
	
	public void setLimbSwing(float limbSwing) {
		this.limbSwing = limbSwing;
	}
	
	public float getLimbSwingAmount() {
		return limbSwingAmount;
	}
	
	public void setLimbSwingAmount(float limbSwingAmount) {
		this.limbSwingAmount = limbSwingAmount;
	}
	
	public float getNetHeadYaw() {
		return netHeadYaw;
	}
	
	public void setNetHeadYaw(float netHeadYaw) {
		this.netHeadYaw = netHeadYaw;
	}
	
	public float getScaleFactor() {
		return scaleFactor;
	}
	
	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	
	public EntityLivingBase getEntitylivingbaseIn() {
		return entitylivingbaseIn;
	}
	
	public void setEntitylivingbaseIn(EntityLivingBase entitylivingbaseIn) {
		this.entitylivingbaseIn = entitylivingbaseIn;
	}
}
