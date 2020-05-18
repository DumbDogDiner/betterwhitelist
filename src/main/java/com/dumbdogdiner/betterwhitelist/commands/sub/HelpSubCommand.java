package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpSubCommand implements BaseClass {
	public void execute(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /betterwhitelist <help|whitelist|unwhitelist|uuidlookup|whois>"));
    }
}