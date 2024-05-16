package board;

public class Cell<T> {


    T state;

    public Cell (T state) {
        this.state = state;
    }


    public T getState() {
        return state;
    }

    public void setState(T state) {
        this.state = state;
    }
}
