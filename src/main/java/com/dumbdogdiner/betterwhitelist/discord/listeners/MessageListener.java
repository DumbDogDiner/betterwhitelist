package com.dumbdogdiner.betterwhitelist.discord.listeners;

import com.dumbdogdiner.betterwhitelist.BaseClass;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

/**
 * Listens for new messages.
 */
public class MessageListener extends ListenerAdapter implements BaseClass {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String rawContent = e.getMessage().getContentRaw();
        String prefix = getConfig().getString("discord.prefix");
        String guildId = getConfig().getString("discord.guildId");

        if (e.getAuthor().isBot() || e.getChannelType() == ChannelType.PRIVATE  || !e.getGuild().getId().equals(guildId)) {
            return;
        }

        // If bot user was mentioned, but not via @everyone
        if (e.getMessage().isMentioned(getBot().getJDA().getSelfUser()) && !e.getMessage().mentionsEveryone()) {
            e.getChannel().sendMessage(String.format(getConfig().getString("lang.discord.mentioned"), prefix)).queue();
            return;
        }

        if (
            !rawContent.startsWith(prefix)
        ) {
            return;
        }

        String content = rawContent.substring(prefix.length());
        String[] args = content.split(" ");
        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        if (!getBot().getCommands().containsKey(commandName)) {
            e.getChannel().sendMessage(String.format(getConfig().getString("lang.discord.unknownCommand"), commandName, prefix)).queue();
            return;
        }

        getLogger().info(String.format("[discord] %s (%s) => %s", e.getAuthor().getAsTag(),e.getAuthor().getId(), commandName));

        try {
            getBot().getCommands().get(commandName).execute(e, args);
        } catch(Exception err) {
        	// Not *really* any point to localise imo, but feel free to do so if you please :)
            getBot().getLogger().severe("Error in command '" + commandName + "':");
            err.printStackTrace();

            e.getChannel().sendMessage(String.format(
            	getConfig().getString("lang.discord.internalError"),
                err.getClass().getCanonicalName()
            )).queue();
       }
    }
}
