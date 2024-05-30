package board.wfc;

import java.util.List;

public class AdjacencyRule {
    private final Tile tile;
    private final List<Tile> allowedNeighbors;

    public AdjacencyRule(Tile tile, List<Tile> allowedNeighbors) {
        this.tile = tile;
        this.allowedNeighbors = allowedNeighbors;
    }

    public Tile getTile() {
        return tile;
    }

    public List<Tile> getAllowedNeighbors() {
        return allowedNeighbors;
    }
}
