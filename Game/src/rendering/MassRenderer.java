package rendering;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class MassRenderer {

    private ArrayList<Vector2f> vecs;

    private final int mode;

    public MassRenderer(int mode) {
        this.mode = mode;
    }

    public void render(Vector2f position) {
        GL11.glPushMatrix();
        GL11.glTranslatef(position.x, position.y, 0);
        GL11.glBegin(mode);
        for (Vector2f v : vecs) {
            GL11.glVertex2f(v.x, v.y);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void push(Vector2f vec) {
        this.vecs.add(vec);
    }

}
