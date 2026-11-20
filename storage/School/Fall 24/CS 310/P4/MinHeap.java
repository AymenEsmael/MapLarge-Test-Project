//no additional imports! 
import java.util.Collection;
import java.util.Queue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
 
/**
* Priority queue represented as a balanced binary heap: the two
* children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
* priority queue is ordered by the elements' natural ordering.
@param <E> a generic class
*/
public class MinHeap<E extends Comparable<E>> implements Queue<E> {
	/**
	 * A default initial capacity for the queue.
	 */
	//size of a queue when empty, should never shrink below this size
	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	
	/**
	 * A queue that is generic that is comparable.
	 */
	//you may not have any other instance variables, only these TWO
	//if you make more instance variables your MinHeap class will
	//receive a 0, no matter how well it works
	private Comparable<E>[] queue;

	/**
	 * A var that keeps track of size of the queue.
	 */
	private int size = 0;

	/**
	 * A public constructor that takes no input.
	 */
	@SuppressWarnings("unchecked")
	public MinHeap() {
		//Initialize the queue here.
		
		//You cannot use the diamond syntax when creating an array, but you can cast to it. So you use this format:
		//ClassWithGeneric<T>[] items = (ClassWithGeneric<T>[]) new ClassWithGeneric[10];
		this.queue = (Comparable<E>[]) new Comparable[DEFAULT_INITIAL_CAPACITY];
	}
	/**
	 * A public constructor that takes an input.
	 * @param other is a type of this class
	 */
	@SuppressWarnings("unchecked")
	public MinHeap(MinHeap<E> other) {
		//Initialize the queue here to be a copy of the other queue
		// this.queue = Arrays.copyOf(other.queue,other.queue.length);//makes a copy of the other and saves it to the queue
		
		this.queue = (Comparable<E>[]) new Comparable[other.size];
		for(int i=0;i<other.size;i++){
			this.queue[i] = other.queue[i];
		}
		this.size = other.size;
	}

	/**
	 * A helper private method that gets the leftchild.
	 * @param index given the index finds its left child
	 * @return the left child
	 */
	private int leftChild(int index){
		return 2*index + 1;
	}
	/**
	 * A helper private method that gets the rightchild.
	 * @param index given the index finds the right child
	 * @return the right child
	 */
	private int rightChild(int index){
		return 2*index + 2;
	}
	/**
	 * A private helper method that gets the parent.
	 * @param index given the index it finds its parent
	 * @return the parent of the index
	 */
	private int parent(int index){
		return (index - 1)/2;
	}
	/**
	 * A private method that swapes two index.
	 * @param index1 to swap pos with index2
	 * @param index2 to swap pos with index1
	 */
	private void swap(int index1,int index2){
		Comparable<E> temp = queue[index1];
		queue[index1] = queue[index2];
		queue[index2] = temp;
	}
	
	/**
	 * A method that adds element to the queue.
	 * @param e a generic data type
	 * @return boolean if succisfuly added
	 */
	@Override
	public boolean add(E e){//thorw and instructions from orcule
		//adds elements to the queue
		//when space is needed I should increase the capacity of the queue
		// return false;
		//should return true if this collection changed due to the call.
		//if success add return true else false.

		// ClassCastException - if the class of the specified element prevents it from being added to this collection
		// NullPointerException - if the specified element is null and this collection does not permit null elements
		// IllegalArgumentException - if some property of the element prevents it from being added to this collection
		//same throw as offer
		
		// if(e == null) throw new NullPointerException();
		// if(!(e instanceof Comparable)) throw new IllegalArgumentException();
		return offer(e);

	}

	/**
	 * A private helper method to resize the queue when ever it is needed.
	 * @param newcap the new size
	 */
	@SuppressWarnings("unchecked")
	private void resize(int newcap){
		// this.queue = Arrays.copyOf(queue, newcap);
		Comparable<E>[] oldCap = this.queue;
		this.queue = (Comparable<E>[]) new Comparable[newcap];
		for(int i=0;i<oldCap.length;i++){
			this.queue[i] = oldCap[i];
		}
	}

