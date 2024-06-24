package board.wfc;

import org.joml.Vector2f;
import org.joml.Vector4f;
import other.math.MathHelper;
import rendering.IsoWindow;
import rendering.RenderHelper;
import rendering.TextureLoader;
import wfc.Cell;

import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;

public class TileCell extends Cell {

    private Vector2f screenPosition;

    public TileCell(int[] position) {
        super(position);
        this.screenPosition = MathHelper.translateCoordinateToScreen(position);
    }

    public Vector2f getScreenPosition () {
        return this.screenPosition;
    }


    public void renderCell(boolean highlight) {

        int textureID = TextureLoader.nameToTextureIDMap.getOrDefault(this.getState().getDisplayName().toLowerCase(Locale.ROOT), -1);
        if(textureID != -1) {
            glPushMatrix();
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, textureID);

            glTranslatef(this.getScreenPosition().x(), this.getScreenPosition().y(), 0);

            glColor3f(1.0f, 1.0f, 1.0f); // Ensure color is white to display the texture correctly
            glBegin(GL_QUADS);

            glTexCoord2f(0, 0);
            glVertex2f(0, 0);

            glTexCoord2f(1, 0);
            glVertex2f(IsoWindow.tileSize.x()/2.0f, IsoWindow.tileSize.y() / 2.0f);

            glTexCoord2f(1, 1);
            glVertex2f(IsoWindow.tileSize.x(), 0);

            glTexCoord2f(0, 1);
            glVertex2f(IsoWindow.tileSize.x()/2.0f, -IsoWindow.tileSize.y()/2.0f);

            glEnd();

            glDisable(GL_TEXTURE_2D);
            glPopMatrix();
            return;
        }

        Vector4f colour = null;
        if (highlight)
            colour = new Vector4f(0, 1, 0, 1);
        else
            colour = new Vector4f(1, 0, 0, 1);


        RenderHelper.rect(
            this.getScreenPosition(),
            new Vector2f(IsoWindow.tileSize.x(), 0),
            new Vector2f(IsoWindow.tileSize.x() / 2.0f, -IsoWindow.tileSize.y() / 2.0f),
            new Vector2f(IsoWindow.tileSize.x()/2.0f, -IsoWindow.tileSize.y()/2.0f),
            colour);

    }

}
