package com.dumbdogdiner.betterwhitelist.discord.commands;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.stream.Collectors;

public class HelpCommand extends Command implements BaseClass {

    public HelpCommand() {
        this.name = "help";
        this.description = "Shows the list of available bot commands.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendMessage(String.format(
                "**Aarrff!!** BetterWhitelist `v%s`\n\nAvailable Commands:\n - %s",
                BetterWhitelistBungee.getInstance().getDescription().getVersion(),
                getBot().getCommands().values().stream().map(this::formatCommandInfo).collect(Collectors.joining("\n - "))
        )).queue();
    }

    private String formatCommandInfo(Command command) {
        if (command.getSyntax() != null) {
            return String.format("`%s %s` - %s", command.getName(), command.getSyntax(), command.getDescription());
        } else {
            return String.format("`%s` - %s", command.getName(), command.getDescription());
        }
    }
}