	/**
	 * A method that also addes elements to the queue.
	 * @param e a generic data type
	 * @return true if succisfuly added
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean offer(E e){
		//no need to worry about classcastexceptino
		//adds elements to the queue
		// return false;

		if(e == null) throw new NullPointerException();
		//IllegalArgumentException - if some property of this element prevents it from being added to this queue
		
		//check if size needs to increase
		if(size() == queue.length){
			resize(queue.length*2);
		}
		//add elements to the queue
		queue[size()] = e;

		//fix inorder to maintain heap properity
		int current = size();
		while(current > 0 && ((E) queue[current]).compareTo((E)queue[parent(current)]) < 0 ){
			swap(current, parent(current));
			current = parent(current);
		}
		size++;
		return true;
	}

	/**
	 * A private helper method that fixes the properity of a min heap once added a new elem.
	 * @param index to fix
	 */
	@SuppressWarnings("unchecked")
	private void perlocateDown(int index){
		int minindex = index;
		while(true){
			int leftindex = leftChild(index);
			int rightindex = rightChild(index);
			if( leftindex < queue.length && queue[leftindex] != null && queue[minindex] != null && ((E) queue[leftindex]).compareTo ((E) queue[minindex]) < 0){
				minindex = leftindex;
			}
			if( rightindex < queue.length && queue[rightindex] != null && queue[minindex] != null && ((E) queue[rightindex]).compareTo ((E) queue[minindex]) < 0){
				minindex = rightindex;
			}
			if(minindex != index){
				swap(index,minindex);
				index = minindex;
			}
			else{
				return;
			}
		}
	}
	/**
	 * A method that removes elem form the queue.
	 * @return the value that was removed
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E remove(){
		//returns the head of the queue
		//throws NoSuchElementException if the queue is empty
		// return null;

		if(size() == 0) throw new NoSuchElementException();
		E minvalue = (E) queue[0];
		queue[0] = queue[size-1];

		size--;
		perlocateDown(0);

		
		//check if the queue size needs shirking
		if(size() < queue.length/3 && queue.length > DEFAULT_INITIAL_CAPACITY){
			resize(Math.max(DEFAULT_INITIAL_CAPACITY, queue.length/2));
		}

		queue[size] = null;
		return minvalue;

	}

	/**
	 * A method that gets the first element of the queue.
	 * @return the first element of the queue
	 */
	@Override
	public E poll(){
		//gets and removes the head of the queue
		//if empty return null
		// return null;

		if(isEmpty()) return null;
		return remove();

	}

	/**
	 * A method that also gets the first elem.
	 * @return the first elem in the queue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E element(){
		//gets the head of the queue without removing the head
		//throws NoSuchElementException if the queue is empty
		// return null;

		if(isEmpty()) throw new NoSuchElementException();
		return (E)queue[0];

	}

	/**
	 * A method that gets the first elem in the queue.
	 * @return the first elem
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E peek(){
		//gets and returns the head of the queue
		//return null if the queue is empty
		if(isEmpty()) return null;
		return (E) queue[0];
	}

	/**
	 * A method that gets the size of the queue.
	 * @return the size of the queue
	 */
	@Override
	public int size(){
		//returns the number of element in the collection
		if(size > Integer.MAX_VALUE) return Integer.MAX_VALUE;
		return size;
	}

	/**
	 * A method that checks if the queue is empty.
	 * @return true if it is empty
	 */
	@Override
	public boolean isEmpty(){
		//returns true if the collection has no elements
		return (size()==0);
	}

	@Override
	public Object[] toArray(){
		//return an array containing all of the elements in this collection
		// return new Object[0];

		//MAKE SURE IT IS DEEP COPY
		//use capacity instead of size because we want to use the empty spaces as well
		Object[] toreturn = new Object[queue.length];
		for(int i=0;i<size();i++)
			toreturn[i] = queue[i];
			
		return toreturn;
	}
 
