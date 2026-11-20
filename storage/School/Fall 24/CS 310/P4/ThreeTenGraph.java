import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
// import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.DirectedGraph;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.apache.commons.collections15.Factory;

//No other imports allowed
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

//no need for class cast exceptions

/**
 * A class that creates a graph.
 *
 * @param <V> is a generic type for vertices in the graph
 */

class ThreeTenGraph<V extends Comparable<V>> implements Graph<V,Destination<V>>, DirectedGraph<V,Destination<V>> {
	//Hint -- Read the Java documentation for LinkedHashMap... it has
	//a lot of methods that will help you write yours!
	//https://docs.oracle.com/javase/9/docs/api/java/util/LinkedHashMap.html
	
	//you may not have any other instance variables, only this ONE
	//if you make more instance variables your graph class will receive a 0,
	//no matter how well it works
 /**
   * An instance variable of adjacency table.
   */
	private LinkedHashMap<V,MinHeap<Destination<V>>> adjHeap = null;
	
    /**
     * Returns a view of all vertices in this graph. In general, this
     * obeys the Collection contract, and therefore makes no guarantees 
     * about the ordering of the vertices within the set.
     * @return a Collection view of all vertices in this graph
     */
    public Collection<V> getVertices() {
        //O(n) where n is the number of nodes in the graph
        
        //IMPORTANT: You must NOT _directly_ return a reference to anything
        //still in use by the graph class (e.g. the adjHeap key set).
        //If you pass that back, it could be edited from outside this class!
        
        //Linked list has a "copy constructor" you can use to make copies
        //of things you want to return that are still in use:
        //https://docs.oracle.com/javase/9/docs/api/java/util/LinkedList.html#LinkedList-java.util.Collection-
        
        //replace this!
        // return null;

        // LinkedList<V> list= new LinkedList<V>();
        // for(Map.Entry<V,MinHeap<Destination<V>>> entry : adjHeap.entrySet()){
        //   list.add(entry.getKey());
        // }
          
        // ;
        // return adjHeap.keySet();
        return new LinkedList<>(adjHeap.keySet());//why not return list directly? piaza
    

	}
	
    /**
     * Returns a view of all edges in this graph. In general, this
     * obeys the Collection contract, and therefore makes no guarantees 
     * about the ordering of the vertices within the set.
     * @return a Collection view of all edges in this graph
     */
    public Collection<Destination<V>> getEdges() {
        //O(n+e) where e is the number of edges in the graph and n is the number
        //of nodes in the graph
        
        //Big-O hint: O(n+e) with the above constraints just means you
        //should be using iterators for your hash-map and your heaps
        
        //Hint: the return type for this is a Collection. Java's LinkedList
        //_is a_ type of collection (and that is imported for you).
        
        //replace this!
        // return null;

        LinkedList<Destination<V>> toreturn = new LinkedList<>();
        for(V  key : adjHeap.keySet()){
            MinHeap<Destination<V>> heap = adjHeap.get(key);
            for(Destination<V> dest : heap){
                if(dest!=null){
                    toreturn.add(dest);
                }
            }
        }
        // return toreturn;//do we need a copy like getVertices()
        return new LinkedList<>(toreturn);

	}
    
    /**
     * Returns the number of vertices in this graph.
     * @return the number of vertices in this graph
     */
    public int getVertexCount() {
        //O(1)
        
        //note: this runtime is not a mistake, think about how
        //you could find out the number of nodes *without*
        //looking at each one
        
        //replace this!
        // return -1;

        return adjHeap.size();

	}
    
    /**
     * Returns the number of edges in this graph.
     * @return the number of edges in this graph
     */
    public int getEdgeCount() {
        //O(n) where n is the number of nodes in the graph
        //note: this is NOT O(n+e), just O(n)
        
        //note: this runtime is not a mistake, think about how
        //you could find out the number of edges *without*
        //looking at each one


        int edgecount = 0;
        for(V v : adjHeap.keySet()) {
            edgecount += adjHeap.get(v).size();
        }
        return edgecount;

	}

