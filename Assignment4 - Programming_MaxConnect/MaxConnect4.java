import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

// The main class which is the entry point to the game
public class MaxConnect4 {
	
	// Read the arguments and validates the format as per the project requirements
	public static void main(String[] args) {
		if(args.length == 4) {
			String mode = args[0];
			String inputFile = args[1];
			int depth = Integer.parseInt(args[3]);
			if(mode.equals("one-move")) {
				String outputFile = args[2];
				// Call playOneMove method when the play mode entered is "one-move"
				new MaxConnect4().playOneMove(inputFile, outputFile, depth);
			}
			else if(mode.equals("interactive")) {
				String nextPlayer = args[2];
				// Call playInteractive method when the play mode entered is "interactive"
				new MaxConnect4().playInteractive(inputFile, nextPlayer, depth);
			}
			// Print the following error when game mode is not "one-move" or "interactive"
			else System.out.println("Enter a valid game mode");
		}
		// Print the following error when arguments count is not equal to 4
		else System.out.println("Enter valid command arguments.");
	}
	
	/*
	This method decides the move to be played according to minimax algorithm with alpha-beta pruning
	and depth-limited search. It validates if the action returned by the algorithm is valid or not and 
	then writes the resultant game state to a file and exit.
	
	@param inputFile: contains the initial game state
	@param outputFile: the resultant game state is written to this file
	@param depth: resource limit for depth-limited search
	*/
	private void playOneMove(String inputFile, String outputFile, int depth) {
		GameBoard startState = new GameBoard(inputFile);
		System.out.println("----Initial State----");
		startState.printGameStateAndScore();
		int action = minimaxDecision(startState, depth);
		if(action==-1) exitGame();
		GameBoard finalState = startState.getActionResult(action);
		System.out.println("----Final State----");
		finalState.printGameStateAndScore();
		finalState.copyToFile(outputFile);
	}
	
	/*
	This method plays the game in interactive mode. The move to be played is human is entered by the 
	user on console and	the move to be played by computer is calculated with minimax algorithm. The 
	output from each of the player is written to respective files.
	
	@param inputFile: contains the initial game state. If not present then, an empty board is created
	@param nextPlayer: can be human or computer
	@param depth: resource limit for depth-limited search
	*/
	private void playInteractive(String inputFile, String nextPlayer, int depth) {
		File input = new File(inputFile);
		if(!input.exists()) writeEmptyBoardInFile(input);
		GameBoard gameState = new GameBoard(inputFile);
		gameState.printGameStateAndScore();
		boolean humanNext = (nextPlayer.equals("human-next")) ? true : false;
		while(!gameState.isTerminalState()) {
			int action = -1;
			if(humanNext) {
				action = promptHumanForAction(gameState);
				gameState = gameState.getActionResult(action);
				gameState.copyToFile("human.txt");
				humanNext = false;
			} else {
				action = minimaxDecision(gameState, depth);
				gameState = gameState.getActionResult(action);
				gameState.copyToFile("computer.txt");
				humanNext = true;
			}
			gameState.printGameStateAndScore();
		}
		exitGame();
	}
	
//	Prompts the user for a valid input until a valid move is obtained
	private int promptHumanForAction(GameBoard gameState) {
		Scanner sc = new Scanner(System.in);
		int humanMove = -1;
		System.out.println("Enter a valid move to play");
		while(!gameState.isValidMove(humanMove)) {
			humanMove = sc.nextInt();
			System.out.println("The entered move is not valid.\nEnter a valid move to play.");
		}
		return humanMove;
	}

//	Create an input file with empty board state, when the input file entered as argument in
//	interactive mode does not exist.
	private void writeEmptyBoardInFile(File input) {
		String emptyEntry = "0000000";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(input, true);
			input.createNewFile();
			for(int i=0;i<6;i++) {
				fos.write(emptyEntry.getBytes());
				fos.write("\r\n".getBytes());
			}
			fos.write("1".getBytes());
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

	/*The method returns an actions calculated using minimax algorithm with alpha-beta pruning and depth-limited search
	@param state: input game state
	@param depth: resource limit for depth-limited search
	@return action with max utility value
	*/
	private int minimaxDecision(GameBoard state, int depth) {
		int[] max = getMaxValue(state, state.nextPlayer, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
//		System.out.println("Utility: " + max[0] + " Action: " + max[1]);
		return max[1];
	}
	
//	The method forms part of the minimax algorithm
	private int[] getMaxValue(GameBoard state, Player maxPlayer, int action, int alpha, int beta, int depth) {
		if(state.isTerminalState()) return new int[] {state.getUtility(maxPlayer), action};
		if(depth > 0) {
			int maxVal = Integer.MIN_VALUE;
			for(int i=0;i<7;i++) {
//			System.out.println("Executing max for i= " + i + " isValid: " + state.isValidMove(i));
				if(state.isValidMove(i)) {
					int minVal = getMinValue(state.getActionResult(i), maxPlayer, action, alpha, beta, depth-1);
					if(minVal > maxVal) {
						maxVal = minVal;
						action = i;
					}
					if(maxVal >= beta) return new int[] {maxVal, action};
					alpha = Math.max(alpha, maxVal);
				}
			}
			return new int[] {maxVal, action};
		}
		else {
			return new int[] {state.evalValue(maxPlayer), action};
		}
	}
	
//	The method forms part of the minimax algorithm
	private int getMinValue(GameBoard state, Player maxPlayer, int action, int alpha, int beta, int depth) {
		if(state.isTerminalState()) return state.getUtility(maxPlayer);
		int minVal = Integer.MAX_VALUE;
		if(depth > 0) {
			for(int i=0;i<7;i++) {
//				System.out.println("Executing min for i= " + i + " isValid: " + state.isValidMove(i));
				if(state.isValidMove(i)) {
					minVal = Math.min(minVal, getMaxValue(state.getActionResult(i), maxPlayer, action, alpha, beta, depth-1)[0]);
					if(minVal <= alpha) return minVal;
					beta = Math.min(beta, minVal);
				}
			}
			return minVal;
		} else {
			return state.evalValue(maxPlayer);
		}
	}
	
	// This method prints a message and exits the game.
	private void exitGame() {
		System.out.println("The game board is full. Exiting the program.");
		System.exit(1);
	}
}
