// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CommandMessage implements SubCommand {

	@Override
	public String getCommandName() {
		return "msg";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.msg.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 2) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		IRCContext target = EiraIRCAPI.parseContext(null, args[0], null);
		if(target.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
			return true;
		} else if(target.getContextType() == IRCContext.ContextType.IRCUser) {
			if(!ConfigHelper.getBotSettings(context).allowPrivateMessages.get()) {
				Utils.sendLocalizedMessage(sender, "commands.msg.disabled");
				return true;
			}
		}
		String message = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ").trim();
		if(message.isEmpty()) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		boolean isEmote = message.startsWith("/me ");
		if(isEmote) {
			message = message.substring(4);
		}
		String format = "{MESSAGE}";
		BotSettings botSettings = ConfigHelper.getBotSettings(target);
		IRCUser botUser = target.getConnection().getBotUser();

		String ircMessage = message;
		if(serverSide) {
			if(target.getContextType() == IRCContext.ContextType.IRCChannel) {
				format = isEmote ? botSettings.getMessageFormat().mcSendChannelEmote : botSettings.getMessageFormat().mcSendChannelMessage;
			} else if (target.getContextType() == IRCContext.ContextType.IRCUser) {
				format = isEmote ? botSettings.getMessageFormat().mcSendPrivateEmote : botSettings.getMessageFormat().mcSendPrivateMessage;
			}
			ircMessage = MessageFormat.formatMessage(format, target.getConnection(), target, botUser, message, MessageFormat.Target.IRC, isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
		}
		target.message(ircMessage);

		format = "{MESSAGE}";
		if(target.getContextType() == IRCContext.ContextType.IRCChannel) {
			format = isEmote ? botSettings.getMessageFormat().mcSendChannelEmote : botSettings.getMessageFormat().mcSendChannelMessage;
		} else if (target.getContextType() == IRCContext.ContextType.IRCUser) {
			format = isEmote ? botSettings.getMessageFormat().mcSendPrivateEmote : botSettings.getMessageFormat().mcSendPrivateMessage;
		}

		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, target.getConnection(), target, botUser, message, MessageFormat.Target.IRC, isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
		if(isEmote) {
			ThemeSettings themeSettings = ConfigHelper.getTheme(target);
			EnumChatFormatting emoteColor = ((IRCUserImpl) botUser).getNameColor();
			if(emoteColor == null) {
				emoteColor = themeSettings.emoteTextColor.get();
			}
			if(emoteColor != null) {
				chatComponent.getChatStyle().setColor(emoteColor);
			}
		}
		EiraIRCAPI.getChatHandler().addChatMessage(sender, chatComponent);
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
				list.add(channelConfig.getName());
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

}
