package util;

public class DoubleLinkedList<E> {
	private DoubleLinkedListNode<E> pointer, marker;
	
	public DoubleLinkedList() {
		pointer = new DoubleLinkedListNode<E>(null);
		marker = pointer;
	}

	public E toNext() {
		if(hasNext()) {
			E next = pointer.toNext;
			pointer = pointer.next;
			return next;
		} else return null;
	}
	
	public E toPrevious() {
		if(hasPrevious()) {
			E prev = pointer.toPrev;
			pointer = pointer.prev;
			return prev;
		} else return null;
	}

	public boolean hasNext() {
		return pointer.next != null;
	}

	public boolean hasPrevious() {
		return pointer.prev != null;
	}
	
	public boolean isMarked() {
		return marker == pointer;
	}
	
	public void mark() {
		marker = pointer;
	}
	
	public void removeMark() {
		marker = null;
	}

	public void append(E toNextElement, E toPrevElement) {
		DoubleLinkedListNode<E> node = new DoubleLinkedListNode<E>(pointer);
		node.toPrev = toPrevElement;
		pointer.next = node;
		pointer.toNext = toNextElement;
		pointer = node;	
	}
	
	private class DoubleLinkedListNode<T> {
		private DoubleLinkedListNode<T> next = null, prev;
		private T toNext;
		private T toPrev;
		
		public DoubleLinkedListNode(DoubleLinkedListNode<T> prev) {
			this.prev = prev;
		}
		
	}

}
