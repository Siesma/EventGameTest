package board.wfc;

import board.Board;
import board.TileState;
import org.joml.Vector2i;

import java.text.NumberFormat;
import java.util.*;

public class WaveFunctionCollapse {

    public void collapseBoard(Board<TileState> board) {
        int[][] entropyOfBoard = new int[board.getWidth()][board.getHeight()];

        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                entropyOfBoard[i][j] = computeEntropy(board, i, j);
            }
        }

        while (!isBoardFullyCollapsed(board)) {
            Vector2i nextCell = findRandomLowestEntropyCell(board, entropyOfBoard);
            collapseCell(board, nextCell, entropyOfBoard);
            propagateConstraints(board, nextCell.x(), nextCell.y(), entropyOfBoard);
            System.out.println("Propagated: " + nextCell.toString(NumberFormat.getCompactNumberInstance()) + " to state: " + board.getState(nextCell.x(), nextCell.y()).getState());
        }
    }

    public int getCollapsedState(Board<TileState> board, Vector2i position) {
        List<Integer> possibleStates = Arrays.asList(1, 2, 3);
        return possibleStates.get(new Random().nextInt(possibleStates.size()));
    }

    public boolean isBoardFullyCollapsed(Board<TileState> board) {
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.getState(i, j).getState() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void collapseCell(Board<TileState> board, Vector2i position, int[][] entropyMap) {
        int collapsedState = getCollapsedState(board, position);
        board.getState(position.x(), position.y()).setState(collapsedState);
        entropyMap[position.x()][position.y()] = -1;
    }

    public Vector2i findRandomLowestEntropyCell(Board<TileState> board, int[][] entropyMap) {
        int minEntropy = Integer.MAX_VALUE;
        List<Vector2i> minEntropyCells = new ArrayList<>();

        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (entropyMap[i][j] != -1 && entropyMap[i][j] < minEntropy) {
                    minEntropy = entropyMap[i][j];
                    minEntropyCells.clear();
                    minEntropyCells.add(new Vector2i(i, j));
                } else if (entropyMap[i][j] == minEntropy) {
                    minEntropyCells.add(new Vector2i(i, j));
                }
            }
        }

        if (minEntropyCells.isEmpty()) {
            throw new IllegalStateException("No cell with positive entropy found");
        }

        return minEntropyCells.get(new Random().nextInt(minEntropyCells.size()));
    }

    public int computeEntropy(Board<TileState> board, int x, int y) {
        if (board.getState(x, y).getState() != 0) {
            return -1;
        }
        return 3; // Assume all tiles (WATER, GROUND, FOREST) are possible initially
    }

    private void propagateConstraints(Board<TileState> board, int x, int y, int[][] entropyMap) {
        List<int[]> neighbors = getNeighbors(x, y, board.getWidth(), board.getHeight());
        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];
            if (entropyMap[nx][ny] != -1) {
                List<Integer> possibleStates = new ArrayList<>(Arrays.asList(1, 2, 3));
                List<Integer> newPossibleStates = new ArrayList<>();

                for (int state : possibleStates) {
                    Tile currentTile = getTileFromState(board.getState(x, y).getState());
                    Tile neighborTile = getTileFromState(state);
                    if (currentTile.getAllowedNeighbors().contains(neighborTile)) {
                        newPossibleStates.add(state);
                    }
                }

                if (newPossibleStates.size() == 1) {
                    board.getState(nx, ny).setState(newPossibleStates.get(0));
                    entropyMap[nx][ny] = -1;
                    propagateConstraints(board, nx, ny, entropyMap);
                } else {
                    entropyMap[nx][ny] = newPossibleStates.size();
                }
            }
        }
    }

    private List<int[]> getNeighbors(int x, int y, int width, int height) {
        List<int[]> neighbors = new ArrayList<>();
        if (x > 0) neighbors.add(new int[]{x - 1, y});
        if (x < width - 1) neighbors.add(new int[]{x + 1, y});
        if (y > 0) neighbors.add(new int[]{x, y - 1});
        if (y < height - 1) neighbors.add(new int[]{x, y + 1});
        return neighbors;
    }

    private Tile getTileFromState(int state) {
        for (Tile tile : Tile.values()) {
            if (tile.getTextureID() == state) {
                return tile;
            }
        }
        return null;
    }
}
