package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.irc.IRCUser;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCChannelTopicEvent extends IRCEvent {

	public final IIRCChannel channel;
	public final IIRCUser user;
	public final String topic;
	
	public IRCChannelTopicEvent(IIRCConnection connection, IIRCChannel channel, IRCUser user, String topic) {
		super(connection);
		this.channel = channel;
		this.user = user;
		this.topic = topic;
	}
}
