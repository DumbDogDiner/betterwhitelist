package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class UnwhitelistSubCommand implements BaseClass {
	public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist unwhitelist <username>"));
            return;
        }

        MojangUser user = UsernameValidator.getUser(args[1]);

        if (user == null || user.id == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user of name '" + args[1] + "'."));
            return;
        }

        if (getSQL().getDiscordIDFromMinecraft(user.id) == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player '" + args[1] + "' is not whitelisted."));
            return;
        }

        if (getSQL().removeEntryUsingUuid(user.id)) {
            sender.sendMessage(new TextComponent(
                    ChatColor.AQUA + "Removed user " + user.name + " ('" + user.id + "') from the whitelist."));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to unmap user - SQL update failed."));
        }
    }
}