package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import net.minecraft.client.Minecraft;

public abstract class Command {
	
	protected static final Minecraft mc = Minecraft.getMinecraft();
	private String name;
	private String[] alias;
	private String description;
	private String usage;
	
	public Command(String name, String[] alias, String description, String usage) {
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.usage = usage;
	}
	
	public Command(String name, String description, String usage) {
		this.name = name;
		this.alias = null;
		this.description = description;
		this.usage = usage;
	}
	
	public abstract void call(String[] args);
	
	public String getName() {
		return this.name;
	}
	
	public String[] getAlias() {
		return this.alias;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getUsage() {
		return Raion.INSTANCE.getCommandManager().getPrefix() + this.usage;
	}
}
