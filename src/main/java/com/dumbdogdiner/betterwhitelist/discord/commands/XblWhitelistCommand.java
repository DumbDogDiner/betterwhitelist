package com.dumbdogdiner.betterwhitelist.discord.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import com.dumbdogdiner.betterwhitelist.discord.utils.RoleUtil;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class XblWhitelistCommand extends Command implements BaseClass {

    public XblWhitelistCommand() {
        this.name = "xblwhitelist";
        this.description = "Add yourself to the whitelist of the Minecraft server.";
        this.syntax = "<username>";

        // Require ratelimit
        RatelimitUtil.registerRateLimit(this, 5.0);
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        if (!getConfig().getBoolean("discord.enableSelfWhitelisting")) {
            e.getChannel().sendMessage(getConfig().getString("lang.discord.selfWhitelistingDisabled")).queue();
            return;
        }

        if (!RoleUtil.checkRequiredRole(e)) {
            e.getChannel().sendMessage(getConfig().getString("lang.discord.missingRequiredRole")).queue();
            return;
        }

        if (args.length == 0 || args[0] == null) {
            e.getChannel().sendMessage(
                    String.format(getConfig().getString("lang.discord.usernameNotSpecified"), getPluginConfig().getPrefix()))
                    .queue();
            return;
        }

        e.getChannel().sendTyping().queue();

        // If the user has already whitelisted an account, and 'oneAccountPerUser' is enabled, show an error.
        if (getSQL().getUuidFromDiscordId("xbl-" + e.getAuthor().getId()) != null && getConfig().getBoolean("discord.oneAccountPerUser")) {
            e.getChannel().sendMessage(
            		String.format(":x: **Failed to verify!** You already have an Xbox Live account whitelisted - you can unwhitelist it by running `%sxblunwhitelist`.", getPluginConfig().getPrefix()))
                    .queue();
            return;
        }

        XboxLiveUser user = XboxLiveUsernameValidator.getUser(args[0], "commands.discord.xblwhitelist");
        

        // If the specified Minecraft user could not be resolved to an UUID (b/c the account does not exist), show an error.
        if (user == null || user.getDecimalXUID() == null) {
            e.getChannel().sendMessage(getConfig().getString("lang.discord.invalidUsername")).queue();
            return;
        }
        
        
        // If the specified Minecraft uuid is already whitelisted, show an error.
        if (getSQL().getDiscordIDFromMinecraft(user.getID()) != null) {
        	e.getChannel().sendMessage(String.format(getConfig().getString("lang.discord.minecraftAccountAlreadyWhitelisted"), user.getEscapedName())).queue();
        	return;
        }

        // Add user to SQL.
        if (!getSQL().addEntry(String.format("X%s", e.getAuthor().getId()), user.getID())) {
        	// If there is an error, display it.
            e.getChannel().sendMessage(getConfig().getString("lang.discord.userAddError")).queue();
            return;
        }

        // Send a success message.
        e.getChannel().sendMessage(String.format(getConfig().getString("lang.discord.userWhitelisted"), user.getEscapedName(), String.format("%s#%s", user.getDecimalXUID(), "0")))
                .queue(message -> RoleUtil.addGrantedRole(e));
    }

}
