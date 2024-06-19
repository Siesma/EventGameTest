package board.wfc;

import wfc.Grid;
import wfc.pattern.Tile;

import java.util.Set;
import java.util.function.Supplier;

public class TileGrid extends Grid<TileCell> {
    @Override
    public TileCell createCell(int[] position) {
        return new TileCell(position);
    }

    public TileGrid(Set<Tile> allPossibleTiles, Supplier<TileCell[][]> gridSupplier) {
        super(allPossibleTiles, gridSupplier);
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<  getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                builder.append(getTile(i,j).getState().getRepresentation());
                builder.append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
