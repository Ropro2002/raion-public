package me.robeart.raion.client.module.misc;

import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.BooleanValue;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.regex.Pattern;

/**
 * @author Robeart
 */
public class ChatTweaksModule extends Module {

	private BooleanValue filter = new BooleanValue("Filter", true);
	private BooleanValue greenText = new BooleanValue("Green Text", true, this.filter);
	private BooleanValue discord = new BooleanValue("Discord", true, this.filter);
	private BooleanValue website = new BooleanValue("Website", true, this.filter);
	private BooleanValue ip = new BooleanValue("IP", true, this.filter);
	private BooleanValue spammer = new BooleanValue("Spammer", true, this.filter);
	private BooleanValue announcer = new BooleanValue("Announcer", true, this.filter);
	private BooleanValue insulter = new BooleanValue("Insulter", true, this.filter);
	private BooleanValue greeter = new BooleanValue("Greeter", true, this.filter);

	private BooleanValue nameColor = new BooleanValue("Name Color", true);

	public ChatTweaksModule() {
		super("ChatTweaks", "Improves your chat", Category.MISC);
	}

	@Listener
	public void receivePacket(PacketReceiveEvent event) {
		if (event.getPacket() instanceof SPacketChat) {
			final SPacketChat packet = (SPacketChat) event.getPacket();
			if (this.filter.getValue() && filterMessage(packet.getChatComponent().getUnformattedText())) {
				event.setCanceled(true);
				return;
			}

			if (this.nameColor.getValue()) {
				String message = packet.getChatComponent().getFormattedText();
				if (mc.player != null && message.contains(mc.player.getName())) {
					String fixedMessage = message.replaceAll(mc.player.getName(), "\u00a74" + mc.player.getName() + "\u00a7f");
					mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(fixedMessage));
					event.setCanceled(true);
				}
			}
		}
	}

	private boolean filterMessage(String msg) {
		return ip.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.IP_ADDR, msg) ||
			greeter.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.GREETER, msg) ||
			insulter.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.INSULTER, msg) ||
			announcer.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.ANNOUNCER, msg) ||
			website.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.WEB_LINK, msg) ||
			discord.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.DISCORD, msg) ||
			greenText.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.GREEN_TEXT, msg) ||
			spammer.getValue() && checkPattern(ChatTweaksModule.FilterPatterns.SPAMMER, msg);
	}

	private boolean checkPattern(String[] patterns, String msg) {
		for (String pattern : patterns) if (Pattern.compile(pattern).matcher(msg).find()) return true;
		return false;
	}

	private static class FilterPatterns {

		private static final String[] ANNOUNCER =
			{
				// RusherHack b8
				"I just walked .+ feet!",
				"I just placed a .+!",
				"I just attacked .+ with a .+!",
				"I just dropped a .+!",
				"I just opened chat!",
				"I just opened my console!",
				"I just opened my GUI!",
				"I just went into full screen mode!",
				"I just paused my game!",
				"I just opened my inventory!",
				"I just looked at the player list!",
				"I just took a screen shot!",
				"I just swaped hands!",
				"I just ducked!",
				"I just changed perspectives!",
				"I just jumped!",
				"I just ate a .+!",
				"I just crafted .+ .+!",
				"I just picked up a .+!",
				"I just smelted .+ .+!",
				"I just respawned!",
				// RusherHack b11
				"I just attacked .+ with my hands",
				"I just broke a .+!",
				// WWE
				"I recently walked .+ blocks",
				"I just droped a .+ called, .+!",
				"I just placed a block called, .+!",
				"Im currently breaking a block called, .+!",
				"I just broke a block called, .+!",
				"I just opened chat!",
				"I just opened chat and typed a slash!",
				"I just paused my game!",
				"I just opened my inventory!",
				"I just looked at the player list!",
				"I just changed perspectives, now im in .+!",
				"I just crouched!",
				"I just jumped!",
				"I just attacked a entity called, .+ with a .+",
				"Im currently eatting a peice of food called, .+!",
				"Im currently using a item called, .+!",
				"I just toggled full screen mode!",
				"I just took a screen shot!",
				"I just swaped hands and now theres a .+ in my main hand and a .+ in my off hand!",
				"I just used pick block on a block called, .+!",
				"Ra just completed his blazing ark",
				"Its a new day yes it is",
				// DotGod.CC
				"I just placed .+ thanks to (http:\\/\\/)?DotGod\\.CC!",
				"I just flew .+ meters like a butterfly thanks to (http:\\/\\/)?DotGod\\.CC!",
			};

		private static final String[] SPAMMER =
			{
				//WWE
				"WWE Client's spammer",
				"Lol get gud",
				"Future client is bad",
				"WWE > Future",
				"WWE > Impact",
				"Default Message",
				"IKnowImEZ is a god",
				"THEREALWWEFAN231 is a god",
				"WWE Client made by IKnowImEZ/THEREALWWEFAN231",
				"WWE Client was the first public client to have Path Finder/New Chunks",
				"WWE Client was the first public client to have color signs",
				"WWE Client was the first client to have Teleport Finder",
				"WWE Client was the first client to have Tunneller & Tunneller Back Fill",
			};

		private static final String[] INSULTER =
			{
				// WWE
				".+ Download WWE utility mod, Its free!",
				".+ 4b4t is da best mintscreft serber",
				".+ dont abouse",
				".+ you cuck",
				".+ https://www.youtube.com/channel/UCJGCNPEjvsCn0FKw3zso0TA",
				".+ is my step dad",
				".+ again daddy!",
				"dont worry .+ it happens to every one",
				".+ dont buy future it's crap, compared to WWE!",
				"What are you, fucking gay, .+?",
				"Did you know? .+ hates you, .+",
				"You are literally 10, .+",
				".+ finally lost their virginity, sadly they lost it to .+... yeah, that's unfortunate.",
				".+, don't be upset, it's not like anyone cares about you, fag.",
				".+, see that rubbish bin over there? Get your ass in it, or I'll get .+ to whoop your ass.",
				".+, may I borrow that dirt block? that guy named .+ needs it...",
				"Yo, .+, btfo you virgin",
				"Hey .+ want to play some High School RP with me and .+?",
				".+ is an Archon player. Why is he on here? Fucking factions player.",
				"Did you know? .+ just joined The Vortex Coalition!",
				".+ has successfully conducted the cactus dupe and duped a itemhand!",
				".+, are you even human? You act like my dog, holy shit.",
				".+, you were never loved by your family.",
				"Come on .+, you hurt .+'s feelings. You meany.",
				"Stop trying to meme .+, you can't do that. kek",
				".+, .+ is gay. Don't go near him.",
				"Whoa .+ didn't mean to offend you, .+.",
				".+ im not pvping .+, im WWE'ing .+.",
				"Did you know? .+ just joined The Vortex Coalition!",
				".+, are you even human? You act like my dog, holy shit.",
			};

		private static final String[] GREETER =
			{
				// WWE
				"Bye, Bye .+",
				"Farwell, .+",
				// incomplete
			};

		private static final String[] DISCORD =
			{
				"discord.gg",
				"discordapp.com",
				"discord.io",
				"invite.gg",
			};

		private static final String[] GREEN_TEXT =
			{
				"^<.+> >",
			};

		private static final String[] WEB_LINK =
			{
				"http:\\/\\/",
				"https:\\/\\/",
				"www.",
			};

		private static final String[] IP_ADDR =
			{
				"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}\\b",
				"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}",
				"^(?:http(?:s)?:\\/\\/)?(?:[^\\.]+\\.)?.*\\..*\\..*$",
				".*\\..*\\:\\d{1,5}$",
			};
	}
}
