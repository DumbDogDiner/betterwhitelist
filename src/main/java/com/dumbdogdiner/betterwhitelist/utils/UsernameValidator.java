package com.dumbdogdiner.betterwhitelist.utils;

import com.google.gson.Gson;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static MojangUser getUser(String username) {
        // Make request to Mojang and decode JSON body.
        String json = fetchUserJson(username);

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
    private static String fetchUserJson(String username) {
        try {
            InputStream input = new URL(formUrl(username)).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                builder.append((char) character);
            }

            input.close();
            reader.close();

            return builder.toString();
        } catch (FileNotFoundException err) {
        	// User does not exist, supress the trace and return null.
        	return null;
        } catch (Exception err) {
            err.printStackTrace();
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
