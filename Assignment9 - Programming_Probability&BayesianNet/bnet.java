import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Given the Bayesian Network of burglary-earthquake-alarm domain, following program computes and
 * prints out the probability of any combination of events given any other combination of events.
 * 
 * Usage Examples:
 * 1) bnet Bt Af given Mf: P(Burglary=true and Alarm=false | MaryCalls=false)
 * 2) bnet Af Et: P(Alarm=false and Earthquake=true)
 * 3) bnet Jt Af given Bt Ef: P(JohnCalls=true and Alarm=false | Burglary=true and Earthquake=false)
 * 4) bnet Bt Af Mf Jt Et: P(Burglary=true and Alarm=false and MaryCalls=false and JohnCalls=true and Earthquake=true)
 */

public class bnet {
	// All variables in the problem domain.
	static String[] allVariables = new String[] {"B", "E", "A", "J", "M"};
	// Probabilities defined in the Bayesian Network
	static double pBurglaryTrue = 0.001;
	static double pEarthquakeTrue = 0.002;
	static double pAlarmGivenBtAndEt = 0.95;
	static double pAlarmGivenBtAndEf = 0.94;
	static double pAlarmGivenBfAndEt = 0.29;
	static double pAlarmGivenBfAndEf = 0.001;
	static double pJohnCallsGivenAt = 0.90;
	static double pJohnCallsGivenAf = 0.05;
	static double pMaryCallsGivenAt = 0.70;
	static double pMaryCallsGivenAf = 0.01;
	
	public static void main(String[] args) {
		// Store the query and fixed variables in different Map, so as to calculate the probability using Bayes rule.
		Map<String, Boolean> queryVariablesMap = new HashMap<String, Boolean>();
		Map<String, Boolean> fixedVariablesMap = new HashMap<String, Boolean>();
		boolean given = false; // Switch if the input does not talk about conditional probability
		
		for(int i=0;i<args.length;i++) {
			if(args[i].equals("given")) {
				given  = true; // Switch the value if "given" occurs in the input string.
				continue;
			}
			// Store both the query and fixed variables in queryVariablesMap, because P(A|B) = P(A,B)/P(B)
			queryVariablesMap.put(args[i].charAt(0)+"", args[i].charAt(1)=='t' ? true : false);
			// Also store the fixed variables separately in fixedVariablesMap, so as to calculate the denominator part.
			if(given) fixedVariablesMap.put(args[i].charAt(0)+"", args[i].charAt(1)=='t' ? true : false);
		}
		
		// Separate list to store the hidden variables for both numerator and denominator.
		List<String> hiddenQueryVariables = new ArrayList<String>(); 
		List<String> hiddenFixedVariables = new ArrayList<String>();
		// Parse through all variables in the problem domain and store the one which does not occur in the map/query
		for(int i=0;i<allVariables.length;i++) {
			if(!queryVariablesMap.containsKey(allVariables[i])) hiddenQueryVariables.add(allVariables[i]);
			if(!fixedVariablesMap.containsKey(allVariables[i])) hiddenFixedVariables.add(allVariables[i]);
		}
		
		// Compute the probability for both numerator and denominator as the case may be
		double output = compute(queryVariablesMap, hiddenQueryVariables);
		if(given) output /= compute(fixedVariablesMap, hiddenFixedVariables);
		System.out.println("Probability = " + (output));
	}
	
	/* 
	 * Input: 
	 * 1) Query Map containing the variables for which truth statement is known/given.
	 * 2) List of hidden variables for which we need to sum over all possibilities.
	 * Output: Recursively assign values to each hidden variable as both true and false and sum over all possible
	 * combinations and return the final sum.
	 */
	private static double compute(Map<String, Boolean> vMap, List<String> hiddenVariables) {
		if(hiddenVariables.isEmpty()) {
			return computeProbability(vMap.get("B"), vMap.get("E"), vMap.get("A"), vMap.get("J"), vMap.get("M"));
		}
		String first = hiddenVariables.get(0);
		LinkedList<String> rest = new LinkedList<String>();
		for(int i=1; i<hiddenVariables.size();i++) {
			rest.add(hiddenVariables.get(i));
		}
		vMap.put(first, true);
		double hiddenAssignedTrue = compute(vMap, rest);
		vMap.put(first, false);
		double hiddenAssignedFalse = compute(vMap, rest);
		return hiddenAssignedTrue + hiddenAssignedFalse;
	}
	
	/*
	 * Input: Truth value for each variable in the problem domain
	 * Output: Product of each variable's probability given its parent
	 */
	private static double computeProbability(boolean burglary, boolean earthquake, boolean alarm, boolean john_calls, boolean mary_calls) {
		return (burglary ? pBurglaryTrue : 1-pBurglaryTrue) 
				* (earthquake ? pEarthquakeTrue : 1-pEarthquakeTrue) 
				*(burglary 
						? (earthquake 
								? (alarm ? pAlarmGivenBtAndEt : 1-pAlarmGivenBtAndEt) 
								: (alarm ? pAlarmGivenBtAndEf : 1-pAlarmGivenBtAndEf)) 
						: (earthquake 
								? (alarm ? pAlarmGivenBfAndEt : 1-pAlarmGivenBfAndEt) 
								: (alarm ? pAlarmGivenBfAndEf : 1-pAlarmGivenBfAndEf)))
				* (alarm 
						? (john_calls ? pJohnCallsGivenAt : 1-pJohnCallsGivenAt) 
						: (john_calls ? pJohnCallsGivenAf : 1-pJohnCallsGivenAf))
				* (alarm 
						? (mary_calls ? pMaryCallsGivenAt : 1-pMaryCallsGivenAt) 
						: (mary_calls ? pMaryCallsGivenAf : 1-pMaryCallsGivenAf));
	}

}
