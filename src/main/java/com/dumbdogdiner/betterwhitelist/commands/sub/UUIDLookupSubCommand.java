package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class UUIDLookupSubCommand implements BaseClass {
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist uuidlookup <username>"));
            return;
        }
		
		MojangUser user = UsernameValidator.getUser(args[1], "commands.ig.uuidlookup");
		
		if (user != null) {
			sender.sendMessage(new TextComponent(ChatColor.AQUA + "User '" + user.name + "' has UUID: " + user.id + " (server #" + user.server + ")"));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "User '" + args[1] + "' does not exist!"));
		}
	}
}