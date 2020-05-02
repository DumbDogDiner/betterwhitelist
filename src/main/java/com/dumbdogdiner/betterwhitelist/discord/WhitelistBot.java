package com.dumbdogdiner.betterwhitelist.discord;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.commands.GetStatusCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.HelpCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.UnwhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.WhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.listeners.GuildEventListener;
import com.dumbdogdiner.betterwhitelist.discord.listeners.MessageListener;
import com.dumbdogdiner.betterwhitelist.discord.listeners.ReadyListener;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Discord bot for whitelisting users from DDD itself.
 */
public class WhitelistBot implements BaseClass {
	
	private JDA jda;

    public WhitelistBot() {
    	// constructor
    	JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(getConfig().getString("discord.token"));
    	
    	configureMemory(builder);

        // Register Events
        builder.addEventListeners(
        	new ReadyListener(),
            new GuildEventListener(),
            new MessageListener()
        );

        // Register Commands
        addCommand(
            new GetStatusCommand(),
            new WhitelistCommand(),
            new UnwhitelistCommand(),
            new HelpCommand()
        );

        getLogger().info(String.format(
            "[discord] Have %d commands: %s",
            commands.size(),
            commands.values().stream().map(Command::getName).collect(Collectors.joining(", "))
        ));

        builder.setActivity(Activity.watching("the cutest fuzzballs \uD83E\uDDE1"));
        try {
            getLogger().info("[discord] Attempting connection to Discord...");
            jda = builder.build();
        } catch (LoginException err) {
            getLogger().severe("[discord] WhitelistBot threw an error while trying to authenticate with Discord.");
            err.printStackTrace();
        }
    }
    
    public JDA getJDA() {
    	return jda;
    }

    private HashMap<String, Command> commands = new HashMap<>();
    public HashMap<String, Command> getCommands() {
        return commands;
    }

    /**
     * Configure flags for the JDABuilder. Saves memory :3
     * @param builder
     */

    private void configureMemory(JDABuilder builder) {
        builder.setDisabledCacheFlags(
                EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
        );
    }

    /**
     * Add commands to the bot.
     * @param commandsToAdd
     */
    public void addCommand(Command... commandsToAdd) {
        for (Command cmd : commandsToAdd) {
            commands.put(cmd.getName(), cmd);
        }
    }
}
