package com.github.aursu.expressions;

/*
 * define required for basic arithmetic expressions operators
 * plus add power operator (to inject most common operator  with right associativity)
 */
enum TokenOperator {
	OP_PLUS, // plus operation
	OP_MINUS, // minus operation
	OP_MULT, // multiplication operation
	OP_DIV, // division operation
	OP_POWER // power operator
}

/*
 * Operators' associativity
 * see: https://en.wikipedia.org/wiki/Operator_associativity for details
 */
enum OperationAssociativity {
	ASSOC_LEFT,
	ASSOC_RIGHT,
}

/*
 * The table with operator precedence and associativity
 * (see also https://en.wikipedia.org/wiki/Order_of_operations for details)
 */
enum OperationOrder {
	OP_PLUS (TokenOperator.OP_PLUS, 2, OperationAssociativity.ASSOC_LEFT), // plus operation
	OP_MINUS (TokenOperator.OP_MINUS, 2, OperationAssociativity.ASSOC_LEFT), // minus operation
	OP_MULT (TokenOperator.OP_MULT, 3, OperationAssociativity.ASSOC_LEFT), // multiplication operation
	OP_DIV (TokenOperator.OP_DIV, 3, OperationAssociativity.ASSOC_LEFT), // division operation
	OP_POWER (TokenOperator.OP_POWER, 4, OperationAssociativity.ASSOC_RIGHT); // power operator ^

	// Operator	Precedence	Associativity
    public final TokenOperator operator;
    public final int precedence;
    public final OperationAssociativity assoc;
    
    OperationOrder(TokenOperator operator, int prec, OperationAssociativity assoc) {
        this.operator = operator;
        this.precedence = prec;
        this.assoc =  assoc;
    }
}

public class OperatorToken extends Token<TokenOperator> {
	// operators
	public static final OperatorToken opPlus = new OperatorToken(TokenOperator.OP_PLUS);
	public static final OperatorToken opMinus = new OperatorToken(TokenOperator.OP_MINUS);
	public static final OperatorToken opMult = new OperatorToken(TokenOperator.OP_MULT);
	public static final OperatorToken opDiv = new OperatorToken(TokenOperator.OP_DIV);
	public static final OperatorToken opPow = new OperatorToken(TokenOperator.OP_POWER);

	private int precedence = -1;
	private OperationAssociativity assoc = OperationAssociativity.ASSOC_LEFT;

	public OperatorToken(TokenOperator value) {
		super(TokenName.OPERATOR, value);
		setAttributes();
	}

	private void setAttributes() {
		for(OperationOrder o: OperationOrder.values()) {
			if (value == o.operator) {
				setPrecedence(o.precedence);
				setAssoc(o.assoc);
			}
		}
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	public OperationAssociativity getAssoc() {
		return assoc;
	}

	public void setAssoc(OperationAssociativity assoc) {
		this.assoc = assoc;
	}
	
	public boolean greaterPrecedence(OperatorToken op) {
		return precedence > op.getPrecedence();
	}
	
	public boolean samePrecedence(OperatorToken op) {
		return precedence == op.getPrecedence();
	}
	
	public boolean leftAssociative() {
		return assoc == OperationAssociativity.ASSOC_LEFT;
	}

	public boolean rightAssociative() {
		return assoc == OperationAssociativity.ASSOC_RIGHT;
	}

	public static boolean isOperator(char ch) {
		switch(ch) {
			case '+', '^':
			case '*', (char) 215, (char) 183: // *, ×, ·
			case '/', (char) 247: // /, ÷
			case '-', '\u2212':   // -, −
				return true;
		}
		return false;
	}
	
	public static OperatorToken getToken(char ch) {
		switch(ch) {
		  	case '+': return new OperatorToken(TokenOperator.OP_PLUS);
		  	case '-', '\u2212': return new OperatorToken(TokenOperator.OP_MINUS);
		  	case '*', (char) 215, (char) 183: return new OperatorToken(TokenOperator.OP_MULT);
		  	case '/', (char) 247: return new OperatorToken(TokenOperator.OP_DIV);
		  	case '^': return new OperatorToken(TokenOperator.OP_POWER);
		}
		return null;
	}
	
	public static String toString(TokenOperator value) {
		switch(value) {
	  		case OP_PLUS: return "+";
	  		case OP_MINUS: return "-";
	  		case OP_MULT: return "*";
	  		case OP_DIV: return "/";
	  		case OP_POWER: return "^";
		}
		return null;
	}
}
