package me.cookiedragon234.falcon.loading;

import com.google.gson.JsonObject;
import me.cookiedragon234.falcon.NativeAccessor;
import me.cookiedragon234.falcon.antidump.CookieFuckery;
import me.cookiedragon234.falcon.authentication.AuthenticatorKt;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;

/**
 * Utility class for loading classes over http
 */
public class Loader {
	
	public static final int protocolVersion = 0;
	
	public static Object attachThread = null;

    private static final HashMap<String, byte[]> classes = new HashMap<>();
    public static JsonObject authPayload;

    public static InputStream downloadFile(URL url) throws IOException {
        HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent", "Raion Client Connect");
        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return httpConn.getInputStream();
        } else {
            throw new IllegalStateException("Server returned response code: " + responseCode);
        }
    }
	
	public static String toString(byte[] a, int length) {
		if (a == null)
			return "null";
		int iMax = length - 1;
		if (iMax == -1)
			return "[]";
		
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; ; i++) {
			b.append(a[i]);
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}

    /**
     * Download the given classes from the given url and load them into the resourceCache
     *
     * @throws IOException            Occurs if any of the above parameters were supplied incorrectly
     * @throws ClassNotFoundException Occurs if one of the provided classes were not found at the supplied url
     * @throws IllegalAccessException Occurs if it for some unknown reason can't acces the resourceCache
     */
    public static void loadClasses(Map<String, String> args) throws Throwable {
        try {
        	ClassLoader cl = Launch.classLoader;
            int i = 0;
            String oldAgent = "";
            Instant start = Instant.now();
            try {
	            NativeAccessor.println("Raion is loading...");
                i = 1;

                Field resourceCache = LaunchClassLoader.class.getDeclaredField("resourceCache");
                resourceCache.setAccessible(true);
                Map<String, byte[]> classCache = (Map<String, byte[]>) resourceCache.get(cl);
                
	            Field b = LaunchClassLoader.class.getDeclaredField("cachedClasses");
	            b.setAccessible(true);
	            Map<String, Class<?>> cachedClasses = (Map<String, Class<?>>) b.get(cl);
	            NativeAccessor.println(cachedClasses.entrySet());

                oldAgent = System.getProperty("http.agent");
                System.setProperty("http.agent", "Raion Client Connect");
                i = 2;

                i = 3;
                File dir = new File("raion");
                if (!dir.exists()) dir.mkdir();
                authPayload = AuthenticatorKt.getAuthenticationPayload(args);
                final URL classUrl = new URL("https://raionclient.com/api/request.php?data=" + URLEncoder.encode(authPayload.toString(), "UTF-8"));
                i = 4;
                
                if (false && (runningFromIntelliJ() || classCache.containsKey("me/robeart/raion/client/command/Command.class"))) {
                    //NativeAccessor.INSTANCE.b("from JNI", classes, Launch.classLoader, Collections.emptyMap());
                    i = 5;
                     NativeAccessor.println("Not loading due to running in debugging environment");
                    // We still need to peform the request just to update hwid/uuid
                    InputStream inStream = downloadFile(classUrl);
                    inStream.close();
                    i = 6;
                    return;
                }
                i = 7;
	
	            CookieFuckery.INSTANCE.printPID();
	            
                /*CookieFuckery.INSTANCE.shutdownListener();

                StructFucker.disassembleStruct();
                i = 8;
                i = 9;

                CookieFuckery.INSTANCE.checkForJavaAgents();
                i = 10;
                //CookieFuckery.INSTANCE.disableJavaAgents();
                i = 11;
                CookieFuckery.INSTANCE.setPackageNameFilter();
                i = 12;*/

                Map<String, byte[]> resources = new HashMap<>();
	
                try {
                    byte[] bytes = kotlin.io.ByteStreamsKt.readBytes(downloadFile(classUrl));
                    //NativeAccessor.decrypt(bytes);
                    ZipInputStream stream = new ZipInputStream(new ByteArrayInputStream(bytes));
                    ZipEntry zipEntry;
                    String[] ends = new String[]{
                            ".json",
                            ".txt",
                            ".ttf",
                            ".png",
                            ".jpg",
                            ".jpeg",
                            ".frag",
                            ".vert",
                            ".bfi",
                            ".shader"
                    };
	                //Launch.blackboard.putIfAbsent("fml.deobfuscatedEnvironment", Launch.classLoader.getClassBytes("net.minecraft.world.World") != null);
	                DeobfuscationTransformer transformer = new DeobfuscationTransformer();
                    while ((zipEntry = stream.getNextEntry()) != null) {
                        if (!zipEntry.isDirectory()) {
                            String name = zipEntry.getName();
                            if (name.endsWith(".class")) {
                            	byte[] classbytes = getBytesFromInputStream(stream);
	                            classbytes = transformer.transform(name, name, classbytes);
                                classes.put(toInternal(zipEntry.getName()), classbytes);
                            } else {
                                for (String end : ends) {
                                    if (name.endsWith(end)) {
                                        resources.put(zipEntry.getName(), getBytesFromInputStream(stream));
                                    }
                                }
                            }
                        }
                    }
                    stream.close();
	                NativeAccessor.println("9");

                    if (!resources.isEmpty()) {
                        File tempFile = File.createTempFile("raionResources", ".jar");
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        JarOutputStream jos = new JarOutputStream(fos);
                        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
                            jos.putNextEntry(new ZipEntry(entry.getKey()));
                            jos.write(entry.getValue());
                            jos.closeEntry();
                            classCache.put(entry.getKey(), entry.getValue());
                        }
                        try {
                            jos.close();
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        tempFile.deleteOnExit();

                        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                        method.setAccessible(true);
                        method.invoke(cl, tempFile.toURI().toURL());
                    }

                    if (classes.isEmpty()) {
                        if (bytes.length < 500) {
                            throw new IllegalStateException(new String(bytes));
                        } else {
                            throw new IllegalStateException("Server didnt provide classes for unknown reason");
                        }
                    }

                    if (!classes.containsKey("me.robeart.raion.LoadClient")) {
                         NativeAccessor.println("(Existing classes: " + classes.keySet() + ")");
                        throw new IllegalStateException("Some classes were excluded");
                    }

                    Launch.classLoader.clearNegativeEntries(classes.keySet());
                } catch (Throwable t) {
                	NativeAccessor.println("Error downloading classes");
                    t.printStackTrace();
                    throw t;
                }
                i = 13;

                /*Field transformersField = LaunchClassLoader.class.getDeclaredField("transformers");
                transformersField.setAccessible(true);
                List<IClassTransformer> transformers = (List<IClassTransformer>) transformersField.get(Launch.classLoader);
                i = 14;

                IClassTransformer deDummyTransformer = (name, transformedName, bytes) ->
                {
                    if (!name.contains("mixin") && classes.containsKey(name)) {
                        byte[] newBytes = classes.get(name);
                        if (newBytes == null) {
                            new IllegalStateException("Bytes were null???").printStackTrace();
                        } else {
                            return newBytes;
                        }
                    }
                    return bytes;
                };
                transformers.add(deDummyTransformer);
                transformersField.set(Launch.classLoader, new CustomTransformerList(transformers, deDummyTransformer));
                i = 15;*/
	            
	            Map<String, byte[]> internalClasses = new HashMap<>(classes.size());
	            for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
		            internalClasses.put(entry.getKey().replace('.', '/'), entry.getValue());
	            }
	            NativeAccessor.prepareTransformer("from JNI", internalClasses, cl, attachThread);

                for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                    String internal = entry.getKey();//toInternal(entry.getKey());
                    if (entry.getKey().contains("mixin")) {
                        classCache.put(internal.replace('/', '.'), entry.getValue());
                    } else {
                    	int requiredLength = entry.getValue().length;
                    	byte[] b1 = CookieFuckery.INSTANCE.createDummyClass(internal);
                    	byte[] b2;
                    	if (b1.length >= requiredLength) {
                    		b2 = b1;
	                    } else {
                    		//System.out.println("Enlarging " + b1.length + " to " + requiredLength);
		                    b2 = new byte[requiredLength];
		                    System.arraycopy(b1, 0, b2, 0, b1.length);
	                    }
                        classCache.put(internal.replace('/', '.'), b2);
                        //Launch.classLoader.addClassLoaderExclusion(entry.getKey().replace('/', '.'));
                        //classCache.put(internal.replace('/', '.'), entry.getValue());
                    }
                }

                CustomMixinServer customService = new CustomMixinServer();
                Class<?> mixinServiceClass = Class.forName("org.spongepowered.asm.service.MixinService");
                Method instanceField = mixinServiceClass.getDeclaredMethod("getInstance");
                instanceField.setAccessible(true);
                Object serviceInstance = instanceField.invoke(null);
                Field serviceField = mixinServiceClass.getDeclaredField("service");
                serviceField.setAccessible(true);
                //if (serviceField.get(serviceInstance) != null) {
                //	throw new IllegalStateException("Too late! " + serviceField.get(serviceInstance).getClass());
                //}
                serviceField.set(serviceInstance, customService);

                if (MixinService.getService() != customService) {
                    throw new IllegalStateException(MixinService.getService().getClass().toString());
                }

                // Redefine classwriter
				/*{
					ClassReader cr = new ClassReader("org.objectweb.asm.ClassWriter");
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
					ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
						@Override
						public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
							if (!name.equals("getCommonSuperClass")) {
								return super.visitMethod(access, name, desc, signature, exceptions);
							}
							return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
								@Override
								public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
									// INVOKEVIRTUAL java/lang/Class.getClassLoader ()Ljava/lang/ClassLoader;
									if (opcode != INVOKEVIRTUAL || !name.equals("getClassLoader")) {
										super.visitMethodInsn(opcode, owner, name, desc, itf);
									}
									super.visitInsn(POP);
									super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/launchwrapper/Launch", "classLoader", "Lnet/minecraft/launchwrapper/LaunchClassLoader;");
								}
							};
						}
					};
					cr.accept(cv, 0);
					redefinitions.put(ClassWriter.class, new byte[0]);
					classes.put("org/objectweb/asm/ClassWriter", cw.toByteArray());
				}*/
                //classes.put("org/objectweb/asm/ClassWriter", new byte[0]);

                //for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                //	Launch.classLoader.addClassLoaderExclusion(entry.getKey().replace('/', '.'));
                //}
            } catch (Throwable t) {
	            NativeAccessor.println("Error while loading raion");
                t.printStackTrace();
                Frame frame = new Frame();
                frame.setAlwaysOnTop(true);
                frame.setState(Frame.ICONIFIED);
	            CookieFuckery.INSTANCE.logIncident(0x817, t.getMessage());
                JOptionPane.showConfirmDialog(
                        frame,
                        "Exception while loading raion '" + t.getMessage() +
                                "'\nPlease send the contents of .minecraft/logs/latest.log to one of the developers\n" + i,
                        "Raion Exception",
                        OK_CANCEL_OPTION,
                        WARNING_MESSAGE
                );
                throw t;
            } finally {
                if (oldAgent == null) {
                    System.clearProperty("http.agent");
                } else {
                    System.setProperty("http.agent", oldAgent);
                }
	            NativeAccessor.println("Raion has finished loading (took " + Duration.between(start, Instant.now())
                        .toMillis() + "ms)");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            NativeAccessor.println("Something seriously wrong just happened...");
            CookieFuckery.INSTANCE.shutdownHard();
            throw t;
        }
    }

    private static void testTransformers() throws Throwable {
        byte[] bytes = CookieFuckery.INSTANCE.createDummyClass("cookiedragon/falcon/Test");
        Field field = LaunchClassLoader.class.getDeclaredField("transformers");
        field.setAccessible(true);
        List<IClassTransformer> transformers = (List<IClassTransformer>) field.get(Launch.classLoader);
        for (IClassTransformer transformer : transformers) {
            byte[] newBytes = transformer.transform("cookiedragon/falcon/Test", "cookiedragon/falcon/Test", bytes);
            if (newBytes == null) {
                throw new IllegalStateException("Invalid class transformer " + transformer.getClass());
            }
        }
         NativeAccessor.println("All transformers passed");
        //Launch.classLoader.addTransformerExclusion();
    }

    private static void removeFinal(Field field) throws Exception {
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    public static boolean runningFromIntelliJ() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }

    private static boolean areClassesAlreadyLoaded() {
        try {
            Class.forName("me.robeart.raion.LoadClient");
             NativeAccessor.println("Classes were already loaded");
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static String toInternal(String className) {
        if (className.endsWith(".class"))
            className = className.substring(0, className.length() - ".class".length());
        return className.replace('/', '.');
    }

    public static void fixKeyLength(Cipher cipher) {
        try {
            Field cryptoPerm = Cipher.class.getDeclaredField("cryptoPerm");
            cryptoPerm.setAccessible(true);
            Class<?> allPermClass = Class.forName("javax.crypto.CryptoAllPermission");
            Field allPermField = allPermClass.getDeclaredField("INSTANCE");
            allPermField.setAccessible(true);
            Object allPerm = allPermField.get(null);
            cryptoPerm.set(cipher, allPerm);
        } catch (Throwable t) {
            throw new RuntimeException("Error fixing key length for specific cipher", t);
        }
    }

    public static void fixKeyLength() {
        String errorString = "Failed manually overriding key-length permissions.";
        int newMaxKeyLength;
        try {
            if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256) {
                Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = c.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);

                c = Class.forName("javax.crypto.CryptoPermissions");
                con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = c.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);

                c = Class.forName("javax.crypto.JceSecurityManager");
                f = c.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);

                newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
            }
        } catch (Exception e) {
            throw new RuntimeException(errorString, e);
        }
        if (newMaxKeyLength < 256)
            throw new RuntimeException(errorString); // hack failed
    }

    private static class CustomMixinServer extends MixinServiceLaunchWrapper {
        @Override
        public byte[] getClassBytes(String name, String transformedName) throws IOException {
            byte[] raionBytes = classes.get(name);
            if (raionBytes != null) {
                return raionBytes;
            }
            return super.getClassBytes(name, transformedName);
        }

        @Override
        public byte[] getClassBytes(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
            byte[] raionBytes = classes.get(name);
            if (raionBytes != null) {
                return raionBytes;
            }
            return super.getClassBytes(name, runTransformers);
        }
    }
}
