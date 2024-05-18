package board;

import other.Pair;

import java.util.ArrayList;

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
        this.board[x][y].setState(state);
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

    public ArrayList<Pair> getAliveStates () {
        ArrayList<Pair> pairs = new ArrayList<>();
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(getState(i, j).isChecked()) {
                    pairs.add(new Pair(i, j));
                }
            }
        }
        return pairs;
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
