package com.dumbdogdiner.betterwhitelist.discord.utils;

import com.dumbdogdiner.betterwhitelist.BaseClass;
import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist.discord.lib.Command;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Class for managing and ratelimiting users to prevent spamming of commands.
 */
public class RatelimitUtil implements BaseClass {
    /**
     * Get an instance of this class, which contains BaseClass.
     * (static functions can't access interfaces)
     */
    private static RatelimitUtil getBase() {
        return new RatelimitUtil();
    }

    private static HashMap<String, Double> ratelimitLengths = new HashMap<>();
    private static HashMap<String, HashMap<String, Double>> userRatelimitData = new HashMap<>();

    /**
     * Register a command that requires rate-limiting.
     * @param command The name of the command
     * @param length The duration in seconds inbetween which users must wait before being able to run the command agian.
     */
    public static <T extends Command> void registerRateLimit(T command, Double length) {
        ratelimitLengths.put(command.getName(), length * 1000);
        userRatelimitData.put(command.getName(), new HashMap<>());
    }

    /**
     * Fetch whether a user is being ratelimited for a particular command.
     * @param command The command to test the ratelimit for.
     * @param user The user to test
     * @return Ratelimited?
     */
    public static boolean isRatelimited(Command command, User user) {
        return fetchTimeRemaining(command, user) != 0.0;
    }

    /**
     * Fetch the remaining time of a ratelimit for a particular user.
     * @param command The command with the ratelimit applied.
     * @param user The user being ratelimited.
     * @return Remaining time
     */
    public static Double fetchTimeRemaining(Command command, User user) {
        var duration = ratelimitLengths.get(command.getName());
        var lastUsed = userRatelimitData.get(command.getName()).get(user.getId());

        if (duration == null || lastUsed == null) {
            return 0.0;
        }

        var raw = duration - (System.currentTimeMillis() - lastUsed);
        return Math.max(raw, 0.0);
    }

    /**
     * Fetch a nicely formatted message informing the user how much longer they must wait.
     * @param command The command with the ratelimit applied
     * @param user The user being ratelimited
     * @return Formatted message
     */
    public static String getRatelimitMessage(Command command, User user) {
        // TODO: Need localisation for this :3
        NumberFormat format = new DecimalFormat("#0.0");

        return String.format(
                ":alarm_clock: Please wait another `%s` seconds before running this command again.",
                format.format(fetchTimeRemaining(command, user) / 1000)
        );
    }

    /**
     * Method which marks the given user's time of command call.
     * @param command
     * @param user
     */
    public static void userDidRunCommand(Command command, User user) {
        if (userRatelimitData.get(command.getName()) != null) {
            userRatelimitData.get(command.getName()).put(
                    user.getId(),
                    (double) System.currentTimeMillis()
            );
        }
    }

    /**
     * Method to clear the ratelimit data to save on memory.
     */
    public static void cleanRatelimits() {
        userRatelimitData.clear();
    }
}
