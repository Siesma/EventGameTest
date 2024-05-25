package board;

import org.joml.Vector2i;
import rendering.IsoWindow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public abstract class CellState<T> {

    protected Vector2i position;

    protected T state;

    public CellState(Vector2i position, T state) {
        this.position = position;
        this.state = state;
    }

    abstract boolean isChecked();

    abstract void setState(T newState);

    abstract T getState();


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
        if (isChecked()) {
            float[] rgb = {0, 0, 0};
            if (state instanceof Integer) {
                rgb[0] = map((Integer) state, 8, 0, 0, 1);
                rgb[1] = map((Integer) state, 8, 0, 0, 1);
                rgb[2] = map((Integer) state, 8, 0, 0, 1);
            } else {

            }
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

}
