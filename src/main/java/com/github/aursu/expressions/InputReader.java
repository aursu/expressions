package com.github.aursu.expressions;

import java.io.IOException;
import java.io.StringReader;

public class InputReader extends StringReader {

	public InputReader(String buffer) {
		super(buffer);
	}

	// peek next character
	public char peek(long n) throws IOException {
		// save current position 
		mark(0);
		
		// skip n characters till the end of the stream
		skip(n);
		
		// read next character after skip
		int c = read();
		
		// reset position to the save
		reset();
		
		if (c == -1) return 0;
		return (char) c;
    }

	public char peek() throws IOException {
        return peek(0);
    }

	public char next() throws IOException {
    	int c = read();

		if (c == -1) return 0;
		return (char) c;
    }

    public boolean eof() throws IOException {
        return peek() == 0;
    }
}
