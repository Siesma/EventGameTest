package sound;

import board.Board;
import board.BooleanState;
import board.Cell;
import org.joml.Vector2i;
import other.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SoundAutomata {

    private Board<BooleanState> board;

    private Board<BooleanState> prevBoard;

    private ArrayList<Vector2D> newBornInStep;

    private HashSet<String> previousStates;

    public void loadInitialState(StartPositions startState, boolean procedural, int gridSize) {

        this.board = new Board<>(gridSize, gridSize);
        this.prevBoard = new Board<>(gridSize, gridSize);
        this.newBornInStep = new ArrayList<>();

        int w = board.getWidth();
        int h = board.getHeight();

        this.previousStates = new HashSet<>();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if(procedural) {
                    this.board.getBoard()[i][j] = new Cell<>(new BooleanState(new Vector2i(i, j), Math.random() > 0.5));
                } else {
                    this.board.getBoard()[i][j] = new Cell<>(new BooleanState(new Vector2i(i, j), false));
                }
                this.prevBoard.getBoard()[i][j] = new Cell<>(new BooleanState(new Vector2i(i, j), false));
            }
        }
        Vector2D offset = startState.getOffsetToCenter();

        if (!procedural) {
            for (Vector2D p : startState.getCoordinatePairs()) {

                int px = p.x() + offset.y();
                int py = p.y() + offset.x();
                newBornInStep.add(new Vector2D(px, py));
                this.board.setState(new BooleanState(new Vector2i(px, py), true), px, py);
            }
        }

    }

    public boolean isDead() {
        String encoded = encodeToString();
        if (this.previousStates.contains(encoded)) {
            System.out.println("Encountered cycle, considered dead!");
            return true;
        }
        this.previousStates.add(encoded);
        /*
        TODO: Figure out if there is a case in which this is necessary. Otherwise skip computation
        for (int i = 0; i < getBoard().getWidth(); i++) {
            for (int j = 0; j < getBoard().getHeight(); j++) {
                boolean now = board.getState(i, j).isChecked();
                boolean prev = prevBoard.getState(i, j).isChecked();
                if (now != prev) {
                    return false;
                }
            }
        }
        System.out.println("Nothing has changed, considered dead!");
        return true;
        */

        return false;
    }

    private String encodeToString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                out.append(board.getState(i, j).isChecked() ? 1 : 0);
            }
        }
        return out.toString();
    }

    public void printBoard() {
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                char ch = ' ';
                if (board.getState(i, j).isChecked()) {
                    ch = '#';
                } else {
                    ch = '-';
                }
                System.out.print(ch);
            }
            System.out.println();
        }
    }

    public boolean[][] encodeToBinary() {
        boolean[][] out = new boolean[Settings.gridSize][Settings.gridSize];

        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                out[i][j] = board.getState(i, j).isChecked();
            }
        }
        return out;
    }

    public SoundAutomata(int w, int h) {
        loadInitialState(Settings.startPosition, false, Math.max(w, h));
    }

    public void playAutomate(SoundGenerator gen) throws InterruptedException {
        while (!this.isDead()) {
            gen.playBoard(this);
            Thread.sleep(Settings.soundNoteDuration);
            this.step();
        }
    }

    private Board<BooleanState> cloneBoard() {
        Board<BooleanState> clone = new Board<>(this.board.getWidth(), this.board.getHeight());
        for (int i = 0; i < this.board.getWidth(); i++) {
            for (int j = 0; j < this.board.getHeight(); j++) {
                clone.getBoard()[i][j] = new Cell<>(new BooleanState(new Vector2i(i, j), false));
                clone.setState(this.board.getState(i, j), i, j);
            }
        }
        return clone;
    }

    public void step() {
        this.prevBoard = cloneBoard();
        try {
            Board<BooleanState> prevState = cloneBoard();
            for (int i = 0; i < board.getWidth(); i++) {
                for (int j = 0; j < board.getHeight(); j++) {
                    this.board.setState(nextState(prevState, i, j), i, j);
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        updateNewborn(prevBoard, board);
    }

    public ArrayList<Vector2D> getNewBornInStep() {
        return newBornInStep;
    }

    private void updateNewborn(Board<BooleanState> prev, Board<BooleanState> now) {
        this.newBornInStep.clear();
        for (int i = 0; i < prev.getWidth(); i++) {
            for (int j = 0; j < prev.getHeight(); j++) {
                boolean before = prev.getState(i, j).getState().booleanValue();
                boolean after = now.getState(i, j).getState().booleanValue();
                if (!before && after) {
                    newBornInStep.add(new Vector2D(i, j));
                }
            }
        }
    }

    public Board<BooleanState> getBoard() {
        return board;
    }

    public Board<BooleanState> getPrevBoard() {
        return prevBoard;
    }

    private BooleanState nextState(Board<BooleanState> prevState, int x, int y) {
        int aliveNeighbors = countAliveNeighbors(prevState, x, y);
        BooleanState currentState = prevState.getState(x, y);
        Vector2i position = new Vector2i(x, y);
        if (currentState.getState()) {
            return new BooleanState(position, aliveNeighbors == 2 || aliveNeighbors == 3);
        } else {
            return new BooleanState(position, aliveNeighbors == 3);
        }
    }

    private int countAliveNeighbors(Board<BooleanState> prevState, int x, int y) {
        int aliveNeighbors = 0;
        int width = prevState.getWidth();
        int height = prevState.getHeight();
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int newX = (x + dx[i] + width) % width;
            int newY = (y + dy[i] + height) % height;
            if (prevState.getState(newX, newY).getState()) {
                aliveNeighbors++;
            }
        }
        return aliveNeighbors;
    }

}
