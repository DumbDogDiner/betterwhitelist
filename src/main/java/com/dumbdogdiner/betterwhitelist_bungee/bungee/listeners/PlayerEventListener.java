package com.dumbdogdiner.betterwhitelist_bungee.bungee.listeners;

import java.util.List;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQL;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Listen for player joins and check them against the SQL database.
 */
public class PlayerEventListener implements Listener {
    @EventHandler
    /**
     * Check whether players are allowed to log in.
     */
    public void onPreLoginEvent(PreLoginEvent e) {
        if (PluginConfig.getConfig().getBoolean("disableUuidChecking")) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .info("Skipping handling new player connection - checking disabled.");
            return;
        }

        // Can someone specify what type of data is in this list, thanks!
        List<?> playerOverrides = PluginConfig.getConfig().getList("overrides");
        if (playerOverrides.contains(e.getConnection().getName())) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .info("Skipping handling new player connection - user is in overrides.");
            return;
        }

        MojangUser user = UsernameValidator.getUser(e.getConnection().getName());

        if (user == null) {
            return;
        }

        BetterWhitelistBungee.getInstance().getLogger().info("Checking that UUID '" + user.id + "' is whitelisted...");

        if (SQL.getDiscordIDFromMinecraft(user.id) == null) {
            e.setCancelled(true);
            e.setCancelReason(new TextComponent(ChatColor.RED + "You are not whitelisted on this network!"));
        }
    }
}
