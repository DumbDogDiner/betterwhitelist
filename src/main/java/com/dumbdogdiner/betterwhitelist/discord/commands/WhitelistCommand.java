package com.dumbdogdiner.betterwhitelist.discord.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import com.dumbdogdiner.betterwhitelist.discord.utils.RoleUtil;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WhitelistCommand extends Command implements BaseClass {

    public WhitelistCommand() {
        this.name = "whitelist";
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
        if (getSQL().getUuidFromDiscordId(e.getAuthor().getId()) != null && getConfig().getBoolean("discord.oneAccountPerUser")) {
            e.getChannel().sendMessage(
            		String.format(getConfig().getString("lang.discord.alreadyWhitelistedOneUserOnly"), getPluginConfig().getPrefix()))
                    .queue();
            return;
        }

        MojangUser user = UsernameValidator.getUser(args[0], "commands.discord.whitelist");
        
        
        // If the specified Minecraft username is already whitelisted, show an error.
        if (getSQL().getDiscordIDFromMinecraft(user.id) != null) {
        	e.getChannel().sendMessage(String.format(getConfig().getString("lang.discord.minecraftAccountAlreadyWhitelisted"), user.name)).queue();
        	return;
        }

        // If the specified Minecraft user could not be resolved to an UUID (b/c the account does not exist), show an error.
        if (user == null || user.id == null) {
            e.getChannel().sendMessage(getConfig().getString("lang.discord.invalidUsername")).queue();
            return;
        }

        // Add user to SQL.
        if (!getSQL().addEntry(e.getAuthor().getId(), user.id)) {
        	// If there is an error, display it.
            e.getChannel().sendMessage(getConfig().getString("lang.discord.userAddError")).queue();
            return;
        }

        // Send a success message.
        e.getChannel().sendMessage(String.format(getConfig().getString("lang.discord.userWhitelisted"), user.name, String.format("%s#%s", user.id, user.server)))
                .queue(message -> RoleUtil.addGrantedRole(e));
    }

}
