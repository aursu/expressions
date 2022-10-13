package com.github.aursu.expressions;

public class NumberToken extends Token<Number> {

	public NumberToken(Number value) {
		super(TokenName.NUMBER, value);
	}
	
	// cast to double
	public double getDouble() {
		return value.doubleValue();
	}

	// nonzero  := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
	public static boolean isNonZero(char ch) {
		int code = ch;
		if (code >= 49 && code <= 57) return true;

		return false;
	}
	
    // digit   := '0' | nonzero
	public static boolean isDigit(char ch) {
		if ( ch == '0') return true;
		if (isNonZero(ch)) return true;

		return false;
	}
	
	// number  := integer | integer '.' fraction
	// fraction := digit | fraction digit
	// integer := '0' | natural | '-' natural
	// natural := nonzero | natural digit
	// digit   := '0' | nonzero
	// nonzero  := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
	public static boolean isNumber(char ch, char lookahead, Token<?> lookbehind) {
    	// if it starts with digit
    	if (isDigit(ch)) return true;
    	// check negative number
    	if (ch == '-' && isNonZero(lookahead)) {
    		/* if start of the input
    		 * 
    		 * -6 + 7
    		 */
    		if (lookbehind == null) return true;

    		/* if it follows binary operator
    		 * 
    		 * -6  + -7
    		 * -6  * -7
    		 */
    		if (lookbehind.isOperator()) return true;
    		
    		/* if it follows open parenthesis
    		 * 
    		 * 5 * (-6 + -4) - (2 + -3)
    		 * 5 * (-6 + -4) - 2
    		 */
    		if (lookbehind.equals(SeparatorToken.openParenthesis)) return true;
    	}
		return false;
	}

	/*
	 * Added ability to store both Double and Integer objects, but it is questionable :)
	 */
	public static NumberToken getToken(String token) {
		if (isFloatingPoint(token)) {
			try {
				return new NumberToken(Double.parseDouble(token));
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
		else {
			try {
				return new NumberToken(Integer.parseInt(token));
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
	}
	
	public static boolean isFloatingPoint(String token) {
		return (token.indexOf('.') > 0);
	}
}
