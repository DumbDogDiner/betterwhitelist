package com.dumbdogdiner.betterwhitelist.utils;

import com.google.gson.Gson;

/**
 * Class for validating Minecraft usernames.
 */
public class XboxLiveUsernameValidator extends BaseUsernameValidator {

    /**
     * Fetch the UUID of a player from their Minecraft username.
     * 
     * @param username The name of the user to fetch from the API.
     * @return Mojang API user object
     */
    public static XboxLiveUser getUser(String username, String ...origin) {
        // Make request to Mojang and decode JSON body.
        String json = fetchUserJson(formUrl(username), username, origin);

        if (json == null || json.equals("")) {
            return null;
        }

        XboxLiveUser result = new Gson().fromJson(json, XboxLiveUser.class);
        result.xuid = hyphenateUUID(result.xuid);

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
        String baseUrl = "https://xbl-api.prouser123.me/xuid/";
        return String.format("%s%s", baseUrl, username);
    }
}
