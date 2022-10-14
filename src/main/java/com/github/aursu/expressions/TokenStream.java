package com.github.aursu.expressions;

public class TokenStream {
	// input stream
	private InputReader input;

	// current input character
	private char peek;
	
	// token start position inside input stream
	private int start = 0;

	// current token (which has been last read)
	private Token<?> current = null;

	public TokenStream(String buffer) {
		input = new InputReader(buffer);
		peek = input.peek();
	}
	
	public Token<?> peek() {
		if (current == null) return next();
		return current;
    }

    public Token<?> next() {
    	current = read();
    	return current;
    }
    
    protected Token<?> read() {
    	// if end of input 
    	if (input.eof()) return Token.EOFToken;
    	
    	// read character
    	setStart(input.getPos());
    	peek = input.next();
    	
    	// skip whitespace (for arithmetic operations is meaningless)
    	if (isWhitespace(peek)) return read();
    	
    	// check if number token
    	if (NumberToken.isNumber(peek, input.peek(), current)) {
    		String number = readNumber();
    		
    		if (number == null) return null;
    		
    		NumberToken token = NumberToken.getToken(number);
    		if (token == null) return null;

			token.setRange(start, input.getPos());
			return token;
    	}

    	// check if operator
    	if (OperatorToken.isOperator(peek)) {
    		OperatorToken token = OperatorToken.getToken(peek);
    		
    		token.setRange(start, input.getPos());
    		return token;
    	}

    	// check if separator
    	if (SeparatorToken.isSeparator(peek)) {
    		SeparatorToken token = SeparatorToken.getToken(peek);
   
    		token.setRange(start, input.getPos());
    		return token;
    	}

    	return null;
    }

	public boolean isWhitespace(char ch) {
    	return ch == ' ' || ch == '\t' || ch == '\n';
    }

	// number  := integer | integer '.' fraction
	// fraction := digit | fraction digit
	// integer := '0' | natural | '-' natural
	// natural := nonzero | natural digit
	// digit   := '0' | nonzero
	// nonzero  := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
	private String readNumber() {
		String token = readInteger();

		if (token == null) return null;
		
		// check float number
		if (input.peek() == '.') {
			input.next(); // skip dot

			peek = input.next();
			
			String fraction = readFraction();
			
			if (fraction == null) return null;
			
			return token + "." + fraction;
		}

		return token;
	}

	// fraction := digit | fraction digit
	private String readFraction() {
		// fraction is a set of digits
		if (!NumberToken.isDigit(peek)) return null;
		
		String token = "" + peek;
		
		while (NumberToken.isDigit(input.peek())) {
			peek = input.next();
			token += peek;
		}

		return token;
	}

	// integer := '0' | natural | '-' natural
	private String readInteger() {
		String token = "" + peek;
		if (peek == '0') return token;
		if (peek == '-') {
			peek = input.next(); // skip minus sign
			
			String natural = readNatural();

			// something is wrong
			if (natural == null) return null;
			
			return token + natural;
		}
		return readNatural();
	}

	// natural := nonzero | natural digit
	// digit   := '0' | nonzero
	// nonzero  := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
	private String readNatural() {
		// natural number can not start from zero
		if (!NumberToken.isNonZero(peek)) return null;
		
		String token = String.valueOf(peek);

		while (NumberToken.isDigit(input.peek())) {
			peek = input.next();
			token += peek;
		}

		return token;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
