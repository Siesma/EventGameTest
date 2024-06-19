package rendering;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class RenderHelper {


    public static void rect(Vector2f position, Vector2f ab, Vector4f colour) {
        glPushMatrix();
        glColor4f(colour.x, colour.y, colour.z, colour.w);
        glTranslatef(position.x(), position.y(), 0);
        glBegin(GL_QUADS);

        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(ab.x(), 0);
        GL11.glVertex2f(ab.x(), ab.y());
        GL11.glVertex2f(0, ab.y());
        glEnd();
        glPopMatrix();
    }

}
