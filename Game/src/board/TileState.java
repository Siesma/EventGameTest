package board;

import event.EventSubscriber;
import event.events.MousePressedEvent;
import org.joml.Vector2f;
import org.joml.Vector2i;
import rendering.IsoWindow;

import java.text.NumberFormat;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class TileState extends CellState<Integer> {

    public TileState(Vector2i position, Integer state, Vector2i windowSize) {
        super(position, state, windowSize);
    }

    @Override
    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public Integer getState() {
        return this.state;
    }

    @Override
    public boolean isChecked() {
        return this.state != 0;
    }

    @EventSubscriber
    public void onMousePressedEvent(MousePressedEvent event) {
        HashMap<String, Object> content = event.getContent();
        if (!content.containsKey("TileUnderCursor")) {
            return;
        }
        Vector2i tileUnder = (Vector2i) content.get("TileUnderCursor");
        if (tileUnder.equals(position.x, position.y)) {
            System.out.println("Tile is under at: " + tileUnder.toString(NumberFormat.getCompactNumberInstance()));
            this.state++;
            this.state = this.state % 8;
        }

    }

}