    /**
     * Returns true if this graph's vertex collection contains vertex.
     * Equivalent to getVertices().contains(vertex).
     * @param vertex the vertex whose presence is being queried
     * @return true iff this graph contains a vertex vertex
     */
    public boolean containsVertex(V vertex) {
        //O(1), NOT O(n)!
        
        //note: this runtime is not a mistake, there is a "quick" way
        //and a slow way to find out if a node is in the graph, look at
        //the storage overview in the project description for ideas
		
		      //replace this!
        if (vertex == null) throw new IllegalArgumentException();//return null or throw an exception
		return adjHeap.containsKey(vertex);
	}
    
    /**
     * Returns a Collection view of the successors of vertex 
     * in this graph.  A successor of vertex is defined as a vertex v 
     * which is connected to 
     * vertex by an edge e, where e is an incoming edge of 
     * v and an outgoing edge of vertex.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a Collection view of the successors of vertex in this graph
     */
    public Collection<V> getSuccessors(V vertex) {
        //O(n lg n) where n is the number of nodes in the graph
        //this MUST return successors in SORTED ORDER (smallest to largest)
        //this may NOT use a non-heap sorting algorithm, you must use heaps!
        //Good news is, you already have those... and can copy them...
        
        //replace this!
        // return null;

        //what happens if vertex does not exist???

        MinHeap<Destination<V>> minHeap = adjHeap.get(vertex);
        if(minHeap==null)return null;//?
        //if vertex ==null
    
        LinkedList<V> successors = new LinkedList<V>();
        //use the current queue and use the constucor to make a copy then wihile loop
        MinHeap<Destination<V>> queuecopy = new MinHeap<Destination<V>>(minHeap);
        while(!queuecopy.isEmpty()){
            Destination<V> toadd = queuecopy.remove();
            successors.add(toadd.node);
        }
        return new LinkedList<>(successors);

        // for(Destination<V> dest : minHeap){
        //   successors.add(dest.node);
        // }
        // return new LinkedList<>(successors);

	}
	
    /**
     * Returns a Collection view of the predecessors of vertex 
     * in this graph.  A predecessor of vertex is defined as a vertex v 
     * which is connected to 
     * vertex by an edge e, where e is an outgoing edge of 
     * v and an incoming edge of vertex.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a Collection view of the predecessors of vertex in this graph
     */
    public Collection<V> getPredecessors(V vertex) {
        //O(n+e) where e is the number of edges in the graph and n is the number of nodes
        
        //replace this!
        // return null;
        if(vertex == null) return null;
        if(!containsVertex(vertex))  throw new IllegalArgumentException("vertex is not in graph");
    
        Collection<V> predecessors = new LinkedList<>();
        for(V key: adjHeap.keySet()) {
            MinHeap<Destination<V>> heap = adjHeap.get(key);
            for(Destination<V> v : heap){
                // if(v.node == null) continue;
                if(v.node.equals(vertex)){
                    predecessors.add(key);
                }
            }
        }
        return new LinkedList<>(predecessors);

	}

    /**
     * Returns an edge that connects v1 to v2.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting 
     * v1 to v2), any of these edges 
     * may be returned.  findEdgeSet(v1, v2) may be 
     * used to return all such edges.
     * Returns null if either of the following is true:
     * <ul>
     * <li/>v1 is not connected to v2
     * <li/>either v1 or v2 are not present in this graph
     * </ul>
     *  
     * <p><b>Note</b>: for purposes of this method, v1 is only considered to be connected to
     * v2 via a given <i>directed</i> edge e if
     * v1 == e.getSource() && v2 == e.getDest() evaluates to true.
     * (v1 and v2 are connected by an undirected edge u if 
     * u is incident to both v1 and v2.)</p>
     * @param v1 edge to find that connect v1 to v2
     * @param v2 edge to find that connect v1 to v2
     * @return  an edge that connects v1 to v2,or null if no such edge exists (or either vertex is not present)
     * @see Hypergraph#findEdgeSet(Object, Object) 
     */
    public Destination<V> findEdge(V v1, V v2) {
        //O(n) where n is the number of nodes in the graph
        
        //replace this!
        // return null;
    
        if(!containsVertex(v1) || !containsVertex(v2)) return null;
    
        MinHeap<Destination<V>> minHeapv1 = adjHeap.get(v1);
        for(Destination<V> i: minHeapv1){
            if(i.node.equals(v2)){
                return i;
            }
        }

        // MinHeap<Destination<V>> minHeapv2 = adjHeap.get(v2);
        // for(Destination<V> I: minHeapv2){
        //   if(I.node.equals(v1)){
        //     return I;
        //   }
        // }
        return null;
	}
	
