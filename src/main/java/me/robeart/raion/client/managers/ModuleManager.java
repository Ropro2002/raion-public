package me.robeart.raion.client.managers;

import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.module.combat.*;
import me.robeart.raion.client.module.combat.cookiecaura.CookieCrystalAura;
import me.robeart.raion.client.module.exploit.*;
import me.robeart.raion.client.module.misc.*;
import me.robeart.raion.client.module.movement.*;
import me.robeart.raion.client.module.player.*;
import me.robeart.raion.client.module.render.*;
import me.robeart.raion.client.module.render.search.SearchModule;
import me.robeart.raion.client.value.Value;
import me.robeart.raion.client.value.kotlin.ValueDelegate;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class ModuleManager {
	
	public static long debugNum;
	public static CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
	private final List<Module> moduleList = new ArrayList<>();
	public Thread moduleLogicThread = null;
	public Thread minecraftThread = null;
	
	public ModuleManager() {
		add(ViewportModule.INSTANCE);
		add(InstantBreakCrystal.INSTANCE);
		add(NoFallModule.INSTANCE);
		add(AutoMountModule.INSTANCE);
		add(AntiAfkModule.INSTANCE);
		add(BetterTabModule.INSTANCE);
		add(ESPModule.INSTANCE);
		add(VisionModule.INSTANCE);
		add(LiquidInteractModule.INSTANCE);
		add(NoCompressionKick.INSTANCE);
		add(StrafeSpeedModule.INSTANCE);
		add(TracersModule.INSTANCE);
		add(SearchModule.INSTANCE);
		add(SpeedModule.INSTANCE);
		add(FreecamModule.INSTANCE);
		add(new Auto32kModule());
		add(new AutoArmorModule());
		add(new AutoDisconnectModule());
		add(new AutoFishModule());
		add(new AutoRespawnModule());
		add(new AutoToolModule());
		add(AutoTotemModule.INSTANCE);
		add(new AutoTrapModule());
		add(new AutoWalkModule());
		add(BlinkModule.INSTANCE);
		add(new BowSpamModule());
		add(new CapeModule());
		add(new ChatAppendModule());
		add(new ChatTweaksModule());
		add(ClickGuiModule.INSTANCE);
		add(new CrasherModule());
		add(CriticalsModule.INSTANCE);
		add(new CrystalAuraModule());
		add(new DirectionModule());
		add(new DiscordRPCModule());
		add(new ElytraFlightModule());
		add(new EnderBackpackModule());
		add(new EntityControlModule());
		add(new ExperimentalModule());
		add(new FastPlaceModule());
		add(new EntityDesyncModule());
		add(new HandSlotRefillModule());
		add(new HoleESPModule());
		add(new HoleFillerModule());
		add(new HudModule());
		add(new InventoryMoveModule());
		add(new JesusModule());
		add(new KillAuraModule());
		add(new LowOffHandModule());
		add(new MiddleClickFriendsModule());
		//add(new NametagsModule());
		add(new NetHandlerModule());
		add(new NewChunksModule());
		add(new NoBossBarModule());
		add(new NoHungerModule());
		add(NoRenderModule.INSTANCE);
		add(new NoRotateModule());
		add(NoSlowDownModule.INSTANCE);
		add(new NotificationsModule());
		add(new PacketFlyModule());
		add(BetterPortalsModule.INSTANCE);
		add(new PortalGodModeModule());
		add(new SafewalkModule());
		//add(new ScaffoldModule());
		add(new SecretCloseModule());
		add(new SprintModule());
		add(new StepModule());
		add(StorageEspModule.INSTANCE);
		add(new SurroundModule());
		add(new TimerModule());
		add(VelocityModule.INSTANCE);
		add(new ViewLinesModule());
		add(new XCarryModule());
		add(new XrayModule());
		add(BlockBreakHighlightModule.INSTANCE);
		add(InteractionTweaksModule.INSTANCE);
		add(NametagsModule.INSTANCE);
		add(YawLockModule.INSTANCE);
		add(MiddleClickIgnore.INSTANCE);
		//add(CookieCrystalAura.INSTANCE);
		add(CrystalAura2.INSTANCE);
		add(CuiModule.INSTANCE);
		add(TrajectoriesModule.INSTANCE);
		add(LazyItemSwitch.INSTANCE);
		
        moduleList.sort(Comparator.comparing(Module::getName));
	
		minecraftThread = Thread.currentThread();
		System.out.println("MC Thread: " + minecraftThread.getName());
		moduleLogicThread = new Thread(this::raionModuleLogicThread, "Raion Module Logic Thread");
		moduleLogicThread.setDaemon(true);
		moduleLogicThread.start();
	}
	
	public void add(Module module) {
		try {
			Set<Value> values = new HashSet<>();
			for (Field field : module.getClass().getDeclaredFields()) {
				if (Value.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					Value value = (Value) field.get(module);
					values.add(value);
				}
				else if (ValueDelegate.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					Value value = ((ValueDelegate) field.get(module)).getValue();
					values.add(value);
				}
			}
			module.getValues().addAll(values);
			module.getValues().sort(Comparator.comparing(Value::getName));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		this.moduleList.add(module);
	}
	
	public Module getModule(String name) {
		name = name.replace(" ", "");
		for (Module module : moduleList) {
			if (module.getName().replace(" ", "").equalsIgnoreCase(name)) return module;
			if (module.getAlias() != null) {
				for (String alias : module.getAlias()) {
					if (alias.replace(" ", "").equalsIgnoreCase(name)) return module;
				}
			}
		}
		return null;
	}
	
	public Module getModule(Class clazz) {
		for (Module module : moduleList) {
			if (module.getClass() == clazz) return module;
		}
		return null;
	}
	
	public <T extends Module> T getModuleGeneric(Class<T> clazz) {
		for (Module module : moduleList) {
			if (module.getClass() == clazz) return (T) module;
		}
		return null;
	}
	
	public List<Module> getModuleList() {
		return this.moduleList;
	}
	
	private void raionModuleLogicThread() {
		while (true) {
			try {
				boolean debug = false;//System.currentTimeMillis() % 1000 == 0;
				if (debug) {
					System.out.println("Doing ");
				}
				if (Minecraft.getMinecraft().player == null) {
					if (debug) {
						System.out.println("Skipping cause player null");
					}
					Thread.yield();
					continue;
				}
				
				if (debug) {
					System.out.println("Running...");
				}
				
				for (Module module : moduleList) {
					if (module.getState()) {
						try {
							module.moduleLogic();
						}
						catch (Throwable t) {
							System.out.println("Exception while invoking module logic for module [" + module.getName() + "]");
							t.printStackTrace();
						}
					}
				}
				try {
					cyclicBarrier.await();
				}
				catch (Throwable t) {
				}
				cyclicBarrier.reset();
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
