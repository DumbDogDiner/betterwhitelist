package com.dumbdogdiner.betterwhitelist.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.commands.sub.RootSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.UUIDLookupSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.UnwhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.WhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.WhoisSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.XUIDLookupSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.XblUnwhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.XblWhitelistSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.XblWhoisSubCommand;
import com.dumbdogdiner.betterwhitelist.commands.sub.HelpSubCommand;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BaseCommand extends Command implements BaseClass {
	
	private final RootSubCommand root;
	private final WhitelistSubCommand whitelist;
	private final XblWhitelistSubCommand xblwhitelist;
	private final UnwhitelistSubCommand unwhitelist;
	private final XblUnwhitelistSubCommand xblunwhitelist;
	private final HelpSubCommand help;
	private final UUIDLookupSubCommand uuid;
	private final XUIDLookupSubCommand xuid;
	private final WhoisSubCommand whois;
	private final XblWhoisSubCommand xblwhois;

    public BaseCommand() {
        super("betterwhitelist", "betterwhitelist.read.base");
        
        root = new RootSubCommand();
        whitelist = new WhitelistSubCommand();
        xblwhitelist = new XblWhitelistSubCommand();
        unwhitelist = new UnwhitelistSubCommand();
        xblunwhitelist = new XblUnwhitelistSubCommand();
        help = new HelpSubCommand();
        uuid = new UUIDLookupSubCommand();
        xuid = new XUIDLookupSubCommand();
        whois = new WhoisSubCommand();
        xblwhois = new XblWhoisSubCommand();
        
        
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		// Root command [/betterwhitelist]
		if (args.length == 0) {
			root.execute(sender);
			
		// Whitelist command [/betterwhitelist whitelist [..] [..]
		} else if (args[0].equalsIgnoreCase("whitelist") && sender.hasPermission("betterwhitelist.admin.whitelist")) {
			whitelist.execute(sender, args);
			
		// Xbox Live Whitelist command [/betterwhitelist xblwhitelist [..] [..]
		} else if (args[0].equalsIgnoreCase("xblwhitelist") && sender.hasPermission("betterwhitelist.admin.xblwhitelist")) {
			xblwhitelist.execute(sender, args);
			
		// Unwhitelist command [/betterwhitelist unwhitelist [..]
		} else if (args[0].equalsIgnoreCase("unwhitelist") && sender.hasPermission("betterwhitelist.admin.unwhitelist")) {
			unwhitelist.execute(sender, args);
			
		// Xbox Live Unwhitelist command [/betterwhitelist xblunwhitelist [..]
		} else if (args[0].equalsIgnoreCase("xblunwhitelist") && sender.hasPermission("betterwhitelist.admin.xblunwhitelist")) {
			xblunwhitelist.execute(sender, args);
			
		// Help command = [/betterwhitelist help]
		} else if (args[0].equalsIgnoreCase("help")) {
			help.execute(sender);
		
		// UUID Lookup command = [/betterwhitelist uuidlookup <username>]
		} else if (args[0].equalsIgnoreCase("uuidlookup")) {
			uuid.execute(sender, args);
			
		// XUID (Xbox Live User ID) Lookup command = [/betterwhitelist xuidlookup <gamertag>]
		} else if (args[0].equalsIgnoreCase("xuidlookup")) {
			xuid.execute(sender, args);
			
		// Whois command = [/betterwhitelist whois <minecraft|discord> [..]]
		} else if (args[0].equalsIgnoreCase("whois")) {
			whois.execute(sender, args);
			
		// Xbove Live Whois command = [/betterwhitelist xblwhois <minecraft|discord> [..]]
		} else if (args[0].equalsIgnoreCase("xblwhois")) {
			xblwhois.execute(sender, args);
			
		// Catch-all, show help
		} else {
			help.execute(sender);
		}
	}
}
