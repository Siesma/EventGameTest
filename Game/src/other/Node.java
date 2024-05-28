package other;

public class Node<T> {
    private Node<T> prev, next;

    private T data;

    public Node(T data) {
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> setNext(Node<T> next) {
        this.next = next;
        return this;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public Node<T> setPrev(Node<T> prev) {
        this.prev = prev;
        return this;
    }

    public T getData() {
        return data;
    }

    public Node<T> setData(T data) {
        this.data = data;
        return this;
    }
}