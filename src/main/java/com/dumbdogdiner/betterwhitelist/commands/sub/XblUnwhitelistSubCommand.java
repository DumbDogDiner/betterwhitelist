package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.IXboxGamertagUtil;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class XblUnwhitelistSubCommand implements BaseClass, IXboxGamertagUtil {
	public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist xblunwhitelist <username>"));
            return;
        }
        
        String username = getGamertagFromArray(1, args);
        
        XboxLiveUser user = XboxLiveUsernameValidator.getUser(username, "commands.ig.xblunwhitelist");

        if (user == null || user.getID() == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user of name '" + username + "'."));
            return;
        }

        if (getSQL().getDiscordIDFromMinecraft(user.getID()) == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player '" + username + "' is not whitelisted."));
            return;
        }

        if (getSQL().removeEntryUsingUuid(user.getID())) {
            sender.sendMessage(new TextComponent(
                    ChatColor.AQUA + "Removed Xbox Live user " + user.getName() + " ('" + user.getDecimalXUID() + "') from the whitelist."));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to unmap user - SQL update failed."));
        }
    }
}