package com.dumbdogdiner.betterwhitelist.discord.commands;

import java.util.concurrent.TimeUnit;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.utils.ConfigMXBean;
import com.dumbdogdiner.betterwhitelist.utils.PoolMXBean;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DebugCommand extends Command implements BaseClass {

    public DebugCommand() {
        this.name = "debug";
        this.description = "Get debug information.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
    	// Make sure the channel matches the config.
    	getLogger().info("GETID: " + e.getChannel().getIdLong());
    	getLogger().info("STR: " + getBot().debugTextChannelId);
    	
    	// JDA is weird, don't ask.
    	if (e.getChannel().getIdLong() != getBot().debugTextChannelId) {
    		// Channel does not match, returning without a message...
    		getLogger().info("FAILED, RET");
    		return;
    	}
    	
    	getLogger().info("PASSED");
    	
    	EmbedBuilder embed = new EmbedBuilder();
    	
    	ConfigMXBean configMx = getSQL().getConfigMXBean();
    	PoolMXBean poolMx = getSQL().getPoolMXBean();
    	
    	embed.setAuthor("BetterwhitelistBungee Debug Info");
    	
    	embed.addField("SQL Pool Name", configMx.getPoolName(), false);
    	embed.addField("SQL Max Pool Size", Integer.toString(configMx.getMaximumPoolSize()), true);
    	embed.addField("SQL Connection Timeout", Long.toString(TimeUnit.MILLISECONDS.toSeconds(configMx.getConnectionTimeout())) + "s", true);
    	
    	embed.addField("SQL Active Connections", Integer.toString(poolMx.getActiveConnections()), true);
    	embed.addField("SQL Idle Connections", Integer.toString(poolMx.getIdleConnections()), true);
    	embed.addField("SQL Threads Awaiting Connection", Integer.toString(poolMx.getThreadsAwaitingConnection()), true);
    	embed.addField("SQL Total Connections", Integer.toString(poolMx.getTotalConnections()), true);
    	
    	// Less important info
    	embed.addField("SQL Idle Timeout", Long.toString(TimeUnit.MILLISECONDS.toMinutes(configMx.getIdleTimeout())) + "m", true);
    	embed.addField("SQL Leak Detection Threshold", Long.toString(configMx.getLeakDetectionThreshold()), true);
    	embed.addField("SQL Max Lifetime", Long.toString(TimeUnit.MILLISECONDS.toMinutes(configMx.getMaxLifetime())) + "m", true);
    	embed.addField("SQL Minimum Idle", Long.toString(configMx.getMinimumIdle()), true);
    	embed.addField("SQL Validation Timeout", Long.toString(TimeUnit.MILLISECONDS.toSeconds(configMx.getValidationTimeout())) + "s", true);
    	
    	// Build and send the embed.
    	e.getChannel().sendMessage(embed.build()).queue();
    }
}
