package com.dumbdogdiner.betterwhitelist_bungee.bungee.commands;

import com.dumbdogdiner.betterwhitelist_bungee.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQL;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class UnwhitelistCommand extends Command {

    public UnwhitelistCommand() {
        super("btw_unwhitelist", "betterwhitelist.admin.unwhitelist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: <username>"));
            return;
        }

        MojangUser user = UsernameValidator.getUser(args[0]);

        if (user == null || user.id == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user of name '" + args[0] + "'."));
            return;
        }

        if (SQL.getDiscordIDFromMinecraft(user.id) == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player '" + args[0] + "' is not whitelisted."));
            return;
        }

        if (SQL.removeEntryUsingUuid(user.id)) {
            sender.sendMessage(new TextComponent(
                    ChatColor.AQUA + "Removed user " + user.name + " ('" + user.id + "') from the whitelist."));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to unmap user - SQL update failed."));
        }
    }
}
