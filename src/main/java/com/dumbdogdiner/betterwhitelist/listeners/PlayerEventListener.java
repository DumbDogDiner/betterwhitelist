package com.dumbdogdiner.betterwhitelist.listeners;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Listen for player joins and check them against the SQL database.
 */
public class PlayerEventListener implements Listener, BaseClass {
    @EventHandler
    /**
     * Check whether players are allowed to log in.
     */
    public void onPostLogin(PostLoginEvent event) {
    	ProxiedPlayer player = event.getPlayer();
    	
        if (getConfig().getBoolean("disableUuidChecking")) {
            getLogger().info(getConfig().getString("lang.console.postLogin.uuidCheckingDisabled"));
            return;
        }
        
        MojangUser user =  UsernameValidator.getUser(player);
        
        // User has bypass permission.
        if (player.hasPermission("betterwhitelist.bypass")) {
        	getLogger().info(String.format("'%s' (UUID %s) has a bypass!", user.getName(), user.getID()));
        	return;
        };
        
        getLogger().info(String.format(getConfig().getString("lang.console.postLogin.checkingUuid"), user.getID()));

        if (getSQL().getDiscordIDFromMinecraft(user.getID()) == null) {
        	// User is not whitelisted.
        	player.disconnect(new TextComponent(ChatColor.RED + getConfig().getString("lang.player.disconnectMessage")));
        }
        
        // no use - users are always treated as mojang users here. - getLogger().info(String.format("[join] [PlayerEventListener] Sucessfully processed player %s (%s) with uuid %s (IUser id '%s')", user.getName(), user.getType(), player.getUniqueId(), user.getID()));
    }
}
