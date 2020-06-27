package com.dumbdogdiner.betterwhitelist.utils;

/**
 * Wrapper class around the Xbox Live XUID API response body.
 */
public class XboxLiveUser implements IUser {
    public String gamertag;
    public String xuid;
    
    @Override
    public String getType() {
    	return "xbl";
    }
    
	@Override
	public String getName() {
		return gamertag;
	}
	/**
	 * Get a valid hex UUID from the decimal xuid.
	 * 
	 * 36-character (hex) UUID with hyphens.
	 */
	@Override
	public String getID() {
		return BaseUsernameValidator.hyphenateUUID(String.format("%32s", getHexXUID()).replace(" ", "0"));
	}
	
	public String getDecimalXUID() {
		return xuid;
	}
	
	// UUIDs are hex. XUIDS are decimal.
	public String getHexXUID() {
		// Integers can't handle XUIDs, use longs instead.
		return Long.toHexString(Long.parseLong(getDecimalXUID()));
	}
}