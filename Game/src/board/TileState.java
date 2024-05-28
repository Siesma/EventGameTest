package board;

import board.wfc.WaveFunctionCollapse;
import event.EventSubscriber;
import event.events.MousePressedEvent;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
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


    @Override
    public void render(boolean highlight) {
        int textureID = WaveFunctionCollapse.stateDictionary.getOrDefault(state == 1 ? "Water" : "Default", -1);
//        if(position.x % 2 == 0 && position.y % 2 == 0) {
//            return;
//        }

        if (textureID == -1) {
            super.render(highlight);
            return;
        }


        if(position.equals(0 ,0)) {
            System.out.println("I exist" + state);
        }

        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTranslatef(screenPos.x, screenPos.y, 0);

        glBegin(GL_QUADS);

        glTexCoord2f(0, 0);
        glVertex2f(0, 0);

        glTexCoord2f(1, 1);
        glVertex2f(IsoWindow.tileSize.x() / 2.0f, IsoWindow.tileSize.y() / 2.0f);

        glTexCoord2f(1, 0);
        glVertex2f(IsoWindow.tileSize.x(), 0);

        glTexCoord2f(0, 1);
        glVertex2f(IsoWindow.tileSize.x() / 2.0f, -IsoWindow.tileSize.y() / 2.0f);

        glEnd();


        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
    }



}