	//Go over
	//size or length or is it just for the first one?
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a){
		//return an array containing all of the lements in this collection
		//if it doesnot fit make a new array
		//empty space should be null

		//Arraystorgaeexc
		// return  (T[]) new Comparable[0];
		if(a==null) throw new NullPointerException();
		//ArrayStoreException - if the runtime type of the specified array is not a supertype of the runtime type of every element in this collection
		if(a.length < size){
			// a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
			T[] newarr = (T[]) new Object[queue.length];
			for(int i = 0; i< size; i++) newarr[i] = (T) queue[i];
			return newarr;
		}
	
		
		for(int i=0;i<size();i++){
		
			try{
				a[i] = (T)queue[i];
			}catch(ArrayStoreException e){
				throw new ArrayStoreException();
			}
		}
		
		if(a.length > size){
			for(int i=size;i<a.length;i++){
				a[i] =null;
			}
		}
		
		return a;
	}

	//Check this method again
	@Override
	public boolean addAll(Collection<? extends E> c){
		//returns true if this collection changed as a result of the call
		// return false;
		//c is a collection and it should be added to this collection, i.e queue

		//if the collection is empty, return false no change
		//if empty thisi is fine


		//ClassCastException - if the class of an element of the specified collection prevents it from being added to this collection
		//NullPointerException - if the specified collection contains a null element and this collection does not permit null elements, or if the specified collection is null
		//IllegalArgumentException - if some property of an element of the specified collection prevents it from being added to this collection

		if(c==null) throw new NullPointerException();

		if(c.isEmpty()) return false;
		//if the collection is not empty add it
		if(size()!=0){
			for(E i: c){
				if(i==null) throw new NullPointerException();

				offer(i);
			}
		}
		else{
			int i=0;
			for(E in: c){
				if(in==null) throw new NullPointerException();
				queue[i++] = in;
			}

			size = c.size();
			for (int j = (size() / 2) - 1; j >= 0; j--) {
				perlocateDown(j);
			}
			
		}
		return true;

		//if the heap is not empty add it once then rearrange
	}

	/**
	 * A method that removes all of the elements form the queue.
	 */
	@Override
	public void clear(){
		//Removes all of the elements from this collection (optional operation).
		//The collection will be empty after this method returns.

		while(size()!=0){
			remove();
		}

		//set every item to null then set size to 0 (also shringk???)
	}
	//do we need to implement equals and hashcode?Ask piazza plsss!!!!!!!!!!!!!!
	// public boolean equals (Object o){
	// 	//no need for override
	// 	//returns true if the object is equal to this collection
	// 	return false;

	// }

	// public int hashCode(){
	// 	//no need for override
	// 	//return the hash code value for this collection
	// 	return 0;
	// }


	/**
	 * A contains method.
	 * @param o to look for
	 * @return true if it is in
	 */
	@Override
	public boolean contains(Object o){
		// throw new UnsupportedOperationException();
		for(int i = 0;i < size;i++){
			if(queue[i].equals(o)){
				return true;
			}
		}
		return false;
	}

	/**
	 * A contains method for all.
	 * @param c that is a collection
	 * @return true if it is in
	 */
	@Override
	public boolean containsAll(Collection<?> c){
		throw new UnsupportedOperationException();
	}

	/**
	 * A mehtod that removes a specific element.
	 * @param o object to remove
	 * @return true if removed
	 */
	@Override
	public boolean remove(Object o){

		if(o==null) throw new NullPointerException();
		if(!contains(o)) return false;

		for(int i=0;i<size();i++){
			if(queue[i].equals(o)){
				if(i==0){
					remove();
					return true;
				}
				queue[i] = queue[size-1];
				queue[size-1] = null;
				size--;

				if(size() < queue.length/3 && queue.length > DEFAULT_INITIAL_CAPACITY){
					resize(Math.max(DEFAULT_INITIAL_CAPACITY, queue.length/2));
				}
			}
		}
		return true;
		// throw new UnsupportedOperationException();
		// System.out.println("Here in remove(Object o)");
		// System.out.println("What am I remvoing::" + o);
		
		// int index = -1;
		// for(int i=0;i<queue.length;i++){
		// 	// System.out.println();
		// 	if(queue[i] != null && queue[i].equals(o)){
		// 		// index = i;
		// 		// System.out.println("Found a match");
		// 		queue[i] = queue[size()-1];
		// 		size--;
		// 		perlocateDown(0);
		// 		// System.out.println(size());
		// 		// System.out.println(queue[size()]);

		// 		queue[size] = null;
		// 		// System.out.println(size());
		// 		return true;
		// 	}
		// 	if (queue[i] instanceof Destination) {
		// 		Destination<?> queueElement = (Destination<?>) queue[i];
		// 		// Destination<?> targetElement = (Destination<?>) o;
		// 		// System.out.println(i+1 +": " +"queue[i] = " + queueElement.node + " " + queueElement.priority + ", o = " + o);
		// 		if(queueElement.node.equals(o)){
		// 			queue[i] = queue[size()-1];
		// 			size--;
		// 			perlocateDown(i);
		// 			queue[size()] = null;
		// 			return true;
		// 		}
				
		// 	}
		// }
		
		// return false;
	}

	/**
	 * A method that removeAll in the collection.
	 * @param c that is a collection
	 * @return true if removed
	 */
	@Override
	public boolean removeAll(Collection<?> c){
		throw new UnsupportedOperationException();
	}

	/**
	 * A retain all method.
	 * @param c that is a collection
	 * @return true
	 */
	@Override
	public boolean retainAll (Collection<?> c){
		throw new UnsupportedOperationException();
	}
	//You will need additional methods defined in the Queue interface!
	/**
	 * An inner class.
	 * @return the next element in the queue
	 */
	//This is an iterator for the queue, complete it!
	@SuppressWarnings("unchecked")
	public Iterator<E> iterator() {
		return new Iterator<>() {
			int index=0;
			public boolean hasNext() {
				//replace this!
				// return false;
				if(index<size()) return true;
				return false;
			}
			
			public E next() {
				//replace this!
				// return null;
				if(!hasNext()) throw new NoSuchElementException();
				return (E)queue[index++];
			}
		};
	}

	/**
	 * A private helper method that prints the heap.
	 */
	private void print(){
		// System.out.println("SIze is: " + size());
		// System.out.println("Length is: " + queue.length);
		// System.out.println("ql/3 is: " + queue.length/3);

		// System.out.println("Heap content: ");
		// System.out.println("Heap size: " + size());
		// System.out.println("Heap.length: " + queue.length);
		System.out.print('[');
		for(int i=0;i<queue.length;i++){
			if(queue[i]!=null){

				System.out.print(queue[i]);
				if(i < queue.length - 1 && queue[i + 1] != null)  System.out.print(',');
			}
			
		}
		System.out.print(']');
		System.out.println();
	}

	
	// public static void main(String[] args) {
	// 	MinHeap<Integer> minHeap = new MinHeap<>();
	// 	minHeap.add(99);
	// 	minHeap.add(72);
	// 	minHeap.add(61);
	// 	minHeap.add(86);
	// 	minHeap.remove();
	// 	minHeap.add(43);
	// 	minHeap.offer(22);
	// 	minHeap.offer(69);
	// 	minHeap.offer(32);
	// 	minHeap.offer(32);
	// 	minHeap.add(198);
	// 	System.out.println(minHeap.peek());
	// 	minHeap.poll();
	// 	minHeap.remove();
	// 	System.out.println(minHeap.element());

		
	// 	System.out.println(minHeap.size());
	// 	System.out.println(minHeap.queue.length);
	// 	// System.out.println(minHeap.queue[2] + " ");
	// 	// System.out.println(minHeap.queue[3]);

	// 	// for (int i = 0; i < minHeap.size; i++) {
	// 	// 	System.out.print(minHeap.queue[i] + " ");
	// 	// }
	// 	// Iterator<Integer> iterator = minHeap.iterator();
	// 	// while (iterator.hasNext())
	// 	// 	System.out.print(iterator.next()+ " ");
	// 	// System.out.println(minHeap.contains(54));
	// 	minHeap.remove(69);
	// 	for(Integer i: minHeap){
	// 		System.out.print(i + " ");
	// 	}
		

	// }
}