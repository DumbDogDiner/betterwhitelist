package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.IXboxGamertagUtil;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class XUIDConvertSubCommand implements BaseClass, IXboxGamertagUtil {
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist xuidconvert <username>"));
            return;
        }
        
        String username = getGamertagFromArray(1, args);
		
		XboxLiveUser user = XboxLiveUsernameValidator.getUser(username, "commands.ig.xuidconvert");
		
		if (user != null) {
			sender.sendMessage(new TextComponent(ChatColor.AQUA + String.format("User '%s' has Java convered UUID: '%s'", user.getName(), user.getID())));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "User '" + username + "' does not exist!"));
		}
	}
}