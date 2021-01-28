package com.dumbdogdiner.betterwhitelist.discord;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.commands.DebugCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.GetStatusCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.HelpCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.UUIDLookupCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.UnwhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.WhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.XUIDConvertCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.XUIDLookupCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.XblUnwhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.commands.XblWhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.listeners.GuildEventListener;
import com.dumbdogdiner.betterwhitelist.discord.listeners.MessageListener;
import com.dumbdogdiner.betterwhitelist.discord.listeners.ReadyListener;

import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Discord bot for whitelisting users from DDD itself.
 */
public class WhitelistBot implements BaseClass {
	
	private JDA jda;
	
	public Long debugTextChannelId = 0L;

    public WhitelistBot() {
    	// constructor
    	JDABuilder builder = JDABuilder.createDefault(getConfig().getString("discord.token"));
    	
    	configureMemory(builder);

        // Register Events
        builder.addEventListeners(
        	new ReadyListener(),
            new GuildEventListener(),
            new MessageListener()
        );

        // Register Commands
        addCommand(
        	new XUIDConvertCommand(),
        	new XUIDLookupCommand(),
        	new UUIDLookupCommand(),
        	new DebugCommand(),
            new GetStatusCommand(),
            new WhitelistCommand(),
            new XblWhitelistCommand(),
            new UnwhitelistCommand(),
            new XblUnwhitelistCommand(),
            new HelpCommand()
        );

        // Register ratelimit task
        getProxy().getScheduler().schedule(getInstance(), RatelimitUtil::cleanRatelimits, 1, TimeUnit.HOURS);

        getLogger().info(String.format(
            "[discord] " + getConfig().getString("lang.console.discord.commands"),
            commands.size(),
            commands.values().stream().map(Command::getName).collect(Collectors.joining(", "))
        ));
        
        // Create a listener to log debug channel information when JDA has connected.
        builder.addEventListeners(new ListenerAdapter() {
        	@Override
        	public void onReady(ReadyEvent event) {
        		
        		// Debug Channel logging. - default to '0' to avoid an IllegalArgumentException by JDA.
                TextChannel debugTextChannel = jda.getTextChannelById(getConfig().getString("discord.debugChannelId", "0"));

                if (debugTextChannel != null) {
                	getLogger().info("[discord] [debug] Debug command listening on: #" + debugTextChannel.getName() + " <#" + debugTextChannel.getId() + ">");
                	debugTextChannelId = debugTextChannel.getIdLong();
                } else {
                	// invalid channel ID.
                	getLogger().warning("[discord] [debug] Invalid text channel, check config!");
                }
        	}
        });

        builder.setActivity(Activity.watching(getConfig().getString("lang.discordStatus")));
        
        try {
            getLogger().info("[discord] " + getConfig().getString("lang.console.discord.attemptingConnection"));
            jda = builder.build();
        } catch (LoginException err) {
            getLogger().severe("[discord] " + getConfig().getString("lang.console.discord.loginError"));
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
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE);
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
