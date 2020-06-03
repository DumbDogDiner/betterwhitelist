package com.dumbdogdiner.betterwhitelist.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BaseUsernameValidator {
	public static IUser getUser(String username, String ...origin) {
		throw new IllegalStateException("This should not be called!");
	}
	public static IUser getUser(ProxiedPlayer player) {
		throw new IllegalStateException("This should not be called!");
	}
	
	/*public static IUser getUser(ProxiedPlayer player) {
		if (player.getUniqueId().toString().startsWith("00000000-0000-0000-")) {
			return XboxLiveUsernameValidator.getCurrentPlatformUser(player);
		} else {
			return UsernameValidator.getCurrentPlatformUser(player);
		}
	}*/
	
    /**
     * Mojang API sends back de-hyphenated UUIDs. This is a util method to add those
     * hyphens back in.
     * 
     * @param uuid
     * @return
     */
    protected static String hyphenateUUID(String uuid) {
        return uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5");
    }
    
    /**
     * Reads all of a request's body and returns a concatenated string of the
     * contents.
     * 
     * @param username
     * @return
     */
    protected static String fetchUserJson(String formedUrl, String username, String ...origin) {
    	try {
    		//URL url = new URL(formUrl(username));
    		URL url = new URL(formedUrl);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	
    		conn.setRequestMethod("GET");
    		conn.setRequestProperty("User-Agent", String.format("DumbDogDiner/%s/%s", BetterWhitelistBungee.getInstance().getDescription().getName(), BetterWhitelistBungee.getInstance().getDescription().getVersion()));
    		conn.setRequestProperty("Accept", "application/json");
    		conn.setRequestProperty("Accept-Charset", "UTF-8");
    		
    		// Set origin header if available for endpoint logging.
    		conn.setRequestProperty("X-DDD-Origin", (origin.length >= 1) ? origin[0] : "unknown");
    		
    		conn.setDoInput(true);
    		conn.setUseCaches(false);
    	
    		int responseCode = conn.getResponseCode();
    	
    		if (responseCode == 200) {
    			// User found.
    			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        	
    			StringBuilder builder = new StringBuilder();
    			int character;
    			while ((character = reader.read()) != -1) {
    				builder.append((char) character);
    			}

    			reader.close();
            
    			// Close the HTTP connection
    			conn.disconnect();

    			return builder.toString();
    			
    		} else if (responseCode == 404) {
    			// User does not exist.
    			return null;
    		} else {
    			// Unexpected HTTP Response Code.
    			BetterWhitelistBungee.getInstance().getLogger().severe(String.format("[UsernameValidator] Unexpected HTTP Status Code! (%s)", responseCode));
    			return null;
    		}
    	} catch (IOException ex) {
    		// IOException - could not connect to server.
    		BetterWhitelistBungee.getInstance().getLogger().severe("[UsernameValidator] Encountered IOException! (Error connecting to HTTP server!)");
    		ex.printStackTrace();
    		return null;
    		
    	} catch (Exception ex) {
    		BetterWhitelistBungee.getInstance().getLogger().severe("[UsernameValidator] Encountered Exception!");
    		ex.printStackTrace();
    		return null;
    	}

    }
}