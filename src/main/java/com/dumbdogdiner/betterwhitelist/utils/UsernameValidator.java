package com.dumbdogdiner.betterwhitelist.utils;

import com.dumbdogdiner.betterwhitelist.BetterWhitelistBungee;
import com.google.gson.Gson;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Class for validating Minecraft usernames.
 */
public class UsernameValidator {

    /**
     * Fetch the UUID of a player from their Minecraft username.
     * 
     * @param username The name of the user to fetch from the API.
     * @return Mojang API user object
     */
    public static MojangUser getUser(String username, String ...origin) {
        // Make request to Mojang and decode JSON body.
        String json = fetchUserJson(username, origin);

        if (json == null || json.equals("")) {
            return null;
        }

        MojangUser result = new Gson().fromJson(json, MojangUser.class);
        result.id = hyphenateUUID(result.id);

        return result;
    }
    
    /**
     * Create a MojangUser instance from ProxiedPlayer data.
     * 
     * @param ProxiedPlayer instance.
     * @return Mojang API user object.
     */
    public static MojangUser getUser(ProxiedPlayer player) {
    	MojangUser result = new MojangUser();
    	
    	result.id = hyphenateUUID(player.getUniqueId().toString());
    	result.name = player.getName();
    	
    	return result;
    }

    /**
     * Forms the base URL for the Mojang API request.
     * 
     * @param username
     * @return
     */
    private static String formUrl(String username) {
        // caching, load-balancing UUID server.
        String baseUrl = "https://mcuuid.jcx.ovh/v1/uuid/";
        return String.format("%s%s", baseUrl, username);
    }

    /**
     * Reads all of a request's body and returns a concatenated string of the
     * contents.
     * 
     * @param username
     * @return
     */
    private static String fetchUserJson(String username, String ...origin) {
    	try {
    		URL url = new URL(formUrl(username));
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

    /**
     * Mojang API sends back de-hyphenated UUIDs. This is a util method to add those
     * hyphens back in.
     * 
     * @param uuid
     * @return
     */
    private static String hyphenateUUID(String uuid) {
        return uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5");
    }
}
