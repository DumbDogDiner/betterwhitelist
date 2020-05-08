package com.dumbdogdiner.betterwhitelist.utils;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;

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
    	
		if (config.getInt("version") != 2) {
			getLogger().info("Version 1 (<2.2.0) file detected! Attempting to migrate...");
			migrateV1();
			getLogger().info("Migration completed!");
			
			loadConfig();
		}
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
        getLogger().info(getConfig().getString("lang.console.config.save"));
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
	        getLogger().severe(getConfig().getString("lang.console.config.loadError"));
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
    
    private Configuration getNewInternalConfiguration() throws IOException {
    	File file = File.createTempFile("betterwhitelist", "bt");
			
		InputStream in = this.getResourceAsStream("config.yml");
		OutputStream out = new FileOutputStream(file);
		ByteStreams.copy(in, out);
			
		Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
		return conf;
    }
    
    private void migrateV1() {
    	try {
    		Configuration newConf = getNewInternalConfiguration();
    		
    		String[] data = {
    				"enableSql",
    				"disableUuidChecking",
    				"mysql.host",
    				"mysql.database",
    				"mysql.port",
    				"mysql.username",
    				"mysql.password",
    				"overrides",
    				"discord.token",
    				"discord.guildId",
    				"discord.prefix",
    				"discord.enableSelfWhitelisting",
    				"discord.enableBanSync",
    				"discord.oneAccountPerUser",
    				"discord.roles.requiredRole.enabled",
    				"discord.roles.requiredRole.roleId",
    				"discord.roles.grantedRole.enabled",
    				"discord.roles.grantedRole.silent",
    				"discord.roles.grantedRole.roleId"
    			};
    		
    		
    		// Migrate the data.
    		newConf = ValueMigrator(newConf, data);
    		
    		// Newconf now contains all new defaults + old data, now write it.
    		writeConfig(newConf);
    		
    	} catch (IOException ex) {
    		getLogger().severe("[config] [migration] Failed to migrate!\n" + ex);
    	}
    }
    
    public Configuration ValueMigrator(Configuration newConfig, String[] data) {
    	for (String item : data) newConfig.set(item, getConfig().get(item));
    	return newConfig;
    		
    }
}
