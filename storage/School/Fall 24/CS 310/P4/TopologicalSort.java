import java.io.*;
import java.util.Scanner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * A class that does topological sort.
 */
class TopologicalSort {
	//yes can use try and catch
	//need to implement addall
	//the user is passing object so need to look at it

	
	//this part works and is done for you
	/**
	 * A method that takes in a file and makes a graph out of it.
	 * @param filename to make the graph
	 * @return a new graph
	 * @throws IOException if file not found
	 */
	public static ThreeTenGraph<String> getGraph(String filename) throws IOException {
		ThreeTenGraph<String> graph = new ThreeTenGraph<>();
		
		try(Scanner r = new Scanner(new File(filename))) {
			int numNodes = Integer.parseInt(r.nextLine());
			for(int i = 0; i < numNodes; i++) {
				String nodeName = r.nextLine().trim();
				graph.addVertex(nodeName);
			}
			
			int numEdges = Integer.parseInt(r.nextLine());
			for(int i = 0; i < numEdges; i++) {
				String[] fromToPriority = r.nextLine().trim().split(",");
				graph.addEdge(
					new Destination<String>(fromToPriority[1], Integer.parseInt(fromToPriority[2])),
					fromToPriority[0],
					fromToPriority[1]
				);
			}
		}
		
		return graph;
	}
	
	/**
	 * A method that does the sorting.
	 * @param <T> a generic type method
	 * @param graph to sort from
	 * @param startNode where to start from
	 * @return a linked List of the sorted graph
	 */
	//uses DFS to create a topological sort
	//accepts a graph
	//throws IllegalArgumentException for invalid inputs for a topological sort
	public static <T extends Comparable<T>> LinkedList<T> topologicalSort(ThreeTenGraph<T> graph, T startNode) {
		//error checking on inputs, requires graph and a valid starting node
		if(graph == null || startNode == null) {
			throw new IllegalArgumentException("Graph does not contain starting node");
		}
		
		//YOUR CODE HERE: Add a check to make sure the start node is in the graph.
		//If not, throw the same exception as above.
		if(!graph.containsVertex(startNode)) throw new IllegalArgumentException("Starting node is not in the graph");
		//YOUR CODE HERE: Create two empty HashSets, one called "started" and one called "finished"
		//these will help us track what we've already seen
		HashSet<T> started = new HashSet<>();
		HashSet<T> finished = new HashSet<>();
		//YOUR CODE HERE: Create an empty linked list called "sortedOrder" use as our stack
		LinkedList<T> sortedOrder = new LinkedList<>();
		//initialize DFS
		boolean dfsDone = false;
		
		while(!dfsDone) {
			//visit the start node
			if(!visit(graph, startNode, started, finished, sortedOrder)) {
				//graph has no topological sorting
				throw new IllegalArgumentException("Graph contains a cycle.");
			}
			
			//make sure all the nodes have been visited
			dfsDone = true;
			for(T v : graph.getVertices()) {
				if(!finished.contains(v)) {
					startNode = v;
					dfsDone = false;
					break;
				}
			}
		}
		
		//return the topological sorting
		return sortedOrder;
	}
	/**
	 * A privet helper method that helps with the sorting.
	 * @param <T> a generic type method 
	 * @param graph to make the sorting
	 * @param currentNode where we are now
	 * @param started where we started
	 * @param finished proccesed node
	 * @param sortedOrder done with the node
	 * @return true if no cycle
	 */
	//returns false if there was a cycle
	private static <T extends Comparable<T>> boolean visit(ThreeTenGraph<T> graph, T currentNode, HashSet<T> started, HashSet<T> finished, LinkedList<T> sortedOrder) {
		//YOUR CODE HERE: This node is "started" so add it to the started set
		started.add(currentNode);
		//YOUR CODE HERE: get the neighbors of the current node in sorted order,
		//reverse them, and store them in a linked list called "revighbors"
		Collection<T> neighbors = graph.getSuccessors(currentNode);
		LinkedList<T> revNeighbors = new LinkedList<>(neighbors);

		int len = revNeighbors.size()-1;
		for(int i = 0; i < revNeighbors.size()/2; i++) {
			T temp = revNeighbors.get(i);
			revNeighbors.set(i,revNeighbors.get(len));
			revNeighbors.set(len,temp);
			len--;
		}

		//visit each neighbor who hasn't been visited before
		for(T neighbor : revNeighbors) {
			//if we find a node that has been started but not finished,
			//then we have a cycle and cannot do a topological sort
			if(started.contains(neighbor) && !finished.contains(neighbor)) {
				return false;
			}
			
			//if the neighbor hasn't been started, we should start them!
			if(!started.contains(neighbor)) {
				//YOUR CODE HERE: visit the neighbor and return false if there was a cycle detected
				//on that visit
				//	private static <T extends Comparable<T>> boolean visit(ThreeTenGraph<T> graph, T currentNode, HashSet<T> started, HashSet<T> finished, LinkedList<T> sortedOrder) {
				if(!visit(graph, neighbor, started, finished, sortedOrder)) 
					return false;

			}
			
		}
		
		//YOUR CODE HERE: This node is "finished" so add it to the finished set
		finished.add(currentNode);
		//YOUR CODE HERE: Push this node onto our state (the front of the LinkedList
		//called "sortedOrder"
		sortedOrder.addFirst(currentNode);
		return true; //leave this, it means no loop was detected
	}

}