package com.dumbdogdiner.betterwhitelist.commands.sub;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.IXboxGamertagUtil;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class XblWhitelistSubCommand implements BaseClass, IXboxGamertagUtil {
	public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(
                    new TextComponent(ChatColor.RED + "Invalid arguments - syntax: /betterwhitelist xblwhitelist <username> <discord_id>"));
            return;
        }
        
        String username = getGamertagFromArray(1, args.length - 1, args);
        getLogger().info("debug username: '" + username + "'");

        XboxLiveUser user = XboxLiveUsernameValidator.getUser(username, "commands.ig.xblwhitelist");
        
        // IF more than 2 args are provided (e.g. 'gamertag', 'id', AND last option is a number.
        String discordId = (args.length > 2 && isNumeric(args[args.length-1])) ? args[args.length-1] : "none";

        if (user == null || user.getID() == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user with name '" + username + "'."));
            return;
        }

        if (getSQL().getDiscordIDFromMinecraft(user.getID()) != null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player " + username
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
	
	private boolean isNumeric(String str) {
	    if (str == null) {
	        return false;
	    }
	    int sz = str.length();
	    for (int i = 0; i < sz; i++) {
	        if (Character.isDigit(str.charAt(i)) == false) {
	            return false;
	        }
	    }
	    return true;
	}
}