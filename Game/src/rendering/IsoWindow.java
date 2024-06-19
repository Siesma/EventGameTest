package rendering;

import board.wfc.TileCell;
import board.wfc.TileGrid;
import board.wfc.tiles.Forest;
import board.wfc.tiles.Grass;
import board.wfc.tiles.Ground;
import board.wfc.tiles.Water;
import event.EventBus;
import event.events.MousePressedEvent;
import event.events.MouseReleasedEvent;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import other.Pair;
import wfc.Cell;
import wfc.Grid;
import wfc.WaveFunctionCollapse;
import wfc.pattern.Tiles;

import java.nio.DoubleBuffer;
import java.util.Locale;
import java.util.function.Supplier;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class IsoWindow {
    private long window;
    public static final Vector2i windowSize = new Vector2i(2560, 1440);
    private static final Vector2d camera = new Vector2d(0, 0);
    private static final float CAMERA_SPEED = 50.0f;
    private Vector2f cursor;

    private Grid tiles;

    private Vector2i tileUnderCursor;

    private FrameTime frameTimes;

    private float zoomLevel;

    private static final int size = (int) Math.pow(2, 8);

    public static Vector2i tileSize = new Vector2i(size, size >> 1);
    public static Vector2i gridDimension = new Vector2i(200, 200);
    private DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
    private DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);

    public void run() {
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Isometric Rendering", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                EventBus.raise(
                    new MousePressedEvent(
                        new Pair<String, Integer>("Button", button),
                        new Pair<String, Integer>("Action", action),
                        new Pair<String, Vector2f>("MousePosition", cursor),
                        new Pair<String, Vector2i>("TileUnderCursor", tileUnderCursor)
                    )
                );
            } else if (action == GLFW_RELEASE) {
                EventBus.raise(
                    new MouseReleasedEvent(
                        new Pair<String, Integer>("Button", button),
                        new Pair<String, Integer>("Action", action),
                        new Pair<String, Vector2f>("MousePosition", cursor),
                        new Pair<String, Vector2i>("TileUnderCursor", tileUnderCursor)
                    )
                );
            }
        });

        this.zoomLevel = 0;
        glfwSetScrollCallback(window, (window, xOffset, yOffset) -> {
            if (yOffset > 0) {
                zoomLevel++;
            } else if (yOffset < 0) {
                zoomLevel--;
            }
            zoomLevel = Math.max(Math.min(zoomLevel, 100), 5);
        });


        Tiles.initDefaultNeighbouringCandidates();
        Tiles.registerTileCandidate(
            new Forest(),
            new Ground(),
            new Grass(),
            new Water()
        );
        Tiles.initAdjacencyOfAllTiles();


        // Initialize the tile grid
        tiles = new TileGrid(Tiles.allTiles(), () -> new TileCell[gridDimension.x()][gridDimension.y()]) {};


        this.frameTimes = new FrameTime();


        long start = System.currentTimeMillis();

        WaveFunctionCollapse wfc = new WaveFunctionCollapse() {
        };
        wfc.init(tiles);

        while(!wfc.collapse()) {
            tiles.init();
            wfc.init(tiles);
        }
        long end = System.currentTimeMillis();

        System.out.printf("It took %s ms after 1 tries\n", (end - start));

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, windowSize.x(), windowSize.y(), 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long now = System.nanoTime();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            cursor = getCursorPos(window);
            updateCamera();
            glLoadIdentity();

            float worldCursorX = cursor.x + (float) camera.x;
            float worldCursorY = cursor.y + (float) camera.y;

            Vector2f tileUnderCursor = screenToIso(worldCursorX, worldCursorY, tileSize.x(), tileSize.y());
            this.tileUnderCursor = new Vector2i((int) tileUnderCursor.x, (int) tileUnderCursor.y);

            renderGrid();

            frameTimes.render();
            glfwSwapBuffers(window);
            glfwPollEvents();
            long then = System.nanoTime();
            frameTimes.update(now, then);

        }
    }


    private void updateCamera() {
        double moveSpeed = CAMERA_SPEED;

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.x -= moveSpeed;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.x += moveSpeed;
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.y -= moveSpeed / 2;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.y += moveSpeed / 2;
        }
    }

    private void renderGrid() {
        glPushMatrix();
        glTranslated(-camera.x, -camera.y, 0);
        for (int x = 0; x < tiles.getWidth(); x++) {
            for (int y = 0; y < tiles.getHeight(); y++) {
                Vector2i tilePosition = new Vector2i(x, y);
                if (isTileVisible(tilePosition, tileSize)) {
                    Cell tile = tiles.getTileSafe(x, y);
                    boolean highlight = x == tileUnderCursor.x && y == tileUnderCursor.y;
                    renderCell(tile, highlight);
                }
            }
        }
        glPopMatrix();
    }

    private void renderCell(Cell cell, boolean highlight) {
        float screenX = (cell.getPosition()[0] - cell.getPosition()[1]) * (IsoWindow.tileSize.x() / 2.0f) + windowSize.x() / 2.0f;
        float screenY = (cell.getPosition()[0] + cell.getPosition()[1]) * (IsoWindow.tileSize.y() / 2.0f) + windowSize.y() / 2.0f;
        Vector2f screenPos = new Vector2f(screenX, screenY);

        int textureID = TextureLoader.nameToTextureIDMap.get(cell.getState().getDisplayName().toLowerCase(Locale.ROOT));
        if(textureID != -1) {
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
            return;
        }

        glPushMatrix();
        glTranslatef(screenPos.x, screenPos.y, 0);
        if (highlight)
            glColor3d(0, 1, 0);
        else
            glColor3d(1, 0, 0);

        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(IsoWindow.tileSize.x() / 2.0f, IsoWindow.tileSize.y() / 2.0f);
        glVertex2f(IsoWindow.tileSize.x(), 0);
        glVertex2f(IsoWindow.tileSize.x() / 2.0f, -IsoWindow.tileSize.y() / 2.0f);
        glEnd();

        glPopMatrix();


    }

    private boolean isTileVisible(Vector2i tilePosition, Vector2i tileSize) {
        int screenX = (tilePosition.x - tilePosition.y) * tileSize.x / 2;
        int screenY = (tilePosition.x + tilePosition.y) * tileSize.y / 2;

        screenX -= camera.x;
        screenY -= camera.y;

        return screenX + tileSize.x >= -windowSize.x / 2 && screenX <= windowSize.x / 2 &&
            (screenY + tileSize.y) >= -windowSize.y / 2 && (screenY - tileSize.y) <= windowSize.y / 2;
    }

    private Vector2f screenToIso(float screenX, float screenY, int tileWidth, int tileHeight) {
        float offsetX = screenX - windowSize.x() / 2.0f;
        float offsetY = screenY - windowSize.y() / 2.0f;

        float isoX = (offsetX / (tileWidth / 2.0f) + offsetY / (tileHeight / 2.0f)) / 2.0f;
        float isoY = (offsetY / (tileHeight / 2.0f) - offsetX / (tileWidth / 2.0f)) / 2.0f;

        float tileX = (float) Math.floor(isoX);
        float tileY = (float) Math.floor(isoY);

        return new Vector2f(tileX, tileY + 1);
    }

    private Vector2f getCursorPos(long windowID) {
        glfwGetCursorPos(windowID, xBuffer, yBuffer);
        return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
    }

    public static void main(String[] args) {
        new IsoWindow().run();
    }
}
