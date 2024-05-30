package board.wfc;

import rendering.TextureLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum Tile {
    WATER(TextureLoader.nameToTextureIDMap.get("Water".toLowerCase(Locale.ROOT)), "Water", new ArrayList<>()),
    GROUND(TextureLoader.nameToTextureIDMap.get("Ground".toLowerCase(Locale.ROOT)), "Ground", new ArrayList<>()),
    FOREST(TextureLoader.nameToTextureIDMap.get("Forest".toLowerCase(Locale.ROOT)), "Forest", new ArrayList<>());

    private final String name;
    private List<Tile> allowedNeighbors;

    private int textureID;

    Tile(int textureID, String name, List<Tile> allowedNeighbors) {
        this.textureID = textureID;
        this.name = name;
        this.allowedNeighbors = allowedNeighbors;
    }

    static {
        WATER.allowedNeighbors = Arrays.asList(WATER, GROUND);
        GROUND.allowedNeighbors = Arrays.asList(WATER, GROUND, FOREST);
        FOREST.allowedNeighbors = Arrays.asList(GROUND, FOREST);
    }

    public int getTextureID() {
        return textureID;
    }

    public List<Tile> getAllowedNeighbors() {
        return allowedNeighbors;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

