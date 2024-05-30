package board;

import other.math.Vector2D;
import sound.Settings;

import java.util.ArrayList;
import java.util.List;

public class Board<T extends CellState<?>> {
    private final Cell<T>[][] board;

    /**
     * prevBoard is not really used anymore. Its exclusively used for the sound generation method. It should definetly be refactored (the entire Synthesizer)
     */
    private Cell<T>[][] prevBoard;
    private final int width, height;

    public Board (int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new Cell[width][height];
        this.prevBoard = new Cell[width][height];
    }

    public Cell<T>[][] getBoard() {
        return board;
    }

    public void setState (T state, int x, int y) {
        int safeX = (x + Settings.gridSize) % Settings.gridSize;
        int safeY = (y + Settings.gridSize) % Settings.gridSize;
        this.board[safeX][safeY].setState(state);
    }

    /**
     * Safe access to a cell, handles out of bounds calls
     * @return
     */
    public T getState (int x, int y) {
        int safeX = (x + width) % width;
        int safeY = (y + height) % height;
        return board[safeX][safeY].state;
    }

    public ArrayList<Vector2D> countCheckedStates() {
        ArrayList<Vector2D> vector2DS = new ArrayList<>();
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(getState(i, j).isChecked()) {
                    vector2DS.add(new Vector2D(i, j));
                }
            }
        }
        return vector2DS;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell<T>[][] getPrevBoard() {
        return prevBoard;
    }

    public void setPrevBoard (Cell<T>[][] newBoard) {
        this.prevBoard = newBoard;
    }
}
