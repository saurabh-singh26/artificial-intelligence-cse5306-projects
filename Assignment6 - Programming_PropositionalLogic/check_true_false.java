import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class check_true_false {
	public static final String outputFile = "result.txt";
	public static String[] operatorArr = {"and", "or", "if", "iff", "not", "xor"};
	public static HashSet<String> operatorSet = new HashSet<String>(Arrays.asList(operatorArr));
	
//	Read the arguments and validates the format as per the project requirements.
	public static void main(String[] args) {
		if(args.length == 3) {
			String wumpusRulesFile = args[0];
			String additionalFile = args[1];
			String statementFile = args[2];
			String KB = Utils.readKnowledgeBase(wumpusRulesFile, additionalFile);
			String alpha = Utils.readFile(statementFile);
			String notAlpha = "(not " + alpha + ")";
			boolean kbEntailsStatement = ttEntails(KB, alpha);
			boolean kbEntailsNotStatement = ttEntails(KB, notAlpha);
			writeOutputToFile(kbEntailsStatement, kbEntailsNotStatement);
		}
		// Print the following error when arguments count is not equal to 3
		else Utils.exit("Enter valid command arguments.");
	}

//	Checks if knowledge base entails alpha.
	private static boolean ttEntails(String KB, String alpha) {
		LinkedList<String> symbols = Utils.getUniquePropositionalSymbols(KB+alpha);
		HashMap<String, Boolean> model = new HashMap<String, Boolean>();
		optimizeForEfficiency(KB, symbols, model);
		model.put("true", true);
		model.put("false", false);
		return ttCheckAll(KB, alpha, symbols, model);
	}
	
//	Recursively creates all the models using propositional symbols to check if knowledge base entails alpha.
	private static boolean ttCheckAll(String KB, String alpha, LinkedList<String> symbols, HashMap<String, Boolean> model) {
		if(symbols.isEmpty()) {
			boolean kbTrueInModel = plTrue(KB, model);
			boolean alphaTrueInModel = plTrue(alpha, model);
//			System.out.println("model + \n>>kbTrueInModel: " + kbTrueInModel + " & alphaTrueInModel: " + alphaTrueInModel);
			if(kbTrueInModel) return alphaTrueInModel;
			else return true;
		}
		else {
			String first = symbols.get(0);
			LinkedList<String> rest = new LinkedList<String>();
			for(int i=1; i<symbols.size();i++) {
				rest.add(symbols.get(i));
			}
			model.put(first, true);
//			System.out.print("Setting symbol " + first + " as true: " + "/nModel: " + model + "/nSymbols:" + symbols);
			boolean pTrue = ttCheckAll(KB, alpha, rest, model);
			model.put(first, false);
//			System.out.print("Setting symbol " + first + " as false: " + "/nModel: " + model + "/nSymbols: " + symbols);
			boolean pFalse = ttCheckAll(KB, alpha, rest, model);
			return pTrue && pFalse;
		}
	}
	
//	Calculates the truth value for a statement using the model.
	private static boolean plTrue(String statement, HashMap<String, Boolean> model) {
		Stack<String> operators = new Stack<String>();
		Stack<String> operands = new Stack<String>();
		statement = statement.replaceAll("\\(", "( ").replaceAll("\\)", " )").replaceAll(" +", " ");
		String[] tokens = statement.split(" ");
		for(String token:tokens) {
			if(operatorSet.contains(token.toLowerCase())) operators.push(token);
			else {
				if(!token.equals(")")) operands.push(token);
				else {
					String operator = operators.pop();
					List<Boolean> operandList = new LinkedList<Boolean>();
					while(!operands.peek().equals("(")) {
						operandList.add(model.get(operands.pop()));
					}
					String value = Utils.evaluate(operator, operandList);
					operands.pop(); //Remove the pending (
					operands.push(value);
				}
			}
		}
		return model.get(operands.pop());
	}

	/*This method is called from ttEntails method. It parses the KB, and checks for symbols for which we have the information 
	about their truth value. Eg) From the statement (and () B () (not A)), extract B and A, remove it from the symbols list
	(as we already have an information about it, and don't need this to be modeled), and insert in model map, with value 
	true for B and false for A.*/
	private static void optimizeForEfficiency(String KB, LinkedList<String> symbols, HashMap<String, Boolean> model) {
		Stack<String> operators = new Stack<String>();
		KB = KB.replaceAll("\\(", "( ").replaceAll("\\)", " )").replaceAll(" +", " ");
		String[] tokens = KB.split(" ");
		int precedingOpenParenthesis = 0;
		for(String token:tokens) {
			if(token.equals("(")) precedingOpenParenthesis++;
			else if(token.equals(")")) {
				precedingOpenParenthesis--;
				operators.pop();
			}
			else if(operatorSet.contains(token.toLowerCase())) operators.push(token);
			else {
				if(precedingOpenParenthesis==2 && operators.peek().equals("not")) {
					symbols.remove(token);
					model.put(token, false);
//					System.out.println("Removed token: " + token + " with value false. Preceding: " + precedingOpenParenthesis);
				}
				if(precedingOpenParenthesis==1 && operators.peek().equals("and")) {
					symbols.remove(token);
					model.put(token, true);
//					System.out.println("Removed token: " + token + " with value true. Preceding: " + precedingOpenParenthesis);
				}
			}
		}
	}

//	Evaluates the values of kbEntailsStatement(kb entails alpha) and kbEntailsNotStatement(kb entails not alpha), 
//	and evaluate the value of final result to be written to result.txt.
	private static void writeOutputToFile(boolean kbEntailsStatement, boolean kbEntailsNotStatement) {
		String result = kbEntailsStatement 
				? (kbEntailsNotStatement ? "both true and false" : "definitely true") 
				: (kbEntailsNotStatement ? "definitely false" : "possibly true, possibly false");
		Utils.writeToFile(result, outputFile);
	}
}
