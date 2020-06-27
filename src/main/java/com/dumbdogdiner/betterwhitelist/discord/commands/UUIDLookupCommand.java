package com.dumbdogdiner.betterwhitelist.discord.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import com.dumbdogdiner.betterwhitelist.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist.utils.UsernameValidator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UUIDLookupCommand extends Command implements BaseClass {

    public UUIDLookupCommand() {
        this.name = "uuidlookup";
        this.description = "Resolve a username to a UUID.";

        // No ratelimit required.
        RatelimitUtil.registerRateLimit(this, 0.0);
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
    	 if (args.length == 0 || args[0] == null) {
             e.getChannel().sendMessage(
                     String.format(":x: Username not specified! Try `%suuidlookup <username>`", getPluginConfig().getPrefix()))
                     .queue();
             return;
         }
    	 
    	 MojangUser user = UsernameValidator.getUser(args[0], "commands.discord.uuidlookup");
 		
 		if (user != null) {
 			e.getChannel().sendMessage(String.format(":information_source:  User **%s** has UUID `%s`  (server #%s)", user.getEscapedName(), user.id, user.server)).queue();
 		} else {
 			e.getChannel().sendMessage(String.format(":x: **Whoops!** User `%s` does not exist!", escapeString(args[0]))).queue();
 		}
    }
}
