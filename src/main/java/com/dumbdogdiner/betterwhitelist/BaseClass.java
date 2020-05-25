package com.dumbdogdiner.betterwhitelist;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import com.dumbdogdiner.betterwhitelist.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist.utils.EscapedString;
import com.dumbdogdiner.betterwhitelist.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist.utils.SQL;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.Configuration;

public interface BaseClass {
	default BetterWhitelistBungee getInstance() {
		return BetterWhitelistBungee.getInstance();
	}
	
	default File getDataFolder() {
		return getInstance().getDataFolder();
	}
	
	default PluginDescription getPluginDescription() {
		return getInstance().getDescription();
	}
	
	default File getFile() {
		return getInstance().getFile();
	}
	
	default Logger getLogger() {
		return getInstance().getLogger();
	}
	
	default ProxyServer getProxy() {
		return getInstance().getProxy();
	}
	
	default InputStream getResourceAsStream(String name) {
		return getInstance().getResourceAsStream(name);
	}
	
	// Custom
	
	default PluginConfig getPluginConfig() {
		return getInstance().getPluginConfig();
	}
	
	default Configuration getConfig() {
		return getInstance().getPluginConfig().getConfig();
	}
	
	default SQL getSQL() {
		return getInstance().getSQL();
	}
	
	default WhitelistBot getBot() {
		return getInstance().getBot();
	}
	
	default String escapeString(String str) {
		return EscapedString.escapeString(str);
	}
}