    /**
     * Returns the endpoints of edge as a Pair.
     * @param edge the edge whose endpoints are to be returned
     * @return the endpoints (incident vertices) of edge or null if the edge does not exist
     */
    public Pair<V> getEndpoints(Destination<V> edge) {
        //O(n+e) where e is the number of edges in the graph and n is the number of nodes
        //first part of the pair should be the source node, the second the destination node.
        
        //You can create a pair with "new Pair<V>(source, dest)" when you find it.
        
        //replace this!
        // return null;

        //destination = edge.node
        //source = key of the edge

        if(edge == null) return null;
        V source = null;
        V dest = edge.node;
        //find the key of the dest edge.
        // for(Map.Entry<V,MinHeap<Destination<V>>> entry :adjHeap.entrySet()) {
        for(V key : adjHeap.keySet()) {
            // V vertex = entry.getKey();
            MinHeap<Destination<V>> heap = adjHeap.get(key);
            for(Destination<V> i: heap){
                if(i.equals(edge)){
                    source = key;
                    return new Pair<V>(source,dest);
                }
            }
        }
        return null;

	}
    
    /**
     * Adds vertex to this graph.
     * Fails if vertex is null or already in the graph.
     * 
     * @param vertex    the vertex to add
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if vertex is null
     */
    public boolean addVertex(V vertex) {
        //O(1) -- NOT O(n)!
        
        //NOTE: a node in the graph should have a non-null linked
        //list in the adjacency matrix, a node not in the graph
        //should have a null reference at that same spot
        
        //replace this!
        // System.out.println("Here in addvertex seeing what is vertex: " + vertex);
        // System.out.println("Here in addvertex seeing what is adjHeap.get(vertex): " + adjHeap.get(vertex));
        if(vertex == null) throw new IllegalArgumentException();
        if(adjHeap.get(vertex) == null){
            adjHeap.put(vertex, new MinHeap<Destination<V>>());    

            return true;
        }
        return false;
	}

    /**
     * Adds edge e to this graph such that it connects 
     * vertex v1 to v2.
     * Equivalent to {@code addEdge(e, new Pair<V>(v1, v2))}.
     * If this graph does not contain v1, v2, 
     * or both, implementations may choose to either silently add 
     * the vertices to the graph or throw an IllegalArgumentException.
     * If this graph assigns edge types to its edges, the edge type of
     * e will be the default for this graph.
     * See Hypergraph.addEdge() for a listing of possible reasons
     * for failure.
     * @param e the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @return true if the add is successful, false otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object, EdgeType)
     */
    public boolean addEdge(Destination<V> e, V v1, V v2) {
        //from v1 to v2 not cas
        //O(n+e) where e is the number of edges in the graph and n is the number
        //of nodes in the graph
        
        //note: you need to make sure there isn't a different edge connecting v1 and v2,
        //that the destination object isn't being used already in the graph, and
        //you need to assign the node in destination to the correct endpoint
        
        //replace this!
        // return false;

        // Returns null if either of the following is true:
        // * <ul>
        // * <li/>v1 is not connected to v2
        // * <li/>either v1 or v2 are not present in this graph
        // * </ul> 
        if(v1 == null || v2 == null) throw new IllegalArgumentException();
        if(!containsVertex(v1) || !containsVertex(v2)) throw new IllegalArgumentException();
        ////Still needs to make sure there isn't a different edge connecting v1 and v2
        if(findEdge(v1, v2) == null){ //if already connected return false?

            Collection<Destination<V>> setofedges = getEdges();
            for(Destination<V> edges : setofedges) {
                if(e.equals(edges)){
                    return false;
                }
            }

            //am I reusing the same edge that already exists
            if(adjHeap.get(v1) != null && adjHeap.get(v2) != null){

                adjHeap.get(v1).add(e);
                e.node = v2;

                return true;
            }
        }
        return false;


	}
    
