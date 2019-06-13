import java.util.*;

public class CalculatorRPN {

	static ArrayList<Token> tokens = new ArrayList<>();
	static Stack<Token> stackRPN = new Stack<>();
	static String line;
	static int lineIndex = 0;
	static int indexOfToken = 0;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			init();
			line = sc.next();
			if (line.equals("00")) break;
			tokenize(line);
			pushStack();
			Token ansToken = evaluate();
			double answer = ansToken.value;
			System.out.println(line+"="+answer);
		}
	}
	static void init() {
		tokens.clear();
		stackRPN.clear();
		lineIndex = 0;
		indexOfToken = 0;
	}

	static void tokenize(String line) {
		lineIndex = 0;
		while (lineIndex < line.length()) {
			char c = line.charAt(lineIndex);
			if (isDigit(c)) readNumber();
			else if (c == '+') readPlus();
			else if (c == '-') readMinus();
			else if (c == '*') readMult();
			else if (c == '/') readDiv();
			else if (c == '(') readOpenParen();
			else if (c == ')') readCloseParen();
			else {
				System.out.println("Invalid character found:" + c);
				System.exit(1);
			}
		}
	}

	static void pushStack() {
		Stack<Token> op = new Stack<>();
		Token preOp;
		while (indexOfToken < tokens.size()) {
			Token token = tokens.get(indexOfToken);
			if (token.type.equals("OPEN_PAREN")) {
				indexOfToken++;
				pushStack();
			} else if (token.type.equals("CLOSE_PAREN")){
				while (op.size()>0) {
					stackRPN.push(op.pop());
				}
				return;
			} else if (token.type.equals("NUMBER")) {
				stackRPN.push(token);
			} else if ((token.type.equals("MULT") || token.type.equals("DIV")))	{
				op.push(token);
			} else if ((token.type.equals("PLUS") || token.type.equals("MINUS"))){
				if (op.size() > 0) {
					preOp = op.peek();
					if ((preOp.type.equals("MULT") || preOp.type.equals("DIV"))) {
						stackRPN.push(op.pop());
					} 
					op.push(token);
				} else {
					op.push(token);
				}
			}
			indexOfToken++;
		}
		int size = op.size();
		while (op.size()>0) {
			stackRPN.push(op.pop());
		}
	}

	static Token evaluate() {
		Token result = new Token("dummy", -1);
		Token token = stackRPN.pop();
		Token tokenX = stackRPN.peek();
		if (isOp(tokenX)) tokenX = evaluate();
		else tokenX = stackRPN.pop();
		Token tokenY = stackRPN.peek();
		if (isOp(tokenY)) tokenY = evaluate();
		else tokenY = stackRPN.pop();
		double x = tokenX.value;
		double y = tokenY.value;
		if (token.type.equals("PLUS")) result = new Token("NUMBER", x+y);
		else if (token.type.equals("MINUS")) result = new Token("NUMBER", y-x);
		else if (token.type.equals("MULT")) result = new Token("NUMBER", x*y);
		else if (token.type.equals("DIV")) result = new Token("NUMBER", y/x);
		return result;
	}

	static void readNumber() {
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
	}

	static void readPlus() {
		Token token = new Token("PLUS", -1);
		tokens.add(token);
		lineIndex++;
	}

	static void readMinus() {
		Token token = new Token("MINUS", -1);
		tokens.add(token);
		lineIndex++;
	}

	static void readMult() {
		Token token = new Token("MULT", -1);
		tokens.add(token);
		lineIndex++;
	}

	static void readDiv() {
		Token token = new Token("DIV", -1);
		tokens.add(token);
		lineIndex++;
	}

	static void readOpenParen() {
		Token token = new Token("OPEN_PAREN", -1);
		tokens.add(token);
		lineIndex++;
	}

	static void readCloseParen() {
		Token token = new Token("CLOSE_PAREN", -1);
		tokens.add(token);
		lineIndex++;
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