package com.dumbdogdiner.betterwhitelist.utils;

/**
 * Wrapper class around the Mojang API response body.
 */
public class MojangUser implements IUser {
    public String id;
    public String name;
    public int server;
    
    @Override
    public String getType() {
    	return "mojang";
    }
    
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getID() {
		return id;
	}
}