    /**
     * Removes vertex from this graph.
     * As a side effect, removes any edges e incident to vertex if the 
     * removal of vertex would cause e to be incident to an illegal
     * number of vertices.  (Thus, for example, incident hyperedges are not removed, but 
     * incident edges--which must be connected to a vertex at both endpoints--are removed.) 
     * 
     * <p>Fails under the following circumstances:
     * <ul>
     * <li/>vertex is not an element of this graph
     * <li/>vertex is null
     * </ul>
     * 
     * @param vertex the vertex to remove
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeVertex(V vertex) {
        //O(n+e) where e is the number of edges in the graph and n is the number of nodes
        
        //remember to remove edges that point _to_ this node!
        
        //replace this!
        // return false;
    
        if(vertex == null) return false;
        if(!containsVertex(vertex)) return false;
        // if(adjHeap.get(vertex) == null) return false;
        
        adjHeap.remove(vertex);
        System.out.println(vertex + " to be removed");
        for(V key:adjHeap.keySet()){
            MinHeap<Destination<V>> heapvalues = adjHeap.get(key);
            for(Destination<V> value : heapvalues){
                if(value.node.equals(vertex)){
                    heapvalues.remove(value);
                }
            }
        }
        return true; 
	}



    /**
     * Removes edge from this graph.
     * Fails if edge is null, or is otherwise not an element of this graph.
     * 
     * @param edge the edge to remove
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeEdge(Destination<V> edge) {
        //O(n+e) where e is the number of edges in the graph and n is the number
        //of nodes in the graph
        
        //don't over think this, find the heap containing the Destination<V> object
        //after this method is over that object shouldn't be in the heap associated with the source node
        //you also may want to look at the optional methods you could implement as part of the Queue interface...
        
        //replace this!
        // return false;
        // System.out.println("IN removeEdge");

        if (edge == null) return false;
        for(V key: adjHeap.keySet()){
            MinHeap<Destination<V>> heapvalues = adjHeap.get(key);
            heapvalues.remove(edge);
        }
        return true;
	}
  
	
	//********************************************************************************
	//   testing code goes here... edit this as much as you want!
	//********************************************************************************
	
	/**
	 *  {@inheritDoc}
	 */
	public String toString() {
		return super.toString();
	}

    /**
     * A hleper method that prints the contents of the graph.
     */
    private void printGraph() {
        System.out.println("Graph contents:");
        System.out.println('{');
        for(V key:adjHeap.keySet()) {
            V vertex = key;
            MinHeap<Destination<V>> heap = adjHeap.get(vertex);
            System.out.print(vertex + " = ");
        
        }
        System.out.print('}');
        System.out.println();
    }

  

	
	//********************************************************************************
	//   YOU MAY, BUT DON'T NEED TO EDIT THINGS IN THIS SECTION
	//********************************************************************************

 /**
  * A public constructor.
  */
	@SuppressWarnings("unchecked")
	public ThreeTenGraph() {
		adjHeap = new LinkedHashMap<>();
	}
	
    /**
     * Returns true if vertex and edge are incident to each other.
     * Equivalent to getIncidentEdges(vertex).contains(edge) and to
     * getIncidentVertices(edge).contains(vertex).
     * @param vertex to look for
     * @param edge to look for
     * @return true if vertex and edge are incident to each other
     */
    public boolean isIncident(V vertex, Destination<V> edge) {
		return getIncidentEdges(vertex).contains(edge);
	}
    
