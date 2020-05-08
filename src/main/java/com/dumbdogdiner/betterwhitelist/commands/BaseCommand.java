package com.dumbdogdiner.betterwhitelist.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.commands.sub.RootSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.UnwhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.WhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.HelpSubCommand;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BaseCommand extends Command implements BaseClass {
	
	private final RootSubCommand root;
	private final WhitelistSubCommand whitelist;
	private final UnwhitelistSubCommand unwhitelist;
	private final HelpSubCommand help;

    public BaseCommand() {
        super("betterwhitelist", "betterwhitelist.read.base");
        
        root = new RootSubCommand();
        whitelist = new WhitelistSubCommand();
        unwhitelist = new UnwhitelistSubCommand();
        help = new HelpSubCommand();
        
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		// Root command [/betterwhitelist]
		if (args.length == 0) {
			root.execute(sender);
			
		// Whitelist command [/betterwhitelist whitelist [..] [..]
		} else if (args[0] == "whitelist" && sender.hasPermission("betterwhitelist.admin.whitelist")) {
			whitelist.execute(sender, args);
			
		// Unwhitelist command [/betterwhitelist unwhitelist [..]
		} else if (args[0] == "unwhitelist" && sender.hasPermission("betterwhitelist.admin.unwhitelist")) {
			unwhitelist.execute(sender, args);
			
		// Help command = [/betterwhitelist help]
		} else if (args[0] == "help") {
			help.execute(sender);
			
		// Catch-all, show help
		} else {
			help.execute(sender);
		}
	}
}
