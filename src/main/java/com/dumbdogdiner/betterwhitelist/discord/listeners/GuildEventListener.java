package com.dumbdogdiner.betterwhitelist.discord.listeners;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;

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
        disconnectWithMessage(e.getUser().getId(), ChatColor.RED + String.format(getConfig().getString("lang.player.guildMemberLeave"), e.getGuild().getName()));
    }

    @Override
    public void onGuildBan(GuildBanEvent e) {
        disconnectWithMessage(e.getUser().getId(), ChatColor.RED + String.format(getConfig().getString("lang.player.guildBan"), e.getGuild().getName()));
    }

    /**
     * Disconnect a Discord member (if they are connected) with a given message.
     * 
     * @param id
     * @param message
     */
    private void disconnectWithMessage(String id, String message) {
        if (!getConfig().getBoolean("discord.enableBanSync")) {
        	getLogger().info("[discord] " + String.format(getConfig().getString("lang.console.discord.banSyncDisabledWarning"), id));
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

        getLogger().info("[discord][ban] " + getConfig().getString("lang.console.discord.disconnectingIfStillOnline"));
        player.disconnect(new TextComponent(message));

        if (getSQL().removeEntry(id)) {
            getLogger().info("[discord][ban] " + String.format(getConfig().getString("lang.console.discord.userRemoved"), id));
        }
    }
}
