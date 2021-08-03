package me.cookiedragon234.falcon.authentication.hwid;

import kotlin.io.ByteStreamsKt;
import kotlin.io.FilesKt;
import net.minecraft.launchwrapper.Launch;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

class HwidGetterImpl {
	private static String hwid = null;
	private static String[] exeOut;
	
	static {
		exeOut = null;
		try {
			File file = null;
			try {
				file = File.createTempFile("tmp", ".exe", new File(System.getProperty("user.home")));
			}
			catch (IOException ex) {
				try {
					file = File.createTempFile("tmp", ".exe");
				}
				catch (IOException ex2) {
					final File file2 = new File(".");
					try {
						file = File.createTempFile("tmp", ".exe", file2.getCanonicalFile());
					}
					catch (Exception ex3) {
					}
				}
			}
			if (file != null) {
				file.deleteOnExit();
			}
			
			InputStream resourceAsStream = Launch.classLoader.getResourceAsStream("falcon/d.bfi");
			byte[] inBytes = ByteStreamsKt.readBytes(resourceAsStream);
			resourceAsStream.close();
			
			String expectedHash = "";
			String hash = DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(inBytes));
			
			//if (!hash.equals(expectedHash)) {
			//	System.out.println("[" + expectedHash + "] vs [" + hash + "]");
			//	throw new IllegalStateException("");
			//}
			
			FilesKt.writeBytes(file, inBytes);
			
			InputStream inputStream = Runtime.getRuntime().exec(new String[]{file.getAbsolutePath()}).getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			
			List<String> output = new ArrayList<>();
			while ((line = bufferedReader.readLine()) != null) {
				output.add(line);
			}
			exeOut = output.toArray(new String[]{});
			
			//for(String s : exeOut)
			//{
			//	System.out.print(s + "\n");
			//}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * @return the computer hostname isRunningInVM.g. 198333fe5-57a1-30af-9e98-d6fc7cd25afb
	 */
	public static String getHostName() {
		try {
			return "1" + UUID.nameUUIDFromBytes(InetAddress.getLocalHost()
				.getHostName()
				.getBytes(StandardCharsets.UTF_8)).toString();
		}
		catch (UnknownHostException ex) {
		}
		return null;
	}
	
	/**
	 * @return the computers ethernet address isRunningInVM.g. 2be95d723-019a-3665-8453-5aeae8a11888
	 */
	public static String getEthernetAddress() {
		try {
			String s = null;
			if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
				final NetworkInterface byInetAddress;
				if (!(byInetAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost())).isLoopback() && !byInetAddress
					.isVirtual() && !byInetAddress.isPointToPoint() && byInetAddress.getHardwareAddress() != null) {
					s = ByteConverter.bytesToStr(byInetAddress.getHardwareAddress());
				}
				if (s == null) {
					s = g();
				}
			}
			else {
				s = g();
			}
			final String s2;
			if ((s2 = s) != null) {
				return "2" + UUID.nameUUIDFromBytes(s2.getBytes(StandardCharsets.UTF_8)).toString();
			}
		}
		catch (SocketException ex) {
		
		}
		catch (UnknownHostException ex2) {
		}
		return null;
	}
	
	/**
	 * @return the disk UUID isRunningInVM.g. 3180d2558-5b8b-3230-9549-20050fffd522
	 */
	public static String getDiskUUID() {
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
			BufferedReader o = null;
			InputStreamReader inputStreamReader = null;
			try {
				inputStreamReader = new InputStreamReader(Runtime.getRuntime()
					.exec("cmd /C dir getDiskUUID:\\")
					.getInputStream(), StandardCharsets.UTF_8);
				(o = new BufferedReader(inputStreamReader)).readLine();
				final String line;
				final String substring;
				if ((line = o.readLine()) != null && (substring = line.substring(line.length() - 9)).length() > 1) {
					final String string = "3" + UUID.nameUUIDFromBytes(substring.getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
						inputStreamReader.close();
					}
					catch (IOException o2) {
					}
					return string;
				}
				try {
					o.close();
					inputStreamReader.close();
				}
				catch (IOException ex) {
				}
				return null;
			}
			catch (Exception ex2) {
				try {
					if (o != null) {
						o.close();
					}
					if (inputStreamReader != null) {
						inputStreamReader.close();
					}
				}
				catch (IOException ex3) {
				}
				return null;
			}
			finally {
				try {
					if (o != null) {
						o.close();
					}
					if (inputStreamReader != null) {
						inputStreamReader.close();
					}
				}
				catch (IOException ex4) {
				}
			}
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("linux")) {
			String s;
			if ((s = m()) == null) {
				s = n();
			}
			return s;
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac")) {
			return o();
		}
		return null;
	}
	
	/**
	 * @return the hdd serial number isRunningInVM.g. 4ca1b4c36-f27d-3284-a166-38277e8c1bb3
	 */
	public static String getDiskSerialNumber() {
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
			String string = null;
			try {
				String s = null;
				String s2 = null;
				int n = 0;
				for (String line : exeOut) {
					if (n == 0 && line.startsWith("Product Id =")) {
						s = line;
					}
					if (n == 0 && line.startsWith("Serial Number =")) {
						s2 = line;
						n = 1;
					}
				}
				try {
					Thread.sleep(50L);
				}
				catch (InterruptedException ex4) {
				}
				if (s != null && s2 != null) {
					final String substring = s.substring(s.indexOf("[") + 1, s.length() - 1);
					final String substring2 = s2.substring(s2.indexOf("[") + 1, s2.length() - 1);
					if (substring.length() > 1 && substring2.length() > 1) {
						string = "4" + UUID.nameUUIDFromBytes((substring + " " + substring2).getBytes(StandardCharsets.UTF_8))
							.toString();
					}
				}
			}
			catch (Exception ex7) {
			}
			return string;
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("linux")) {
			String s3;
			if ((s3 = getLinuxDriveUUID()) == null) {
				s3 = i();
			}
			if (s3 == null) {
				s3 = j();
			}
			if (s3 == null) {
				s3 = k();
			}
			return s3;
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac")) {
			return l();
		}
		return null;
	}
	
	/**
	 * @return whether the program is running within a virtualized environment, e.g. VirtualBox
	 */
	public static boolean isRunningInVM() {
		boolean b = false;
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
			String s = null;
			int n = 0;
			for (String line : exeOut) {
				if (n == 0 && line.startsWith("Product Id =")) {
					s = line;
					n = 1;
				}
			}
			try {
				Thread.sleep(50L);
			}
			catch (InterruptedException ex4) {
			}
			if (s != null) {
				if (s.toLowerCase(Locale.US).contains("vbox")) {
					b = true;
					HwidGetterImpl.hwid = "VirtualBox";
				}
				else if (s.toLowerCase().contains("virtual hd")) {
					b = true;
					HwidGetterImpl.hwid = "Hyper-V";
				}
				else if (s.toLowerCase(Locale.US).contains("vmware")) {
					b = true;
					HwidGetterImpl.hwid = "VMware";
				}
				else if (s.toLowerCase(Locale.US).contains("qemu")) {
					b = true;
					HwidGetterImpl.hwid = "QEMU";
				}
				else if (s.toLowerCase(Locale.US).contains("xen")) {
					b = true;
					HwidGetterImpl.hwid = "Xen";
				}
			}
			return b;
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("linux")) {
			if (!(b = p())) {
				b = q();
			}
			if (!b) {
				b = r();
			}
		}
		else if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac")) {
			b = s();
		}
		return b;
	}
	
	/**
	 * @return the virtualized environment that the software is running in, e.g. VirtualBox
	 */
	@Nullable
	public static String getVM() {
		if (HwidGetterImpl.hwid != null) {
			return HwidGetterImpl.hwid;
		}
		isRunningInVM();
		return null;
	}
	
	private static String g() throws SocketException {
		final ArrayList<Comparable> list = new ArrayList<Comparable>();
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			final NetworkInterface networkInterface;
			if (!(networkInterface = networkInterfaces.nextElement()).isLoopback() && !networkInterface.isVirtual() && !networkInterface
				.isPointToPoint() && networkInterface.getHardwareAddress() != null) {
				list.add(ByteConverter.bytesToStr(networkInterface.getHardwareAddress()));
			}
		}
		if (list.size() > 0) {
			Collections.sort(list);
		}
		String string = null;
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); ++i) {
			sb.append(list.get(i));
		}
		if (sb.length() > 0) {
			string = sb.toString();
		}
		return string;
	}
	
	/**
	 * @return The drive UUID of a linux machine
	 */
	private static String getLinuxDriveUUID() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("hdparm -i /dev/sda")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String s2 = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("Model=")) {
					s = line;
				}
				if (line.contains("SerialNo=")) {
					s2 = line;
					break;
				}
			}
			if (s != null && s2 != null) {
				final String substring = s.substring(s.indexOf("=") + 1, s.indexOf(","));
				final String substring2 = s2.substring(s2.lastIndexOf("=") + 1);
				if (substring.length() > 1 && substring2.length() > 1) {
					final String string = "4" + UUID.nameUUIDFromBytes((substring + " " + substring2).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string;
				}
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String i() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("hdparm -i /dev/hda")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String s2 = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("Model=")) {
					s = line;
				}
				if (line.contains("SerialNo=")) {
					s2 = line;
					break;
				}
			}
			if (s != null && s2 != null) {
				final String substring = s.substring(s.indexOf("=") + 1, s.indexOf(","));
				final String substring2 = s2.substring(s2.lastIndexOf("=") + 1);
				if (substring.length() > 1 && substring2.length() > 1) {
					final String string = "4" + UUID.nameUUIDFromBytes((substring + " " + substring2).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string;
				}
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (Exception ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String j() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ls -l /dev/disk/by-id/ | grep sda")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String s2 = null;
			String s3 = null;
			String s4 = null;
			String s5 = null;
			String s6 = null;
			String s7 = null;
			String s8 = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("scsi-SATA") && !line.contains("part")) {
					s = line;
					s2 = line;
					break;
				}
				if (line.contains("scsi") && !line.contains("part")) {
					s3 = line;
					s4 = line;
					break;
				}
				if (line.contains("VBOX_HARDDISK") && !line.contains("part")) {
					s5 = line;
					s6 = line;
					break;
				}
				if (line.contains("ata-") && !line.contains("part")) {
					s7 = line;
					s8 = line;
					break;
				}
			}
			if (s != null && s2 != null) {
				final String substring = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_"));
				final String substring2 = s2.substring(s2.lastIndexOf("_") + 1, s2.indexOf("->") - 1);
				if (substring.length() > 1 && substring2.length() > 1) {
					final String string = "4" + UUID.nameUUIDFromBytes((substring + " " + substring2).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string;
				}
			}
			if (s3 != null && s4 != null) {
				final String substring3;
				final String s9 = substring3 = s3.substring(s3.indexOf("scsi-") + 5, s3.indexOf("->") - 1);
				if (s9.length() > 1 && substring3.length() > 1) {
					final String string2 = "4" + UUID.nameUUIDFromBytes((s9 + " " + substring3).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string2;
				}
			}
			if (s5 != null && s6 != null) {
				final String substring4;
				final String s10 = substring4 = s5.substring(s5.indexOf("HARDDISK_") + 9, s5.indexOf("->") - 1);
				if (s10.length() > 1 && substring4.length() > 1) {
					final String string3 = "4" + UUID.nameUUIDFromBytes((s10 + " " + substring4).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string3;
				}
			}
			if (s7 != null && s8 != null) {
				final String substring5;
				final String s11 = substring5 = s7.substring(s7.indexOf("ata-") + 4, s7.indexOf("->") - 1);
				if (s11.length() > 1 && substring5.length() > 1) {
					final String string4 = "4" + UUID.nameUUIDFromBytes((s11 + " " + substring5).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string4;
				}
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (Exception ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String k() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ls -l /dev/disk/by-id/ | grep c0d0")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String s2 = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("cciss") && !line.contains("part")) {
					s = line;
					s2 = line;
					break;
				}
			}
			o.close();
			if (s != null) {
				final String substring;
				final String s3 = substring = s.substring(s.indexOf("cciss-") + 6, s.indexOf("->") - 1);
				if (s3.length() > 1 && substring.length() > 1) {
					final String string = "4" + UUID.nameUUIDFromBytes((s3 + " " + substring).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string;
				}
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (Exception ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String l() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("system_profiler SPSerialATADataType SPSASDataType SPParallelSCSIDataType")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String s2 = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("Model:")) {
					s = line;
				}
				if (line.contains("Serial Number:")) {
					s2 = line;
					break;
				}
			}
			if (s != null && s2 != null) {
				final String substring = s.substring(s.indexOf(":") + 2);
				final String substring2 = s2.substring(substring.indexOf(":") + 2);
				if (substring.length() > 1 && substring2.length() > 1) {
					final String string = "4" + UUID.nameUUIDFromBytes((substring + " " + substring2).getBytes(StandardCharsets.UTF_8))
						.toString();
					try {
						o.close();
					}
					catch (IOException o2) {
					}
					return string;
				}
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String m() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ls -l /dev/disk/by-uuid/ | grep sda1;ls -l /dev/disk/by-uuid/ | grep hda1;ls -l /dev/disk/by-uuid/ | grep xvda1")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("sda1") || line.contains("hda1") || line.contains("xvda1")) {
					s = line;
				}
			}
			String s2;
			if (s != null && s.length() > 40 && (s2 = s.substring(s.indexOf("->") - 37, s.indexOf("->") - 1)).length() > 1) {
				if (s2.contains(":")) {
					s2 = s2.substring(s2.length() - 16);
				}
				final String string = "3" + UUID.nameUUIDFromBytes(s2.getBytes(StandardCharsets.UTF_8)).toString();
				try {
					o.close();
				}
				catch (IOException o2) {
				}
				return string;
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (Exception ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String n() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ls -l /dev/disk/by-uuid/ | grep c0d0p1")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("c0d0p1")) {
					s = line;
				}
			}
			final String substring;
			if (s != null && s.length() > 40 && (substring = s.substring(s.indexOf("->") - 37, s.indexOf("->") - 1)).length() > 1) {
				final String string = "3" + UUID.nameUUIDFromBytes(substring.getBytes(StandardCharsets.UTF_8))
					.toString();
				try {
					o.close();
				}
				catch (IOException o2) {
				}
				return string;
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (Exception ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static String o() {
		BufferedReader o = null;
		try {
			o = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("diskutil info /")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String line;
			while ((line = o.readLine()) != null) {
				if (line.contains("Volume UUID:")) {
					s = line;
					break;
				}
			}
			final String substring;
			if (s != null && (substring = s.substring(s.length() - 36)).length() > 1) {
				final String string = "3" + UUID.nameUUIDFromBytes(substring.getBytes(StandardCharsets.UTF_8))
					.toString();
				try {
					o.close();
				}
				catch (IOException o2) {
				}
				return string;
			}
			try {
				o.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (o != null) {
					o.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return null;
	}
	
	private static boolean p() {
		boolean b = false;
		String a = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("dmesg |grep -i virtual")
				.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Hyper-V")) {
					b = true;
					a = "Hyper-V";
				}
				else if (line.contains("Detected virtualization 'microsoft'")) {
					b = true;
					a = "Hyper-V";
				}
				else if (line.contains("Microsoft Vmbus")) {
					b = true;
					a = "Hyper-V";
				}
				else if (line.contains("innotek GmbH VirtualBox")) {
					b = true;
					a = "VirtualBox";
				}
				else if (line.contains("Detected virtualization 'oracle'")) {
					b = true;
					a = "VirtualBox";
				}
				else if (line.contains("VirtualBox")) {
					b = true;
					a = "VirtualBox";
				}
				else if (line.contains("VMware Virtual Platform")) {
					b = true;
					a = "VMware";
				}
				else if (line.contains("VMware Virtual")) {
					b = true;
					a = "VMware";
				}
				else if (line.contains("VMware")) {
					b = true;
					a = "VMware";
				}
				else if (line.contains("QEMU")) {
					b = true;
					a = "QEMU";
				}
				else {
					if (!line.contains("paravirtualized kernel on KVM")) {
						continue;
					}
					b = true;
					a = "QEMU";
				}
			}
			try {
				bufferedReader.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException a2) {
			}
		}
		if (b) {
			HwidGetterImpl.hwid = a;
		}
		return b;
	}
	
	private static boolean q() {
		boolean b = false;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ls -l /dev/disk/by-id/")
				.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Virtual")) {
					b = true;
					HwidGetterImpl.hwid = "Hyper-V";
				}
				else if (line.contains("VBOX")) {
					b = true;
					HwidGetterImpl.hwid = "VirtualBox";
				}
				else if (line.contains("VMware")) {
					b = true;
					HwidGetterImpl.hwid = "VMware";
				}
				else {
					if (!line.contains("QEMU")) {
						continue;
					}
					b = true;
					HwidGetterImpl.hwid = "QEMU";
				}
			}
			try {
				bufferedReader.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return b;
	}
	
	private static boolean r() {
		boolean b = false;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ls -l /dev/disk/by-uuid/ | grep xvda1")
				.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("xvda1")) {
					b = true;
					HwidGetterImpl.hwid = "Xen";
				}
			}
			try {
				bufferedReader.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return b;
	}
	
	private static boolean s() {
		boolean b = false;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("system_profiler SPSerialATADataType SPSASDataType SPParallelSCSIDataType")
				.getInputStream(), StandardCharsets.UTF_8));
			String s = null;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Model:")) {
					s = line;
					break;
				}
			}
			if (s != null) {
				if (line.contains("Virtual")) {
					b = true;
					HwidGetterImpl.hwid = "Hyper-V";
				}
				else if (line.contains("VBOX")) {
					b = true;
					HwidGetterImpl.hwid = "VirtualBox";
				}
				else if (line.contains("VMware")) {
					b = true;
					HwidGetterImpl.hwid = "VMware";
				}
				else if (line.contains("QEMU")) {
					b = true;
					HwidGetterImpl.hwid = "QEMU";
				}
			}
			try {
				bufferedReader.close();
			}
			catch (IOException ex) {
			}
		}
		catch (IOException ex2) {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex3) {
			}
		}
		finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException ex4) {
			}
		}
		return b;
	}
}