    /**
     * Returns true if v1 is a predecessor of v2 in this graph.
     * Equivalent to v1.getPredecessors().contains(v2).
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return true if v1 is a predecessor of v2, and false otherwise.
     */
    public boolean isPredecessor(V v1, V v2) {
		return getPredecessors(v1).contains(v2);
	}
    
    /**
     * Returns true if v1 is a successor of v2 in this graph.
     * Equivalent to v1.getSuccessors().contains(v2).
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return true if v1 is a successor of v2, and false otherwise.
     */
    public boolean isSuccessor(V v1, V v2) {
		return getSuccessors(v1).contains(v2);
	}
	
    /**
     * Returns a Collection view of the incoming edges incident to vertex
     * in this graph.
     * @param vertex    the vertex whose incoming edges are to be returned
     * @return  a Collection view of the incoming edges incident to vertex in this graph
     */
    public Collection<Destination<V>> getInEdges(V vertex) {
		LinkedList<Destination<V>> ret = new LinkedList<>();
		for(V pred : getPredecessors(vertex)) {
			ret.add(findEdge(pred, vertex));
		}
		return ret;
	}
    
    /**
     * Returns a Collection view of the outgoing edges incident to vertex
     * in this graph.
     * @param vertex    the vertex whose outgoing edges are to be returned
     * @return  a Collection view of the outgoing edges incident to vertex in this graph
     */
    public Collection<Destination<V>> getOutEdges(V vertex) {
		LinkedList<Destination<V>> ret = new LinkedList<>();
		for(V pred : getSuccessors(vertex)) {
			ret.add(findEdge(vertex, pred));
		}
		return ret;
	}

    /**
     * Returns true if v1 and v2 share an incident edge.
     * Equivalent to getNeighbors(v1).contains(v2).
     * 
     * @param v1 the first vertex to test
     * @param v2 the second vertex to test
     * @return true if v1 and v2 share an incident edge
     */
    public boolean isNeighbor(V v1, V v2) {
		return (findEdge(v1, v2) != null);
	}
    
    /**
     * Returns true if this graph's edge collection contains edge.
     * Equivalent to getEdges().contains(edge).
     * @param edge the edge whose presence is being queried
     * @return true iff this graph contains an edge edge
     */
    public boolean containsEdge(Destination<V> edge) {
		return (getEndpoints(edge) != null);
	}
    
    /**
     * Returns the number of incoming edges incident to vertex.
     * Equivalent to getInEdges(vertex).size().
     * @param vertex    the vertex whose indegree is to be calculated
     * @return  the number of incoming edges incident to vertex
     */
    public int inDegree(V vertex) {
		return getInEdges(vertex).size();
	}
    
    /**
     * Returns the number of outgoing edges incident to vertex.
     * Equivalent to getOutEdges(vertex).size().
     * @param vertex    the vertex whose outdegree is to be calculated
     * @return  the number of outgoing edges incident to vertex
     */
    public int outDegree(V vertex) {
		return getOutEdges(vertex).size();
	}
    
    /**
     * Returns the collection of edges in this graph which are of type edge_type.
     * @param edgetype the type of edges to be returned
     * @return the collection of edges which are of type edgetype, or null if the graph does not accept edges of this type
     * @see EdgeType
     */
    public Collection<Destination<V>> getEdges(EdgeType edgetype) {
		if(edgetype == EdgeType.DIRECTED) {
			return getEdges();
		}
		return null;
	}
    
    /**
     * If directedEdge is a directed edge in this graph, returns the source; 
     * otherwise returns null. 
     * The source of a directed edge d is defined to be the vertex for which  
     * d is an outgoing edge.
     * directedEdge is guaranteed to be a directed edge if 
     * its EdgeType is DIRECTED. 
     * @param directedEdge to look for
     * @return  the source of directedEdge if it is a directed edge in this graph, or null otherwise
     */
    public V getSource(Destination<V> directedEdge) {
		return getEndpoints(directedEdge).getFirst();
	}

