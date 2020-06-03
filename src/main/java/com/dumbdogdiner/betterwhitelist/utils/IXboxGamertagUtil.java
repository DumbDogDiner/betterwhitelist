package com.dumbdogdiner.betterwhitelist.utils;

import java.util.Arrays;

public interface IXboxGamertagUtil {
	
	// Since Xbox Live Gamertags can include spaces, we need to use the array to get the full username.
	default String getGamertagFromArray(int offset, String[] arr) {
        String[] usernameArr = Arrays.copyOfRange(arr, offset, arr.length);
        return String.join(" ", usernameArr);
	}
	
	default String getGamertagFromArray(int offset, int to, String[] arr) {
		String[] usernameArr = Arrays.copyOfRange(arr, offset, to);
		return String.join(" ", usernameArr);
	}
}