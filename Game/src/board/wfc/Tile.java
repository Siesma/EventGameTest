package board.wfc;

import rendering.TextureLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum Tile {
    WATER(TextureLoader.nameToTextureIDMap.get("Water".toLowerCase(Locale.ROOT)), "Water", new ArrayList<>()),
    VOID(TextureLoader.nameToTextureIDMap.get("Void".toLowerCase(Locale.ROOT)), "Void", new ArrayList<>()),
    GROUND(TextureLoader.nameToTextureIDMap.get("Ground".toLowerCase(Locale.ROOT)), "Ground", new ArrayList<>()),
    GRASS(TextureLoader.nameToTextureIDMap.get("Grass".toLowerCase(Locale.ROOT)), "Grass", new ArrayList<>()),
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
        VOID.allowedNeighbors = List.of();
        WATER.allowedNeighbors = Arrays.asList(WATER, GROUND);
        GRASS.allowedNeighbors = Arrays.asList(GRASS, GROUND, FOREST);
        GROUND.allowedNeighbors = Arrays.asList(WATER, GROUND, GRASS);
        FOREST.allowedNeighbors = Arrays.asList(GRASS, FOREST);
    }

    public static Tile getFromInt (int state) {
        for(Tile t : Tile.values()) {
            if(t.textureID == state) {
                return t;
            }
        }
        return WATER;
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

