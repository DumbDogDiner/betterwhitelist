package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class XblWhitelistSubCommand implements BaseClass {
	public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(
                    new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist xblwhitelist <username> <discord_id>"));
            return;
        }

        XboxLiveUser user = XboxLiveUsernameValidator.getUser(args[0], "commands.ig.xblwhitelist");
        
        String discordId = args.length > 2 ? args[2] : "none";

        if (user == null || user.getID() == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user with name '" + args[1] + "'."));
            return;
        }

        if (getSQL().getDiscordIDFromMinecraft(user.getID()) != null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player " + args[1]
                    + " is already whitelisted. Run '/betterwhitelist xblunwhitelist <player_name>' to remove them."));
            return;
        }

        if (getSQL().addEntry(String.format("X%s", discordId), user.getID())) {
            sender.sendMessage(new TextComponent(
                    ChatColor.AQUA + "Mapped Xbox Live user '" + user.getName() + "' to Discord ID '" + discordId + "'."));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to map user - SQL update failed."));
        }
    }
}