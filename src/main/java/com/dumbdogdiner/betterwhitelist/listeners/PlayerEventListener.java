package com.dumbdogdiner.betterwhitelist.listeners;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
    public void onPostLoginEvent(ProxiedPlayer player) {
        if (getConfig().getBoolean("disableUuidChecking")) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .info(getConfig().getString("lang.console.postLogin.uuidCheckingDisabled"));
            return;
        }
        
        // User has bypass permission.
        if (player.hasPermission("betterwhitelist.bypass")) return;

        MojangUser user = UsernameValidator.getUser(player);

        BetterWhitelistBungee.getInstance().getLogger().info(String.format(getConfig().getString("lang.console.postLogin.checkingUuid"), user.id));

        if (getSQL().getDiscordIDFromMinecraft(user.id) == null) {
        	// User is not whitelisted.
        	player.disconnect(new TextComponent(ChatColor.RED + getConfig().getString("lang.player.disconnectMessage")));
        }
    }
}
