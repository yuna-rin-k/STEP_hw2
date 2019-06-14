import java.util.*;

public class Calculator_modularize {

	public static void main(String[] arga) {
		Scanner sc = new Scanner(System.in);
		String line = sc.next();
		ArrayList<Token> tokens = tokenize(line);
		double answer = evaluate(tokens);
		System.out.println(line + "=" + answer);
	}

	static ArrayList<Token> tokenize(String line) {
		ArrayList<Token> tokens = new ArrayList<>();
		int lineIndex = 0;
		while (lineIndex < line.length()) {
			char c = line.charAt(lineIndex);
			if (isDigit(c))  lineIndex = readNumber(line, lineIndex, tokens);
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

	static double evaluate(ArrayList<Token> tokens) {
		return evaluateParenExpr(tokens);
	}

	static double evaluateNoParenExpr(ArrayList<Token> tokensHasNoParens) {
		ArrayList<Token> tokensHasNo_MULT_DIV = mul_div(tokensHasNoParens);
		double answer = add_sub(tokensHasNo_MULT_DIV);
		Token token = new Token("NUMBER", answer);
		tokensHasNoParens.add(token);
		return answer;
	}

	static double evaluateParenExpr(ArrayList<Token> tokens) {
		int indexOfToken = 0;
		ArrayList<Token> tokensHasNoParens = new ArrayList<Token>();
		ArrayList<Token> tokensInsideParens = new ArrayList<>();
		while (indexOfToken < tokens.size()) {
			String type = tokens.get(indexOfToken).type;
			if (type.equals("OPEN_PAREN")) {
				int indexOfTokenInsideParens = indexOfToken+1;
				int parenCount = 1;
				while (indexOfToken < tokens.size()) {
					indexOfToken++;
					type = tokens.get(indexOfToken).type;
					if (type.equals("OPEN_PAREN")) parenCount++;
					if (type.equals("CLOSE_PAREN")) parenCount--;
					if (parenCount == 0) break;
					tokensInsideParens.add(tokens.get(indexOfToken));
				}
				double resultInside = evaluateParenExpr(tokensInsideParens);
				Token token = new Token("NUMBER", resultInside);
				tokensHasNoParens.add(token);
			} else {
				tokensHasNoParens.add(tokens.get(indexOfToken));
			}
			indexOfToken++;
		}
		return evaluateNoParenExpr(tokensHasNoParens);
	}

	static ArrayList<Token> mul_div(ArrayList<Token> tokens) {
		ArrayList<Token> tokensHasNo_MULT_DIV = new ArrayList<>(tokens);
		int index = 0;
		int removeIndex = 0;
		double result = 0;
		while (index < tokens.size()) {
			String type = tokens.get(index).type;
			if (type.equals("MULT") || type.equals("DIV")) {
				double x = tokens.get(index-1).value;
				double y = tokens.get(index+1).value;
				if (type.equals("MULT")) result = x * y;
				else if (type.equals("DIV")) result = x / y;
				Token token = new Token("NUMBER", result);
				removeIndex--;
				tokensHasNo_MULT_DIV.remove(removeIndex);
				tokensHasNo_MULT_DIV.remove(removeIndex);
				tokensHasNo_MULT_DIV.remove(removeIndex);
				tokensHasNo_MULT_DIV.add(removeIndex,token);
				tokens.remove(index+1);
				tokens.add(index+1,token);
				removeIndex++;
				index += 2;
			} else {
				index++;
				removeIndex++;
			}
		}
		return tokensHasNo_MULT_DIV;
	}

	static double add_sub(ArrayList<Token> tokens) {
		int index = 1;
		double answer = 0.0;
		if (tokens.get(0).type.equals("NUMBER")) {
			Token dummyPlus = new Token("PLUS", -1);
			tokens.add(0, dummyPlus);
		}
		while (index < tokens.size()) {
			if (tokens.get(index).type.equals("NUMBER")) {
				double value = tokens.get(index).value;
				String type = tokens.get(index-1).type;
				if (type.equals("PLUS")) answer += value;
				else if (type.equals("MINUS")) answer -= value;
				index++;
			}
			index++;
		}
		return answer;
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