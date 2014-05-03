package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public class IRCUserJoinEvent extends IRCEvent {

	public final IIRCChannel channel;
	public final IIRCUser user;
	
	public IRCUserJoinEvent(IIRCConnection connection, IIRCChannel channel, IIRCUser user) {
		super(connection);
		this.channel = channel;
		this.user = user;
	}
}
