import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

//	Takes file path of wumpus rules and additional rules file as an input and returns the combined knowledge base statement.
	public static String readKnowledgeBase(String wumpusRulesFile, String additionalFile) {
		StringBuffer kb = new StringBuffer();
		kb.append("(and ");
		kb.append(readFile(wumpusRulesFile));
		kb.append(readFile(additionalFile));
		kb.append(")");
		return kb.toString();
	}

//	Writes the 'result' input to the output file. This method is used to write the final result of the program in result.txt file.
	public static void writeToFile(String result, String outputFile) {
		FileOutputStream fos = null;
		try {
			File output = new File(outputFile);
			if (!output.exists())
				output.createNewFile();
			fos = new FileOutputStream(output, false);
			fos.write(result.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	Reads and returns the contents of the given file.
	public static String readFile(String fileName) {
		StringBuffer fileContent = new StringBuffer();
		File fileObj = new File(fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileObj.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#") && !line.isEmpty()) {
					fileContent.append(line);
					fileContent.append(" ");
				}
			}
		} catch (FileNotFoundException e) {
			exit("Statement file not found. Execute the command with valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContent.toString();
	}

//	Evaluate the truth value of the input in operandList for the operator(either of if, iff, and, or, xor, not) 
//	and returns the output as string, "true" if truth value is true, else "false".
	public static String evaluate(String operator, List<Boolean> operandList) {
		boolean result = true;
		int size = operandList.size();
		if (operator.equalsIgnoreCase("and") && size > 1) result = !operandList.contains(false);
		else if (operator.equalsIgnoreCase("or") && size > 1) result = operandList.contains(true);
		else if (operator.equalsIgnoreCase("if") && size == 2) result = (operandList.get(1) == true && operandList.get(0) == false) ? false : true;
		else if (operator.equalsIgnoreCase("iff") && size == 2) result = (operandList.get(0) == operandList.get(1)) ? true : false;
		else if (operator.equalsIgnoreCase("not") && size == 1) result = !operandList.get(0);
		else if (operator.equalsIgnoreCase("xor") && size > 1) result = ((operandList.indexOf(true)!=-1) && (operandList.indexOf(true) == operandList.lastIndexOf(true))) ? true : false;
		else exit("Invalid Statement!!\n>>>" + operator + " with length " + size);
		return result ? "true" : "false";
	}

//	Returns a linked list of the unique propositional symbols involved in the knowledge base and statement.
	public static LinkedList<String> getUniquePropositionalSymbols(String targetString) {
		LinkedList<String> symbolsList = new LinkedList<String>();
		Pattern p = Pattern.compile("[A-Za-z]_[1-4]_[1-4]");
		Matcher match = p.matcher(targetString);
		while (match.find()) {
			String symbol = match.group();
			if (!symbolsList.contains(symbol))
				symbolsList.add(match.group());
		}
		return symbolsList;
	}
	
//	Exit the program with the message msg displayed on the console.
	public static void exit(String msg) {
		System.out.println(msg);
		System.exit(1);
	}
}
