package com.dumbdogdiner.betterwhitelist.utils;

public interface IUser {
	String getName();
	String getID();
	String getType();
	
	/**
     * Get the username but escaped to work with Discord.
     * @return {String} Discord-safe username.
     * @see {@link com.dumbdogdiner.betterwhitelist.utils.EscapedString}
     */
    public default String getEscapedName() {
    	return getName().replaceAll("/_+/g", "\\_");
    }
}