    /**
     * If directedEdge is a directed edge in this graph, returns the destination; 
     * otherwise returns null. 
     * The destination of a directed edge d is defined to be the vertex 
     * incident to d for which  
     * d is an incoming edge.
     * directedEdge is guaranteed to be a directed edge if 
     * its EdgeType is DIRECTED. 
     * @param directedEdge to get its dest
     * @return  the destination of directedEdge if it is a directed edge in this graph, or null otherwise
     */
    public V getDest(Destination<V> directedEdge) {
		return getEndpoints(directedEdge).getSecond();
	}

    /**
     * Returns the number of edges of type edge_type in this graph.
     * @param edgetype the type of edge for which the count is to be returned
     * @return the number of edges of type edge_type in this graph
     */
    public int getEdgeCount(EdgeType edgetype) {
		if(edgetype == EdgeType.DIRECTED) {
			return getEdgeCount();
		}
		return 0;
	}
    
    /**
     * Returns the number of edges incident to vertex.  
     * Special cases of interest:
     * <ul>
     * <li/> Incident self-loops are counted once.
     * <li> If there is only one edge that connects this vertex to
     * each of its neighbors (and vice versa), then the value returned 
     * will also be equal to the number of neighbors that this vertex has
     * (that is, the output of getNeighborCount).
     * <li> If the graph is directed, then the value returned will be 
     * the sum of this vertex's indegree (the number of edges whose 
     * destination is this vertex) and its outdegree (the number
     * of edges whose source is this vertex), minus the number of
     * incident self-loops (to avoid double-counting).
     * </ul>
     * 
     * <p>Equivalent to getIncidentEdges(vertex).size().
     * 
     * @param vertex the vertex whose degree is to be returned
     * @return the degree of this node
     * @see Hypergraph#getNeighborCount(Object)
     */
    public int degree(V vertex) {
		return inDegree(vertex)+outDegree(vertex);
	}
    
    /**
     * Returns the number of vertices that are adjacent to vertex
     * (that is, the number of vertices that are incident to edges in vertex's
     * incident edge set).
     * 
     * <p>Equivalent to getNeighbors(vertex).size().
     * @param vertex the vertex whose neighbor count is to be returned
     * @return the number of neighboring vertices
     */
    public int getNeighborCount(V vertex) {
		return getNeighbors(vertex).size();
	}
    
    /**
     * Returns the collection of edges in this graph which are connected to vertex.
     * 
     * @param vertex the vertex whose incident edges are to be returned
     * @return  the collection of edges which are connected to vertex, or null if vertex is not present
     */
    public Collection<Destination<V>> getIncidentEdges(V vertex) {
		//O(n) where n is the number of nodes in the graph
		
		LinkedList<Destination<V>> ret = new LinkedList<>();
		
		ret.addAll(getInEdges(vertex));
		ret.addAll(getOutEdges(vertex));
		
		return ret;
	}
    
    /**
     * Returns the collection of vertices which are connected to vertex
     * via any edges in this graph.
     * If vertex is connected to itself with a self-loop, then 
     * it will be included in the collection returned.
     * 
     * @param vertex the vertex whose neighbors are to be returned
     * @return  the collection of vertices which are connected to vertex, or null if vertex is not present
     */
    public Collection<V> getNeighbors(V vertex) {
		LinkedList<V> ret = new LinkedList<>();
		
		Collection<V> fetch = getPredecessors(vertex);
		if(fetch != null) ret.addAll(fetch);
		fetch = getSuccessors(vertex);
		if(fetch != null) ret.addAll(fetch);
		
		return ret;
	}
    
