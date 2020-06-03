package com.dumbdogdiner.betterwhitelist.discord.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist.discord.utils.RatelimitUtil;
import com.dumbdogdiner.betterwhitelist.utils.IXboxGamertagUtil;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUser;
import com.dumbdogdiner.betterwhitelist.utils.XboxLiveUsernameValidator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class XUIDLookupCommand extends Command implements BaseClass, IXboxGamertagUtil {

    public XUIDLookupCommand() {
        this.name = "xuidlookup";
        this.description = "Resolve an Xbox Live username to a XUID.";

        // No ratelimit required.
        RatelimitUtil.registerRateLimit(this, 0.0);
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
    	 if (args.length == 0 || args[0] == null) {
             e.getChannel().sendMessage(
                     String.format(":x: Username not specified! Try `%sxuidlookup <username>`", getPluginConfig().getPrefix()))
                     .queue();
             return;
         }
         
        String username = getGamertagFromArray(0, args);
    	 
    	XboxLiveUser user = XboxLiveUsernameValidator.getUser(username, "commands.discord.xuidlookup");
 		
 		if (user != null) {
 			e.getChannel().sendMessage(String.format(":information_source:  User **%s** has XUID `%s`", user.getEscapedName(), user.getDecimalXUID())).queue();
 		} else {
 			e.getChannel().sendMessage(String.format(":x: **Whoops!** User `%s` does not exist!", escapeString(username))).queue();
 		}
    }
}
