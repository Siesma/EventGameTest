package board.wfc;

import board.Board;
import board.TileState;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import other.math.Vector2I;
import rendering.IsoWindow;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public class WaveFunctionCollapse {

    private final Board<TileState> board;
    private static final ArrayList<Tile> allTiles = new ArrayList<>();
    public static HashMap<String, Integer> stateDictionary = new HashMap<>();

    public WaveFunctionCollapse(Board<TileState> board) {
        this.board = board;
    }

    static {
        String path = "Game/ressources/wfc/tiles/";
        File folder = new File(path);
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName();
                String tileName = fileName.substring(0, fileName.indexOf('.'));
                int textureID = loadTexture(file.getAbsolutePath());
                allTiles.add(new Tile(textureID, tileName.hashCode()));

                stateDictionary.put(tileName, textureID);
            }
        }
    }


    private static int loadTexture(String path) {
        // Load image data
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 0);
        if (image == null) {
            System.err.println("Failed to load texture file " + path);
            return -1;
        }

        System.out.println("Loaded texture: " + path + " (width: " + width.get(0) + ", height: " + height.get(0) + ", channels: " + channels.get(0) + ")");

        // Generate texture ID
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Determine the image format
        int format;
        if (channels.get(0) == 3) {
            format = GL_RGB;
        } else if (channels.get(0) == 4) {
            format = GL_RGBA;
        } else {
            System.err.println("Unsupported number of channels: " + channels.get(0));
            STBImage.stbi_image_free(image);
            return -1;
        }

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL_UNSIGNED_BYTE, image);

        // Generate mipmaps
        glGenerateMipmap(GL_TEXTURE_2D);

        // Free the image memory
        STBImage.stbi_image_free(image);

        return textureID;
    }


    public void collapse() {
        HashMap<Integer, ArrayList<Vector2I>> entropyList = new HashMap<>();

        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                TileState state = board.getState(i, j);
                int cellEntropy = Tile.computeEntropy(state, this.allTiles);
                entropyList.computeIfAbsent(cellEntropy, k -> new ArrayList<>());
                entropyList.get(cellEntropy).add(new Vector2I(i, j));
            }
        }

        while (!entropyList.isEmpty()) {
            int minEntropy = Integer.MAX_VALUE;
            for (int entropy : entropyList.keySet()) {
                if (entropy < minEntropy) {
                    minEntropy = entropy;
                }
            }

            ArrayList<Vector2I> cellsWithMinEntropy = entropyList.get(minEntropy);
            if (cellsWithMinEntropy.isEmpty()) {
                entropyList.remove(minEntropy);
                continue;
            }

            Vector2I cellPos = cellsWithMinEntropy.remove(new Random().nextInt(cellsWithMinEntropy.size()));
            if (cellsWithMinEntropy.isEmpty()) {
                entropyList.remove(minEntropy);
            }

            // Collapse the selected cell
            selectNewStateForCell(cellPos);

            // Update entropy of affected cells
            updateEntropy(entropyList, cellPos);
        }
    }

    private void selectNewStateForCell(Vector2I cellPos) {
        int x = cellPos.getX();
        int y = cellPos.getY();
        TileState currentState = board.getState(x, y);

        // Find possible new states based on adjacency rules
        ArrayList<Tile> possibleTiles = new ArrayList<>();
        for (Tile tile : allTiles) {
            boolean fits = true;
            for (AdjacencyRules rule : AdjacencyRules.values()) {
                TileState neighborState = getNeighborState(x, y, rule);
                if (neighborState != null && !tile.doesTileFit(new Tile(stateDictionary.get(neighborState.getState()), neighborState.getState()), rule)) {
                    fits = false;
                    break;
                }
            }
            if (fits) {
                possibleTiles.add(tile);
            }
        }

        // Select a random possible state and update the board
        if (!possibleTiles.isEmpty()) {
            Tile selectedTile = possibleTiles.get(new Random().nextInt(possibleTiles.size()));
            board.setState(new TileState(new Vector2i(x, y), selectedTile.state, IsoWindow.windowSize), x, y);
        }
    }

    private TileState getNeighborState(int x, int y, AdjacencyRules dir) {
        int newX = x, newY = y;
        switch (dir) {
            case TOP -> newY--;
            case RIGHT -> newX++;
            case BOTTOM -> newY++;
            case LEFT -> newX--;
        }
        if (newX >= 0 && newX < board.getWidth() && newY >= 0 && newY < board.getHeight()) {
            return board.getState(newX, newY);
        }
        return null;
    }

    private void updateEntropy(HashMap<Integer, ArrayList<Vector2I>> entropyList, Vector2I cellPos) {
        int x = cellPos.getX();
        int y = cellPos.getY();

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < board.getWidth() && newY >= 0 && newY < board.getHeight()) {
                TileState adjacentState = board.getState(newX, newY);
                int newEntropy = Tile.computeEntropy(adjacentState, this.allTiles);
                Vector2I adjacentPos = new Vector2I(newX, newY);
                entropyList.computeIfAbsent(newEntropy, k -> new ArrayList<>()).add(adjacentPos);
            }
        }
    }

    public enum AdjacencyRules {
        TOP, RIGHT, BOTTOM, LEFT
    }

    record Tile(int textureID, int state) {
        private boolean doesTileFit(Tile other, AdjacencyRules dir) {
            return true;
        }

        public static int computeEntropy(TileState state, ArrayList<Tile> allTiles) {
            if (state == null || state.getState() == null) {
                return allTiles.size();
            }
            Tile myTile = new Tile(stateDictionary.get(state.getState()), state.getState());
            int entropy = 0;
            for (AdjacencyRules rule : AdjacencyRules.values()) {
                for (Tile t : allTiles) {
                    if (myTile.doesTileFit(t, rule)) {
                        entropy++;
                    }
                }
            }
            return entropy;
        }
    }
}
