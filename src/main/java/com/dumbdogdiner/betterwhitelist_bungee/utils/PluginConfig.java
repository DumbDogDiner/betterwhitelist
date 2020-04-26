package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BaseClass;
import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PluginConfig implements BaseClass {

    public PluginConfig() {
    	loadConfig();
    }
    
    private Configuration config;

    /**
     * Fetch and cache the configuration file for the plugin.
     */
    public Configuration getConfig() {
    	return config;
    }

    /**
     * Return the prefix used by the Discord bot.
     * @return
     */
    public String getPrefix() {
        String prefix = getConfig().getString("discord.prefix");
        return prefix == null ? "-" : prefix;
    }
    /**
     * Save the current cached config to disk.
     * @return
     */
    public boolean saveConfig() {
        getLogger().info("Saving configuration to disk...");
        return writeConfig(config);
    }

    /**
     * Write the provided configuration to 'config.yml'.
     * @param configuration
     */
    private boolean writeConfig(Configuration configuration) {
        File file =  new File(BetterWhitelistBungee.getInstance().getDataFolder(), "config.yml");

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
            return true;
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch the 'config.yml' stored in the plugin data folder, and set it to the private 'config' variable.
     */
    private void loadConfig() {
    	
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(loadResource("config.yml"));
		} catch (IOException e) {
			getLogger().severe("Error loading config.yml");
		}
    }
    
    /**
     * Load a resource from the plugin's folder, creating it if it does not already exist.
     * @param resource Filename to load.
     */
    private File loadResource(String resource) {
            File folder = this.getDataFolder();
            if (!folder.exists())
                folder.mkdir();
            File resourceFile = new File(folder, resource);
            try {
                if (!resourceFile.exists()) {
                    resourceFile.createNewFile();
                    try (InputStream in = this.getResourceAsStream(resource);
                         OutputStream out = new FileOutputStream(resourceFile)) {
                        ByteStreams.copy(in, out);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resourceFile;
        }
}