    /**
     * Returns the collection of vertices in this graph which are connected to edge.
     * Note that for some graph types there are guarantees about the size of this collection
     * (i.e., some graphs contain edges that have exactly two endpoints, which may or may 
     * not be distinct).  Implementations for those graph types may provide alternate methods 
     * that provide more convenient access to the vertices.
     * 
     * @param edge the edge whose incident vertices are to be returned
     * @return  the collection of vertices which are connected to edge, or null if edge is not present
     */
    public Collection<V> getIncidentVertices(Destination<V> edge) {
		
		Pair<V> p = getEndpoints(edge);
		if(p == null) return null;
		
		LinkedList<V> ret = new LinkedList<>();
		ret.add(p.getFirst());
		ret.add(p.getSecond());
		return ret;
	}

    /**
     * Returns the number of predecessors that vertex has in this graph.
     * Equivalent to vertex.getPredecessors().size().
     * @param vertex the vertex whose predecessor count is to be returned
     * @return  the number of predecessors that vertex has in this graph
     */
    public int getPredecessorCount(V vertex) {
		return inDegree(vertex);
	}
    
    /**
     * Returns the number of successors that vertex has in this graph.
     * Equivalent to vertex.getSuccessors().size().
     * @param vertex the vertex whose successor count is to be returned
     * @return  the number of successors that vertex has in this graph
     */
    public int getSuccessorCount(V vertex) {
		return outDegree(vertex);
	}
    
    /**
     * Returns the vertex at the other end of edge from vertex.
     * (That is, returns the vertex incident to edge which is not vertex.)
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return the vertex at the other end of edge from vertex
     */
    public V getOpposite(V vertex, Destination<V> edge) {
		Pair<V> p = getEndpoints(edge);
		if(p.getFirst().equals(vertex)) {
			return p.getSecond();
		}
		else {
			return p.getFirst();
		}
	}
    
    /**
     * Returns all edges that connects v1 to v2.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting 
     * v1 to v2), any of these edges 
     * may be returned.  findEdgeSet(v1, v2) may be 
     * used to return all such edges.
     * Returns null if v1 is not connected to v2.
     * <br/>Returns an empty collection if either v1 or v2 are not present in this graph.
     * 
     * <p><b>Note</b>: for purposes of this method, v1 is only considered to be connected to
     * v2 via a given <i>directed</i> edge d if
     * v1 == d.getSource() && v2 == d.getDest() evaluates to true.
     * (v1 and v2 are connected by an undirected edge u if 
     * u is incident to both v1 and v2.)
     * @param v1 to find edge
     * @param v2 to find edge
     * @return  a collection containing all edges that connect v1 to v2, or null if either vertex is not present
     * @see Hypergraph#findEdge(Object, Object) 
     */
    public Collection<Destination<V>> findEdgeSet(V v1, V v2) {
		Destination<V> edge = findEdge(v1, v2);
		if(edge == null) {
			return null;
		}
		
		LinkedList<Destination<V>> ret = new LinkedList<>();
		ret.add(edge);
		return ret;
		
	}
	
    /**
     * Returns true if vertex is the source of edge.
     * Equivalent to getSource(edge).equals(vertex).
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return true iff vertex is the source of edge
     */
    public boolean isSource(V vertex, Destination<V> edge) {
		return getSource(edge).equals(vertex);
	}
    
    /**
     * Returns true if vertex is the destination of edge.
     * Equivalent to getDest(edge).equals(vertex).
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return true iff vertex is the destination of edge
     */
    public boolean isDest(V vertex, Destination<V> edge) {
		return getDest(edge).equals(vertex);
	}
    
    /**
     * Adds edge e to this graph such that it connects 
     * vertex v1 to v2.
     * Equivalent to {@code addEdge(e, new Pair<V>(v1, v2))}.
     * If this graph does not contain v1, v2, 
     * or both, implementations may choose to either silently add 
     * the vertices to the graph or throw an IllegalArgumentException.
     * If edgeType is not legal for this graph, this method will
     * throw IllegalArgumentException.
     * See Hypergraph.addEdge() for a listing of possible reasons
     * for failure.
     * @param e the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @param edgeType the type to be assigned to the edge
     * @return true if the add is successful, false otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object)
     */
    public boolean addEdge(Destination<V> e, V v1, V v2, EdgeType edgeType) {
		//NOTE: Only directed edges allowed
		
		if(edgeType == EdgeType.UNDIRECTED) {
            throw new IllegalArgumentException();
        }
		
		return addEdge(e, v1, v2);
	}
    
