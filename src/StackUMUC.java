

public class StackUMUC <T> {

	private T [] array;
	private int size = 0;
	
	public StackUMUC(int capacity) {
		array = (T[]) new Object[capacity];
	}
	
	public void push(T t) {
		// check: is full?
		array[size] = t;
		size++;
	}
	
	public T pop() {
		size--;
		return array[size];
	}
	
	public T peek() {
		return array[size - 1];
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean isFull() {
		return size == array.length;
	}
	
	public String toString() {
		// TODO: what's a good way to display this?
		return "";
	}
}
