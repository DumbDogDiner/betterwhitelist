package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class RootSubCommand implements BaseClass {
	public void execute(CommandSender sender) {
		sender.sendMessage(new TextComponent("BetterwhitelistBungee v" + getPluginDescription().getVersion() + "\nCreated by: " + getPluginDescription().getAuthor()));
	}
}