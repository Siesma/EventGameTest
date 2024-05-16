package board;

public interface CellState<T> {

    boolean isChecked();

    void setState(T newState);

    T getState ();

}
