package me.robeart.raion.client;

import kotlin.io.ByteStreamsKt;
import me.robeart.raion.client.events.ForgeEventProcessor;
import me.robeart.raion.client.gui.clickgui.ClickGui;
import me.robeart.raion.client.gui.cui.RaionCui;
import me.robeart.raion.client.managers.*;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import team.stiff.pomelo.impl.annotated.AnnotatedEventManager;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public final class Raion {
	public static final String VERSION = "0.8";
	public static final Raion INSTANCE = new Raion();
	public static int tick = 0;
	public static String VERSION_INFO = "UNKNOWN"; // Normally the git commit hash
	public static String LOCAL_VERSION_INFO = "UNKNOWN";
	public static MinecraftFontRenderer fontRenderer;
	private Font font;
	private FileManager fileManager;
	private ClickGui clickGui;
	private PopupManager popupManager;
	private CommandManager commandManager;
	private ModuleManager moduleManager;
	private FriendManager friendManager;
	private ConfigManager configManager;
	private ConnectionManager connectionManager;
	private MacroManager macroManager;
	private AnnotatedEventManager eventManager = new AnnotatedEventManager();
	
	private static Hashtable<String, String> loadCredentials() throws IOException {
		FileInputStream fos = new FileInputStream("raion/credentials.xml");
		XMLDecoder e = new XMLDecoder(fos);
		Hashtable<String, String> credentials = (Hashtable<String, String>) e.readObject();
		e.close();
		return credentials;
	}
	
	private static class ClientInitStep {
		public final Runnable runnable;
		public final String name;
		public ClientInitStep(String name, Runnable runnable) {
			this.runnable = runnable;
			this.name = name;
		}
	}
	
	public void initClient() {
		Display.setTitle("Raion");
		
		ClientInitStep[] initSteps = new ClientInitStep[]{
			new ClientInitStep("Files", () -> this.fileManager = new FileManager()),
			new ClientInitStep("Fonts", this::createFont),
			new ClientInitStep("Events", () -> this.eventManager = new AnnotatedEventManager()),
			new ClientInitStep("Modules", () -> this.moduleManager = new ModuleManager()),
			new ClientInitStep("Gui", () -> this.clickGui = new ClickGui()),
			new ClientInitStep("Commands", () -> this.commandManager = new CommandManager()),
			new ClientInitStep("Friends", () -> this.friendManager = new FriendManager()),
			new ClientInitStep("Macros", () -> this.macroManager = MacroManager.INSTANCE),
			new ClientInitStep("CUI", () -> {RaionCui cui = RaionCui.INSTANCE;}),
			new ClientInitStep("Popup Manager", () -> this.popupManager = new PopupManager()),
			new ClientInitStep("Configs", () -> this.configManager = new ConfigManager()),
			new ClientInitStep("Font Renderer", () -> Raion.fontRenderer = Fonts.INSTANCE.getFont20()),
			new ClientInitStep("Forge Events", () -> MinecraftForge.EVENT_BUS.register(new ForgeEventProcessor())),
			new ClientInitStep("Loading Configs", () -> configManager.loadAll()),
			new ClientInitStep("Get Version", this::parseVersionInfo)
		};
		ProgressManager.ProgressBar pb = ProgressManager.push("Init Raion", initSteps.length);
		for (ClientInitStep step : initSteps) {
			pb.step(step.name);
			step.runnable.run();
		}
		ProgressManager.pop(pb);
		
		this.connectionManager = new ConnectionManager();
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
			Raion.INSTANCE.getConfigManager().saveAll()));
    }
    
    private void parseVersionInfo() {
		try (InputStream is = Launch.classLoader.getResourceAsStream("raion/version.txt")) {
			VERSION_INFO = "public " + new String(ByteStreamsKt.readBytes(is));
		}
		catch (Throwable t) {
			if (!t.getMessage().startsWith("Parameter specified as non-null is null")) {
				t.printStackTrace();
			}
			try {
				ProcessBuilder builder = new ProcessBuilder().command("git", "rev-parse", "--short", "HEAD");
				String output = IOUtils.toString(builder.start().getInputStream(), StandardCharsets.UTF_8);
				VERSION_INFO = "dev " + output;
			}
			catch (Throwable t2) {
				t2.printStackTrace();
				VERSION_INFO = "unknown";
			}
		}
		try (InputStream is = Launch.classLoader.getResourceAsStream("raion/loaderversion.txt")) {
			LOCAL_VERSION_INFO = "loader " + new String(ByteStreamsKt.readBytes(is));
		}
		catch (Throwable t) {
			if (!t.getMessage().startsWith("Parameter specified as non-null is null")) {
				t.printStackTrace();
			}
			try {
				ProcessBuilder builder = new ProcessBuilder().command("git", "rev-parse", "--short", "HEAD");
				String output = IOUtils.toString(builder.start().getInputStream(), StandardCharsets.UTF_8);
				LOCAL_VERSION_INFO = "loader " + output;
			}
			catch (Throwable t2) {
				t2.printStackTrace();
				LOCAL_VERSION_INFO = "unknown";
			}
		}
	}

    public Font getFont() {
        return this.font;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public ClickGui getGui() {
        return this.clickGui;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public PopupManager getPopupManager() {
        return this.popupManager;
    }

    public AnnotatedEventManager getEventManager() {
        return this.eventManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public MacroManager getMacroManager() {
        return this.macroManager;
    }

    public FileManager getFileManager() { return fileManager; }

    private void createFont() {
        try {
            downloadFonts();
            final InputStream inputStream = new FileInputStream(new File(fileManager.getDir(), "font.ttf"));
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, 18f);
            inputStream.close();
            this.font = awtClientFont;
        } catch (Exception e) {
            e.printStackTrace();
            this.font = new Font("Verdana", Font.PLAIN, 9);
        }
    }

    private void downloadFonts() {
        try {
            final File outputFile = new File(fileManager.getDir(), "font.ttf");
            if(outputFile.exists()) return;
            if(!outputFile.exists()) {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://raionclient.com/font/font.ttf").openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                connection.setInstanceFollowRedirects(true);
                connection.setDoOutput(true);
                FileUtils.copyInputStreamToFile(connection.getInputStream(), outputFile);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

}
