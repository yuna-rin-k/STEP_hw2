import java.util.*;

public class CalculatorRPN2 {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		while (true) {
			Stack<Token> stackRPN = new Stack<>();
			String line = sc.next();
			if (line.equals("00")) break;
			ArrayList<Token> tokens = tokenize(line, 0);
			pushStack(stackRPN, tokens, 0, false);
			Token ansToken = evaluate(stackRPN);
			double answer = ansToken.value;
			System.out.println(line+"="+answer);
		}
	}

	static ArrayList<Token> tokenize(String line, int lineIndex) {

		ArrayList<Token> tokens = new ArrayList<>();
		while (lineIndex < line.length()) {
			char c = line.charAt(lineIndex);
			if (isDigit(c)) lineIndex = readNumber(line, lineIndex, tokens);
			else if (c == '+') lineIndex = readPlus(line, lineIndex, tokens);
			else if (c == '-') lineIndex = readMinus(line, lineIndex, tokens);
			else if (c == '*') lineIndex = readMult(line, lineIndex, tokens);
			else if (c == '/') lineIndex = readDiv(line, lineIndex, tokens);
			else if (c == '(') lineIndex = readOpenParen(line, lineIndex, tokens);
			else if (c == ')') lineIndex = readCloseParen(line, lineIndex, tokens);
			else {
				System.out.println("Invalid character found:" + c);
				System.exit(1);
			}
		}
		return tokens;
	}

	static int pushStack(Stack<Token> stackRPN, ArrayList<Token> tokens, int indexOfToken, boolean insideParen) {

		Stack<Token> op = new Stack<>();
		while (indexOfToken < tokens.size()) {
			Token token = tokens.get(indexOfToken);
			if (token.type.equals("OPEN_PAREN")) {
				indexOfToken++;
				indexOfToken = pushStack(stackRPN, tokens, indexOfToken, true);
				if (tokens.get(indexOfToken).type != "CLOSE_PAREN") {
					System.out.println("error");
					System.exit(1);
				} 
				insideParen = false;
			} else if (token.type.equals("CLOSE_PAREN")){
				if (!insideParen) {
					System.out.println("error");
					System.exit(1);
				}
				while (op.size()>0) {
					stackRPN.push(op.pop());
				}
				return indexOfToken;
			} else if (token.type.equals("NUMBER")) {
				stackRPN.push(token);
			} else if ((token.type.equals("MULT") || token.type.equals("DIV")))	{
				op.push(token);
			} else if ((token.type.equals("PLUS") || token.type.equals("MINUS"))){
				if (op.size() > 0) {
					Token preOp = op.peek();
					if ((preOp.type.equals("MULT") || preOp.type.equals("DIV"))) {
						stackRPN.push(op.pop());
					} 
					op.push(token);
				} else {
					op.push(token);
				}
			} else {
				System.out.println("Invalid character");
				return Integer.MAX_VALUE;
			}
			indexOfToken++;
		}
		while (op.size()>0) {
			stackRPN.push(op.pop());
		}
		return indexOfToken;
	}

	static Token evaluate(Stack<Token> stackRPN) {

		Token token = stackRPN.pop();
		if (token.type == "NUMBER") return token;
		Token left = evaluate(stackRPN);
		Token right = evaluate(stackRPN);
		double leftVal = left.value;
		double rightVal = right.value;
		String operator = token.type;

		if (operator == "PLUS") stackRPN.push(new Token("NUMBER", leftVal + rightVal));
		else if (operator == "MINUS") stackRPN.push(new Token("NUMBER", rightVal - leftVal));
		else if (operator == "MULT") stackRPN.push(new Token("NUMBER", leftVal * rightVal));
		else if (operator == "DIV") stackRPN.push(new Token("NUMBER", rightVal / leftVal));

		return stackRPN.pop();
	}

	static int readNumber(String line, int lineIndex, ArrayList<Token> tokens) {

		double num = 0.0;
		char c = line.charAt(lineIndex);
		while (lineIndex < line.length() && isDigit(c)) {
			num = num * 10 + changeToDouble(c);
			changeToDouble(c);
			lineIndex++;
			if (lineIndex >= line.length()) break;
 			c = line.charAt(lineIndex);
		}
		if (c == '.') {
			lineIndex++;
			double keta = 0;
			c = line.charAt(lineIndex);
			while (lineIndex < line.length() && isDigit(c)) {
				num = num*10 + changeToDouble(c);
				changeToDouble(c);
				keta++;
				lineIndex++;
				c = line.charAt(lineIndex);
			}
			num /= Math.pow(10, keta);
		}
		Token token = new Token("NUMBER", num);
		tokens.add(token);
		return lineIndex;
	}

	static int readPlus(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("PLUS", -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readMinus(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("MINUS", -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readMult(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("MULT", -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readDiv(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("DIV", -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readOpenParen(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("OPEN_PAREN", -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readCloseParen(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("CLOSE_PAREN", -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static boolean isDigit(char c) {

		return c >= '0' && c <= '9';
	}

	static boolean isOp(Token token) {

		return token.type.equals("PLUS") || token.type.equals("MINUS") ||
				token.type.equals("MULT") || token.type.equals("DIV") ;
	}
	static double changeToDouble(char c) {

		return c - '0';
	}

	static class Token {

		String type;
		double value;
		public Token(String type, double value) {
			this.type = type;
			this.value = value;
		}
	}	
}