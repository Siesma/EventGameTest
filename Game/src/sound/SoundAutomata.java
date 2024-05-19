package sound;

import board.Board;
import board.BooleanState;
import board.Cell;
import other.Vector2D;

import java.util.ArrayList;

public class SoundAutomata {

    private final Board<BooleanState> board;

    private Board<BooleanState> prevBoard;

    private ArrayList<Vector2D> newBornInStep;

    private void loadInitialState(StartPositions startState, boolean center, int gridSize) {
        int w = board.getWidth();
        int h = board.getHeight();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                this.board.getBoard()[i][j] = new Cell<>(new BooleanState(false));
                this.prevBoard.getBoard()[i][j] = new Cell<>(new BooleanState(false));
            }
        }
        Vector2D offset;
        if (center) {
            offset = startState.calculateRequiredOffset(gridSize);
        } else {
            //offset = new Pair(0, 0);
            //offset = new Pair(4, 4);
            offset = startState.getOffsetToCenter();
        }

        for (Vector2D p : startState.getCoordinatePairs()) {

            int px = p.x() + offset.y();
            int py = p.y() + offset.x();
            newBornInStep.add(new Vector2D(px, py));
            //System.out.printf("(%s, %s)\n", px, py);
            this.board.setState(new BooleanState(true), px, py);
        }

        //printBoard();
        //System.exit(1);
    }

    public boolean isDead () {
        for(int i = 0; i < getBoard().getWidth(); i++) {
            for(int j = 0; j < getBoard().getHeight(); j++) {
                boolean now = board.getState(i,j).isChecked();
                boolean prev = prevBoard.getState(i,j).isChecked();
                if(now != prev) {
                    return false;
                }
            }
        }
        return true;
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

    public SoundAutomata(int w, int h) {
        this.board = new Board<>(w, h);
        this.prevBoard = new Board<>(w, h);
        this.newBornInStep = new ArrayList<>();
        loadInitialState(Settings.startPosition, false, Math.max(w, h));
    }

    public void playAutomate (SoundGenerator gen) throws InterruptedException {
        while(!this.isDead()) {
            gen.playBoard(this);
            Thread.sleep(Settings.soundNoteDuration);
            this.step();
        }
    }

    private Board<BooleanState> cloneBoard() {
        Board<BooleanState> clone = new Board<>(this.board.getWidth(), this.board.getHeight());
        for (int i = 0; i < this.board.getWidth(); i++) {
            for (int j = 0; j < this.board.getHeight(); j++) {
                clone.getBoard()[i][j] = new Cell<>(new BooleanState(false));
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
        if (currentState.getState()) {
            return new BooleanState(aliveNeighbors == 2 || aliveNeighbors == 3);
        } else {
            return new BooleanState(aliveNeighbors == 3);
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
