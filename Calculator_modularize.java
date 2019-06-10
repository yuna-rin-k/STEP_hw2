//途中です
//やり方間違っています
import java.util.*;

public class Calculator_modularize {

	static int index;
	static Stack<Character> op = new Stack<>();
	static Stack<Double> numbers = new Stack<>();

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		//while(sc.hasNext()) {
			String line = sc.next();
			init();
			changeToRPN(line);
			double ans = evaluate(line);
			System.out.println(ans);
		//}
		
	}
	static void init() {
		index = 0;
		op.clear();
		numbers.clear();
	}
 

	static void changeToRPN(String line) {
		//RPN(Reverse Polish Notation)逆ポーランド記法に変換

		while (index < line.length()) {
			char c = line.charAt(index);
			if (isDigit(c)) {
				double number = readNumber(line);	
				numbers.push(number);
			} else if (c == '+' || c == '-' || c == '*' || c == '/'){
				op.push(c);
				index++;
			} else if (c != '(' && c != ')'){
				System.out.println("Invalid character found:" + c);
				System.exit(1);
			}
		}
	}
	static double evaluate(String line) {

		while (numbers.size() > 1) {
			char operator = op.pop();
			double x = numbers.pop();
			double y = numbers.pop();
			double result = 0.0;
			if (operator == '+') result = add(x, y);
			else if (operator == '-') result = sub(x, y);
			else if (operator == '*') result = mult(x, y);
			else if (operator == '/') result = div(x, y);
			numbers.push(result);
		}
		return numbers.pop();		
	}
	static double readNumber(String line) {

		double num = 0.0;
		char c = line.charAt(index);

		while (index < line.length() && isDigit(c)) {
			num = num * 10 + changeToDouble(c);
			changeToDouble(c);
			index++;
			if (index >= line.length()) break;
 			c = line.charAt(index);
		}
		if (c == '.') {
			index++;
			double keta = 0;
			c = line.charAt(index);
			while (index < line.length() && isDigit(c)) {
				num = num*10 + changeToDouble(c);
				changeToDouble(c);
				keta++;
				index++;
			}
			num /= Math.pow(10, keta);
		}
		return num;
	}

	static double add(double x, double y) {
		return x + y;
	}

	static double sub(double x, double y) {
		return y - x;
	}

	static double mult(double x, double y) {
		return x * y;
	}

	static double div(double x, double y) {
		return y / x;
	}

	static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	static double changeToDouble(char c) {
		return c - '0';
	}
}