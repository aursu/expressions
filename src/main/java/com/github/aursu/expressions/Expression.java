package com.github.aursu.expressions;

import java.text.ParseException;

public class Expression extends RPNParser {
	// expression representation
	// infix is standard  arithmetic form (see https://en.wikipedia.org/wiki/Infix_notation)
	// polish is Reverse Polish notation 
	private String infix = null;

	// calculated expression value
	private double value;

	// if expression is valid
	private boolean valid = false;

	// to store stack trace rather than print it
	private ErrorMessenger errors;

	public Expression(String input) {
		super(input);
		setup();
	}

	public Expression(InputReader input) {
		super(input);
		setup();
	}

	private void setup() {
		errors = new ErrorMessenger();
		try {
			parse();

			valid = true;
			value = result.doubleValue();
			infix = buffer;
		} catch (ParseException e) {
			errors.process(e);
		}
	}

	public boolean isValid() {
		return valid;
	}

	public double value() {
		return value;
	}

	public String infix() {
		return infix;
	}

	public String stackTrace() {
		return errors.stack();		
	}
}
