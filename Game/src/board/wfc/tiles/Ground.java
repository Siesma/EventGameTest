package board.wfc.tiles;

import wfc.Vector2i;
import wfc.pattern.Tile;
import wfc.pattern.Tiles;

import java.util.Set;

public class Ground  extends Tile {
    @Override
    public Set<Tile> getPotentialAdjacency(Vector2i neighbouring) {
        return adjacencies.get(neighbouring);
    }

    @Override
    public Set<Tile> getPotentialAdjacency() {
        return adjacencies.get(defaultNeighbouringVector);
    }

    @Override
    public void initAdjacency() {
        adjacencies.get(defaultNeighbouringVector).add(Tiles.getTile("Grass"));
        adjacencies.get(defaultNeighbouringVector).add(Tiles.getTile("Ground"));
        adjacencies.get(defaultNeighbouringVector).add(Tiles.getTile("Water"));

        for (Vector2i vec : Tiles.getNeighbouringCandidates().values()) {
            adjacencies.get(vec).add(Tiles.getTile("Grass"));
            adjacencies.get(vec).add(Tiles.getTile("Ground"));
            adjacencies.get(vec).add(Tiles.getTile("Water"));
        }
    }
}
