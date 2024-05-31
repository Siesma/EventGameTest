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
            //System.out.println("Propagated: " + nextCell.toString(NumberFormat.getCompactNumberInstance()) + " to state: " + board.getState(nextCell.x(), nextCell.y()).getState());
        }
    }

    public List<Integer> getPossibleStates(Board<TileState> board, Vector2i position) {
        Set<Integer> possibleStates = new HashSet<>(Arrays.asList(
                Tile.WATER.getTextureID(),
                Tile.GROUND.getTextureID(),
                Tile.GRASS.getTextureID(),
                Tile.FOREST.getTextureID(),
                Tile.VOID.getTextureID()
        ));

        List<int[]> neighbors = getNeighbors(position.x(), position.y(), board.getWidth(), board.getHeight());
        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];
            int neighborState = board.getState(nx, ny).getState();
            if (neighborState != 0) {
                Tile neighborTile = getTileFromState(neighborState);
                possibleStates.retainAll(getAllowedStates(neighborTile));
            }
        }
        return new ArrayList<>(possibleStates);
    }

    public int getCollapsedState(Board<TileState> board, Vector2i position) {
        List<Integer> possibleStates = getPossibleStates(board, position);
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
        return getPossibleStates(board, new Vector2i(x, y)).size();
    }

    private void propagateConstraints(Board<TileState> board, int x, int y, int[][] entropyMap) {
        Queue<Vector2i> queue = new LinkedList<>();
        queue.add(new Vector2i(x, y));

        while (!queue.isEmpty()) {
            Vector2i cell = queue.poll();
            int cx = cell.x();
            int cy = cell.y();

            List<int[]> neighbors = getNeighbors(cx, cy, board.getWidth(), board.getHeight());
            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];
                if (entropyMap[nx][ny] != -1) {
                    List<Integer> possibleStates = getPossibleStates(board, new Vector2i(nx, ny));
                    int previousStateCount = possibleStates.size();

                    if (possibleStates.size() == 1) {
                        board.getState(nx, ny).setState(possibleStates.get(0));
                        entropyMap[nx][ny] = -1;
                        queue.add(new Vector2i(nx, ny));
                    } else {
                        entropyMap[nx][ny] = possibleStates.size();
                    }

                    if (possibleStates.size() != previousStateCount) {
                        queue.add(new Vector2i(nx, ny));
                    }
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
        return Tile.VOID;
    }

    private List<Integer> getAllowedStates(Tile tile) {
        List<Integer> allowedStates = new ArrayList<>();
        for (Tile t : tile.getAllowedNeighbors()) {
            allowedStates.add(t.getTextureID());
        }
        return allowedStates;
    }
}
