package board.wfc;

import org.joml.Vector2f;
import other.math.MathHelper;
import rendering.IsoWindow;
import wfc.Cell;
import wfc.Grid;
import wfc.pattern.Tile;

import java.util.Set;
import java.util.function.Supplier;

import static rendering.IsoWindow.windowSize;

public class TileCell extends Cell {

    private Vector2f screenPosition;

    public TileCell(int[] position) {
        super(position);
        this.screenPosition = MathHelper.translateToIsometric(position);
    }

    public Vector2f getScreenPosition () {
        return this.screenPosition;
    }

}
