package com.dumbdogdiner.betterwhitelist.utils;

/**
 * Wrapper class around the Mojang API response body.
 */
public class MojangUser {
    public String id;
    public String name;
    public int server;
    
    /**
     * Get the username but escaped to work with Discord.
     * @return {String} Discord-safe username.
     * @see {@link com.dumbdogdiner.betterwhitelist.utils.EscapedString}
     */
    public String getEscapedName() {
    	return name.replaceAll("/_+/g", "\\_");
    }
}
