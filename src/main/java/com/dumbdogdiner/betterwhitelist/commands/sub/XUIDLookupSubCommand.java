package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.IXboxGamertagUtil;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class XUIDLookupSubCommand implements BaseClass, IXboxGamertagUtil {
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist xuidlookup <username>"));
            return;
        }
        
        String username = getGamertagFromArray(1, args);
		
		XboxLiveUser user = XboxLiveUsernameValidator.getUser(username, "commands.ig.xuidlookup");
		
		if (user != null) {
			String.format("User '%s' has XUID: '%s'", user.getName(), user.getDecimalXUID());
			sender.sendMessage(new TextComponent(ChatColor.AQUA + String.format("User '%s' has (decimal) XUID: '%s'", user.getName(), user.getDecimalXUID())));
			sender.sendMessage(new TextComponent(ChatColor.AQUA + String.format("User '%s' has (hex - for uuids!) XUID: '%s'", user.getName(), user.getHexXUID())));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "User '" + username + "' does not exist!"));
		}
	}
}