package com.dumbdogdiner.betterwhitelist;

import com.dumbdogdiner.betterwhitelist.commands.UnwhitelistCommand;
import com.dumbdogdiner.betterwhitelist.commands.WhitelistCommand;
import com.dumbdogdiner.betterwhitelist.commands.WhoisCommand;
import com.dumbdogdiner.betterwhitelist.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist.listeners.PlayerEventListener;
import com.dumbdogdiner.betterwhitelist.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist.utils.SQL;

import net.dv8tion.jda.api.JDA;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * The Bungee proxy plugin for propagating whitelist changes/bans to all
 * sub-server instances.
 */
public class BetterWhitelistBungee extends Plugin {

    private static BetterWhitelistBungee instance;

    public static BetterWhitelistBungee getInstance() {
        return instance;
    }

    private PluginConfig config;
    private SQL sql;
    private WhitelistBot bot;
    
    public PluginConfig getPluginConfig() {
    	return config;
    }
    
    public SQL getSQL() {
    	return sql;
    }
    
    public WhitelistBot getBot() {
    	return bot;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new PluginConfig();
        bot = new WhitelistBot();
        sql = new SQL();
        
        PluginManager manager = getProxy().getPluginManager();

        manager.registerListener(this, new PlayerEventListener());
        
        manager.registerCommand(this, new WhoisCommand());
        manager.registerCommand(this, new WhitelistCommand());
        manager.registerCommand(this, new UnwhitelistCommand());
    }

    @Override
    public void onDisable() {
        // Has the unfortunate downside of overriding changes made to the config.
        // Temporarily disabled since config isn't modified anywhere else in the plugin.
        /* PluginConfig.saveConfig(); */

        // Shut down the Discord bot gracefully.
        JDA jda = getBot().getJDA();
        if (jda != null) getBot().getJDA().shutdown();
        
        // Close the SQL datasource.
        getSQL().ds.close();
        

        getLogger().info("Aarrff!! (see you again soon :3)");
    }
}