package board;

import board.wfc.Tile;
import board.wfc.WaveFunctionCollapse;
import event.EventSubscriber;
import event.events.MousePressedEvent;
import org.joml.Quaterniond;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import rendering.IsoWindow;
import rendering.TextureLoader;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;

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
            this.state = this.state % Tile.values().length;
        }

    }


    @Override
    public void render(boolean highlight) {
        int textureID = TextureLoader.nameToTextureIDMap.getOrDefault(Tile.getFromInt(state).getName().toLowerCase(Locale.ROOT),-1);

        // Render the texture on top of the quad
        if (textureID != -1 && !highlight) {
            glPushMatrix();
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, textureID);

            glTranslatef(screenPos.x, screenPos.y, 0);

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
        } else {
            super.render(highlight);
        }

        if(highlight) {

            glPushMatrix();
            glTranslatef(screenPos.x, screenPos.y, 0);

            glBegin(GL_QUADS);
            glVertex2f(0, 0);
            glVertex2f(IsoWindow.tileSize.x() / 2.0f, IsoWindow.tileSize.y() / 2.0f);
            glVertex2f(IsoWindow.tileSize.x(), 0);
            glVertex2f(IsoWindow.tileSize.x() / 2.0f, -IsoWindow.tileSize.y() / 2.0f);
            glEnd();

            glPopMatrix();
        }


    }




}