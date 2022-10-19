package com.github.aursu.expressions;

/*
 * to meet task requirements we need only parentheses
 */
enum TokenSeparator {
	OPEN_PARENTHESIS, // open parenthesis
	CLOSE_PARENTHESIS // close parenthesis
}

public class SeparatorToken extends Token<TokenSeparator> {
	// separators
	public static final SeparatorToken
		openParenthesis = new SeparatorToken(TokenSeparator.OPEN_PARENTHESIS);
	public static final SeparatorToken
		closeParenthesis = new SeparatorToken(TokenSeparator.CLOSE_PARENTHESIS);

	public SeparatorToken(TokenSeparator value) {
		super(TokenName.SEPARATOR, value);
	}

	public boolean leftParenthesis() {
		return value == TokenSeparator.OPEN_PARENTHESIS;
	}
	
	public boolean rightParenthesis() {
		return value == TokenSeparator.CLOSE_PARENTHESIS;
	}

	public String toString() {
		return SeparatorToken.toString(value);
	}

	public static boolean isSeparator(char ch) {
		return ch == '(' || ch == ')';
	}

	public static SeparatorToken getToken(char ch) {
		switch(ch) {
		  	case '(': return new SeparatorToken(TokenSeparator.OPEN_PARENTHESIS);
		  	case ')': return new SeparatorToken(TokenSeparator.CLOSE_PARENTHESIS);
		}
		return null;
	}

	public static String toString(TokenSeparator value) {
		switch(value) {
	  		case OPEN_PARENTHESIS: return "(";
	  		case CLOSE_PARENTHESIS: return ")";
		}
		return null;
	}
}
