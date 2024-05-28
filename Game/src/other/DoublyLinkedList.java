package other;

// This doublyLinkedList is simplified, it does not have a lot of functions that normal linked list need
// this implementation is only required to perform one thing fast and only has functions required for that purpose
public class DoublyLinkedList<T extends Number> {

    private Node<T> head;
    private Node<T> tail;

    private int maxNumElements;

    private int size;

    public DoublyLinkedList(int maxNumElements) {
        this.head = null;
        this.tail = null;
        size = 0;
        this.maxNumElements = maxNumElements;
    }

    public void append(T data) {
        Node<T> temp = new Node<T>(data);
        if (tail == null) {
            head = temp;
        } else {
            tail.setNext(temp);
            temp.setPrev(tail);
        }
        tail = temp;
        size++;
    }

    public void deleteHead() {
        if (head == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
            return;
        }
        Node<T> temp = head;
        head = head.getNext();
        head.setPrev(null);
        temp.setNext(null);
        size--;
    }

    public void traverse() {
        Node<T> cur = head;
        while (cur != null) {
            System.out.print(cur.getData() + ", ");
            cur = cur.getNext();
        }
        System.out.println();
    }

    public Node<T> get(int index) {
        if (index > getSize()) {
            return tail;
        }
        if (index < 0) {
            return head;
        }
        int i = 0;
        Node<T> out = head;
        while (true) {
            if (++i < index) {
                out = out.getNext();
                continue;
            }
            break;
        }
        return out;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public T getLargest () {
        T largest = head.getData();
        Node<T> cur = head;
        while(cur != null) {
            if(largest.doubleValue() < cur.getData().doubleValue()) {
                largest = cur.getData();
            }
            cur = cur.getNext();
        }
        return largest;
    }

    public double getAverage() {
        double sum = 0;
        Node<T> cur = head;
        while (cur != null) {
            sum += cur.getData().doubleValue();
            cur = cur.getNext();
        }
        return sum / getSize();
    }

    public int getSize() {
        return size;
    }

    public int getMaxNumElements() {
        return maxNumElements;
    }
}
