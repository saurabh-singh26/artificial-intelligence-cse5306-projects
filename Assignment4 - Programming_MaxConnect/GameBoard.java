import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

//This class is used to manage Player's name and their scores.
class Player {
	String name;
	int score;
	public Player(String name, int score) {
		this.name = name;
		this.score = score;
	}
	public Player(String name) {
		this(name, 0);
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getScore() {
		return this.score; 
	}
}

//This class manages the gameboard, the players of the game and next player to play the next move
public class GameBoard {
	private final static int ROW_LENGTH = 6;
	private final static int COLUMN_LENGTH = 7;
	char[][] board = new char[ROW_LENGTH][COLUMN_LENGTH];
	Player player1 = new Player("1");
	Player player2 = new Player("2");
	private final static String[] targetString = new String[] {"1111", "2222"};
	Player nextPlayer;
	
	/*This constructor parses the input file and creates the game boards and player as per the information
	present in the input file.*/
	public GameBoard(String inputFile) {
		parseInputFile(inputFile);
		initGameScore(gameScore(targetString));
	}
	
//	This constructor creates a game object with the given board state and nextPlayer information
	public GameBoard(char[][] boardState, Player nextPlayer) {
		this.board = boardState;
		this.nextPlayer = nextPlayer;
		initGameScore(gameScore(targetString));
	}
	
//	Initialize the player scores from the argument passed to the method
	private void initGameScore(int[] scores) {
		this.player1.score = scores[0];
		this.player2.score = scores[1];
	}

//	Validates if the board state is a terminal state or not
	public boolean isTerminalState() {
		for(int i=0;i<COLUMN_LENGTH;i++) {
			if(board[ROW_LENGTH-1][i]=='0') return false;
		}
		return true;
	}
	
//	Validates if the input column number is valid as per the input board state
	public boolean isValidMove(int column) {
		return column>=0 && column<COLUMN_LENGTH && board[ROW_LENGTH-1][column]=='0' ? true : false;
	}
	
//	Calculate the row number to play the move for a given column number
	public int getPosForValidMove(int column) {
		for(int i=0;i<ROW_LENGTH;i++) {
			if(board[i][column]=='0') return i;
		}
		return -1;
	}
	
//	Returns a new game board object after playing a move with given column number
	public GameBoard getActionResult(int column){
		char[][] resultBoard = createCopy(this.board);
		Player next = this.nextPlayer.name.equals("1") ? this.player2 : this.player1;
		int xPos = getPosForValidMove(column);
		if(isValidMove(column) && xPos!=-1) {
			resultBoard[xPos][column] =  this.nextPlayer.name.charAt(0);
		}
		return new GameBoard(resultBoard, next);
	}
	
	
	private char[][] createCopy(char[][] board) {
		char[][] result = new char[ROW_LENGTH][COLUMN_LENGTH];
		for(int i=ROW_LENGTH-1;i>=0;i--) {
			for(int j=0;j<COLUMN_LENGTH;j++) {
				result[i][j] = this.board[i][j];
			}
		}
		return result;
	}

//	Return the utility value depending on the player
	public int getUtility(Player maxPlayer) {
		if(maxPlayer.name.equals("1")) return this.player1.score - this.player2.score;
		return this.player2.score - this.player1.score;
	}
	
//	Print game board and score information
	public void printGameStateAndScore() {
		printGameState();
		printGameScore();
	}
	
	private void printGameState() {
		System.out.println(getGameStateAsString());
	}
	
	private void printGameScore() {
		System.out.println("Player 1: " + player1.score);
		System.out.println("Player 2: " + player2.score);
		System.out.println();
	}
	
