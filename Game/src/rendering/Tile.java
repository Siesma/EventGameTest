package rendering;

import event.EventSubscriber;
import event.events.MousePressedEvent;
import org.joml.Vector2i;

import java.text.NumberFormat;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class Tile {
    private Vector2i position;
    private int state;

    public Tile(int x, int y, int state) {
        this.position = new Vector2i(x, y);
        this.state = state;
    }

    public Vector2i getPosition() {
        return position;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void render(boolean highlight, Vector2i windowSize) {
        float screenX = (position.x - position.y) * (IsoWindow.tileSize.x() / 2.0f) + windowSize.x() / 2.0f;
        float screenY = (position.x + position.y) * (IsoWindow.tileSize.y() / 2.0f) + windowSize.y() / 2.0f;

        glPushMatrix();
        glTranslatef(screenX, screenY, 0);

        if (highlight) {
            glColor3f(1.0f, 0.0f, 0.0f);
        } else {
            glColor3f(1.0f, 1.0f, 1.0f);
        }
        if(state != 0) {
            float[] rgb = {0,0,0};
            rgb[0] = map(state, 8,0, 0, 1);
            rgb[1] = map(state, 8,0, 0, 1);
            rgb[2] = map(state, 8,0, 0, 1);
            glColor3f(rgb[0], rgb[1], rgb[2]);
        }

        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(IsoWindow.tileSize.x() / 2.0f, IsoWindow.tileSize.y() / 2.0f);
        glVertex2f(IsoWindow.tileSize.x(), 0);
        glVertex2f(IsoWindow.tileSize.x() / 2.0f, -IsoWindow.tileSize.y() / 2.0f);
        glEnd();

        glPopMatrix();
    }

    static public final float map(float value,
                                  float istart,
                                  float istop,
                                  float ostart,
                                  float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
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