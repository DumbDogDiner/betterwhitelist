package com.dumbdogdiner.betterwhitelist.utils;

import static com.dumbdogdiner.betterwhitelist.utils.EscapedString.escapeString;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscapedStringTest {
	
	// Note that you *must* always escape a backslash '\' in Java.
	
	@Test
	public void testSlash() {
		assertEquals(escapeString("/"), "\\/");
	}
	
	@Test
	public void testTilde() {
		assertEquals(escapeString("`"), "\\`");
	}
	
	@Test
	public void testStar() {
		assertEquals(escapeString("*"), "\\*");
	}
	
	@Test
	public void testUnderscore() {
		assertEquals(escapeString("_"), "\\_");
	}
	
	@Test
	public void testOpenBracket() {
		assertEquals(escapeString("{"), "\\{");
	}
	
	@Test
	public void testCloseBracket() {
		assertEquals(escapeString("}"), "\\}");
	}
	
	@Test
	public void testOpenSquareBracket() {
		assertEquals(escapeString("["), "\\[");
	}
	
	@Test
	public void testCloseSquareBracket() {
		assertEquals(escapeString("]"), "\\]");
	}
	
	@Test
	public void testOpenParentheses() {
		assertEquals(escapeString("("), "\\(");
	}
	
	@Test
	public void testCloseParentheses() {
		assertEquals(escapeString(")"), "\\)");
	}
	
	@Test
	public void testPlus() {
		assertEquals(escapeString("+"), "\\+");
	}
	
	@Test
	public void testMinus() {
		assertEquals(escapeString("-"), "\\-");
	}
	
	@Test
	public void testDot() {
		assertEquals(escapeString("."), "\\.");
	}
	
	@Test
	public void testExclamationMark() {
		assertEquals(escapeString("!"), "\\!");
	}
	
	@Test
	public void testHash() {
		assertEquals(escapeString("#"), "\\#");
	}
}