	public String getGameStateAsString() {
		StringBuffer sb = new StringBuffer();
		for(int i=ROW_LENGTH-1;i>=0;i--) {
			for(int j=0;j<COLUMN_LENGTH;j++) {
				sb.append(this.board[i][j]);
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
//	Calculates the game score as per the input targetString and return the scores in form of array,
//	with player 1 score as first element and player 2 score as second element
	public int[] gameScore(String[] targetString) {
		// Row
		int[] score = new int[] {0,0};
		for(int i=0;i<ROW_LENGTH;i++) {
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<COLUMN_LENGTH;j++) {
				sb.append(board[i][j]);
			}
			score = calculateAndAssignScore(sb.toString(), targetString, score);
		}
		
		// Column
		for(int i=0;i<COLUMN_LENGTH;i++) {
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<ROW_LENGTH;j++) {
				sb.append(board[j][i]);
			}
			score = calculateAndAssignScore(sb.toString(), targetString, score);
		}
		
		// Diagonals
		int[][] set1 = new int[][] {{2,0}, {1,0}, {0,0}, {0,1}, {0,2}, {0,3}};
		int[][] set2 = new int[][] {{2,6}, {1,6}, {0,6}, {0,5}, {0,4}, {0,3}};
		for(int i=0;i<set1.length;i++) {
			int x1 = set1[i][0];
			int y1 = set1[i][1];
			int x2 = set2[i][0];
			int y2 = set2[i][1];
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			do sb1.append(board[x1][y1]);
			while(++x1 < ROW_LENGTH && ++y1 < COLUMN_LENGTH);
			do sb2.append(board[x2][y2]);
			while(++x2 < ROW_LENGTH && --y2 >= 0);
			score = calculateAndAssignScore(sb1.toString(), targetString, score);
			score = calculateAndAssignScore(sb2.toString(), targetString, score);
		}
		return score;
	}
	
	// Used to calculate score information
	private int[] calculateAndAssignScore(String actualString, String[] targetString, int[] score) {
		int fromIndex = 0;
		int index1 = actualString.indexOf(targetString[0], fromIndex);
		int index2 = actualString.indexOf(targetString[1], fromIndex);
		while(index1!=-1 || index2!=-1) {
			if(index1 >= 0) {
				score[0] = score[0] + 1;
				fromIndex = index1+1;
			}
			if(index2 >= 0) {
				score[1] = score[1] + 1;
				fromIndex = index2+1;
			}
			index1 = actualString.indexOf(targetString[0], fromIndex);
			index2 = actualString.indexOf(targetString[1], fromIndex);
		}
		return score;
	}

//	Reads the input file provided in the constructor and create a game object 
	private void parseInputFile(String inputFile) {
		File input = new File(inputFile);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(input.getPath()));
			String line;
			int row = ROW_LENGTH-1;
			while((line=br.readLine()) != null) {
				int columnLength = line.length();
				if(columnLength == COLUMN_LENGTH && row >= 0) {
					for(int column = 0;column < columnLength; column++) {
						board[row][column] = line.charAt(column);
					}
				}
				else if(columnLength==1) nextPlayer = line.equals("1") ? player1 : player2;
				else {
					System.out.println("Invalid entry in board input file at row " + row + " -> " + line);
					exitGame("Enter a valid input file. Exiting program.");
				}
				row--;
			}
		} catch (FileNotFoundException e) {
			exitGame("Input file not found. Execute the command with valid input file.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br!=null) br.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	}

//	The method writes the resultant game state to the file provided in the input
	public void copyToFile(String outputFile) {
		FileOutputStream fos = null;
		try {
			File output = new File(outputFile);
			if(!output.exists()) output.createNewFile();
			fos = new FileOutputStream(output, false);
			fos.write(getGameStateAsString().getBytes());
			fos.write(this.nextPlayer.name.getBytes());
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

//	The method calculates the evalScore for non terminal states
	public int evalValue(Player maxPlayer) {
		String[] ones = new String[] {"1110", "1100", "1000"};
		String[] twos = new String[] {"2220", "2200", "2000"};
		int[] score = new int[2];
		for(int i=0;i<ones.length;i++) {
			int[] tempScore = gameScore(new String[] {ones[i], twos[i]});
			if(score[0]==0 && tempScore[0]!=0) score[0] = 3-i;
			if(score[1]==0 && tempScore[1]!=0) score[1] = 3-i;
			if(score[0]!=0 && score[1]!=0) break;
		}
//		System.out.println("Eval Scores: " + score[0] + " & " + score[1]);
		if(maxPlayer.name.equals("1")) return score[0] - score[1];
		else return score[1] - score[0];
	}
	
	private void exitGame(String msg) {
		System.out.println(msg);
		System.exit(1);
	}
	
}
