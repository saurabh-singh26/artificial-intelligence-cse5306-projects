import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

// A Node object stores the current city name, its parents and the cumulative path cost to reach the city
class Node{
	String city;
	Node parent;
	int pathCost;
	Node(String city, Node parent, int pathCost){
		this.city = city;
		this.parent = parent;
		this.pathCost = pathCost;
	}
}

// An explicit comparator defined for the priority queue which sorts the Node object on the basis of cumulative path cost
class NodeComparator implements Comparator<Node>{
	public int compare(Node n1, Node n2) {
		if(n1.pathCost > n2.pathCost) return 1;
		else if(n1.pathCost < n2.pathCost) return -1;
		else return 0;
	}	
}

public class find_route {
	/*
	successorMap is used to store the information provided in input.txt file. Every city in the input is a key for which the value is
	an ArrayList of arrays. Eg:
	1) Arad: {[Sibiu,140], [Zerind,75], [Timisoara,118]}
	2) Zerind: {[Arad,75], [Oradea,71]}
	*/
	Map<String, ArrayList<String[]>> successorMap = new HashMap<String, ArrayList<String[]>>();
	
	/*
	routeMap store the shortest cumulative distance to a node from start state. 
	This is used to back trace the path once goal test is passed. Example:
	1) Sibiu: [Arad, 140] - The least cost path to reach Sibiu from the source Node contains Arad as its immediate parent, which
	is 140 km from Sibiu
	*/
	Map<String, Object[]> routeMap = new HashMap<String, Object[]>();
	
	private void findRoute(String inputFile, String source, String destination) {
		// Parse the input file and store the given information in successor map initialized above
		parseInputFile(inputFile);
		// The set stores the nodes which has been already been expanded
		Set<String> visited = new HashSet<String>();
		// The queue stores the Node object on the basis of cumulative path cost
		PriorityQueue<Node> fringe = new PriorityQueue<Node>(1, new NodeComparator());
		// Add the start node to the fringe with parent as null and cumulative path cost as zero
		fringe.add(new Node(source, null, 0));
		
		// Keep exploring the nodes until the fringe is empty or the goal is reached, which is verified inside the loop
		while(!fringe.isEmpty()) {
			Node current = fringe.poll(); // Extract the Node with least cumulative path cost
			insertInRouteMap(current); // Insert the Node in routeMap as it has already been visited. The least cost check is implemented in the method
			if(current.city.equals(destination)) break; // Break from the loop if current node passes the goal test
			
			// If goal test is not achieved then, parse all the children of current city and store those in fringe if it has not been visited already
			ArrayList<String[]> children = successorMap.get(current.city);
			Iterator<String[]> itr = children.iterator();
			while(itr.hasNext()) {
				String[] s = itr.next();
				Node n = new Node(s[0], current, current.pathCost + Integer.parseInt(s[1]));
				if(!visited.contains(n.city)) fringe.add(n);
			}
			
			// Add the current city to visited set as the Node has already been expanded above
			visited.add(current.city);
		}
		// After exiting from the loop, print the output using the information stored in routeMap
		printOutput(destination);
	}
	
	/*
	The method takes as input the destination city and search in the routeMap if it exists. If not, then the goal test is failed,
	and distance is infinity, else read the parent information and iterate through the map entries to read the parent information
	until its null, which means we have reached the start state.
	*/
	private void printOutput(String destination) {
		String totalDistance = "infinity";
		Stack<String> route = new Stack<String>(); // Store the information in stack so as to print the path from start state
		if(routeMap.containsKey(destination)) {
			totalDistance = routeMap.get(destination)[1] + " km";
			String parent = (String) routeMap.get(destination)[0];
			while(parent!=null) {
				int dist = (Integer) routeMap.get(destination)[1] - (Integer) routeMap.get(parent)[1];
				route.push(parent + " to " + destination + ", " + dist + " km");
				destination = parent;
				parent = (String) routeMap.get(destination)[0];
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("distance: " + totalDistance + "\n");
		sb.append("route:\n");
		if(route.isEmpty()) sb.append("none");
		else {
			while(!route.isEmpty()) {
				sb.append(route.pop());
				sb.append("\n");
			}
		}
		System.out.println(sb.toString());
	}
	
	/*
	The method put an entry in the map if:
	1) an entry does not exist yet, OR
	2) an entry exist with cumulative cost more than the current Node.
	*/
	private void insertInRouteMap(Node current) {
		if(!routeMap.containsKey(current.city) || (Integer) routeMap.get(current.city)[1] > current.pathCost) {
			// Update the cost as well as the parent
			Object[] value = {current.parent!=null ? current.parent.city : null, current.pathCost}; 
			routeMap.put(current.city, value);			
		}
	}
	
	// Parse the input file and store the information in successorMap
	private void parseInputFile(String inputFile) {
		File input = new File(inputFile);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(input.getPath()));
			String line;
			while(!(line=br.readLine()).equals("END OF INPUT")) {
				String start = line.split(" ")[0];
				String end = line.split(" ")[1];
				String distance = line.split(" ")[2];
				storeInMap(start, end, distance);
				// If there is a path from A to B, then there is also a path from B to A with same distance
				storeInMap(end, start, distance);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Input file not found");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// The method stores the input file information in successor map
	private void storeInMap(String start, String end, String distance) {
		String[] entry = {end, distance};
		if(successorMap.containsKey(start)) {
			successorMap.get(start).add(entry);
		} else {
			ArrayList<String[]> temp = new ArrayList<String[]>();
			temp.add(entry);
			successorMap.put(start, temp);
		}
	}

	public static void main(String[] args) {
		find_route fr = new find_route();
		// Validating if the command line arguments provided are valid. If yes, the call findRoute method else display a error message
		if(args.length == 3 
				&& (args[0]!=null || args[0]!="") 
				&& (args[1]!=null || args[1]!="") 
				&& (args[2]!=null || args[2]!="")) {
			fr.findRoute(args[0], args[1], args[2]);
		} else {
			System.out.println("Enter valid input arguments");
		}
	}
}
