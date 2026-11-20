/**
 * A Destination class.
 * @param <V> a generic class
 */
//a (node,priority) to store in the adjacency-heap
//note: source is indicated by the key in the adj-heap
class Destination<V> implements Comparable<Destination<V>> {
	//these are directly accessable because they are package-visible
	//don't remove/rename these!
	/**
	 * A node.
	 */
	V node;
	/**
	 * A priority-> wiehgt of the edge.
	 */
	Integer priority;
	/**
	 * A constructor.
	 * @param n node
	 * @param e edge
	 */
	Destination(V n, Integer e) { this.node = n; this.priority = e; }
	
	/**
	 * A compare to method.
	 * @param other node to compare
	 */
	@Override
	public int compareTo(Destination<V> other) {
		return this.priority.compareTo(other.priority);
	}
	
	//You may add _additional_ constructors to this class and private helpers
	//if you want, but you may not remove/change anything that is already here
	//You may override additional methods defined in Object if that's useful to you (such as toString())
	//do NOT override .equals() -- the default implementation is important for this project

	//my edit to this class
 // 	@Override
 // 	public String toString() {
 //  return "{node=" + node + ", priority=" + priority + "}";
 // 	}

}