package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BaseClass;
import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Listener that waits for members to leave/be banned and removes them from the
 * Minecraft whitelist.
 */
public class GuildEventListener extends ListenerAdapter implements BaseClass {
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent e) {
        disconnectWithMessage(e.getUser().getId(), ChatColor.RED + "You have left " + e.getGuild().getName()
                + " and have been removed from the whitelist!");
    }

    @Override
    public void onGuildBan(GuildBanEvent e) {
        disconnectWithMessage(e.getUser().getId(), ChatColor.RED + "You were banned from " + e.getGuild().getName()
                + " and have been removed from the whitelist!");
    }

    /**
     * Disconnect a Discord member (if they are connected) with a given message.
     * 
     * @param id
     * @param message
     */
    private void disconnectWithMessage(String id, String message) {
        if (!getConfig().getBoolean("discord.enableBanSync")) {
            getLogger()
                    .info("[discord] Not removing user '" + id + "' from whitelist - enableBanSync=false");
            return;
        }

        // Disconnect the player if they are connected.
        String playerUuid = getSQL().getUuidFromDiscordId(id);
        if (playerUuid == null) {
            return;
        }

        ProxiedPlayer player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(UUID.fromString(playerUuid));
        if (player == null) {
            return;
        }

        getLogger().info("[discord][ban] Disconnecting player if they are still online...");
        player.disconnect(new TextComponent(message));

        if (getSQL().removeEntry(id)) {
            getLogger()
                    .info("[discord][ban] Removed user with Discord ID '" + id + "' from the whitelist.");
        }
    }
}
