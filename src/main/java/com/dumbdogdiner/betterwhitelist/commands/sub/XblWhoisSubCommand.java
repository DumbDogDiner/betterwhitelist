package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class XblWhoisSubCommand implements BaseClass {
	
	private void sendUsage(CommandSender sender) {
		sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist xblwhois <minecraft|discord> [..]"));
	}
	public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender);
            return;
        }

        if (args[1].equalsIgnoreCase("minecraft") && args[2].length() <= 16) {
        	// Minecraft (Bedrock, therefore Xbox Live) Username, same command name for ease of use compared to other commands.
        	
        	XboxLiveUser user = XboxLiveUsernameValidator.getUser(args[2], "commands.ig.xblwhois.minecraft.username");
        	
        	String discordId = getSQL().getDiscordIDFromMinecraft(user.getID());
        	
        	if (discordId != null) {
        		// Result found!
        		sender.sendMessage(new TextComponent(ChatColor.AQUA + String.format("User '%s' (%s) has Discord ID: '%s'", user.getName(), user.getID(), discordId.replace("X", ""))));
        		return;
        	} else {
        		sender.sendMessage(new TextComponent(ChatColor.RED + "User is not whitelisted!"));
        		return;
        	}
        	
        } else if (args[1].equalsIgnoreCase("minecraft")) {
        	// Minecraft UUID.
        	
        	// Assuming the UUID is valid, query it.
        	
        	String discordId = getSQL().getDiscordIDFromMinecraft(args[2]);
        	
        	if (discordId != null) {
        		// Result found!
        		sender.sendMessage(new TextComponent(ChatColor.AQUA + String.format("User '%s' has Discord ID: '%s'", args[2], discordId.replace("X", ""))));
        		return;
        	} else {
        		sender.sendMessage(new TextComponent(ChatColor.RED + "User is not whitelisted! Did you include hyphens?"));
        		return;
        	}
        	
        } else if (args[1].equalsIgnoreCase("discord")) {
        	// Discord ID - Names not supported!
        	
        	String uuid = getSQL().getUuidFromDiscordId(String.format("xbl-%s", args[2]));
        	
        	if (uuid != null) {
        		// Result found!
        		sender.sendMessage(new TextComponent(ChatColor.AQUA + String.format("Discord user '%s' has Minecraft UUID: '%s' (converted from Xbox Live)", args[2], uuid)));
        		return;
        	} else {
        		sender.sendMessage(new TextComponent(ChatColor.RED + "User is not whitelisted!"));
        		return;
        	}
        } else {
        	sendUsage(sender);
        }
    }
}