package com.github.aursu.expressions;

/*
 * Similar to StringReader
 * support lookahead peek() methods
 * current position inside string is available
 * 
 * see https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/StringReader.html
 */
public class InputReader {
	private String buf;
	private int count;
	private int pos = 0;

	public InputReader(String buffer) {
		setInput(buffer);
	}

	public void setInput(String buffer) {
		buf = buffer;
		count = buffer.length();
		pos = 0;
	}

	public char peek(int n) {
        int index = pos + Math.min(count - pos, n < 0 ? 0 : n);

        if (index < count)
            return buf.charAt(index);

        return 0;
    }

	public char peek() {
        return peek(0);
    }

    public int getPos() {
		return pos;
	}

	public char next() {
    	char c = peek();

    	// avoid to increase current position unnecessary 
    	if (c != 0) pos++;

        return c;
    }

    public boolean eof() {
        return peek() == 0;
    }
}

