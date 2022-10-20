package com.github.aursu.expressions;

import java.text.ParseException;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

/*
 * Parser using Shunting yard algorithm
 * for more details see https://en.wikipedia.org/wiki/Shunting_yard_algorithm
 * Shunting yard algorithm on Wikipedia EN generates RPN sequence into output queue
 * TODO: to add in-place evaluation
 */

public class RPNParser {
	private TokenStream input;
	private String polish = null;

	// input string
	protected String buffer = null;
	protected Number result = null;

	// output queue
	private Deque<Token<?>> outQueue = new LinkedList<>();
	
	public RPNParser(String input) {
		setup(input);
	}
	
	public RPNParser(InputReader input) {
		setup(input);
	}
	
	// setup token stream and string buffer with new values
	protected void setup(InputReader input) {
		this.input = new TokenStream(input);

		buffer = input.toString();
		polish = null;
		result = null;

		outQueue.clear();
	}

	protected void setup(String input) {
		setup(new InputReader(input));
	}
	
	// reset input stream to initial state (stored in buffer)
	public void reset() {
		if(buffer == null) return;
		setup(new InputReader(buffer));
	}

	// Shunting yard algorithm
	public final void parse() throws ParseException {
		// operators stack
		Stack<Token<?>> opStack = new Stack<>();
		
		// read first token
		Token<?> token = input.next();
		
		if (token == null)
			throw new ParseException("Unable to get next token at position " + input.getStart(), input.getStart());
		
		if (token.equals(Token.EOFToken))
			throw new ParseException("Expression is empty at position " + input.getStart(), input.getStart());

		// while there are tokens to be read
		while (!token.equals(Token.EOFToken)) {	
			// if the token is a number
			if (token.isNumber()) {
				// put it into the output queue
				outQueue.add(token);
			}
			/*
			 * TODO: not required for initial task (simple arithmetic operations)
			 * if the token is a function:
			 *   push it onto the operator stack 
			 */
			// if the token is an operator o1
			else if (token.isOperator()) {
				OperatorToken o1 = (OperatorToken) token;

				// while there is an operator o2 other than the left parenthesis at the top of the operator stack
				while (!opStack.empty() && opStack.peek().isOperator()) {
					OperatorToken o2 = (OperatorToken) opStack.peek();
					// and o2 has greater precedence than o1 or they have the same precedence and o1 is left-associative
					if (o2.greaterPrecedence(o1) || o2.samePrecedence(o1) && o1.leftAssociative()) {
						// pop o2 from the operator stack into the output queue
						outQueue.add(opStack.pop());
					}
					else break;
				}
				// push o1 onto the operator stack
				opStack.add(o1);
			}
			else if (token.isSeparator()) {
				SeparatorToken sep = (SeparatorToken) token;
				
				// if the token is a left parenthesis (i.e. "(")
				if (sep.leftParenthesis()) {
					// push it onto the operator stack
					opStack.add(sep);
				}
				// if the token is a right parenthesis (i.e. ")")
				else if (sep.rightParenthesis()) {
					// while the operator at the top of the operator stack is not a left parenthesis
					while (!opStack.empty() && opStack.peek().isOperator())
						// pop the operator from the operator stack into the output queue
						outQueue.add(opStack.pop());
					
					/* If the stack runs out without finding a left parenthesis, then there are mismatched parentheses. */
					if (opStack.empty())
						throw new ParseException("There are mismatched parentheses. Expected left parenthesis but operator stack is empty at " + sep.getStart(), sep.getStart());
					
					SeparatorToken op = (SeparatorToken) opStack.peek();
					// pop the left parenthesis from the operator stack and discard it
					if (op.isSeparator() && op.leftParenthesis())
						opStack.pop();
					// assert there is a left parenthesis at the top of the operator stack
					else throw new ParseException("There are mismatched parentheses. Expected left parenthesis for parenthesis at " + sep.getStart(), sep.getStart());

					/* if there is a function token at the top of the operator stack, then:
					 *   pop the function from the operator stack into the output queue
					 * */
				}
			}
			
			// read a token
			token = input.next();

			if (token == null)
				throw new ParseException("Unable to get next token at position " + input.getStart(), input.getStart());
		}
		// while there are tokens on the operator stack:
		while (!opStack.empty()) {
			Token<?> op = opStack.peek();
			/* If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses. */
			if (op.isSeparator())
				throw new ParseException("There are mismatched parentheses. Unexpected parenthesis found in operator stack at " + op.getStart(), op.getStart());
			// pop the operator from the operator stack onto the output queue
			outQueue.add(opStack.pop());
		}

		if (!outQueue.isEmpty()) {
			polish = String.join(" ", outQueue.stream().map(t -> t.toString()).toList());
			result = evaluate();
		}
	}

	private Number evaluate() throws ParseException {
		// operands stack
		Stack<NumberToken> opStack = new Stack<>();

		while (!outQueue.isEmpty()) {
			Token<?> token = outQueue.pop();
			
			if (token.isNumber()) {
				opStack.add((NumberToken) token);
			}
			else {
				OperatorToken op = (OperatorToken) token;
				NumberToken o1, o2;
				
				try {
					o2 = opStack.pop();
					o1 = opStack.pop();
				}
				catch (EmptyStackException e) {
					throw new ParseException("Not enough operands for operation " + op.toString() + " at " + op.getStart(), op.getStart());
				}

				double val1 = o1.getDouble(),
					   val2 = o2.getDouble(),
					   res;

				switch(op.getValue()) {
					case OP_DIV:
						res = val1 / val2;
						break;
					case OP_MINUS:
						res = val1 - val2;
						break;
					case OP_MULT:
						res = val1 * val2;
						break;
					case OP_PLUS:
						res = val1 + val2;
						break;
					case OP_POWER:
						res = Math.pow(val1, val2);
						break;
					default:
						throw new ParseException("Not supported operation " + op.toString(), op.getStart());
				}

				opStack.add(new NumberToken(res));
			}
		}

		if (opStack.empty())
			throw new ParseException("RPN evaluation stack is empty. It must contain only one value which is a result", 0);

		NumberToken result = opStack.pop();

		return result.getValue();
	}

	public String rpn() {
		return polish;
	}

	public String toString() {
		return input.toString();
	}
	
	public Number rpnEvaluate() {
		return result;
	}
}
