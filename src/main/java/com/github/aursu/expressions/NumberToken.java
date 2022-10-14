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
	public static boolean isNumber(char peek, char lookahead, Token<?> lookbehind) {
    	// if it starts with digit
    	if (isDigit(peek)) return true;
    	// check negative number
    	if (peek == '-' && isNonZero(lookahead)) {
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
	 * check if number at the start of the String input
	 */
	public static boolean isNumber(InputReader input, Token<?> lookbehind) {
		char peek      = input.peek(0),
			 lookahead = input.peek(1);
		return isNumber(peek, lookahead, lookbehind);
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
	
	// number  := integer | integer '.' fraction
	// fraction := digit | fraction digit
	// integer := '0' | natural | '-' natural
	// natural := nonzero | natural digit
	// digit   := '0' | nonzero
	// nonzero  := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
	public static String readNumber(InputReader input) {
		String token = readInteger(input);

		// number must start from integer
		if (token == null) return null;
		
		// check float number
		if (input.peek() == '.') {
			input.next(); // skip dot
			
			String fraction = readFraction(input);
			
			if (fraction == null) return null;
			
			return token + "." + fraction;
		}

		return token;
	}

	// fraction := digit | fraction digit
	public static String readFraction(InputReader input) {
		char peek = input.peek();
		
		// consume character
		if (isDigit(peek)) input.next();
		// fraction is a set of digits
		else return null;
		
		String token = String.valueOf(peek);
		
		while (isDigit(input.peek())) {
			peek = input.next();
			token += peek;
		}

		return token;
	}
	
	// integer := '0' | natural | '-' natural
	public static String readInteger(InputReader input) {
		char peek = input.peek();
		
		String token = String.valueOf(peek);
		
		if (peek == '0') {
			input.next(); // consume '0' character
			return token;
		}
		else if (peek == '-') {
			peek = input.next(); // consume minus sign
			
			String natural = readNatural(input);

			// something is wrong
			if (natural == null) return null;
			
			return token + natural;
		}
		return readNatural(input);
	}
	
	// natural := nonzero | natural digit
	// digit   := '0' | nonzero
	// nonzero  := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
	public static String readNatural(InputReader input) {
		char peek = input.peek();

		// move to next character in stream
		if (isNonZero(peek)) input.next();
		// natural number can not start from zero or any other character
		else return null;
		
		String token = String.valueOf(peek);

		while (isDigit(input.peek())) {
			peek = input.next();
			token += peek;
		}

		return token;
	}
}
