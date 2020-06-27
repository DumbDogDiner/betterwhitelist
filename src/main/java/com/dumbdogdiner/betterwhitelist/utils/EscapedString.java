package com.dumbdogdiner.betterwhitelist.utils;

public class EscapedString {
	/**
	 * Escape the markdown elements of a string. (Discord uses a subset of markdown)
	 * @param str The string to escape.
	 * @return {String} The escaped string.
	 */
	public static String escapeString(String str) {
		return str
				.replaceAll("\\/+", "\\\\/") // '/' --> '\/'	Discord extension.
				
				//.replaceAll("\\+", "\\\\") // '\' --> '\\'	Does not work in discord.
				
				.replaceAll("`+", "\\\\`")     // '`' --> '\`'
				.replaceAll("\\*+", "\\\\*")   // '*' --> '\*'
				.replaceAll("_+", "\\\\_")     // '_' --> '\_'
				
				.replaceAll("\\{+", "\\\\{")   // '{' --> '\{'	Not required for discord, can be used for bots.
				.replaceAll("\\}+", "\\\\}")   // '}' --> '\}'	Not required for discord, can be used for bots.
				.replaceAll("\\[+", "\\\\[")   // '[' --> '\['	Not required for discord, can be used for bots.
				.replaceAll("\\]+", "\\\\]")   // ']' --> '\]'	Not required for discord, can be used for bots.
				.replaceAll("\\(+", "\\\\(")   // '(' --> '\('	Not required for discord, can be used for bots.
				.replaceAll("\\)+", "\\\\)")   // ')' --> '\)'	Not required for discord, can be used for bots.
				.replaceAll("\\++", "\\\\+")   // '+' --> '\+'	Not required for discord, can be used for bots.
				.replaceAll("-+", "\\\\-")     // '-' --> '\-'	Not required for discord, can be used for bots.
				.replaceAll("\\.+", "\\\\.")   // '.' --> '\.'	Not required for discord, can be used for bots.
				.replaceAll("!+", "\\\\!")     // '!' --> '\!'	Not required for discord, can be used for bots.
		
				.replaceAll("#+", "\\\\#"); // '#' --> '\#'	Does not work with discord channel names, server side bug.
	}
}