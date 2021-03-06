//こっちではなくCalculatorRPN2.javaが正しいプログラム		
import java.io.*;
import java.util.*;

public class CalculatorRPN {

	public static void main(String[] args) {

		Scanner testData = null;
		Scanner ansData = null;

		try{
			testData = new Scanner(new File("testExpression.txt"));
			ansData = new Scanner(new File("testAnswer.txt"));

			while (testData.hasNext()) {

				Stack<Token> stackRPN = new Stack<>();
				String line = testData.next();
				if (line.equals("00")) break;
				if (!CheckError(line)) continue;
				ArrayList<Token> tokens = tokenize(line, 0);
				pushStack(stackRPN, tokens, 0);
				Token ansToken = evaluate(stackRPN);
				double answer = ansToken.value;
				double trueAns = Double.parseDouble(ansData.next());
				if (answer == trueAns) System.out.println("Pass: "+line+"="+answer);
				else System.out.println("Wrong answer: "+ line+"="+answer + " ("+ trueAns + ")");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			testData.close();
			ansData.close();
		}
	}

	static ArrayList<Token> tokenize(String line, int lineIndex) {

		ArrayList<Token> tokens = new ArrayList<>();
		if (line.charAt(0) == '-') {
			tokens.add(new Token("NUMBER", 0.0));
			lineIndex++;
		}

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

	static int pushStack(Stack<Token> stackRPN, ArrayList<Token> tokens, int indexOfToken) {

		Stack<Token> op = new Stack<>();
		while (indexOfToken < tokens.size()) {
			Token token = tokens.get(indexOfToken);
			if (token.type.equals("OPEN_PAREN")) {
				indexOfToken++;
				indexOfToken = pushStack(stackRPN, tokens, indexOfToken);
			} else if (token.type.equals("CLOSE_PAREN")){
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
			}
			indexOfToken++;
		}
		while (op.size()>0) {
			stackRPN.push(op.pop());
		}
		if (stackRPN.size() == 1) {
			stackRPN.push(new Token("NUMBER",0));
			stackRPN.push(new Token("PLUS",-1));
		}
		return indexOfToken;
	}

	static Token evaluate(Stack<Token> stackRPN) {

		Token token = stackRPN.pop();
		Token tokenX = stackRPN.peek();
		if (isOp(tokenX)) tokenX = evaluate(stackRPN);
		else tokenX = stackRPN.pop();
		Token tokenY = stackRPN.peek();
		if (isOp(tokenY)) tokenY = evaluate(stackRPN);
		else tokenY = stackRPN.pop();
		double x = tokenX.value;
		double y = tokenY.value;
		Token result = null;
		if (token.type.equals("PLUS")) result = new Token("NUMBER", x+y);
		else if (token.type.equals("MINUS")) result = new Token("NUMBER", y-x);
		else if (token.type.equals("MULT")) result = new Token("NUMBER", x*y);
		else if (token.type.equals("DIV")) result = new Token("NUMBER", y/x);
		return result;
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

	// ((2)) OK
	// ((2)  NG
	// (2))  NG
	//  2)	 NG
	//  演算子が2つ以上続く NG
	static boolean CheckError(String line) {

		int openParenCount = 0;
		int index = 0;
		boolean isOperator = false;

		while (index < line.length()) {

			char c = line.charAt(index);
			if (isDigit(c)) {
				isOperator = false;
			} else if (c == '+' || c == '-' || c == '*' || c == '/') {
				if (isOperator) {
					System.out.println("wrong grammer for operator");
					return false;
				}
				isOperator = true;
			} else if (c == '(') {
				openParenCount++;
				isOperator = false;
			} else if (c == ')') {
				openParenCount--;
				if (openParenCount < 0) {
					System.out.println("wrong grammer for '()'");
					return false;
				}
				isOperator = false;
			}
			index++;
		}
		if (openParenCount != 0) return false;
		return true;
	}
}