 /**
 * Adds edge to this graph.
 * Fails under the following circumstances:
 * <ul>
 * <li/>edge is already an element of the graph 
 * <li/>either edge or vertices is null
 * <li/>vertices has the wrong number of vertices for the graph type
 * <li/>vertices are already connected by another edge in this graph,
 * and this graph does not accept parallel edges
 * </ul>
 * 
 * @param edge to addedge
 * @param vertices to add
 * @return true if the add is successful, and false otherwise
 * @throws IllegalArgumentException if edge or vertices is null, or if a different vertex set in this graph is already connected by edge, or if vertices are not a legal vertex set for edge 
 */
	@SuppressWarnings("unchecked")
    public boolean addEdge(Destination<V> edge, Collection<? extends V> vertices) {
		if(edge == null || vertices == null || vertices.size() != 2) {
			return false;
		}
		
		V[] vs = (V[])vertices.toArray();
		return addEdge(edge, vs[0], vs[1]);
	}

 /**
 * Adds edge to this graph with type edgetype.
 * Fails under the following circumstances:
 * <ul>
 * <li/>edge is already an element of the graph 
 * <li/>either edge or vertices is null
 * <li/>vertices has the wrong number of vertices for the graph type
 * <li/>vertices are already connected by another edge in this graph,
 * and this graph does not accept parallel edges
 * <li/>edgetype is not legal for this graph
 * </ul>
 * 
 * @param edge to add
 * @param vertices to add
 * @param edgetype to add
 * @return true if the add is successful, and false otherwise
 * @throws IllegalArgumentException if edge or vertices is null, or if a different vertex set in this graph is already connected by edge, or if vertices are not a legal vertex set for edge 
     */
	@SuppressWarnings("unchecked")
    public boolean addEdge(Destination<V> edge, Collection<? extends V> vertices, EdgeType edgetype) {
		if(edge == null || vertices == null || vertices.size() != 2) {
			return false;
		}
		
		V[] vs = (V[])vertices.toArray();
		return addEdge(edge, vs[0], vs[1], edgetype);
	}
	
	//********************************************************************************
	//   DO NOT EDIT ANYTHING BELOW THIS LINE
	//********************************************************************************
	
	/**
     * Returns a {@code Factory} that creates an instance of this graph type.
     * @param <V> the vertex type for the graph factory
     * @param <E> the edge type for the graph factory
     * @return a directed graph
     */
	public static <V,E> Factory<DirectedGraph<V,E>> getFactory() { 
		return new Factory<DirectedGraph<V,E>> () {
			@SuppressWarnings("unchecked")
			public DirectedGraph<V,E> create() {
				return (DirectedGraph<V,E>) new ThreeTenGraph();
			}
		};
	}
    
    /**
     * Returns the edge type of edge in this graph.
     * @param edge to get
     * @return the EdgeType of edge, or null if edge has no defined type
     */
    public EdgeType getEdgeType(Destination<V> edge) {
		return EdgeType.DIRECTED;
	}
    
    /**
     * Returns the default edge type for this graph.
     * 
     * @return the default edge type for this graph
     */
    public EdgeType getDefaultEdgeType() {
		return EdgeType.DIRECTED;
	}
    
    /**
     * Returns the number of vertices that are incident to edge.
     * For hyperedges, this can be any nonnegative integer; for edges this
     * must be 2 (or 1 if self-loops are permitted). 
     * 
     * <p>Equivalent to getIncidentVertices(edge).size().
     * @param edge the edge whose incident vertex count is to be returned
     * @return the number of vertices that are incident to edge.
     */
    public int getIncidentCount(Destination<V> edge) {
		return 2;
	}
}
