package me.cookiedragon234.falcon.authentication;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;

public class Hwid {
	private final String processorIdentifier;
	private final String processorName;
	private final String diskSN;
	private final String diskUUID;
	
	public Hwid(String processorIdentifier, String processorName, String diskSN, String diskUUID) {
		this.processorIdentifier = processorIdentifier;
		this.processorName = processorName;
		this.diskSN = diskSN;
		this.diskUUID = diskUUID;
	}
	
	public static Hwid create() {
		String processorIdentifier = HwidBuilder.getProcessorIdentifier();
		String processorName = HwidBuilder.getProcessorName();
		
		//String diskSN = HwidGetter.getDiskSN();
		//String diskUUID = HwidGetter.getDiskUUID();
		String diskSN = "nonce";
		String diskUUID = "nonce";
		
		return new Hwid(processorIdentifier, processorName, diskSN, diskUUID);
	}
	
	@SuppressWarnings("UnstableApiUsage")
	public Hwid hash() {
		String processorIdentifier = Hashing.sha512()
			.hashString(this.processorIdentifier, StandardCharsets.UTF_8)
			.toString();
		String processorName = Hashing.sha512().hashString(this.processorName, StandardCharsets.UTF_8).toString();
		String diskSN = Hashing.sha512().hashString(this.diskSN, StandardCharsets.UTF_8).toString();
		String diskUUID = Hashing.sha512().hashString(this.diskUUID, StandardCharsets.UTF_8).toString();
		
		return new Hwid(processorIdentifier, processorName, diskSN, diskUUID);
	}
	
	@Override
	public String toString() {
		return Hashing.sha512()
			.hashString(processorIdentifier + ":" + processorName + ":" + diskSN + ":" + diskUUID, StandardCharsets.UTF_8)
			.toString();
	}
	
	public JsonObject serialize() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Processor Identifier", processorIdentifier);
		jsonObject.addProperty("Processor Name", processorName);
		jsonObject.addProperty("Disk SN", diskSN);
		jsonObject.addProperty("Disk UUID", diskUUID);
		return jsonObject;
	}
}
