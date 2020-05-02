package com.dumbdogdiner.betterwhitelist.listeners;

import java.util.List;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
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
    public void onPreLoginEvent(PreLoginEvent e) {
        if (getConfig().getBoolean("disableUuidChecking")) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .info("Skipping handling new player connection - checking disabled.");
            return;
        }

        // Can someone specify what type of data is in this list, thanks!
        List<?> playerOverrides = getConfig().getList("overrides");
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

        if (getSQL().getDiscordIDFromMinecraft(user.id) == null) {
            e.setCancelled(true);
            e.setCancelReason(new TextComponent(ChatColor.RED + "You are not whitelisted on this network!"));
        }
    }
}
