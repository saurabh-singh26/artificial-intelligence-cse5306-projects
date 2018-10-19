// Submitted by Saurabh Singh - 1001568347

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * The program calculates posterior probability of different hypotheses for bags and candies 
 * problem, which states that there are five types of bags of candies. Each bag has an 
 * infinite amount of candies. We have one of those bags, and we are picking candies out of it. 
 * We don't know what type of bag we have, so we want to figure out the probability of each 
 * type based on the candies that we have picked.
 * 
 * Usage Example: compute_a_posteriori CLLCCLLLCCL
 */

public class compute_a_posteriori {
	
	public static void main(String[] args) {
		String observations = args.length>0 ? args[0] : "";
		int obsLength = observations.length();
		// Prior probabilities of picking up each bag
		double[] h_i = new double[] {0.1, 0.2, 0.4, 0.2, 0.1};
		// Probability of picking a cherry out of bag i.
		double[] h_i_cherry = new double[] {1.0, 0.75, 0.5, 0.25, 0};
		// Probability of picking a lime out of bag i.
		double[] h_i_lime = new double[] {0, 0.25, 0.5, 0.75, 1.0};
		
		StringBuffer sb = new StringBuffer();
		sb.append("Observation sequence Q: " + observations + "\r\n");
		sb.append("Length of Q: " + obsLength + "\r\n" + "\r\n");
		
		double probNextCandyIsCherry = 0, probNextCandyIsLime = 0;
		/*
		 *  Iterate through each observation starting with i=0 (when no observation has been made) 
		 *  and calculate following values: 
		 *  1) Probability of picking each bag given past observations (5 computation)
		 *  2) Probability of picking the next item to be cherry or lime given past observations (2 computation)
		 */
		for(int i=0; i<=obsLength; i++) {
			String obs = (i==0 ? "<null>" : observations.charAt(i-1)+"");
			sb.append("After Observation " + i + " = " + obs +":\r\n\r\n");
			for(int j=0; i>0 && j<5; j++) {
				if(obs.equals("C")) h_i[j] = (h_i[j]*h_i_cherry[j])/probNextCandyIsCherry;
				if(obs.equals("L")) h_i[j] = (h_i[j]*h_i_lime[j])/probNextCandyIsLime;
			}
			probNextCandyIsCherry = 0;
			probNextCandyIsLime = 0;
			for(int j=0; j<5; j++) {
				sb.append("P(h" + (j+1) + " | Q) = " + h_i[j] + "\r\n");
				probNextCandyIsCherry += h_i[j]*h_i_cherry[j];
				probNextCandyIsLime += h_i[j]*h_i_lime[j];
			}
			sb.append("\r\nProbability that the next candy we pick will be C, given Q: " + probNextCandyIsCherry + "\r\n");
			sb.append("Probability that the next candy we pick will be L, given Q: " + probNextCandyIsLime + "\r\n\r\n");
		}
		writeToFile(sb, "result.txt");
	}
	
	// Write the formatted result in result.txt file
	public static void writeToFile(StringBuffer sb, String resultFile) {
		String result = sb.toString();
		FileOutputStream fos = null;
		try {
			File output = new File(resultFile);
			fos = new FileOutputStream(output, false);
			fos.write(result.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
