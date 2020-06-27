package com.dumbdogdiner.betterwhitelist.discord.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import com.dumbdogdiner.betterwhitelist.discord.utils.RoleUtil;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class XblUnwhitelistCommand extends Command implements BaseClass {

    public XblUnwhitelistCommand() {
        this.name = "xblunwhitelist";
        this.description = "Remove yourself from the whitelist of the Minecraft server.";

        // Require ratelimit
        RatelimitUtil.registerRateLimit(this, 5.0);
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendTyping().queue();

        Member target = e.getMember();

        if (e.getMessage().getMentionedMembers().size() > 0) {
            Member member = e.getMessage().getMentionedMembers().get(0);
            if (!Objects.requireNonNull(e.getMember()).canInteract(member)) {
                e.getChannel().sendMessage(getConfig().getString("lang.discord.userRemovedPermissionError")).queue();
                return;
            }
            target = member;
        }

        if (target == null) {
            e.getChannel().sendMessage(getConfig().getString("lang.discord.userRemovedNotFoundError")).queue();
            return;
        }

        if (getSQL().removeEntry(String.format("X%S", target.getId()))) {
            e.getChannel().sendMessage(
            		String.format(getConfig().getString("lang.discord.userRemoved"), target.getUser().getAsTag()))
                    .queue(message -> RoleUtil.removeGrantedRole(e));
        } else {
            e.getChannel().sendMessage(getConfig().getString("lang.discord.userRemovedError")).queue();
        }
    }
}
