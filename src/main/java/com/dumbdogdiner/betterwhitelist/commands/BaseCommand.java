package com.dumbdogdiner.betterwhitelist.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.commands.sub.RootSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.UUIDLookupSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.UnwhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.WhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.WhoisSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.HelpSubCommand;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BaseCommand extends Command implements BaseClass {
	
	private final RootSubCommand root;
	private final WhitelistSubCommand whitelist;
	private final UnwhitelistSubCommand unwhitelist;
	private final HelpSubCommand help;
	private final UUIDLookupSubCommand uuid;
	private final WhoisSubCommand whois;

    public BaseCommand() {
        super("betterwhitelist", "betterwhitelist.read.base");
        
        root = new RootSubCommand();
        whitelist = new WhitelistSubCommand();
        unwhitelist = new UnwhitelistSubCommand();
        help = new HelpSubCommand();
        uuid = new UUIDLookupSubCommand();
        whois = new WhoisSubCommand();
        
        
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		// Root command [/betterwhitelist]
		if (args.length == 0) {
			root.execute(sender);
			
		// Whitelist command [/betterwhitelist whitelist [..] [..]
		} else if (args[0].equalsIgnoreCase("whitelist") && sender.hasPermission("betterwhitelist.admin.whitelist")) {
			whitelist.execute(sender, args);
			
		// Unwhitelist command [/betterwhitelist unwhitelist [..]
		} else if (args[0].equalsIgnoreCase("unwhitelist") && sender.hasPermission("betterwhitelist.admin.unwhitelist")) {
			unwhitelist.execute(sender, args);
			
		// Help command = [/betterwhitelist help]
		} else if (args[0].equalsIgnoreCase("help")) {
			help.execute(sender);
		
		// UUID Lookup command = [/betterwhitelist uuidlookup <username>]
		} else if (args[0].equalsIgnoreCase("uuidlookup")) {
			uuid.execute(sender, args);
			
		// Whois command = [/betterwhitelist whois <minecraft|discord> [..]]
		} else if (args[0].equalsIgnoreCase("whois")) {
			whois.execute(sender, args);
			
		// Catch-all, show help
		} else {
			help.execute(sender);
		}
	}
}
