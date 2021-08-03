package me.robeart.raion.client.module.misc;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.imixin.ICPacketChatMessage;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.network.play.client.CPacketChatMessage;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

/**
 * @author Robeart
 */
public class ChatAppendModule extends Module {
	
	private final String SPECIAL_CHARACTERS = "!@#$%^&*()_+=-{}][:;/?.,<\\~";
	public ListValue append = new ListValue("Text", "\u1D3F\u1D2C\u1D35\u1D3C\u1D3A", Arrays.asList("\u1D3F\u1D2C\u1D35\u1D3C\u1D3A", "\u24C7\u24B6\u24BE\u24C4\u24C3", "\u0280\u1D00\u026A\u1D0F\u0274", "\uFF32\uFF21\uFF29\uFF2F\uFF2E", "RAION"));
	
	public ChatAppendModule() {
		super("ChatAppend", "Adds some text at the end of your chat message", Category.MISC);
	}
	
	@Listener
	public void onPacketSend(PacketSendEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (event.getPacket() instanceof CPacketChatMessage) {
			CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
			String msg = packet.getMessage();
			if (SPECIAL_CHARACTERS.indexOf(msg.charAt(0)) >= 0) return;
			msg += "\u4E28" + append.getValue();
			if (msg.length() >= 256) msg = msg.substring(0, 256);
			final ICPacketChatMessage iPacket = (ICPacketChatMessage) event.getPacket();
			iPacket.setMessage(msg);
		}
	}
	
}
