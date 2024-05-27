package rendering;

import board.*;
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

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class IsoWindow {
    private long window;
    public static final Vector2i windowSize = new Vector2i(1920, 1080);
    private static final Vector2d camera = new Vector2d(0, 0);
    private static final float CAMERA_SPEED = 5.0f;
    private Vector2f cursor;

    private Board<TileState> tiles;

    private Vector2i tileUnderCursor;

    private float zoomLevel;

    public static Vector2i tileSize = new Vector2i(16, 8);

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

        // Initialize the tile grid
        int n = 100;
        int m = 100;
        tiles = new Board<>(n, m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                TileState tileState = new TileState(new Vector2i(i, j), 0, windowSize);
                tiles.getBoard()[i][j] = new Cell<>(tileState);
                EventBus.register(tileState);
            }
        }

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

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, windowSize.x(), windowSize.y(), 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long curTime = System.currentTimeMillis();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            cursor = getCursorPos(window);
            updateCamera();
            glLoadIdentity();
            glTranslated(-camera.x, -camera.y, 0);

            float worldCursorX = cursor.x + (float) camera.x;
            float worldCursorY = cursor.y + (float) camera.y;

            Vector2f Vec2ftileUnderCursor = screenToIso(worldCursorX, worldCursorY, tileSize.x(), tileSize.y());
            this.tileUnderCursor = new Vector2i((int) Vec2ftileUnderCursor.x, (int) Vec2ftileUnderCursor.y);
            renderGrid();
            glfwSwapBuffers(window);
            glfwPollEvents();
            long timeForFrame = System.currentTimeMillis() - curTime;
            System.out.printf("Took %s ms to render the frame\n", timeForFrame);
        }
    }

    private boolean isTileVisible(Vector2i tilePosition, Vector2i tileSize) {
        int screenX = (tilePosition.x - tilePosition.y) * tileSize.x / 2;
        int screenY = (tilePosition.x + tilePosition.y) * tileSize.y / 2;

        screenX -= camera.x;
        screenY -= camera.y;

        return screenX + tileSize.x >= -windowSize.x/2 && screenX <= windowSize.x/2 &&
            (screenY + tileSize.y) >= -windowSize.y/2 && (screenY - tileSize.y) <= windowSize.y/2;
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
            camera.y -= moveSpeed;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.y += moveSpeed;
        }
    }

    private void renderGrid() {
        for (int x = 0; x < tiles.getWidth(); x++) {
            for (int y = 0; y < tiles.getHeight(); y++) {
                Vector2i tilePosition = new Vector2i(x, y);
                if (isTileVisible(tilePosition, tileSize)) {
                    Cell<TileState> tile = tiles.getBoard()[x][y];
                    boolean highlight = x == tileUnderCursor.x && y == tileUnderCursor.y;
                    tile.getState().render(highlight);
                }
            }
        }
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
