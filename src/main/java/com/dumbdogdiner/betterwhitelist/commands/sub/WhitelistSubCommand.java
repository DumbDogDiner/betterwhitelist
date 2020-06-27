package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class WhitelistSubCommand implements BaseClass {
	public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(
                    new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist whitelist <username> <discord_id>"));
            return;
        }

        MojangUser user = UsernameValidator.getUser(args[1], "commands.ig.whitelist");
        String discordId = args.length > 2 ? args[2] : "none";

        if (user == null || user.id == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user with name '" + args[1] + "'."));
            return;
        }

        if (getSQL().getDiscordIDFromMinecraft(user.id) != null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player " + args[1]
                    + " is already whitelisted. Run 'unwhitelist <player_name>' to remove them."));
            return;
        }

        if (getSQL().addEntry(discordId, user.id)) {
            sender.sendMessage(new TextComponent(
                    ChatColor.AQUA + "Mapped user " + user.name + " to Discord ID '" + discordId + "'. (via uuid server #" + user.server + ")"));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to map user - SQL update failed."));
        }
    }
}