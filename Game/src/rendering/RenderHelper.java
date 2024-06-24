package rendering;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class RenderHelper {


    // AA -> AxisAligned
    public static void rectAA(Vector2f position, Vector2f ab, Vector4f colour) {
        glPushMatrix();
        setColour(colour);
        glTranslatef(position.x(), position.y(), 0);
        glBegin(GL_QUADS);

        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(ab.x(), 0);
        GL11.glVertex2f(ab.x(), ab.y());
        GL11.glVertex2f(0, ab.y());
        glEnd();
        glPopMatrix();
    }

    public static void rect (Vector2f a, Vector2f b, Vector2f c, Vector2f d, Vector4f colour) {
        glPushMatrix();
        setColour(colour);
        glTranslatef(a.x(), a.y(), 0);
        glBegin(GL_QUADS);

        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(b.x(), b.y());
        GL11.glVertex2f(c.x(), c.y());
        GL11.glVertex2f(d.x(), d.y());
        glEnd();
        glPopMatrix();
    }


    public static void setColour (Vector4f colour) {
        glColor4f(colour.x, colour.y, colour.z, colour.w);
    }

}
