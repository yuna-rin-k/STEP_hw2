import java.io.*;
import java.util.*;

public class CalculatorRPN2 {

	public static void main(String[] args) {

		runTest();
		run();
	}

	static void run() {

		Scanner sc = new Scanner(System.in);
		while (true) {

			Stack<Token> stackRPN = new Stack<>();
			String line = sc.next();
			if (line.equals("00")) break;
			ArrayList<Token> tokens = tokenize(line, 0);
			int numOfConsumedToken =  pushStack(stackRPN, tokens, 0);
			if (numOfConsumedToken != tokens.size()) {
				System.out.println("error");
				continue;
			}
			Token ansToken = evaluate(stackRPN);
			double answer = ansToken.value;
			System.out.println(line+"="+answer);
		}
	}

	static void runTest() {

		Scanner testData = null;
		Scanner ansData = null;

		try{
			testData = new Scanner(new File("testExpression.txt"));
			ansData = new Scanner(new File("testAnswer.txt"));

			while (testData.hasNext()) {

				Stack<Token> stackRPN = new Stack<>();
				String line = testData.next();
				if (line.equals("00")) break;
				ArrayList<Token> tokens = tokenize(line, 0);
				int numOfConsumedToken = pushStack(stackRPN, tokens, 0);
				if (numOfConsumedToken != tokens.size()) {
					System.out.println("error");
					continue;
				}
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
		if (line.charAt(0) == '-') line = "0"+line;

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
				break;
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
				if (indexOfToken < tokens.size() && tokens.get(indexOfToken).type != "CLOSE_PAREN") {
					System.out.println("error");
					break;
				} 
			} else if (token.type.equals("CLOSE_PAREN")){
				break;
			} else if (token.type.equals("NUMBER")) {
				stackRPN.push(token);
			} else if (op.size() == 0){
				op.push(token);
			} else if (isOp(token)){
				Token preToken = op.peek();
				if (token.priority <= preToken.priority) {
					stackRPN.push(op.pop());
				} 
				op.push(token);
			} else {
				break;
			}
			indexOfToken++;
		}
		
		while (op.size()>0) {
			stackRPN.push(op.pop());
		}
		return indexOfToken;
	}

	static Token evaluate(Stack<Token> stackRPN) {

		if (stackRPN.size() == 0) return new Token("ERROR", Integer.MAX_VALUE, -1);

		Token token = stackRPN.pop();
		if (token.type == "NUMBER") return token;
		Token left = evaluate(stackRPN);
		Token right = evaluate(stackRPN);
		double leftVal = left.value;
		double rightVal = right.value;
		String operator = token.type;

		if (operator == "PLUS") return new Token("NUMBER", leftVal + rightVal, 3);
		else if (operator == "MINUS") return new Token("NUMBER", rightVal - leftVal, 3);
		else if (operator == "MULT") return new Token("NUMBER", leftVal * rightVal, 3);
		else if (operator == "DIV") return new Token("NUMBER", rightVal / leftVal, 3);

		return null;
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
		Token token = new Token("NUMBER", num, 3);
		tokens.add(token);
		return lineIndex;
	}

	static int readPlus(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("PLUS", -1, 1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readMinus(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("MINUS", -1, 1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readMult(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("MULT", -1, 2);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readDiv(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("DIV", -1, 2);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readOpenParen(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("OPEN_PAREN", -1, -1);
		tokens.add(token);
		lineIndex++;
		return lineIndex;
	}

	static int readCloseParen(String line, int lineIndex, ArrayList<Token> tokens) {

		Token token = new Token("CLOSE_PAREN", -1, -1);
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
		int priority;
		public Token(String type, double value, int priority) {
			this.type = type;
			this.value = value;
			this.priority = priority;
		}
	}	
}